/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import net.wimpi.modbus.util.BitVector;

/**
 *
 * @author Utilizador
 */
public class Controller extends Thread
{
    // the buffer with orders
    public String[] factoryBuffer;
    private int capacity;
    // mutex used to synchronize access to the buffer
    private final Object mutex = new Object();
    // data to write to PLC in bitvector form
    private BitVector dataToWrite;
    // protocol used to communicate with PLC
    public Modbus protocolToPLC;
    // boolean used to kill controller thread
    private boolean killThread = false;
    
    // Hashtable that maps orders to offset in buffer
    private Hashtable<String, Integer> bufferMap;
    private Hashtable<String, BitVector> blockBitvectorTable;

    /**
     * Constructor
     * @param protocol
     * @param virtualFactory
     */
    public Controller(Modbus protocol, Factory virtualFactory)
    {
        // if no protocol was given
        if (null == protocol)
        {
            System.out.println("No protocol given to Controller");
            System.exit(-1);
        }        
        // if no factory was given
        else if (null == virtualFactory)
        {
            System.out.println("No block type map given.\n");
            System.exit(-1);
        }
        // if all input arguments are OK
        else
        {
            this.protocolToPLC = protocol;
            capacity = 65;
            dataToWrite = new BitVector(capacity);
            blockBitvectorTable = virtualFactory.getBlockBitvectorTable();
            if (!createBuffer(capacity))
            {
                System.out.println("Buffer not created.\n");
                System.exit(-1);
            }
        }
    }
    
    /**
     * Initializes buffer from factory
     * @return 
     */
    public boolean initController()
    {
        bufferMap = new Hashtable<>();

        bufferMap.put("Enter T2",0);
        bufferMap.put("Enter T5",1);
        bufferMap.put("Enter T7",2);
        bufferMap.put("Enter T10",3);
        bufferMap.put("Enter T12",4);
        bufferMap.put("Enter T14",5);
        bufferMap.put("Enter T15",6);
        
        bufferMap.put("KeepGoing T2",7);
        bufferMap.put("KeepGoing T5",8);
        bufferMap.put("KeepGoing T7",9);
        bufferMap.put("KeepGoing T10",10);
        bufferMap.put("KeepGoing T12",11);
        bufferMap.put("KeepGoing T14",12);
        bufferMap.put("KeepGoing T15",13);
        
        bufferMap.put("P1P2 C1",14);
        bufferMap.put("P1P7 C1",15);
        bufferMap.put("P2P3 C1",16);
        bufferMap.put("P3P4 C1",17);
        bufferMap.put("P3P6 C1",18);
        bufferMap.put("P4P6 C1",19);
        bufferMap.put("P4P7 C1",20);
        bufferMap.put("P5P7 C1",21);
        bufferMap.put("P6P7 C1",22);
        bufferMap.put("P1P6 C1",23);
        bufferMap.put("P3P7 C1",24);
        
        bufferMap.put("P1P3 C2",25);
        bufferMap.put("P1P4 C2",26);
        bufferMap.put("P1P7 C2",27);
        bufferMap.put("P2P1 C2",28);
        bufferMap.put("P2P3 C2",29);
        bufferMap.put("P3P5 C2",30);
        bufferMap.put("P4P5 C2",31);
        bufferMap.put("P4P7 C2",32);
        bufferMap.put("P1P7 C2 2",33);
        bufferMap.put("P1P5 C2",34);
        
        bufferMap.put("P1P2 C3",35);
        bufferMap.put("P1P7 C3",36);
        bufferMap.put("P2P3 C3",37);
        bufferMap.put("P3P4 C3",38);
        bufferMap.put("P3P6 C3",39);
        bufferMap.put("P4P6 C3",40);
        bufferMap.put("P4P7 C3",41);
        bufferMap.put("P5P7 C3",42);
        bufferMap.put("P6P7 C3",43);
        bufferMap.put("P1P6 C3",44);
        bufferMap.put("P3P7 C3",45);
                
        bufferMap.put("P1P3 C4",46);
        bufferMap.put("P1P4 C4",47);
        bufferMap.put("P1P7 C4",48);
        bufferMap.put("P2P1 C4",49);
        bufferMap.put("P2P3 C4",50);
        bufferMap.put("P3P5 C4",51);
        bufferMap.put("P4P5 C4",52);
        bufferMap.put("P4P7 C4",53);
        bufferMap.put("P1P7 C4 2",54);
        bufferMap.put("P1P5 C4",55);
        
        bufferMap.put("Create P1",56);
        bufferMap.put("Create P2",56);
        bufferMap.put("Create P3",56);
        bufferMap.put("Create P4",56);
        bufferMap.put("Create P5",56);
        bufferMap.put("Create P6",56);
        bufferMap.put("Create P7",56);
        bufferMap.put("Create P8",56);
        bufferMap.put("Create P9",56);
        
        return true;
    }
   
    /**
     * Creates a buffer to write to PLC
     * @param bufferCapacity
     * @return 
     */
    public boolean createBuffer(int bufferCapacity)
    {
        // if the given size is zero
        if (bufferCapacity == 0)
        {
            System.out.println("Creating a buffer with size zero.\n");
            return false;
        }
        // if all input arguments are OK
        else
        {
            this.factoryBuffer = new String[bufferCapacity];
            return true;
        }
    }
    
    /**
     * Updates orders to give to the PLC
     * @param order
     * @return 
     */
    public boolean updateBuffer(String order)
    {        
        // if no order was given
        if (null == order)
        {
            System.out.println("No order was given.\n");
            return false;
        }
        // if all input arguments are OK
        else
        {
            int bufferOffset = this.bufferMap.get(order);
            String[] orderArray = order.split(" "); 
            if (null != orderArray[0])
                switch (orderArray[0]) 
                {
                    case "Create":
                        System.out.println("DEBUG:: Ordem de criação de peça.");
                        BitVector blockBitvector = this.blockBitvectorTable.get(orderArray[1]);
                        // updates factory buffer
                        factoryBuffer[bufferOffset] = blockBitvector.toString();
                        // sets the bitvector to write
                        dataToWrite.setBit(bufferOffset, blockBitvector.getBit(0));
                        dataToWrite.setBit(bufferOffset + 1, blockBitvector.getBit(1));
                        dataToWrite.setBit(bufferOffset + 2, blockBitvector.getBit(2));
                        dataToWrite.setBit(bufferOffset + 3, blockBitvector.getBit(3));
                        dataToWrite.setBit(bufferOffset + 4, blockBitvector.getBit(4));
                        dataToWrite.setBit(bufferOffset + 5, blockBitvector.getBit(5));
                        dataToWrite.setBit(bufferOffset + 6, blockBitvector.getBit(6));
                        dataToWrite.setBit(bufferOffset + 7, blockBitvector.getBit(7));
                        break;
                        
                    case "Enter":
                        System.out.println("Order de enter");
                        //System.out.println("DEBUG:: Ordem de entrada.");
                         // updates factory buffer
                        this.factoryBuffer[bufferOffset] = "1";
                        // sets the bitvector to write
                        this.dataToWrite.setBit(bufferOffset, true);
                        break;
                        
                    case "KeepGoing":
                        //System.out.println("DEBUG:: Ordem de KeepGoing.");
                         // updates factory buffer
                        //System.out.println("Offset: " + bufferOffset);
                        this.factoryBuffer[bufferOffset] = "1";
                        // sets the bitvector to write
                        this.dataToWrite.setBit(bufferOffset, true);
                        break;
                        
                    default:
                        // updates the buffer
                        this.factoryBuffer[bufferOffset] = "1";
                        // sets the bitvector to write
                        this.dataToWrite.setBit(bufferOffset, true);
                        break;
                }

            // executes when you hold the mutex
            synchronized(mutex)
            {   
                // writes the order to the PLC
                this.protocolToPLC.writeModbus(88, dataToWrite);
                try
                {
                    TimeUnit.MILLISECONDS.sleep(500);
                }
                catch(Exception Ex)
                {
                    System.out.println("error in sleep " + Ex);
                }
                // resets data to write
                this.dataToWrite = new BitVector(capacity);
                this.protocolToPLC.writeModbus(88, dataToWrite);
                return true;
            }
        }
    }
        
    /**
     * Gets the biffer capacity
     * @return 
     */
    public int getCapacity()
    {
        return capacity;
    }
    
    /**
     * Sets the capacity of the buffer
     * @param bufferCapacity
     * @return 
     */
    public boolean setCapacity(int bufferCapacity)
    {
        // if the given capacity is zero
        if (bufferCapacity == 0)
        {
            System.out.println("Trying to set buffer with size zero.\n");
            return false;
        }
        // if all input arguments are OK
        else
        {
            this.capacity = bufferCapacity;
            return true;
        }
    }
    
    
    @Override
    public void run()
    { 
       while(!killThread)
        {
            synchronized(mutex)
            {
                protocolToPLC.writeModbus(0, dataToWrite);
                
                /*
                Might need a Sleep Here !
                */
                
            }
        }
    }
    
    
    public void stopThread()
    {
        killThread = true;
    }
}
