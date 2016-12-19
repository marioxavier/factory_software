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
 * Implements a buffer between MES and PLC
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
            mesOrder = newOrder;
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
        offset = newOffset;
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
    List<WriteModbus> factoryBuffer = new ArrayList<>();
    
    /**
     * 
     * @param dataToUpdate
     * @param classType
     * @return 
     */
    public boolean updateBuffer(String dataToUpdate, String classType)
    {
        WriteModbus bufferElement = new WriteModbus();
        BitVector dataToWrite = new BitVector(16);
                
        // if no data to update was given
        if (null == dataToUpdate)
        {
            System.out.println("No data to update.\n");
            return false;
        }
        // if no class type was given
        else if (null == classType)
        {
            System.out.println("No class type given.\n");
            return false;
        }
        
        // if the object to update is a block
        else if ("block".equals(classType))
        {
            switch(dataToUpdate)
            {
                case "0.2":
                {
                    dataToWrite.setBit(0, true);
                    dataToWrite.setBit(8, false);
                    bufferElement.setMesOrder(dataToWrite);
                    bufferElement.setOffset(0);
                    factoryBuffer.add(bufferElement);
                    break;
                }
                    
                case "0.5":
                {
                    dataToWrite.setBit(0, false);
                    dataToWrite.setBit(1, true);
                    dataToWrite.setBit(8, true);
                    dataToWrite.setBit(9, false);
                    bufferElement.setMesOrder(dataToWrite);
                    bufferElement.setOffset(0);
                    factoryBuffer.add(bufferElement);
                    break;
                }
                    
                case "0.7":
                {
                    dataToWrite.setBit(1, false);
                    dataToWrite.setBit(2, true);
                    dataToWrite.setBit(9, true);
                    dataToWrite.setBit(10, false);
                    bufferElement.setMesOrder(dataToWrite);
                    bufferElement.setOffset(0);
                    factoryBuffer.add(bufferElement);
                    break;
                }
                    
                case "0.10":
                {
                    dataToWrite.setBit(2, false);
                    dataToWrite.setBit(3, true);
                    dataToWrite.setBit(10, true);
                    dataToWrite.setBit(11, false);
                    bufferElement.setMesOrder(dataToWrite);
                    bufferElement.setOffset(0);
                    factoryBuffer.add(bufferElement);
                    break;
                }
                    
                case "0.12":
                {
                    dataToWrite.setBit(3, false);
                    dataToWrite.setBit(4, true);
                    dataToWrite.setBit(11, true);
                    dataToWrite.setBit(12, false);
                    bufferElement.setMesOrder(dataToWrite);
                    bufferElement.setOffset(0);
                    factoryBuffer.add(bufferElement);
                    break;
                }
                
                case "0.14":
                {
                    //TO DO
                    break;
                    
                }
                default:
                    System.out.println("Data type not recognized.\n");
                    return false;         
            }  
        }
        else if("transform".equals(classType))
        {
            switch(dataToUpdate)
            {
                case "A":
                case "B":
                case "C":
                case "D":
                case "E":
                case "F":
                case "G":
                case "H":
                case "I":
                case "J":
                case "K":
                case "L":
                case "M":
            }
            
        }
        return true;
    }
}
