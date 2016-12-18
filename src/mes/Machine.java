/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import net.wimpi.modbus.util.BitVector;

/**
 *
 * @author Mário Xavier
 */
public class Machine {
    
    public int ID;
    private String type;
    private String status;
    private String currentTool;
    private String[] tools;
    
    /**
     * 
     * @param machineType 
     */
    public Machine(String machineType)
    {
        if (null == machineType)
            System.out.println("Machine type not given.\n");
        else
        {
            type = machineType;
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
     * Adds a tool to a machine
     * @param toolType
     * @return 
     */
    public boolean addTool(String toolType)
    {
        // if no tool type was given
        if (null == toolType)
        {
            System.out.println("No tool type was given.\n");
            return false;
        }
        // if a tool type was given
        else
        {
            tools[tools.length + 1] = toolType;
            return true;
        }
    }
    
}
