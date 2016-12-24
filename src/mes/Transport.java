/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import mes.graph.exception.InvalidConstructionException;
import net.wimpi.modbus.util.BitVector;


class Transporter implements Runnable
{
    Transport transportUnit;
    Block blockToFollow;
    DecisionMaker decisionUnit;
    Factory virtualFactory;
    Controller controlUnit;
    
    public Transporter(Transport transportObject, Block blockToTransport, 
            DecisionMaker decisionObject, Factory currentFactory)
    {
        // if no transport object was given
        if (null == transportObject)
        {
            System.out.println("No transport object given.\n");
            System.exit(-1);
        }
        // if no block to transport was given
        else if (null == blockToTransport)
        {
            System.out.println("No block to transport was given.\n");
            System.exit(-1);
        }
        // all input arguments are OK
        else 
        {
            this.transportUnit = transportObject;
            this.blockToFollow = blockToTransport;
            this.decisionUnit = decisionObject;
            this.virtualFactory = currentFactory;
            this.controlUnit = currentFactory.getControlUnit();
            controlUnit.initController();
            controlUnit.protocolToPLC = this.transportUnit.getProtocol();
        }
    }
    
    /**
     * Thread 
     */
    @Override
    @SuppressWarnings("empty-statement")
    public void run()
    {
        // loops forever
        while(true)
        {
            // updates position
            blockToFollow.setPosition(this.virtualFactory.getNewPosition(blockToFollow));            
            
            if(!blockToFollow.isDestination())
            {
               //System.out.println("DEBUG:: Não é destino.");
               // decides destination
               blockToFollow.setDestination(decisionUnit.decideDestination());
               
               // orders block to keep going 
               switch(blockToFollow.getPosition())
               {
                    case "0.2":
                        this.controlUnit.updateBuffer("KeepGoing T2");
                        break;
                    case "0.5":
                        this.controlUnit.updateBuffer("KeepGoing T5");
                        break;
                    case "0.7":
                        this.controlUnit.updateBuffer("KeepGoing T7");
                        break;
                    case "0.10":
                        this.controlUnit.updateBuffer("KeepGoing T10");
                        break;
                    case "0.12":
                        this.controlUnit.updateBuffer("KeepGoing T12");
                        break;
                    case "0.14":
                        this.controlUnit.updateBuffer("KeepGoing T14");
                        break;                       
               }
            }
            // arrived destination
            else
            {
                // when block is at his destination, gives enter order and writes in the buffer
                this.controlUnit.updateBuffer(blockToFollow.getEnterOrder());
                    
                // updates block status
                blockToFollow.updateStatus("waiting");  
            }            
        }
    }         
}

/**
 *
 * @author Mário Xavier
 */
public class Transport extends Thread  
{    
    private int ID;
    private String type;
    private String status;
    private Factory virtualFactory;
    private Modbus protocolToPLC;
    public Hashtable<String, Block> blocksInFactory;
    public Controller controlUnit;
            
    /**
     * Constructor
     * @param transportType
     * @param currentFactory
     * @param protocol
     * @throws mes.graph.exception.InvalidConstructionException
     */
    public Transport(String transportType, Factory currentFactory, Modbus protocol) throws InvalidConstructionException
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
            virtualFactory = currentFactory;
            protocolToPLC = protocol;
            blocksInFactory = currentFactory.getBlocksInFactory();
            // creating empty control unit to be set by Factory
            controlUnit = new Controller(this.protocolToPLC, virtualFactory);
            switch(transportType)
            {
                case "input":
                {
                    type = transportType;
                    // creates transport conveyors in the curreny factory
                    currentFactory.addTransportConveyors("linear", 16);
                    break;
                }
                    
                case "output":
                {
                    type = transportType;
                    //creates transport conveyors in the current factory
                    currentFactory.addTransportConveyors("linear", 16);
                    break; 
                }
                   
                default:
                    System.out.println("Transport type not recognized.\n");
                    System.exit(-1);
            }
    }
    
    /**
     * Creates and runs a new transporter
     * @param blockToTransport 
     */
    public void startTransport(Block blockToTransport)
    {
        DecisionMaker decisionUnit = new DecisionMaker(protocolToPLC, virtualFactory);
        Runnable transporter = new Transporter(this, blockToTransport, decisionUnit, virtualFactory);
        new Thread(transporter).start();       
    }
    
    /**
     * Gets transport ID
     * @return 
     */
    public int getID()
    {
        return ID;
    }
    
    /**
     * Set the transport ID
     * @param transportID
     * @return 
     */
    public boolean setID(int transportID)
    {
        ID = transportID;
        return true;
    }
    
    /**
     *Gets transport type 
     * @return 
     */
    public String getType()
    {
        return type;
    }
    
    /**
     * Sets transport type
     * @param transportType
     * @return 
     */
    public boolean setType(String transportType)
    {
        if (null == transportType)
        {
            System.out.println("Transport type not given.\n");
            return false;
        }
        else
        {
            type = transportType;
            return true;
        } 
    }
    
    /**
     * Gets factory status
     * @return 
     */
    public String getStatus()
    {
        return status;
    }
    
    /**
     * Sets status
     * @param transportStatus
     * @return 
     */
    public boolean setStatus(String transportStatus)
    {
        if (null == transportStatus)
        {
            System.out.println("Transport status not given.\n");
            return false;
        }
        else
        {
            status = transportStatus;
            return false;
        }  
    }        
    
    /**
     * Gets factory
     * @return 
     */
    public Factory getFactory()
    {
        return virtualFactory;
    }
    
    /**
     * Gets modbus protocol
     * @return 
     */
    public Modbus getProtocol()
    {
        return protocolToPLC;
    }
    
    /**
    * Gets the control unit
    * @return 
    */
   public Controller getControlUnit()
   {
       return controlUnit;
   }
   
   /**
    * Gets the control unit
     * @param factoryControl
    * @return 
    */
   public boolean setControlUnit(Controller factoryControl)
   {
       if (null == factoryControl)
       {
           System.out.println("No control unit given.\n");
           return false;
       }
       else
       {
           controlUnit = factoryControl;
           return true;
       }       
   }
}
