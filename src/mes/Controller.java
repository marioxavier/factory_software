/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import java.util.ArrayList;
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
    
    
    /**
     * Constructor
     * @param bufferCapacity 
     */
    public Controller(int bufferCapacity, Modbus protocol)
    {
        // if the bufferCapacity is zero
        if (bufferCapacity == 0)
        {
            System.out.println("Buffer created with size zero.\n");
            System.exit(-1);
        }
        
        else if (null == protocol)
        {
            System.out.println("No protocol given to Controller");
            System.exit(-1);
        }
        // if all input arguments are OK
        else
        {
            protocolToPLC = protocol;
            dataToWrite = new BitVector(bufferCapacity);
            capacity = bufferCapacity;
            if (!createBuffer(bufferCapacity))
            {
                System.out.println("Buffer not created.\n");
                System.exit(-1);
            }           
        }
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
     * @param offset
     * @param dataToUpdate
     * @return 
     */
    public boolean updateBuffer(int offset, int dataToUpdate)
    {
        // if no data to update was given
        if (dataToUpdate != 0 || dataToUpdate != 1)
        {
            System.out.println("Wrong data to update.\n");
            return false;
        }
        // if all input arguments are OK
        else
        {
            // executes when you hold the mutex
            synchronized(mutex)
            {
                // updates the buffer
                this.factoryBuffer[offset] = dataToUpdate;
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
