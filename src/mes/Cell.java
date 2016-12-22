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
    
    public Producer(Cell cellObject)
    {
        // if no transport object was given
        if (null == cellObject)
        {
            System.out.println("No cell object given.\n");
            System.exit(-1);
        }
        else
            cellUnit = cellObject;
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
                else if(blockToTransport.updateStatus("producing"));
                {
                    //TO DO escrever em máquina
                }   
            }
        }         
    }

    
    
}


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
     * Constructor
     * @param cellType 
     * @param currentFactory
     * @throws mes.graph.exception.InvalidConstructionException
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
    
    
    
    public void setID(String idToSet)
    {
        ID = idToSet;
    }
    
}


// TESTE TESTE ESTE
