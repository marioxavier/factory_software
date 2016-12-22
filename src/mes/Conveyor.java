/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

/**
 *
 * @author Utilizador
 */
public class Conveyor {
    
    public String ID;
    private String type;
    private String group;
    private String status;
    private boolean sensor;
    private boolean actuator;
    private boolean orientation;
   
    /**
     * Constructor
     * @param conveyorGroup
     * @param conveyorType
     */
    public Conveyor(String conveyorGroup, String conveyorType)
    {
        // if no conveyor type was given
        if (null == conveyorType)
        {
            System.out.println("No conveyor type given.\n"); 
            System.exit(-1);
        }
         
        else
        {
            // sets the conveyor group
            switch(conveyorGroup)
            {
                case "cell":
                {
                    this.group = conveyorGroup;
                    break;
                }
              
                case "transport":
                {
                    this.group = conveyorGroup;
                    break;
                }
              
                default:
                    System.out.println("Conveyor group not recognized.\n");
                    System.exit(-1);
            }
          
            // sets the conveyor type
            switch(conveyorType)
            {
                case "rotator":
                {
                    this.type = conveyorType;
                    break;
                }
                  
                case "linear":
                {
                    this.type = conveyorType;
                    break;
                }
                  
                case "slide":
                {
                    this.type = conveyorType;
                    break;
                }

                default:
                    System.out.println("Conveyor type not recognized.\n");
                    System.exit(-1);
            }
        }
    }
    
    /**
     * Updates the status of the conveyor
     * @param conveyorStatus
     * @return 
     */
    public boolean updateStatus(String conveyorStatus)
    { 
        if (null == conveyorStatus)
        {
            System.out.println("No conveyor status given.\n");
            return false;
        }
        else
        {
             status = conveyorStatus;
             return true;
        }   
    }
    
    /**
     * Sets convoyr orientation
     * @param conveyorOrientation
     * @return 
     */
    public boolean setOrientation(boolean conveyorOrientation)
    {
        orientation = conveyorOrientation;
        return orientation;
    }
    
      /**
     * Sets convoyr orientation
     * @return 
     */
    public boolean getOrientation()
    {
        return orientation;
    }
    
    /**
     * Gets conveyor status
     * @return 
     */
    public String getStatus()
    {
        return status;
    }
    
    /**
     * 
     * Sets a conveyor type
     * @param conveyorType
     * @return 
     */
    public boolean setType(String conveyorType)
    {
        // if no conveyorType was given
        if (null == conveyorType)
        {
            System.out.println("No conveyor type was given.\n");
            return false;
        }
        // if a conveyor type was given
        else
        {
            type = conveyorType;
            return true;
        }
    }
    
    /**
     * Gets the conveyor type
     * @return 
     */
    public String getType()
    {
        return type;   
    }
    
    /**
     * 
     * Sets a conveyor group
     * @param conveyorGroup
     * @return 
     */
    public boolean setGroup(String conveyorGroup)
    {
        // if no conveyorGroup was given
        if (null == conveyorGroup)
        {
            System.out.println("No conveyor group was given.\n");
            return false;
        }
        // if a conveyor group was given
        else
        {
            group = conveyorGroup;
            return true;
        }
    }
    
    /**
     * Gets the conveyor group
     * @return 
     */
    public String getGroup()
    {
        return group;   
    }
    
    /**
     * Sets conveyor ID
     * @param conveyorID 
     */
    public void setID(String conveyorID)
    {
        ID = conveyorID;
    }
    
    
    /**
     * Updates the sensor of a conveyor
     * @param updateValue
     */
    public void updateSensor(boolean updateValue)
    {
        sensor = updateValue;
    }
    
    
    /**
     * 
     * @return 
     */
    public boolean getSensor()
    {
        return sensor;
    }
    
     /**
     * 
     * @param updateValue 
     */
    public void updateActuator(boolean updateValue)
    {
        actuator = updateValue;
    }
    
    /**
     * 
     * @return 
     */
    public boolean getActuator()
    {
        return this.actuator;
    }
}
