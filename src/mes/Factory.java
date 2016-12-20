package mes;

import java.util.*;
import java.util.concurrent.TimeUnit;
import mes.graph.*;
import mes.graph.exception.InvalidConstructionException;
import net.wimpi.modbus.util.BitVector;


/**
 *
 * @author Mário Xavier
 */
public class Factory extends Thread 
{
    public Integer ID;
    private boolean status;
    private Graph<Conveyor> cellConveyors, transportConveyors;
    private Hashtable<String, Conveyor> conveyorsTable;
    private Transport inputTransport, outputTransport;
    private Machine[] machines;
    private Cell[] parallelCells, serialCells;
    @SuppressWarnings("UseOfObsoleteCollectionType")
    private Hashtable<String, Block> blocksInFactory;
    private Hashtable<String, String> memoryMap;
    private Hashtable<String, BitVector> blockVector;
    private int numberOfConveyors;
    private int activeSensors;
    private int numberOfBlocks;
    private String factoryData;
    private Monitor factoryMonitor;
    private systemManager systemManager;
    private Modbus protocolToPLC;
    private String[] transportMemoryIndexes;
    private Hashtable<String, String> memoryMap;
    private Controller controlUnit;
    private volatile boolean killThread;
    private boolean firstConveyorReady;
  
   
    /**
     * Thread to run
     */
    @Override
    public void run()
    {
        while(!killThread)
        {
            if(firstConveyorReady)
            {
                try
                {
                    ProductionOrder nextOrder = this.systemManager.orderQueue.pollLast();
                    
                    this.addBlock(nextOrder.originalType,nextOrder.finalType,
                            "0.15", "1", nextOrder.blockOperation);
                }
                catch(Exception s)
                {
                }                
            }
        }
        
        /*
        ler fabrica
        atualizar a posiçao de todos os blocos
        
        atualizar estado de tapetes
        
        atualizar estado de maquinas
        */
    }
    
    /**
     * 
     */
    public void stopThread()
    {
        killThread = true;
    }
    
    /**
     * 
     * @param modbusProtocol
     * @param manager 
     */
    public Factory(Modbus modbusProtocol, systemManager manager)
    {
        // if no modbus protocol was given
        if (null == modbusProtocol)
        {
            System.out.println("No modbus protocol was given.\n");
            System.exit(-1);
        }
        // if no manager was given
        else if (null == manager)
        {
            System.out.println("No manager was given.\n");
            System.exit(-1);
        }
        // if all parameters are OK
        else
        {
            protocolToPLC = modbusProtocol;
            systemManager = manager;
            // creates an instance of a monitor
            factoryMonitor = new Monitor(modbusProtocol, this);
            // starts the factory monitor thread
            factoryMonitor.start();
        } 
    }
    
    /**
     * Gets factory data
     * @return 
     */
    public String getFactoryData()
    {
        return factoryData;
    }
    
    /**
     * Initizes factory
     * @return 
     * @throws mes.graph.exception.InvalidConstructionException 
     */
    @SuppressWarnings("UseOfObsoleteCollectionType")
    public boolean initFactory() throws InvalidConstructionException
    {   
        // initial factory status
        status = false;
        // initalizes variable that kills the thread as false
        killThread = false;
        // resets the number of conveyors
        numberOfConveyors = 0;
        // resets the number of active conveyors
        activeSensors = 0;
        // initializes number of Blocks
        numberOfBlocks = 0;

        // asks the factory to get in the init status
        // TO DO
        
        // creates Hashtable to store all the incoming blocks 
        blocksInFactory = new Hashtable<>();
        // creates graph to store all the transport conveyors
        transportConveyors = new Graph<>();
        // creates graph to store all cell Conveyors
        cellConveyors = new Graph<>();
        // creates conveyors tables
        conveyorsTable = new Hashtable<>();

         
        // creates Cell array containing all parallel cells
        parallelCells = new Cell[2];
        // creates Cell array containing all serial cells
        serialCells = new Cell[2];         
        // creates the machine array containing all the machines
        machines = new Machine[8];
            
        // creates the array containing the memory index of sensors/actuators
        this.mapObjectsToMemory();
        this.createBlockTypeMap();
        // starts reading factory;
        this.startFactoryMonitor();
         
        // resets the first conveyor
        BitVector b = new BitVector(8);
        protocolToPLC.writeModbus(144, b);
        // resets the status of the first conveyor
        firstConveyorReady = false;
         
         // concerning each transport conveyor
         this.generateTransportMemoryIndexes();
         
        // adds a transport unit to the factory
        if(this.addTransport(this))
        {
            // adds parallel cell units to the factory
            if(this.addCells("parallel", 2, this))
                if (this.addCells("serial", 2, this))
                {
                    status = true;
                    return true;
                }
                else
                {
                    System.out.println("Error creating serial cell.\n");
                    return false;
                } 
            else
            {
                System.out.println("Error creating parallel cell.\n");
                return false;   
            } 
        }
        else
        {
            System.out.println("Error creating transport unit.\n");
            return false;
        }
    }
   
    
    
    /**
     * Gets factory status
     * @param factoryData
     */
    public void isReady(String factoryData)
    {
        //System.out.println("DEBUG:: Entro no isReady (Factory).\n");
        
        // if factory data was not given
        if (null == factoryData)
        {
            System.out.println("No factory data was given.\n");
            System.exit(-1);
        }
        // if factory data was given        
        else
        {
            String[] factoryDataArray = factoryData.split(",");        
            // if the conveyor 0 is full returns false
            this.firstConveyorReady = !factoryDataArray[0].equals("1"); 
        }        
    }

    /**
     * Gets all the conveyors of a given type
     * @param conveyorType
     * @return 
     */
    public Graph getConveyors(String conveyorType)
    {
        // if no conveyor type was given
        if (null == conveyorType)
            return null;
        
        else
        {
            switch (conveyorType) {
                case "transport":
                    return transportConveyors;
                    
                case "cell":
                    return cellConveyors;
                    
                // if some error occured
                default:
                    return null;
            }
        }
    }
    
    /**
     * Gets a specific conveyor
     * @param conveyors
     * @param conveyorIndex
     * @return 
     */
    public Conveyor getConveyor(Conveyor[] conveyors, int conveyorIndex)
    {
        // if no conveyor array was given
        if (null == conveyors)
            return null;
        // if a conveyor array was given
        else
        {
            // if is a valid conveyor ID
            if (conveyorIndex > 0)
                return conveyors[conveyorIndex];
            // if some error occured
            else
                return null;
        }
    }
    
    /**
     * Gets machines
     * @return 
     */
    public Machine[] getMachines()
    {
        return machines;
    }
    
    /**
     * Gets a specific conveyor
     * @param machines
     * @param machineIndex
     * @return 
     */
    public Machine getMachine(Machine[] machines, int machineIndex)
    {
        // if no machine array was given
        if (null == machines)
            return null;
        // if a machine array was given
        else
        {
            // if is a valid machine ID
            if (machineIndex > 0)
                return machines[machineIndex];
            // if some error occured
            else
                return null;
        }
    }
 
    /**
     * Gets cells of a given type
     * @param cellType
     * @return 
     */
    public Cell[] getCells(String cellType)
    {
        if (null == cellType)
            return null;
        else
        {
            switch (cellType) {
                case "parallel":
                    return parallelCells;
                case "serial":
                    return serialCells;
                    // if some error occured
                default:
                    return null;
            }
        }
    }
    
    /**
     * Gets a specific conveyor
     * @param cells
     * @param cellIndex
     * @return 
     */
    public Cell getCell(Cell[] cells, int cellIndex)
    {
        // if no cell array was given
        if (null == cells)
            return null;
        // if a cell array was given
        else
        {
            // if is a valid cell ID
            if (cellIndex > 0)
                return cells[cellIndex];
            // if some error occured
            else
                return null;
        }
    }
  
/**
 * 
 * @param conveyorType
 * @param numberOfConveyors
 * @return
 * @throws InvalidConstructionException 
 */
    public boolean addTransportConveyors(String conveyorType,
            int numberOfConveyors) throws InvalidConstructionException
    {
        // no conveyor type was given
        if (null == conveyorType)
        {
            System.out.println("No conveyor type given\n");
            return false;
        }
        
        // the number of conveyors is zero
        else if (numberOfConveyors == 0)
        {
            System.out.println("The number of conveyors is zero.\n");
            return false;
        }
        
        
        // if the input arguments are OK
        else
        {
                    String ID ="";
                    // creates conveyors
                    
                    for(int i = 0; i < numberOfConveyors; i++)
                    {
                         // adds the cell entrance conveyor
                        if (i == 2 || i == 5 || i == 7 || i == 10)
                        {
                            //this.transportConveyors.addVertex(new Conveyor(conveyorGroup, "rotator"));
                            ID = "0."+Integer.toString(i);
                            Conveyor rotatingConveyor = new Conveyor("transport", "rotator");
                            rotatingConveyor.setID(ID);
                            this.conveyorsTable.put(ID, rotatingConveyor);
                            
                        }
                            

                        else
                        {
                            //this.transportConveyors.addVertex(new Conveyor(conveyorGroup, conveyorType));
                            ID = "0."+Integer.toString(i);
                            Conveyor linearConveyor = new Conveyor("transport", conveyorType);
                            linearConveyor.setID(ID);
                            this.conveyorsTable.put(ID, linearConveyor);
                        }
                            
                    }

        }
        return true;
    }
                
    
    public boolean addCellConveyors(String conveyorType, String cellID, int numberOfConveyors )
    {
        // no conveyor type was given
            if(null == conveyorType)
            {
                System.out.println("No conveyor type given\n");
                return false;
            }
            
            // no cell id was given
            else if (null == cellID)
            {
                System.out.println("No cell ID given\n");
                return false;
            }
            
            else
            {
                switch(conveyorType)
                {
                    case "linear":
                    {
                        for (int i=0; i < numberOfConveyors; i++)
                        {
                            Conveyor linearConveyor = new Conveyor("cell", conveyorType);
                            
                            // atributing ID conveyor
                            switch(cellID)
                            {
                                case "1":
                                {
                                    // if its first cell and first linear conveyor
                                    if (i==0)
                                    {
                                        linearConveyor.setID("2.2");
                                        this.conveyorsTable.put("2.2", linearConveyor);
                                    }
                                    
                                    // if its first cell and second linear conveyor
                                    else
                                    {
                                        linearConveyor.setID("2.3");
                                        this.conveyorsTable.put("2.3", linearConveyor);
                                    }
                                break;
                                }
                                case "2":
                                {
                                    // if its second cell and first linear conveyor
                                    if(i==0)
                                    {
                                        linearConveyor.setID("1.5");
                                        this.conveyorsTable.put("1.5", linearConveyor);
                                    }
                                        
                                    // if its second cell and second linear conveyor
                                    else if (i==1)
                                    {
                                        linearConveyor.setID("2.5");
                                        this.conveyorsTable.put("2.5", linearConveyor);
                                    }
                                        
                                    // if its second cell and third linear conveyor
                                    else
                                    {
                                        linearConveyor.setID("3.5");
                                        this.conveyorsTable.put("3.5", linearConveyor);
                                    }
                                break;
                                        
                                }
                                case "3":
                                {
                                    // if its third cell and first linear conveyor
                                    if (i==0)
                                    {
                                        linearConveyor.setID("2.7");
                                        this.conveyorsTable.put("2.7", linearConveyor);
                                    }

                                    // if its third cell and second linear conveyor
                                    else
                                    {
                                        linearConveyor.setID("2.8");
                                        this.conveyorsTable.put("2.8", linearConveyor);
                                    }
                                break;
                                }
                                case "4":
                                {
                                    // if its fourth cell and first linear conveyor
                                    if(i==0)
                                    {
                                        linearConveyor.setID("1.10");
                                        this.conveyorsTable.put("1.10", linearConveyor);
                                    }
                                        
                                    
                                    // if its fourth cell and second linear conveyor
                                    else if (i==1)
                                    {
                                        linearConveyor.setID("2.10");
                                        this.conveyorsTable.put("2.10", linearConveyor);
                                    }
                                        
                                    
                                    // if its fourth cell and third linear conveyor
                                    else
                                    {
                                        linearConveyor.setID("3.10");
                                        this.conveyorsTable.put("3.10", linearConveyor);
                                    }
                                break;
                                        
                                }
                                default:
                                    System.out.println("Wrong Cell ID");
                                    System.exit(-1);
                            }
                            
                        }
                    break;
                    }
                        
                    case "slide":
                    {
                        for(int i = 0; i < numberOfConveyors; i++)
                        {
                        
                        Conveyor slideConveyor = new Conveyor("cell", conveyorType);
                        
                        // attributing conveyor id
                        switch (cellID)
                        {
                            case "1":
                            {
                                if (i==0)
                                {
                                    slideConveyor.setID("1.2");
                                    this.conveyorsTable.put("1.2", slideConveyor);
                                }
                                    
                                else
                                {
                                    slideConveyor.setID("3.2");
                                    this.conveyorsTable.put("3.2", slideConveyor);
                                }
                            break;
                            }
                            case "3":
                            {
                                if (i==0)
                                {
                                    slideConveyor.setID("1.7");
                                    this.conveyorsTable.put("1.7", slideConveyor);
                                }
                                    
                                else
                                {
                                    slideConveyor.setID("3.7");
                                    this.conveyorsTable.put("3.7", slideConveyor);
                                }
                            break;
                            }
                            default:
                            {
                                System.out.println("Wrong Cell ID");
                                System.exit(-1);
                            }
                        }
                        }
                        
                        break;
                    }
                
                }
            }    

            return true;
    }
    
    /**
     * Adds machines of a given type
     * @param machineType
     * @param numberOfMachines 
     * @return
     */
    public boolean addMachines(String machineType, int numberOfMachines) throws InvalidConstructionException
    {        
        // no machine type was given
        if (null == machineType)
        {
            System.out.println("No machine type given\n");
            return false;
        }
        
        // the number of conveyors is zero
        else if (numberOfMachines == 0)
        {
            System.out.println("The number of machines if zero.\n");
            return false;
        }
        
        // if a number of conveyors was given
        else
        switch(machineType)
        {
            // creates type A machines
            case "A":
                for(int i = 0; i < numberOfMachines; i++)
                    machines[i] = new Machine(machineType);
                break;
            // creates type B machines    
            case "B":

                for(int i = 0; i < numberOfMachines; i++)
                    machines[i] = new Machine(machineType);
                break;
            // creates type C machines
            case "C":
                for(int i = 0; i < numberOfMachines; i++)
                    machines[i] = new Machine(machineType);
                break;
            // if machine type is not recognized
            default:
                System.out.println("Machine type not recognized.\n");
                return false;
        }  
        return true;
    }
    
    /**
     * Adds cells of a given type
     * @param cellType
     * @param numberOfCells
     * @param currentFactory
     * @return 
     * @throws mes.graph.exception.InvalidConstructionException 
     */
    public boolean addCells(String cellType, int numberOfCells, Factory
            currentFactory) throws InvalidConstructionException
    {  
        // no cell type was given
        if (null == cellType)
        {
            System.out.println("No cell type given\n");
            return false;
        }
        
        // the number of cells is zero
        else if (numberOfCells == 0)
        {
            System.out.println("The number of cells is zero.\n");
            return false;
        }
        
        // if all parameters are OK
        else
            switch(cellType)
            {
                case "parallel":
                    // creates parallel cells
                     for(int i = 0; i < numberOfCells; i++)
                     {
                         // if we are adding first parallel Cell, ID is 1
                         if (i==0)
                         {
                             parallelCells[i] = new Cell(cellType, currentFactory, factoryMonitor.getProtocol(), "1");
                         }
                            //parallelCells[i].setID("1");
                         
                         // if we are adding second parallel Cell, ID is 3
                         else  
                         {
                              parallelCells[i] = new Cell(cellType, currentFactory, factoryMonitor.getProtocol(), "3");   
                         }
                             //parallelCells[i].setID("3");
                         
                         
                     }
                    break;

                case "serial":
                    // creates serial cells
                    for(int i = 0; i < numberOfCells; i++)
                    {
                        // if we are adding first serial Cell ID is 2
                        if (i==0)
                        {
                            serialCells[i] = new Cell(cellType, currentFactory, factoryMonitor.getProtocol(), "2");
                        }
                            //serialCells[i].setID("2");
                        
                        // if we are adding second serial Cell ID is 4
                        else
                        {
                            serialCells[i] = new Cell(cellType, currentFactory, factoryMonitor.getProtocol(),"4");
                        }
                            //serialCells[i].setID("4");
                        
                    }
                        
                    break; 

                default:
                    System.out.println("Cell type not recognized.\n");
                    return false;
            }  
        
        return true;
    }
    
    /**
     * Adds transport to factory
     * @param currentFactory
     * @return 
     * @throws mes.graph.exception.InvalidConstructionException 
     */
    public boolean addTransport(Factory currentFactory) throws InvalidConstructionException
    {
        // checks if the current factory is null
        if (null == currentFactory)
        {
            System.out.println("No factory given\n");
            return false;
        }
        // if a factory was given
        else
        {
            // creates input transport
            inputTransport = new Transport("input", currentFactory, factoryMonitor.getProtocol());
            
            // error creating input Transport unit
            if (null == inputTransport)
            {
                System.out.println("Error creating input transport.\n");
                return false;                    
            }
            else
            {
                // creates output transport
                
                // ***************************** May create an ERROR ******************************
                outputTransport = new Transport("output", currentFactory, factoryMonitor.getProtocol());
            }
             
            // error creating output Transport unit
            if (null == outputTransport)
            {
                System.out.println("Error creating output transport.\n");
                return false; 
            }
            else
                return true;
        }
    }
   
    /**
     * Updates the number of conveyors using + to increment and - to decrement
     * @param updateOperation
     * @return 
     */
    public boolean updateNumberOfBlocks(String updateOperation)
    {
        // if no update operation was given
        if (null == updateOperation)
        {
            System.out.println("No update operation was given.\n");
            return false;
        }
        // if a update operation was given
        else
            switch(updateOperation)
            {
                case "+": 
                    // increments number of conveyors
                    numberOfBlocks += 1;
                    return true;
                    
                case "-":
                    // decrements number of conveyors
                    numberOfBlocks -= 1;
                    return true;
                    
                default:
                    System.out.println("Update operation not recognized.\n");
                    return false;
            }
    }
    
    /**
     * Gets the number of conveyors
     * @return 
     */
    public int getNumberOfConveyors()
    {
        return numberOfConveyors;
    }
    
    /**
     * Updates active sensors
     * @param conveyors
     * @return 
     */
    public boolean updateActiveSensors(Conveyor[] conveyors)
    {
        // if no array of conveyors was given
        if (null == conveyors)
        {
            System.out.println("No array of conveyors was given");
            return false;
        }
        // if an array of conveyors was given
        else
        {
            // counts all active conveyors
            for (Conveyor conveyor : conveyors) 
            {
                if ("active".equals(conveyor.getStatus()))
                    activeSensors += 1; 
            }
            return true;
        }
    }
    
    /**
     * Reads factory sensors and actuators
     * @return 
     */
    public boolean startFactoryMonitor()
    {
       factoryMonitor.start();
       return true;
    }
    

/*
    public boolean generateTransportConveyorID()
    {
        
        
        
        String conveyorID;
        
        for (int i = 0; i < transportConveyorsTable.size(); i++)
        {
            conveyorID = "0.";
            conveyorID += Integer.toString(i);
            //TO DO
        }
        
        // Mudar return
        return true;
        
    }

 */
    
    public String getNewPosition(Block blockToUpdate)
    {
        
        String newPosition;
        
        // gets a block with given ID
        Block blockInFactory = blocksInFactory.get(blockToUpdate.ID);
        
        // checks the position the block was in
        String pastBlockPosition = blockInFactory.getPosition();
        
        // stores the conveyor index that the block was in
        int pastConveyor = Integer.parseInt(pastBlockPosition.split("\\.")[1]);
        
        // stores conveyor index of the conveyor in front of the block
        int nextConveyor = pastConveyor + 1;
  
        char[] factoryDataArray = factoryData.toCharArray();
        
        // gets the memory indexes of the conveyor the block was in
        String[] memoryOfPastConveyor = transportMemoryIndexes[pastConveyor].split(",");
        
        // gets the memory indexes of the conveyor in front of the block
        String[] memoryOfNextConveyor = transportMemoryIndexes[nextConveyor].split(",");

        // stores the value of both sensors
        char pastConveyorSensor = factoryDataArray[Integer.parseInt(memoryOfPastConveyor[0])];
        char nextConveyorSensor = factoryDataArray[Integer.parseInt(memoryOfNextConveyor[0])];
        
        // if block changed position
        if (Character.getNumericValue(pastConveyorSensor)==0 && Character.getNumericValue(nextConveyorSensor)==1)
        {
            newPosition = "0."+Integer.toString(nextConveyor);
        }
        
        // if block didn't change position
        else
        {
           newPosition = pastBlockPosition;
        }
      
        return newPosition;
        
        
        /**
        // loops the block array
        for (Block pastBlock : blocksInFactory)
        {
            // if the block exists in the array
            if (pastBlock.ID.equals(blockToUpdate.ID))
            {
                
                String pastBlockPosition = pastBlock.getPosition();
                
                
                int pastConveyor = Integer.parseInt(pastBlockPosition.split("\\.")[1]);
                int nextConveyor = pastConveyor + 1;
                
                // reads the factory and stores it in factoryDataArray
                readFactory();
                char[] factoryDataArray = factoryData.toCharArray();
                
                String[] memoryOfPastConveyor = 
                        transportMemoryIndexes[pastConveyor].split(",");
                
                String[] memoryOfNextConveyor = 
                        transportMemoryIndexes[nextConveyor].split(",");
                
                char pastConveyorSensor = 
                        factoryDataArray[Integer.parseInt(memoryOfPastConveyor[0])];
                char nextConveyorSensor = 
                        factoryDataArray[Integer.parseInt(memoryOfNextConveyor[0])];
                

                System.out.println(pastConveyorSensor);
                System.out.println(nextConveyorSensor);
                
                // if block changed position
                if (Character.getNumericValue(pastConveyorSensor)==0 && 
                        Character.getNumericValue(nextConveyorSensor)==1)
                {
                    newPosition = "0."+Integer.toString(nextConveyor);
                }
                // if block didn't change position
                else
                {
                    newPosition = pastBlockPosition;
                    return newPosition;    
                }
            }
            // if the given does not exist
            else
            {
                System.out.println("Couldn't find given block in factory.\n");
                return null;
            }
        }
        * */
    }

    /**
     * 
     * @return 
     */
    public boolean addBlock(String blockType, String finalBlockType, String blockDestination, String blockID, String operation)
    {
            System.out.println("entrou no addBlock");

            //System.out.println(blockVector.get(newBlock.getType()));

            BitVector b = new BitVector(8);
            protocolToPLC.writeModbus(144, b);

            // Needs to wait before sending consecutive packets to PLC
            try
            {
                TimeUnit.SECONDS.sleep(2);
            }
            catch(Exception Ex)
            {
                System.out.println("error in sleep");
            }
            
            b.setBit(0,true);
            protocolToPLC.writeModbus(144, b);
            
            // Needs to wait before sending consecutive packets to PLC
            try
            {
                TimeUnit.SECONDS.sleep(2);
            }
            catch(Exception Ex)
            {
                System.out.println("error in sleep");
            }
            
            b.setBit(0, false);
            protocolToPLC.writeModbus(144, b);
            
            // Needs to wait before sending consecutive packets to PLC
            try
            {
                TimeUnit.SECONDS.sleep(2);
            }
            catch(Exception Ex)
            {
                System.out.println("error in sleep");
            }
        
        Block newBlock = new Block(blockType, finalBlockType, blockDestination, blockID, operation);
        

        
        // if no block was given
        if (null == newBlock)
        {
            System.out.println("No block given to add.\n");
            return false;
        }
       
        else if (null == newBlock.ID)
        {
            System.out.println("No ID given to this block.\n");
            return false;
        }
        
        // if all parameters are 
        else
        {
            // adds a block to the hashtable
            this.blocksInFactory.put(newBlock.ID, newBlock);
            updateNumberOfBlocks("+");
            return true;
        }
    }
    
    /**
     * removes a block from the virtual Factory
     * @param deleteBlock
     * @return 
     */
    public boolean removeBlock(Block deleteBlock)
    {
        // if no block was given
        if(null == deleteBlock)
        {
            System.out.println("block to remove not given");
            return false;
        }
        else
            {
            // error removing Block from the Hashtable
            if(null == blocksInFactory.remove(deleteBlock.ID))
            {
                System.out.println("cannot remove block because it does not exist in the factory");
                return false;
            }
            
            else
                return updateNumberOfBlocks("-");
            }
        
    }
    
    /**
     * Maps the memory of transport conveyor
     */
    public void generateTransportMemoryIndexes()
    {
        // initializes the string array
        transportMemoryIndexes = new String[16];
        
        // fills the array with the respective values
        transportMemoryIndexes[0] = "0,146,147";
        transportMemoryIndexes[1] = "2,151,152";
        transportMemoryIndexes[2] = "3,4,5,153,154,155";
        transportMemoryIndexes[3] = "6,7,157,158";
        transportMemoryIndexes[4] = "32,193,194";
        transportMemoryIndexes[5] = "33,34,35,195,196,197,198";
        transportMemoryIndexes[6] = "53,225,226";
        transportMemoryIndexes[7] = "54,55,56,227,228,229,230";
        transportMemoryIndexes[8] = "57,58,231,232";
        transportMemoryIndexes[9] = "83,267,268";
        transportMemoryIndexes[10] = "84,85,86,269,270,271,272";
        transportMemoryIndexes[11] = "104,299,300";
        transportMemoryIndexes[12] = "105,106,107,301,302,303,304";
        transportMemoryIndexes[13] = "128,322,323";
        transportMemoryIndexes[14] = "129,130,131,324,325,326,327";
        transportMemoryIndexes[15] = "132,328,329";
    }
    
    /**
     * Returns the map of transport conveyor
     * @return 
     */
    public String[] getTransportMemoryIndexes()
    {
        return transportMemoryIndexes;
    }
    
    public Transport getInputTransport()
    {
        return inputTransport;
    }
    
    public Transport getOutputTransport()
    {
        return outputTransport;
    }
    
    public boolean updateConveyorStatus(String factoryData)
    {
        //status = "Ready", "Sending", "receiving"
        
        // percorrer todos os tapetes
        // para cada tapete, ir à hashtable com o get (.hashcode).
        // fazer parse "1,2,7" (split)
        // rotativos (7), lineares, duplos (4) e paralelos (7 posições);
        // ler nessas posições de memória e atualizar as variáveis abaixo
        
 
        // orientation, status
        // TO DO
        return true;
    }
    
    /**
     * Updates block position
     * @param factoryData
     * @return 
     */
    public boolean updateBlockPositions(String factoryData)
    {
        //System.out.println("DEBUG:: Entro no updateBlockPositions.\n");
        
        // if factory data is empty
        if (null == factoryData)
        {
            System.out.println("Factory data is empty.\n");
            System.exit(-1);
            return false;
        }
        // if factory data was given
        else
        {
            // create auxiliar variables;
            String position;
        
            // runs for all blocks in factory
            for (String i : blocksInFactory.keySet())
            {
                // gets the block to update
                Block blockToUpdate = blocksInFactory.get(i);
                // reads the block position 
                position = blockToUpdate.getPosition();
                
                // stores the conveyor index that the block was in
                int pastConveyor = Integer.parseInt(position.split("\\.")[1]);

                // stores conveyor index of the conveyor in front of the block
                int nextConveyor = pastConveyor + 1;
                
                // 
                char[] factoryDataArray = factoryData.toCharArray();
                
                Conveyor presentConveyor = conveyorsTable.get("0."+Integer.toString(pastConveyor));
                Conveyor futureConveyor = conveyorsTable.get("0."+Integer.toString(nextConveyor));
                

                String[] memoryOfPastConveyor = memoryMap.get(presentConveyor.hashCode()).split(",");
                String[] memoryOfNextConveyor = memoryMap.get(futureConveyor.hashCode()).split(",");
                
                
                // stores the value of both sensors
                char pastConveyorSensor = factoryDataArray[Integer.parseInt(memoryOfPastConveyor[0])];
                char nextConveyorSensor = factoryDataArray[Integer.parseInt(memoryOfNextConveyor[0])];
                
                // if block changed position
                if (Character.getNumericValue(pastConveyorSensor)==0 && Character.getNumericValue(nextConveyorSensor)==1)
                {
                    System.out.println("Unreadeble position.\n");
                    System.exit(-1);
                    return false;
                }
                // if position was read
                else
                {
                    //System.out.println("DEBUG:: Leio posição do bloco.\n");
                    // stores the conveyor index that the block was in
                    int currentConveyor = Integer.parseInt(position.split("\\.")[1]);
                    // stores conveyor index of the conveyor in front of the block
                    int nextConveyor = currentConveyor + 1;
                    
                    // retrieves the conveyors from the hashtable
                    Conveyor presentConveyor = transportConveyorsTable.get("0." + Integer.toString(currentConveyor));
                    Conveyor futureConveyor = transportConveyorsTable.get("0." + Integer.toString(nextConveyor));
                
                    // retrieves from the hashtable the memory of conveyors 
                    String[] memoryOfCurrentConveyor = memoryMap.get(presentConveyor.ID).split(",");
                    String[] memoryOfNextConveyor = memoryMap.get(futureConveyor.ID).split(",");
               
                    // the factory data is splitted in chars
                    char[] factoryDataArray = factoryData.toCharArray();
                    // stores the value of both sensors
                    char pastConveyorSensor = factoryDataArray[Integer.parseInt(memoryOfCurrentConveyor[0])];
                    char nextConveyorSensor = factoryDataArray[Integer.parseInt(memoryOfNextConveyor[0])];
                
                    // checks if block changed position
                    if (Character.getNumericValue(pastConveyorSensor) == 0 && Character.getNumericValue(nextConveyorSensor) == 1)
                        blockToUpdate.setPosition(futureConveyor.ID);

                    try
                    {
                        TimeUnit.SECONDS.sleep(2);
                    }
                    catch(Exception Ex)
                    {
                        System.out.println("UpdateBlock:: Error in sleep.\n");
                        return false;
                    }
                }
            }
        //System.out.println("DEBUG:: Retorno true no updateBlockPositions.\n");
        return true;     
        }
    }
    
    /**
     * 
     * @param factoryData
     * @return 
     */
    public boolean updateMachineStatus(String factoryData)
    {
         // percorrer todas as máquinas
        // para cada tapete, ir à hashtable com o get (.hashcode).
        // fazer parse "1,2,7" (split)
        // ler nessas posições de memória e atualizar as variáveis abaixo
        
        
        // status = "Working", "Shifting", "Ready"
        
        //current tool, nextTool (decisor)
        // TO DO
        return true;
    }
    
    
    public void mapObjectsToMemory()
    {
        // percorrer todos os objectos
        
        memoryMap = new Hashtable<>();

        for (String i : conveyorsTable.keySet())
        {
            switch(conveyorsTable.get(i).ID)
            {
                case "0.0":
                    memoryMap.put(conveyorsTable.get(i).ID, "0,146,147");
                    break;
                case "0.1":
                    memoryMap.put(conveyorsTable.get(i).ID, "2,151,152");
                    break;
                case "0.2":
                    memoryMap.put(conveyorsTable.get(i).ID, "3,4,5,153,154,155");
                    break;
                case "0.3":
                    memoryMap.put(conveyorsTable.get(i).ID, "6,7,157,158");
                    break;
                case "0.4":
                    memoryMap.put(conveyorsTable.get(i).ID, "32,193,194");
                    break;
                case "0.5":
                    memoryMap.put(conveyorsTable.get(i).ID, "33,34,35,195,196,197,198");
                    break;
                case "0.6":
                    memoryMap.put(conveyorsTable.get(i).ID, "53,225,226");
                    break;
                case "0.7":
                    memoryMap.put(conveyorsTable.get(i).ID, "54,55,56,227,228,229,230");
                    break;
                case "0.8":
                    memoryMap.put(conveyorsTable.get(i).ID, "57,58,231,232");
                    break;
                case "0.9":
                    memoryMap.put(conveyorsTable.get(i).ID, "83,267,268");
                    break;
                case "0.10":
                    memoryMap.put(conveyorsTable.get(i).ID, "84,85,86,269,270,271,272");
                    break;
                case "0.11":
                    memoryMap.put(conveyorsTable.get(i).ID, "104,299,300");
                    break;
                case "0.12":
                    memoryMap.put(conveyorsTable.get(i).ID, "105,106,107,301,302,303,304");
                    break;
                case "0.13":
                    memoryMap.put(conveyorsTable.get(i).ID, "128,322,323");
                    break;
                case "0.14":
                    memoryMap.put(conveyorsTable.get(i).ID, "3,4,5,153,154,155");
                    break;
                case "0.15":
                    memoryMap.put(conveyorsTable.get(i).ID, "132,328,329");
                    break;
                case "1.2":
                    memoryMap.put(conveyorsTable.get(i).ID, "8,9,10,159,160,161,162");
                    break;
                case "2.2":
                    memoryMap.put(conveyorsTable.get(i).ID, "11,12,13,14,15,16,163,164,165,166,167,168,169,170,171");
                    break;
                case "2.3":
                    memoryMap.put(conveyorsTable.get(i).ID, "17,18,19,20,21,22,172,173,174,175,176,177,178,179,180");
                    break;
                case "3.2":
                    memoryMap.put(conveyorsTable.get(i).ID, "23,24,25,181,182,183,184");
                    break;
                case "1.5":
                    memoryMap.put(conveyorsTable.get(i).ID, "36,37,38,39,40,41,199,200,201,202,203,204,205,206,207");
                    break;
                case "2.5":
                    memoryMap.put(conveyorsTable.get(i).ID, "42,208,209");
                    break;
                case "3.5":
                    memoryMap.put(conveyorsTable.get(i).ID, "43,44,45,46,47,48,210,211,212,213,214,215,216,217,218");
                    break;
                case "1.7":
                    memoryMap.put(conveyorsTable.get(i).ID, "59,60,61,233,234,235,236");
                    break;
                case "2.7":
                    memoryMap.put(conveyorsTable.get(i).ID, "62,63,64,65,66,67,237,238,239,240,241,242,243,244,245");
                    break;
                case "2.8":
                    memoryMap.put(conveyorsTable.get(i).ID, "68,69,70,71,72,73,246,247,248,249,250,251,252,253,254");
                    break;
                case "3.7":
                    memoryMap.put(conveyorsTable.get(i).ID, "74,75,76,255,256,257,258");
                    break;
                case "1.10":
                    memoryMap.put(conveyorsTable.get(i).ID, "87,88,89,90,91,92,273,274,275,276,277,278,279,280,281");
                    break;
                case "2.10":
                    memoryMap.put(conveyorsTable.get(i).ID, "93,282,283");
                    break;
                case "3.10":
                    memoryMap.put(conveyorsTable.get(i).ID, "94,95,96,97,98,99,284,285,286,287,288,289,290,291,292");
                    break;

            }
        }
    }
    
   /**
    * Gets the blocks hashtable
    * @return 
    */ 
   public Hashtable<String, Block> getBlocksInFactory()
   {
       return blocksInFactory;
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
   
   
   /**
     * Creates a hashtable to store the block type 
     */
    public void createBlockTypeMap()
    {
        // creates the hashtable
        blockVector = new Hashtable<>();
        
        // creates bitvector to insert in hastable
        BitVector blockBitVector = new BitVector(8);
        
        // inserting P1 and corresponding BitVector
        blockBitVector.setBit(0, true);
        this.blockVector.put("P1", blockBitVector);

        // inserting P2 and corresponding BitVector
        blockBitVector.setBit(0, false);
        blockBitVector.setBit(1, true);
        blockVector.put("P2", blockBitVector);

        // inserting P3 and corresponding BitVector
        blockBitVector.setBit(0, true);
        blockVector.put("P3", blockBitVector);

        // inserting P4 and corresponding BitVector
        blockBitVector.setBit(0, false);
        blockBitVector.setBit(1,false);
        blockBitVector.setBit(2,true);
        blockVector.put("P4", blockBitVector);

        // inserting P5 and corresponding BitVector
        blockBitVector.setBit(0, true);
        blockVector.put("P5", blockBitVector);

        // inserting P6 and corresponding BitVector
        blockBitVector.setBit(0, false);
        blockBitVector.setBit(1, true);
        blockVector.put("P6", blockBitVector);

        // inserting P7 and corresponding BitVector
        blockBitVector.setBit(0, true);
        blockVector.put("P7", blockBitVector);

        // inserting P8 and corresponding BitVector
        blockBitVector.setBit(0, false);
        blockBitVector.setBit(1,false);
        blockBitVector.setBit(2,false);
        blockBitVector.setBit(3,true);
        blockVector.put("P8", blockBitVector);

        // inserting P9 and corresponding BitVector
        blockBitVector.setBit(0, true);
        blockVector.put("P9", blockBitVector);
    }
   
   
   
   
   
   
}

