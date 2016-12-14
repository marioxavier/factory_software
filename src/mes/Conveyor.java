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
    private boolean isReady;
    private boolean orientation;
      
    /**
     * Constructor
     * @param conveyorGroup
     * @param conveyorType
     */
    public Conveyor(String conveyorGroup, String conveyorType)
    {
      if (null == conveyorType)
          System.out.println("No conveyor type given.\n");
      else
       {
          
          // sets the conveyor group
          switch(conveyorGroup)
          {
              case "cell":
              {
                  group = conveyorGroup;
                  break;
              }
              case "transport":
              {
                  group = conveyorGroup;
                  break;
              }
              default:
                System.out.println("Conveyor group not recognized.\n");
          }
          
          // sets the conveyor type
          switch(conveyorType)
          {
              case "rotator":
              {
                  type = conveyorType;
                  break;
              }
                  
              case "linear":
              {
                  type = conveyorType;
                  break;
              }
                  
              case "slide":
              {
                  type = conveyorType;
                  break;
              }
                  
              default:
                System.out.println("Conveyor type not recognized.\n");
          }
      }
    }
    
    /**
     * Updates the status of the conveyor
     * @param conveyorStatus
     * @return 
     */
    public boolean updateStatus(boolean conveyorStatus)
    { 
        isReady = conveyorStatus;
        return true;
    }
    
    /**
     * Gets conveyor status
     * @return 
     */
    public boolean getStatus()
    {
        return isReady;
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
    
    
    public void setID(String id)
    {
        ID = id;
    }
   
    
}
