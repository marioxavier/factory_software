/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

/**
 *
 * @author profanaxavier
 */
public class Block {
    
    
    public String ID;
    private String type;
    private String finalType;
    private String position;
    private String status;
    private String destination;
    
    public String operation;
    private String enterOrder;
    
    /**
     * Constructor
     * @param blockType
     * @param finalBlockType
     * @param blockDestination
     * @param blockID 
     */
    public Block(String blockType, String finalBlockType, String blockDestination, String blockID)
    {
        // if block type was not given
        if (null == blockType)
        {
                System.out.println("Block type not sepcified.\n");
                System.exit(-1);
        }
           
        // if block destination was not given
        else if (null == blockDestination)
        {
            System.out.println("Block destination not specified.\n");
            System.exit(-1);
        }
        
        else if(null == blockID)
        {
            System.out.println("Block ID not specified.\n");
            System.exit(-1);
        }
        
        // all input arguments OK
        else
        {
            this.finalType = finalBlockType;
            this.type = blockType;
            this.destination = blockDestination;
            this.ID = blockID;
            // initial position
            this.position = "0.0";
            this.status="transporting";
        }
    }
    
    public Block(String blockType, String finalBlockType, String blockDestination, String blockID, String blockOperation)
    {
        // if block type was not given
        if (null == blockType)
        {
                System.out.println("Block type not sepcified.\n");
                System.exit(-1);
        }
           
        // if block destination was not given
        else if (null == blockDestination)
        {
            System.out.println("Block destination not specified.\n");
            System.exit(-1);
        }
        
        else if(null == blockID)
        {
            System.out.println("Block ID not specified.\n");
            System.exit(-1);
        }
        
        // all input arguments OK
        else
        {
            this.operation = blockOperation;
            this.finalType = finalBlockType;
            this.type = blockType;
            this.destination = blockDestination;
            this.ID = blockID;
            // initial position
            this.position = "0.0";
            this.status="transporting";
        }
    }
    
    
    
    /**
     * Gets block type
     * @return 
     */
    public String getType()
    {
        return type;
    }
    
    public boolean setType(String blockType)
    {
        // if block type was not given
        if (null == blockType)
        {
            System.out.println("No block type given.\n");
            return false;
        }
        // if block type was given
        else 
        {
            type = blockType;
            return true;
        }
    }
    
    /**
     * Gets block status
     * @return 
     */
    public String getStatus()
    {
        return status;
    }
    
    /**
     * 
     * @param blockStatus
     * @return 
     */
    public boolean updateStatus(String blockStatus)
    {
        // if no block status was given
        if(null == blockStatus)
        {
            System.out.println("No block status was given.\n");
            return false;
        }
        // if a block status was given
        else
        {
        status = blockStatus;
        return true;
        }
    }
    
    /**
     * Gets the block position
     * @return 
     */
    public String getPosition()
    {
        return position;
    }
    
    /**
     * Sets the block position
     * @param blockPosition
     * @return 
     */
    public boolean setPosition(String blockPosition)
    {
        position = blockPosition;
        return true;
    }
    
    // gets the block destination
    public String getDestination()
    {
        return destination;
    }
    
    /**
     * Sets the block destination
     * @param blockDestination
     * @return 
     */
    public boolean setDestination(String blockDestination)
    {
        // if no destination was given
        if (null == blockDestination)
        {
            System.out.println("No destination was given.\n");
            return false;
        }
        // if a destination was given
        else
        {
            destination = blockDestination;
            // updates order to write to PLC;
            this.setEnterOrder();
            return true;
        }
    }
    
    /**
     * Checks if position of the block is the destination
     * @return 
     */
    public boolean isDestination()
    {
        // if position is equal to destination returns true
        return position.equals(destination);
    }
    
    /**
     * 
     * @return 
     */
    public String getEnterOrder()
    {
        return this.enterOrder;
    }
    
    /**
     * 
     * @return 
     */
    public boolean setEnterOrder()
    { 
        String[] destinationArray = destination.split("\\.");
        enterOrder = "Enter T" + destinationArray[1];
        return true;
    }
    
    
    /**
     * 
     * @param newOperation
     * @return 
     */
    public boolean setOperation(String newOperation)
    {
        operation = newOperation;
        return true;
    }
    
    public String getFinalType()
    {
        return finalType;
    }
    
    
}