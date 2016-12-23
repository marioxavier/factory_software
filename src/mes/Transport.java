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
    
    public Transporter(Transport transportObject, Block blockToTransport, DecisionMaker decisionObject)
    {
        // if no transport object was given
        if (null == transportObject)
        {
            System.out.println("No transport object given.\n");
            System.exit(-1);
        }
        else if (null == blockToTransport)
        {
            System.out.println("No block to transport was given.\n");
            System.exit(-1);
        }
        else 
        {
            transportUnit = transportObject;
            blockToFollow = blockToTransport;
            decisionUnit = decisionObject;
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
            while(!blockToFollow.isDestination())
            {
               blockToFollow.setDestination(decisionUnit.decideDestination());
               
               switch(blockToFollow.getPosition())
               {
                   case "0.2":
                    transportUnit.controlUnit.updateBuffer();
                       
               }
            }
            // writes in the buffer
            transportUnit.controlUnit.updateBuffer(blockToFollow.getEnterOrder());
                    
            // updates block status
            blockToFollow.updateStatus("waiting");  
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
            controlUnit = currentFactory.getControlUnit();
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
        Runnable transporter = new Transporter(this, blockToTransport, decisionUnit);
        new Thread(transporter).start();       
    }
   
    /*
      // if no new block was given
        if (null == blockToControl)
        {
            System.out.println("No block was given to start transport.\n");
            System.exit(-1);
        }
        
        
        // if a block was given sends it to destination
        else
        {
            // Adding the block to the virtual factory
            if(!virtualFactory.addBlock(blockToControl))
            {
                System.out.println("No block was created\n");
                System.exit(-1);
            }
            
            // setting "Blocks to add in factory" to zero
            BitVector setBlock = new BitVector(8);
            protocolToPLC.writeModbus(144, setBlock);
            
            
            // needs to wait before sending consecutive packets to PLC
            try
            {
                TimeUnit.SECONDS.sleep(2);
            }
            catch(Exception Ex)
            {
                System.out.println("error in sleep.\n");
            }
            
            // stores the bitVector to write in order to create this block
            setBlock = blockVector.get(blockToControl.getType());
            
            // sends order to PLC to create this Block
            protocolToPLC.writeModbus(144, setBlock);
            
            // variable to control in which cell entrance the decision is being made
            short conditionEnterFlag = 0;
            
            // bitvector with decision to keep going
            BitVector keepGoingDecision = new BitVector(8);
    
        
            // creates byte array with size 1
            byte[] decisionToBitvector = new byte[1];
            
            // creates a byte with the information about where to let the block enter
            decisionToBitvector[0] = (byte)(conditionEnterFlag & 0xff);
            
            // turns byte into bitvector
            BitVector enterDecision = BitVector.createBitVector(decisionToBitvector);
            
            // writes the decision where to enter in PLC
            protocolToPLC.writeModbus(0, enterDecision);
    
    */
    
    
/*
    @Override
    public void run()
    {
        
        // if no new block was given
        if (null == blockToControl)
        {
            System.out.println("No block was given to start transport.\n");
            System.exit(-1);
        }
        
        
        // if a block was given sends it to destination
        else
        {
            // Adding the block to the virtual factory
            if(!virtualFactory.addBlock(blockToControl))
            {
                System.out.println("No block was created\n");
                System.exit(-1);
            }
            
            // setting "Blocks to add in factory" to zero
            BitVector setBlock = new BitVector(8);
            protocolToPLC.writeModbus(144, setBlock);
            
            
            // needs to wait before sending consecutive packets to PLC
            try
            {
                TimeUnit.SECONDS.sleep(2);
            }
            catch(Exception Ex)
            {
                System.out.println("error in sleep.\n");
            }
            
            // stores the bitVector to write in order to create this block
            setBlock = blockVector.get(blockToControl.getType());
            
            // sends order to PLC to create this Block
            protocolToPLC.writeModbus(144, setBlock);
            
            // variable to control in which cell entrance the decision is being made
            short conditionEnterFlag = 0;
            
            // bitvector with decision to keep going
            BitVector keepGoingDecision = new BitVector(8);
            
            // control algorithm
            while(!blockToControl.isDestination())
            {  
                
                System.out.println(blockToControl.getPosition());
                
                // decision not to enter in first Cell
                if (blockToControl.getPosition().equals("0.2") && !(blockToControl.isDestination()) && conditionEnterFlag == 0)
                {
                    conditionEnterFlag +=1;
                    keepGoingDecision.setBit(0, true);
                    protocolToPLC.writeModbus(8, keepGoingDecision);

                }
                // decision not to enter in second Cell
                else if (blockToControl.getPosition().equals("0.5") && !(blockToControl.isDestination()) && conditionEnterFlag == 1)
                {
                    conditionEnterFlag+=1;
                    keepGoingDecision.setBit(0, false);
                    keepGoingDecision.setBit(1, true);
                    protocolToPLC.writeModbus(8, keepGoingDecision);
                }
                // decision not to enter in third Cell
                else if (blockToControl.getPosition().equals("0.7") && !(blockToControl.isDestination()) && conditionEnterFlag == 2)
                {
                    conditionEnterFlag += 1;
                    keepGoingDecision.setBit(1, false);
                    keepGoingDecision.setBit(2, true);
                    protocolToPLC.writeModbus(8, keepGoingDecision);
                }
                // decision not to enter in fourth Cell
                else if (blockToControl.getPosition().equals("0.10") && !(blockToControl.isDestination()) && conditionEnterFlag == 3)
                {
                    conditionEnterFlag += 1;
                    keepGoingDecision.setBit(2, false);
                    keepGoingDecision.setBit(3, true);
                    protocolToPLC.writeModbus(8, keepGoingDecision);
                }
                
                // decision not to enter in fifth Cell
                else
                {
                    conditionEnterFlag += 1;
                    keepGoingDecision.setBit(3, false);
                    keepGoingDecision.setBit(4, true);
                    protocolToPLC.writeModbus(8, keepGoingDecision);
                }
            }
            
            // creates byte array with size 1
            byte[] decisionToBitvector = new byte[1];
            
            // creates a byte with the information about where to let the block enter
            decisionToBitvector[0] = (byte)(conditionEnterFlag & 0xff);
            
            // turns byte into bitvector
            BitVector enterDecision = BitVector.createBitVector(decisionToBitvector);
            
            // writes the decision where to enter in PLC
            protocolToPLC.writeModbus(0, enterDecision);
        }
    }
    */
    
    
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
