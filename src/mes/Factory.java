package mes;

import java.util.*;

import mes.graph.*;
import mes.graph.exception.InvalidConstructionException;


/**
 *
 * @author Mário Xavier
 */
public class Factory extends Thread 
{
    
    public Integer ID;
    private boolean status;
    private Graph<Conveyor> cellConveyors, transportConveyors;
    private Transport inputTransport, outputTransport;
    private Machine[] machines;
    private Cell[] parallelCells, serialCells;
    @SuppressWarnings("UseOfObsoleteCollectionType")
    private Hashtable<String, Block> blocksInFactory;
    private int numberOfConveyors;
    private int activeSensors;
    private int numberOfBlocks;
    private String factoryData;
    private Monitor factoryMonitor;
    private String[] transportMemoryIndexes;
    
    @Override
    public void run()
    {
        System.out.println(this.factoryMonitor.getInputData());    
    }
    
    public Factory(Monitor receivedMonitor)
    {
        factoryMonitor = receivedMonitor;
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
        // initial status
        status = false;
        
        // resets the number of conveyors
        numberOfConveyors = 0;
        
        // resets the number of active conveyors
        activeSensors = 0;

        // asks the factory to get in the init status
        // TO DO
        
        // creates Hashtable to store all the incoming blocks 
        blocksInFactory = new Hashtable<>();
        
        // creates graph to store all the transport conveyors
        transportConveyors = new Graph<>();
         
        // creates graph to store all cell Conveyors
        cellConveyors = new Graph<>();
         
        // creates Cell array containing all parallel cells
        parallelCells = new Cell[2];
         
        // creates Cell array containing all serial cells
        serialCells = new Cell[2];
         
         // creates the machine array containing all the machines
         machines = new Machine[8];
        
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
     * @return 
     */
    public boolean isReady()
    {
        return status;
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
     * Adds conveyors of a given type
     * @param conveyorGroup
     * @param conveyorType
     * @param numberOfConveyors 
     * @return
     * @throws mes.graph.exception.InvalidConstructionException
     */
    public boolean addConveyors(String conveyorGroup, String conveyorType,
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
        
        // no conveyor group given
        else if (null == conveyorGroup)
        {
            System.out.println("No conveyor group given\n");
            return false;
        }
        
        // if the input arguments are OK
        else
            switch(conveyorGroup)
            {
                // creates transport conveyors
                case "transport":
                {
                    // creates conveyors
                    for(int i = 0; i < numberOfConveyors; i++)
                    {
                         // adds the cell entrance conveyor
                        if (i == 2 || i == 5 || i == 7 || i == 10)
                        {
                            this.transportConveyors.addVertex(new Conveyor(conveyorGroup, "rotator"));
                            this.updateNumberOfConveyors("+");
                           
                            
                        }
                        else
                        {
                            this.transportConveyors.addVertex(new Conveyor(conveyorGroup, conveyorType));
                            this.updateNumberOfConveyors("+");
                        }
                    }
                    
                    break;
                }
                

                // creates cell conveyors
                case "cell":
                {
                    // creates conveyors
                    for(int i = 0; i < numberOfConveyors; i++)
                        this.cellConveyors.addVertex(new Conveyor(conveyorGroup,conveyorType));
                    break; 
                }
                
                default:
                {
                    System.out.println("Conveyor type not recognized.\n");
                    return false;
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
                         parallelCells[i] = new Cell(cellType, currentFactory);
                         
                         // DEBUGGING
                         System.out.println("entrou no for");
                         
                     }
                    break;

                case "serial":
                    // creates serial cells
                    for(int i = 0; i < numberOfCells; i++)
                        serialCells[i] = new Cell(cellType, currentFactory);
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
            inputTransport = new Transport("input", currentFactory);
            
            // error creating input Transport unit
            if (null == inputTransport)
            {
                System.out.println("Error creating input transport.\n");
                return false;                    
            }
            else
            {
                // creates output transport
                outputTransport = new Transport("output", currentFactory);
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
    public boolean updateNumberOfConveyors(String updateOperation)
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
                    numberOfConveyors += 1;
                    return true;
                    
                case "-":
                    // decrements number of conveyors
                    numberOfConveyors -= 1;
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
    public boolean readFactory()
    {
        if(factoryMonitor.readSensors())
            if (factoryMonitor.readActuators())
            {
                // updates factory data
                factoryData = factoryMonitor.getInputData() + 
                factoryMonitor.getOutputData();
                return true;
            }
            else
            {
                System.out.println("Error reading actuators.\n");
                return false;
            }
        else
        {
            System.out.println("Error reading sensors.\n");
            return false;
        }
    }
    
    /**
     * Generates an ID in the following format - "0.xx"
     * @return 
     * true - if the ID's were generated
     * false - if the ID's were not generated
     */
    public boolean generateTransportConveyorID()
    {
        // TO DO - tapetes duplos
        String conveyorID;
        
        for (int i = 0; i < transportConveyors.vertexCount; i++)
        {
            conveyorID = "0.";
            conveyorID += Integer.toString(i);
            //TO DO
        }
        
        // Mudar return
        return true;
    }
    
    /**
     * Updates the position of a given Block
     * @param blockToUpdate
     * @return 
     */
    public String getNewPosition(Block blockToUpdate)
    {
        String newPosition;
        // gets a block with given ID
        blocksInFactory.get(blockToUpdate.ID);
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
        return null;
    }

    /**
     * 
     * @param newBlock
     * @return 
     */
    public boolean addBlock(Block newBlock)
    {
        
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
            return true;
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
}

