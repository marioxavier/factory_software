/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import java.util.ArrayList;
import java.util.List;
import net.wimpi.modbus.util.BitVector;

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
        if (null == dataToUpdate)
        {
            System.out.println("No data to update.\n");
            return false;
        }
        else if (null == classType)
        {
            System.out.println("No class type given.\n");
            return false;
        }
        
        if ("block".equals(classType))
        {
            switch(dataToUpdate)
            {
                case "0.2":
                    break;
                case "0.5":
                    break;
                case "0.7":
                    break;
                case "0.10":
                    break;
                case "0.12":
                    break;
                default:
                    System.out.println("Data type not recognized.\n");
                    return false;         
            }
             
        }
        return true;
    }
}
