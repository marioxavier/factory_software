/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import mes.graph.exception.InvalidConstructionException;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;
import mes.graph.exception.InvalidConstructionException;
import net.wimpi.modbus.util.BitVector;


class Producer implements Runnable
{

    Cell cellUnit;
    Block blockToProduce;
    DecisionMaker decisionUnit;
    Factory virtualFactory;
    Controller controlUnit;
    boolean killThread;
    
    String blockPosition;
    String blockOperation;
    String factoryData;
    
    
    public Producer(Cell cellObject, Block newBlock, DecisionMaker decisionObject, Factory currentFactory)
    {
        // if no transport object was given
        if (null == cellObject)
        {
            System.out.println("No cell object given.\n");
            System.exit(-1);
        }
        else
        {
            this.cellUnit = cellObject;
            this.blockToProduce = newBlock;
            this.decisionUnit = decisionObject;
            this.virtualFactory = currentFactory;
            this.controlUnit = currentFactory.getControlUnit();
            controlUnit.initController();
            controlUnit.protocolToPLC = this.cellUnit.getProtocol();
            this.killThread = false;
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
        while(!killThread)
        {
            if ("waiting".equals(blockToProduce.getStatus()))
            {
                //
                blockToProduce.setOperation(decisionUnit.decideTransformation(blockToProduce));
                
                //
                System.out.println(blockToProduce.operation);
                
                //
                controlUnit.updateBuffer(blockToProduce.operation);
                
                //
                blockToProduce.updateStatus("producing");
            }
                
                
            
            /*
            factoryData = virtualFactory.getFactoryData();
            
            switch (cellUnit.ID)
            {
                case "1":
                    blockPosition = factoryData.charAt()
                case "2":
                case "3":
                case "4":
                            
            }
            
            switch(blockPosition)
            {
                case "1.2":
                    System.out.println("escreveu no buffer - "+blockToProduce.operation);
                    controlUnit.updateBuffer(blockToProduce.operation);
                    break;
                case "1.5":
                    System.out.println("escreveu no buffer - "+blockToProduce.operation);
                    controlUnit.updateBuffer(blockToProduce.operation);
                    break;
                case "1.7":
                    System.out.println("escreveu no buffer - "+blockToProduce.operation);
                    controlUnit.updateBuffer(blockToProduce.operation);
                    break;
                case "1.10":
                    System.out.println("escreveu no buffer - "+blockToProduce.operation);
                    controlUnit.updateBuffer(blockToProduce.operation);
                    break;
                case "1.12":
                    System.out.println("escreveu no buffer - "+blockToProduce.operation);
                    controlUnit.updateBuffer(blockToProduce.operation);
                    break;
                case "1.14":
                    System.out.println("escreveu no buffer - "+blockToProduce.operation);
                    controlUnit.updateBuffer(blockToProduce.operation);
                    break;
                default:
                    System.out.println(blockPosition);
                    // Block still getting to first cell conveyor
                    break;
            }
            */
            
            
        }         
    }

}


/*
            // loops all blocks
            for (String i : cellUnit.blocksInFactory.keySet())
            {
                Block blockToTransport = cellUnit.blocksInFactory.get(i);
                // if the block arrived destination
                if("waiting".equals(blockToTransport.getStatus()))
                {
                    blockToTransport.updateStatus("producing");
                    
                    // writes in the buffer
                    cellUnit.controlUnit.updateBuffer(blockToTransport.operation);
                }
                else if("producing".equals(blockToTransport.getStatus()));
                {
                    //TO DO escrever em máquina
                }   
            }
*/




/**
 *
 * @author Utilizador
 */
public class Cell extends Thread{
    
    public String ID;
    private String type;
    private Factory virtualFactory;
    private Modbus protocolToPLC;
    public Hashtable<String, Block> blocksInFactory;
    public Controller controlUnit;
    
    
    
/**
 * 
 * @param cellType
 * @param currentFactory
 * @param protocol
 * @param id
 * @throws InvalidConstructionException 
 */
    public Cell(String cellType, Factory currentFactory, Modbus protocol, String id) 
            throws InvalidConstructionException
    {
         if (null == cellType)
         {
            System.out.println("No conveyor type given.\n");
            System.exit(-1);
         }
         else
            virtualFactory = currentFactory;
            protocolToPLC = protocol;
            blocksInFactory = currentFactory.getBlocksInFactory();
            controlUnit = currentFactory.getControlUnit();
            ID = id;
            switch(cellType)
            {
               case "parallel":
               {
                   type = cellType;
                   // creates cells in the current factory
                   currentFactory.addCellConveyors("linear", ID, 2);
                   currentFactory.addCellConveyors("slide", ID, 2);
                   currentFactory.addMachines("B", 1);
                   currentFactory.addMachines("C", 1);
                   break;
               }

               case "serial":
               {
                   type = cellType;
                   // creates cells in the current factory
                   currentFactory.addCellConveyors("linear", ID, 3);
                   currentFactory.addMachines("A", 1);
                   currentFactory.addMachines("B", 1); 
                   break;
               }

               default:
                   System.out.println("Cell type not recognized.\n");
                   System.exit(-1);
            }
    }
    
    public void startProduction(Block blockToProduce)
    {
        DecisionMaker decisionUnit = new DecisionMaker(protocolToPLC, virtualFactory);
        Runnable producer = new Producer(this, blockToProduce, decisionUnit, virtualFactory);
        new Thread(producer).start();
    }
    
    
    
    public void setID(String idToSet)
    {
        ID = idToSet;
    }
    
    
    public Modbus getProtocol()
    {
        return protocolToPLC;
    }
    
}


// TESTE TESTE ESTE
