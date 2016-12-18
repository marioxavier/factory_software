/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import mes.graph.exception.InvalidConstructionException;
import net.wimpi.modbus.util.BitVector;

/**
 *
 * @author Mário Xavier
 */
public class Transport extends Thread  {
    
    private int ID;
    private String type;
    private String status;
    private Factory virtualFactory;
    private Modbus protocolToPLC;
    
    private Hashtable<String, BitVector> blockVector;
    
    
    /**
     * Constructor that creates his own conveyors
     * @param transportType 
     * @param currentFactory
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
            this.generateHashTable();
            switch(transportType)
            {
                case "input":
                {
                    type = transportType;
                    // creates transport conveyors in the curreny factory
                    currentFactory.addConveyors("transport", "linear", 30);
                    break;
                    
                }
                    

                case "output":
                {
                    type = transportType;
                    //creates transport conveyors in the current factory
                    currentFactory.addConveyors("transport", "linear", 30);
                    break;
                    
                }
                    

                default:
                    System.out.println("Transport type not recognized.\n");
                    System.exit(-1);
            }
    }
    
    
    
    public void generateHashTable()
    {
        // creates the hashtable
        blockVector = new Hashtable<>();
        
        // creates bitvector to insert in hastable
        BitVector iterationVector = new BitVector(8);
        
        // inserting P1 and corresponding BitVector
        iterationVector.setBit(0, true);
        this.blockVector.put("P1", iterationVector);

        // inserting P1 and corresponding BitVector
        iterationVector.setBit(0, false);
        iterationVector.setBit(1, true);
        blockVector.put("P2", iterationVector);

        // inserting P1 and corresponding BitVector
        iterationVector.setBit(0, true);
        blockVector.put("P3", iterationVector);

        // inserting P1 and corresponding BitVector
        iterationVector.setBit(0, false);
        iterationVector.setBit(1,false);
        iterationVector.setBit(2,true);
        blockVector.put("P4", iterationVector);

        // inserting P1 and corresponding BitVector
        iterationVector.setBit(0, true);
        blockVector.put("P5", iterationVector);

        // inserting P1 and corresponding BitVector
        iterationVector.setBit(0, false);
        iterationVector.setBit(1, true);
        blockVector.put("P6", iterationVector);

        // inserting P1 and corresponding BitVector
        iterationVector.setBit(0, true);
        blockVector.put("P7", iterationVector);

        // inserting P1 and corresponding BitVector
        iterationVector.setBit(0, false);
        iterationVector.setBit(1,false);
        iterationVector.setBit(2,false);
        iterationVector.setBit(3,true);
        blockVector.put("P8", iterationVector);

        // inserting P1 and corresponding BitVector
        iterationVector.setBit(0, true);
        blockVector.put("P9", iterationVector);

    }
    
    
    /**
     * 
     * @return 
     */
    public Factory getFactory()
    {
        return virtualFactory;
    }
    
    /**
     * 
     * @return 
     */
    public Modbus getProtocol()
    {
        return protocolToPLC;
    }
    
    
    /**
     * 
     * @param newBlock 
     */
    
    
    public void run(Block newBlock, String destination)
    {
        // if no new block was given
        if (null == newBlock)
        {
            System.out.println("No block was given to start transport.\n");
            System.exit(-1);
        }
        
        // if no destination was given
        else if (null == destination)
        {
            System.out.println("No destination was given");
            System.exit(-1);
        }
        
        // if a block was given sends it to destination
        else
        {
            // Adding the block to the virtual factory
            if(!virtualFactory.addBlock(newBlock))
            {
                System.out.println("No block was created\n");
                System.exit(-1);
            }
            
            
            // Setting "Blocks to add in factory" to zero
            BitVector setBlock = new BitVector(8);
            System.out.println(protocolToPLC.writeModbus(144, setBlock));
            
            
            // Needs to wait before sending consecutive packets to PLC
            try
            {
                TimeUnit.SECONDS.sleep(2);
            }
            catch(Exception Ex)
            {
                System.out.println("error in sleep");
            }
            
            // stores the bitVector to write in order to create this block
            setBlock = blockVector.get(newBlock.getType());
            
            // sends order to PLC to create this Block
            protocolToPLC.writeModbus(144, setBlock);
            
            
            
            // variable to control in which cell entrance the decision is being made
            short entradaCelula=0;
            
            // Bitvector with decision to keep going
            BitVector keepGoingDecision = new BitVector(8);
            
            // control algorithm
            while(!newBlock.isDestination())
            {  
                // decision not to enter in first Cell
                if (newBlock.getPosition().equals("0.2") && !(newBlock.isDestination()) && entradaCelula==0)
                {
                    entradaCelula +=1;
                    keepGoingDecision.setBit(0, true);
                    protocolToPLC.writeModbus(8, keepGoingDecision);
                }
                // decision not to enter in second Cell
                if (newBlock.getPosition().equals("0.5") && !(newBlock.isDestination()) && entradaCelula==1)
                {
                    entradaCelula+=1;
                    keepGoingDecision.setBit(0, false);
                    keepGoingDecision.setBit(1, true);
                    protocolToPLC.writeModbus(8, keepGoingDecision);
                }
                // decision not to enter in third Cell
                if (newBlock.getPosition().equals("0.7") && !(newBlock.isDestination()) && entradaCelula==2)
                {
                    entradaCelula+=1;
                    keepGoingDecision.setBit(1, false);
                    keepGoingDecision.setBit(2, true);
                    protocolToPLC.writeModbus(8, keepGoingDecision);
                }
                
                // decision not to enter in fourth Cell
                if (newBlock.getPosition().equals("0.10") && !(newBlock.isDestination()) && entradaCelula==3)
                {
                    entradaCelula+=1;
                    keepGoingDecision.setBit(2, false);
                    keepGoingDecision.setBit(3, true);
                    protocolToPLC.writeModbus(8, keepGoingDecision);
                }
                
                // decision not to enter in fifth Cell
                if (newBlock.getPosition().equals("0.12") && !(newBlock.isDestination()) && entradaCelula==4)
                {
                    entradaCelula+=1;
                    keepGoingDecision.setBit(3, false);
                    keepGoingDecision.setBit(4, true);
                    protocolToPLC.writeModbus(8, keepGoingDecision);
                }
                
            }
            
            // creates byte array with size 1
            byte[] decisionToBitvector = new byte[1];
            // creates a byte with the information about where to let the block enter
            decisionToBitvector[0] = (byte)(entradaCelula & 0xff);
            // turns byte into bitvector
            BitVector enterDecision = BitVector.createBitVector(decisionToBitvector);
            
            // writes the decision where to enter in PLC
            protocolToPLC.writeModbus(0, enterDecision);
            

        }
    }

    
}
