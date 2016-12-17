/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

/**
 *
 * @author Mário Xavier
 */
public class Transport {
    
    private int ID;
    private String type;
    private String status;
    
    /**
     * Constructor that creates his own conveyors
     * @param transportType 
     * @param currentFactory
     */
    public Transport(String transportType, Factory currentFactory)
    {
        // if no transport type was given
        if (null == transportType)
        {
            System.out.println("No transport type given.\n");
            System.exit(-1);
        }
        // if no factory was given
        else if (null == currentFactory)
        {
            System.out.println("No factory was given.\n");
            System.exit(-1);
        }
        // all parameters are OK
        else
            switch(transportType)
            {
                case "input":
                    type = transportType;
                    // creates transport conveyors in the curreny factory
                    currentFactory.addConveyors("transport", "linear", 30);
                    break;

                case "output":
                    type = transportType;
                    //creates transport conveyors in the current factory
                    currentFactory.addConveyors("transport", "linear", 30);
                    break;

                default:
                    System.out.println("Transport type not recognized.\n");
                    System.exit(-1);
            }
    }
    
    /**
     * 
     * @param newBlock
     * @return 
     */
    public boolean start(Block newBlock)
    {
        // if no new block was given
        if (null == newBlock)
        {
            System.out.println("No block was given to start transport.\n");
            return false;
        }
        // if a block was given
        else
        {
            newBlock.setPosition("00");
            return true;
        }
    }
    
}