/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import mes.graph.exception.InvalidConstructionException;

/**
 *
 * @author Utilizador
 */
public class Cell {
    
    public int ID;
    private String type;
    
    /**
     * Constructor
     * @param cellType 
     * @param currentFactory
     * @throws mes.graph.exception.InvalidConstructionException
     */
    public Cell(String cellType, Factory currentFactory) 
            throws InvalidConstructionException
    {
         if (null == cellType)
         {
            System.out.println("No conveyor type given.\n");
            System.exit(-1);
         }
         else
            switch(cellType)
            {
               case "parallel":
               {
                   type = cellType;
                   // creates cells in the current factory
                   currentFactory.addConveyors("cell", "linear", 2);
                   currentFactory.addConveyors("cell", "slide", 2);
                   currentFactory.addMachines("B", 1);
                   currentFactory.addMachines("C", 1);
                   break;
               }

               case "serial":
               {
                   type = cellType;
                   // creates cells in the current factory
                   currentFactory.addConveyors("cell", "linear", 3);
                   currentFactory.addMachines("A", 1);
                   currentFactory.addMachines("B", 1); 
                   break;
               }

               default:
                   System.out.println("Cell type not recognized.\n");
                   System.exit(-1);
            }
    }
}


// TESTE TESTE ESTE
