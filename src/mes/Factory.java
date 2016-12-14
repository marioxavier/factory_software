package mes;

import java.util.*;


/**
 *
 * @author Mário Xavier
 */
public class Factory extends Thread {
    
    public int ID;
    private boolean status;
    private Conveyor[] cellConveyors, transportConveyors;
    private Transport inputTransport, outputTransport;
    private Machine[] machines;
    private Cell[] parallelCells, serialCells;
    
    private List<Block> blocksInFactory;
    
    private int numberOfConveyors;
    private int activeSensors;
    private int numberOfBlocks;
    
    // Alteração do Nuno
    private String factoryData;
    private Monitor factoryMonitor;
    
    private String[] transportMemoryIndexes;
    
    
    
    
    public void run()
    {
        System.out.println(this.factoryMonitor.getInputData());
        
        
    }
    
    public Factory(Monitor receivedMonitor)
    {
        factoryMonitor = receivedMonitor;
    }
    
    
    public String getFactoryData()
    {
        return factoryData;
    }
    
    

    public boolean initFactory()
    {
        
        
        //Alteração do Nuno
        this.transportConveyors = new Conveyor[16];
        this.cellConveyors = new Conveyor[14];
        
        this.addConveyors("transport", "linear", 16);
        generateTransportConveyorID();
        generateTransportMemoryIndexes();
        
        this.addTransport(this);
        

        //Initializing Block Array, needs some testing
         blocksInFactory = new ArrayList<Block>();
        
        this.addCells("parallel", 2, this);
        this.addCells("serial", 2, this);
        
        //this.addConveyors()

        status = true;
        return status;
         
         
         


        
        
        
        
   
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
    public Conveyor[] getConveyors(String conveyorType)
    {
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
     */
    public boolean addConveyors(String conveyorGroup, String conveyorType, int numberOfConveyors)
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
        
        // if a number of conveyors was given
        else
        switch(conveyorGroup)
        {
            // creates transport conveyors
            case "transport":
            {
                // creates conveyors
                for(int i = 0; i < transportConveyors.length-1; i++)
                {
                    if (i==2 || i==5 || i==7 || i==10)
                    {
                        transportConveyors[i] = new Conveyor(conveyorGroup, "rotator");
                    }
                    else
                    {
                        transportConveyors[i] = new Conveyor(conveyorGroup, conveyorType);
                    }

                }
                break;
            }
            
            // creates cell conveyors
            case "cell":
            {
                // creates conveyors
                for(int i = 0; i < numberOfConveyors; i++)
                    cellConveyors[i] = new Conveyor(conveyorGroup,conveyorType);
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
    public boolean addMachines(String machineType, int numberOfMachines)
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
     */
    public boolean addCells(String cellType, int numberOfCells, Factory
            currentFactory)
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
        
        // if a number of cells was given
        else
        switch(cellType)
        {
            case "parallel":
                // creates cells
                 for(int i = 0; i < numberOfCells; i++)
                    parallelCells[i] = new Cell(cellType, currentFactory);
                break;
            
            case "serial":
                // creates cells
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
     */
    public boolean addTransport(Factory currentFactory)
    {
        // creates input transport
        inputTransport = new Transport("input", currentFactory);
       
        // creates output transport
        outputTransport = new Transport("output", currentFactory);
        
        
        // Error creating input Transport
        if (null == inputTransport)
        {
            System.out.println("Error creating input transport.\n");
            return false;                    
        }
        
        // Error creating output Transport
        else if (null == outputTransport)
        {
            System.out.println("Error creating output transport.\n");
            return false; 
        }
        
        
        // if no error ocurred
        else
            return true;
    }
    
    
    
    
    
    /**
     * Updates the number of conveyors
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
    
    public boolean updateActiveSensores(Conveyor[] conveyors)
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
            for (Conveyor conveyor : conveyors) 
            {
                if ("active".equals(conveyor.getStatus()))
                    activeSensors += 1; 
            }
            return true;
        }
    }
    
    
    public void readFactory()
    {
       factoryMonitor.readSensors();

       factoryMonitor.readActuators();

       factoryData = factoryMonitor.getInputData()+factoryMonitor.getOutputData();
    }
    
    
    
    /**
     * Falta ter em atenção tapetes duplos
     * @return 
     */
    public boolean generateTransportConveyorID()
    {
        String conveyorID="";
        
        for (int i=0; i<transportConveyors.length-1; i++)
        {
            conveyorID="0.";
            conveyorID += Integer.toString(i);
            transportConveyors[i].setID(conveyorID);
            
        }
        
        // Mudar
        return true;
        
        
        
        /*
        if (null == conveyorGroup)
        {
            System.out.println("no conveyor group given");
        }
        else if (null == conveyorType)
            System.out.println("no conveyor type given");
        else
        {
            if (conveyorGroup.equals("transport"))
            {
                // if the transport conveyor is empty, the first conveyor is "00"
                if (transportConveyors[0]==null)
                    return "0.0";
                
                else
                {
                    
                    //gets the ID of the last conveyor added
                    String transportConveyorID = this.transportConveyors[this.transportConveyors.length - 1].ID;
                    
                    // turns the last part of the ID, corresponding to the column, and adds 1
                    int generatedID = Integer.parseInt(transportConveyorID.split(".")[1])+1;
                    
                    // returns the ID in string format
                    return Integer.toString(generatedID);
                    
                }
               

                
            }
            
            else if (conveyorGroup.equals("cell"))
            {
                // TO DO
                
            }
            
        }

        return "00";
        
        */
    }
    
    
    
    /**
     * updates the position of a given Block
     * @param block
     * @return 
     */
    public String getNewPosition(Block block)
    {
        String newPosition="";
        for (Block pastBlock : blocksInFactory)
        {

            if (pastBlock.ID.equals(block.ID))
            {
                
                String pastBlockPosition = pastBlock.getPosition();
                
                
                int pastConveyor = Integer.parseInt(pastBlockPosition.split("\\.")[1]);
                int nextConveyor = pastConveyor+1;
                
                // reads the factory and stores it in factoryDataArray
                readFactory();
                char[] factoryDataArray = factoryData.toCharArray();
                
                String[] memoryOfPastConveyor = transportMemoryIndexes[pastConveyor].split(",");
                
                String[] memoryOfNextConveyor = transportMemoryIndexes[nextConveyor].split(",");
                
                //System.out.println(Integer.parseInt(memoryOfPastConveyor[0]));
                //System.out.println(Integer.parseInt(memoryOfNextConveyor[0]));
                
                //System.out.println(factoryDataArray[Integer.parseInt(memoryOfPastConveyor[0])]);
                //System.out.println(factoryDataArray[Integer.parseInt(memoryOfNextConveyor[0])]);
                
                
                //int pastConveyorSensor,nextConveyorSensor=0;
                
                char pastConveyorSensor = factoryDataArray[Integer.parseInt(memoryOfPastConveyor[0])];
                char nextConveyorSensor = factoryDataArray[Integer.parseInt(memoryOfNextConveyor[0])];
                

                System.out.println(pastConveyorSensor);
                System.out.println(nextConveyorSensor);
                
                
                if (Character.getNumericValue(pastConveyorSensor)==0 && Character.getNumericValue(nextConveyorSensor)==1)
                {
                    newPosition = "0."+Integer.toString(nextConveyor);
                }
                    

                else
                {
                    newPosition = pastBlockPosition;
                }


            }
            
            else
            {
                System.out.println("couldn't find given block in factory");
                return null;
            }
                
            
        }
        
          return newPosition;      
    }
    

    public boolean addBlock(Block newBlock)
    {
        /*
        if (null == blockType)
        {
            System.out.println("block type not sepcified");
            return false;
        }
        
        else if (null == blockDestination)
        {
            System.out.println("block destination not specified");
            return false;
        }
        */
        
        if (null == newBlock)
        {
            System.out.println("No block given to addBlock");
            return false;
        }
        
        else
        {
        blocksInFactory.add(newBlock);
        return true;
        }

    }
    
    /**
     * generates the array with the memory indexes corresponding to each transport conveyor
     */
    public void generateTransportMemoryIndexes()
    {
        // initializes the string array
        transportMemoryIndexes = new String[16];
        
        // fills the array with the respective values
        transportMemoryIndexes[0]="0,146,147";
        transportMemoryIndexes[1]="2,151,152";
        transportMemoryIndexes[2]="3,4,5,153,154,155";
        transportMemoryIndexes[3]="6,7,157,158";
        transportMemoryIndexes[4]="32,193,194";
        transportMemoryIndexes[5]="33,34,35,195,196,197,198";
        transportMemoryIndexes[6]="53,225,226";
        transportMemoryIndexes[7]="54,55,56,227,228,229,230";
        transportMemoryIndexes[8]="57,58,231,232";
        transportMemoryIndexes[9]="83,267,268";
        transportMemoryIndexes[10]="84,85,86,269,270,271,272";
        transportMemoryIndexes[11]="104,299,300";
        transportMemoryIndexes[12]="105,106,107,301,302,303,304";
        transportMemoryIndexes[13]="128,322,323";
        transportMemoryIndexes[14]="129,130,131,324,325,326,327";
        transportMemoryIndexes[15]="132,328,329";

    }
    
    /**
     * Returns a string array with the memory indexes of all the transport arrays
     * @return 
     */
    public String[] getTransportMemoryIndexes()
    {
        return transportMemoryIndexes;
    }
    
    
    
    
}