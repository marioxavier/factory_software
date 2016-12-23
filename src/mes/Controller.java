/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import net.wimpi.modbus.util.BitVector;

/**
 * Implements the element of a buffer with orders from MES to PLC
 * @author Mário
 */
class WriteModbus
{
    private BitVector mesOrder;
    private int offset;
    
    /**
     * Sets factory buffer
     * @param newOrder
     * @return 
     */
    public boolean setMesOrder(BitVector newOrder)
    {
        if (null == newOrder)
        {
            System.out.println("No bitvector given.\n");
            return false;
        }
        else
        {
            this.mesOrder = newOrder;
            return true;
        }
    }
    
   /**
    * Gets MES order
    * @return 
    */
    public BitVector getMesOrder()
    {
        return mesOrder;
    }
    
    /**
     * Sets the new offset
     * @param newOffset
     * @return 
     */
    public boolean setOffset(int newOffset)
    {
        this.offset = newOffset;
        return true;
    }
    
    /**
     * Gets offset
     * @return 
     */
    public int getOffset()
    {
        return offset;
    }
}





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
    private Modbus protocolToPLC;
    // boolean used to kill controller thread
    private boolean killThread = false;
    
    // Hashtable that maps orders to offset in buffer
    private Hashtable<String, Integer> bufferMap;
    private Hashtable<String, BitVector> blockBitvectorTable;
    
    public Controller()
    {
        
    }
    
    /**
     * Constructor
     * @param protocol
     * @param blockBitvector
     */
    public Controller(Modbus protocol, Factory virtualFactory)
    {
        if (null == protocol)
        {
            System.out.println("No protocol given to Controller");
            System.exit(-1);
        }
        
        // if all input arguments are OK
        else if (null == virtualFactory)
        {
            System.out.println("No block type map given.\n");
            System.exit(-1);
        }
        else
        {
            protocolToPLC = protocol;
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
    
    
    public boolean initController()
    {
        bufferMap = new Hashtable<>();

        bufferMap.put("Enter C1",0);
        bufferMap.put("Enter C2",1);
        bufferMap.put("Enter C3",2);
        bufferMap.put("Enter C4",3);
        bufferMap.put("Enter C5",4);
        bufferMap.put("Enter C6",5);
        bufferMap.put("Enter C7",6);
        
        bufferMap.put("KeepGoing C1",7);
        bufferMap.put("KeepGoing C2",8);
        bufferMap.put("KeepGoing C3",9);
        bufferMap.put("KeepGoing C4",10);
        bufferMap.put("KeepGoing C5",11);
        bufferMap.put("KeepGoing C6",12);
        bufferMap.put("KeepGoing C7",13);
        
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
        bufferMap.put("P1P7 C2",33);
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
        bufferMap.put("P1P7 C4",54);
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
            int bufferOffset = bufferMap.get(order);
            String[] orderArray = order.split(" "); 
            if ("Create".equals(orderArray[0]))
            {
                BitVector blockBitvector = this.blockBitvectorTable.get(orderArray[1]);
                factoryBuffer[bufferOffset] = blockBitvector.toString();
                dataToWrite.setBit(bufferOffset, blockBitvector.getBit(0));
                dataToWrite.setBit(bufferOffset + 1, blockBitvector.getBit(1));
                dataToWrite.setBit(bufferOffset + 2, blockBitvector.getBit(2));
                dataToWrite.setBit(bufferOffset + 3, blockBitvector.getBit(3));
                dataToWrite.setBit(bufferOffset + 4, blockBitvector.getBit(4));
                dataToWrite.setBit(bufferOffset + 5, blockBitvector.getBit(5));
                dataToWrite.setBit(bufferOffset + 6, blockBitvector.getBit(6));
                dataToWrite.setBit(bufferOffset + 7, blockBitvector.getBit(7));
            }
            else
            {
                factoryBuffer[bufferOffset] = "1";   
                dataToWrite.setBit(bufferOffset, true);                
            } 
           
            // executes when you hold the mutex
            synchronized(mutex)
            {            
                protocolToPLC.writeModbus(0, dataToWrite);
                // resets data to write
                dataToWrite = new BitVector(capacity);
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
