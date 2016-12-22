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
    public int[] factoryBuffer;
    private int capacity;
    // mutex used to synchronize access to the buffer
    private final Object mutex = new Object();
    // data to write to PLC in bitvector form
    private BitVector dataToWrite;
    
    // protocol used to communicate with PLC
    private Modbus protocolToPLC;
    
    // boolean used to kill controller thread
    private boolean killThread=false;
    
    // Hashtable that maps orders to offset in buffer
    private Hashtable<String, Integer> bufferMap;
    
    
    
    /**
     * Constructor
     * @param bufferCapacity 
     */
    public Controller(Modbus protocol)
    {
        // if the bufferCapacity is zero

        if (null == protocol)
        {
            System.out.println("No protocol given to Controller");
            System.exit(-1);
        }
        
        // if all input arguments are OK
        else
        {
            protocolToPLC = protocol;
            capacity = 65;
            dataToWrite = new BitVector(capacity);
            if (!createBuffer(capacity))
            {
                System.out.println("Buffer not created.\n");
                System.exit(-1);
            }           
        }
    }
    
    
    public boolean initController()
    {
        bufferMap = new Hashtable<String, Integer>();

        bufferMap.put("EnterC1",0);
        bufferMap.put("EnterC2",0);
        bufferMap.put("EnterC3",0);
        bufferMap.put("EnterC4",0);
        bufferMap.put("EnterC5",0);
        bufferMap.put("EnterC6",0);
        bufferMap.put("EnterC7",0);
        
        bufferMap.put("KeepGoingC1",0);
        bufferMap.put("KeepGoingC2",0);
        bufferMap.put("KeepGoingC3",0);
        bufferMap.put("KeepGoingC4",0);
        bufferMap.put("KeepGoingC5",0);
        bufferMap.put("KeepGoingC6",0);
        bufferMap.put("KeepGoingC7",0);
        
        bufferMap.put("P1P2@C1",0);
        bufferMap.put("P1P7@C1",0);
        bufferMap.put("P2P3@C1",0);
        bufferMap.put("P3P4@C1",0);
        bufferMap.put("P3P6@C1",0);
        bufferMap.put("P4P6@C1",0);
        bufferMap.put("P4P7@C1",0);
        bufferMap.put("P5P7@C1",0);
        bufferMap.put("P6P7@C1",0);
        bufferMap.put("P1P6@C1",0);
        bufferMap.put("P3P7@C1",0);
        
        bufferMap.put("P1P3@C2",0);
        bufferMap.put("P1P4@C2",0);
        bufferMap.put("P1P7@C2",0);
        bufferMap.put("P2P1@C2",0);
        bufferMap.put("P2P3@C2",0);
        bufferMap.put("P3P5@C2",0);
        bufferMap.put("P4P5@C2",0);
        bufferMap.put("P4P7@C2",0);
        bufferMap.put("P1P7@C2",0);
        bufferMap.put("P1P5@C2",0);
        
        bufferMap.put("P1P2@C3",0);
        bufferMap.put("P1P7@C3",0);
        bufferMap.put("P2P3@C3",0);
        bufferMap.put("P3P4@C3",0);
        bufferMap.put("P3P6@C3",0);
        bufferMap.put("P4P6@C3",0);
        bufferMap.put("P4P7@C3",0);
        bufferMap.put("P5P7@C3",0);
        bufferMap.put("P6P7@C3",0);
        bufferMap.put("P1P6@C3",0);
        bufferMap.put("P3P7@C3",0);
                
        bufferMap.put("P1P3@C4",0);
        bufferMap.put("P1P4@C4",0);
        bufferMap.put("P1P7@C4",0);
        bufferMap.put("P2P1@C4",0);
        bufferMap.put("P2P3@C4",0);
        bufferMap.put("P3P5@C4",0);
        bufferMap.put("P4P5@C4",0);
        bufferMap.put("P4P7@C4",0);
        bufferMap.put("P1P7@C4",0);
        bufferMap.put("P1P5@C4",0);
        
        bufferMap.put("Criar P1",0);
        bufferMap.put("Criar P2",0);
        bufferMap.put("Criar P3",0);
        bufferMap.put("Criar P4",0);
        bufferMap.put("Criar P5",0);
        bufferMap.put("Criar P6",0);
        bufferMap.put("Criar P7",0);
        bufferMap.put("Criar P8",0);
        bufferMap.put("Criar P9",0);
        
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
            this.factoryBuffer = new int[bufferCapacity];
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
           
            // executes when you hold the mutex
            synchronized(mutex)
            {
                factoryBuffer[bufferOffset] = 1;
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
