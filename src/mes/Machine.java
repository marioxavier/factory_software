/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import mes.graph.exception.InvalidConstructionException;
import net.wimpi.modbus.util.BitVector;

/**
 *
 * @author Mário Xavier
 */
public final class Machine 
{
    
    public int ID;
    private String type;
    private String status; //rotating, 
    private String currentTool;
    private String nextTool;
    
    /**
     * 
     * @param machineType 
     * @throws mes.graph.exception.InvalidConstructionException 
     */
    public Machine(String machineType) throws InvalidConstructionException
    {
        if (null == machineType)
            System.out.println("Machine type not given.\n");
        else
        {
            type = machineType;
            //this.checkTools();
            this.currentTool = "T1";
            this.nextTool = "T1";
        }
    }
    
    /**
     * 
     * Sets a machine type
     * @param machineType
     * @return 
     */
    public boolean setType(String machineType)
    {
        // if no machineType was given
        if (null == machineType)
        {
            System.out.println("No machine type was given.\n");
            return false;
        }
        // if a machine type was given
        else
        {
            type = machineType;
            return true;
        }
    }
    
    /**
     * Gets the machine type
     * @return 
     */
    public String getType()
    {
        return type;   
    }
    
    /**
     * Updates the status of a machine
     * @param vectorOfBits
     * @return 
     */
    public boolean updateStatus(BitVector vectorOfBits)
    {
        // if no vector of bits was given
        if (null == vectorOfBits)
        {
            System.out.println("No vector of bits was given.\n");
            return false;
        }
        // if a vector of bits was given
        else
        {
            status = vectorOfBits.toString();
            return true;
        } 
    }
    
    /**
     * Gets the status of a machine
     * @return 
     */
    public String getStatus()
    {
        return status;
    }
    
    /**
     * Adds a tool to a machine (returns "left" or "right")
     * @return 
     * @throws mes.graph.exception.InvalidConstructionException 
     */
    public String checkToolMovement() throws InvalidConstructionException
    {      
        if ("T1".equals(currentTool) && "T2".equals(nextTool))
            return "right";
        else if ("T1".equals(currentTool) && "T3".equals(nextTool))
            return "left";
        else if ("T2".equals(currentTool) && "T1".equals(nextTool))
            return "left";
        else if ("T2".equals(currentTool) && "T3".equals(nextTool))
            return "right";
        else if ("T3".equals(currentTool) && "T1".equals(nextTool))
            return "right";
        else
            return "left";
    }
    
    public String updateTool(String nextTool)
    {
        return null;
    }
}
