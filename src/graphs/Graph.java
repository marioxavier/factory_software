/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graphs;

import java.io.FileNotFoundException;
import java.io.IOException;
import mes.Conveyor;


/**
 *
 * @author ee10092
 */


class Neighbor
{
    public int vertexNumber;
    public Neighbor next;
    
    /**
     * Constructor
     * @param numberOfVertex
     * @param neighborVertex 
     */
    public Neighbor(int numberOfVertex, Neighbor neighborVertex)
    {
        this.vertexNumber = numberOfVertex;
        next = neighborVertex;
    }
}

class Vertex
{
    Conveyor conveyorVertex;
    Neighbor adjacencyList;
    
    Vertex(Conveyor newConveyor, Neighbor neighbors)
    {
        this.conveyorVertex = newConveyor;
        this.adjacencyList = neighbors;
    }
}



public class Graph 
{
    Vertex[] adjacencyLists;
    
    /**
     * Constructor
     * @param conveyors
     * @throws FileNotFoundException 
     */
    public Graph (Conveyor[] conveyors) throws FileNotFoundException
    {
        System.out.println("Entrei no graph\n");
        
        // converts conveyors to vertices
        for (int v = 0; v < adjacencyLists.length; v++) {
            System.out.println("Entrei no for que cria vertices\n");
            adjacencyLists[v] = new Vertex(conveyors[v], null);
        }
 
        // read edges
       for (int v = 0; v < conveyors.length; v++) 
       {
           System.out.println("Entrei no for que cria conexões\n");
            // read vertex names and translate to vertex numbers
            int v1 = indexForConveyor(conveyors[v]);
            int v2 = indexForConveyor(conveyors[v+1]);
             
            // add v2 to front of v1's adjacency list 
            adjacencyLists[v1].adjacencyList = new Neighbor(v2, 
                    adjacencyLists[v1].adjacencyList);
        }
    }
        
    /**
     * 
     * @param name
     * @return 
     */
    private int indexForConveyor(Conveyor conveyorToAdd) 
    {
        System.out.println("Entrei no index generator\n");
        for (int v = 0; v < adjacencyLists.length; v++) {
            if (adjacencyLists[v].conveyorVertex.equals(conveyorToAdd)) 
            {
                return v;
            }
        }
        
        return -1;
    } 
    
    /**
     * 
     */
    public void print() 
    {
        System.out.println("Entrei no print\n");
        for (Vertex adjacencyList : adjacencyLists) {
            System.out.print(adjacencyList.conveyorVertex);
            for (Neighbor nbr = adjacencyList.adjacencyList; nbr != null; nbr = nbr.next) {
                System.out.print(" --> " + adjacencyLists[nbr.vertexNumber].
                        conveyorVertex);
            }
            System.out.println("\n");
        }
    }
       
}
