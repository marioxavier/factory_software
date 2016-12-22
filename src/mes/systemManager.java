/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import java.sql.SQLException;
import java.util.*;
import mes.graph.exception.InvalidConstructionException;


/**
 * Classe para gerir o arranque e funcionamento do sistema
 * @author Mário Xavier
 */
public class systemManager 
{    
    private String ID;
    public LinkedList<ProductionOrder> orderQueue;
    private int status;
    public Database systemDatabase;
    
    public systemManager(Database DB)
    {
        if (null == DB)
            System.exit(-1);
        else
            systemDatabase = DB;
    }
    
    public static void main (String[] args) throws SQLException, InvalidConstructionException
    { 
       // creates new instance of database 
       Database systemDatabase = new Database();
       // initializes database
       if(systemDatabase.initDatabase("org.postgresql.Driver", 
                "jdbc:postgresql://dbm.fe.up.pt/sinf15g44"))
       {
            // sets database credentials
            if(systemDatabase.setCredentials("sinf45g44", "123"))
            {
                // opens a connection with the database
                if(systemDatabase.openConnection())
                {
                    // executes a query
                    //db.executeQuery("CREATE TABLE mes.TEST_FOUR();");
                }
            }
       }
       
        // creates new instance of system manager
        systemManager manager = new systemManager(systemDatabase);
     
        // creates new protocol to PLC
        Modbus protocolToPLC = new Modbus();
        // sets the Modbus connection   
        if (protocolToPLC.setModbusConnection())
        {
            System.out.println("Modbus connection on.\n");
            // opens the modbus connection
            if(protocolToPLC.openConnection())
            {
                System.out.println("Connection with factory opened.\n");
                
                /*It only makes sense to keep running if modbus conection is done*/
                
                // creates an instance of the factory
                Factory virtualFactory = new Factory(protocolToPLC, manager);
                // starts the factory thread
                virtualFactory.start();                   
            }
            else
                System.out.println("Connection with factory not opened.\n");
        }
        else
        {
            System.out.println("Modbus connection failed.\n");
            System.exit(-1);
        }
        
        Controller controlUnit = new Controller(protocolToPLC);
     }
       
        
       /* 
        // creates UDP protocol object
        UDP protocolToERP = new UDP(manager);
        // initializes UDP
        protocolToERP.initUDP();
        // starts UDP thread
        protocolToERP.start();
         
        // creates a decision unit
        //DecisionMaker decisionUnit = new DecisionMaker(protocolToPLC, virtualFactory    
    }
     
     /**
      * Adds a new order to the order queue
      * @param newOrder
      * @return 
      */
     public boolean addToQueue(ProductionOrder newOrder)
     {
        return orderQueue.add(newOrder);
     }
     
     /**
      * Removes a order from the order queue
      * @param orderToRemove
      * @return 
      */
     public boolean removeFromQueue(ProductionOrder orderToRemove)
     {
         return orderQueue.remove(orderToRemove);
     }  
     
      /**
      * Converts received order from UDP to ProductionOrder
      * @param receivedOrder
      * @return 
      */
     public ProductionOrder convertToOrder(String receivedOrder)
     {
         ProductionOrder newOrder = new ProductionOrder(receivedOrder);
         return newOrder;
     }
     
     public boolean printQueue(LinkedList<ProductionOrder> orderQueue)
     {
             while(orderQueue.descendingIterator().hasNext())
                System.out.println(orderQueue.descendingIterator().next().finalType);
             return true;
     }
}












        /*
        if(virtualFactory.addConveyors("transport", "linear", 2))
            System.out.println("ok");
        else
            System.out.println("erro");
            */

        /*
        Conveyor Conveyor1, Conveyor2;
        
        Conveyor1 = new Conveyor("transport","linear","04");
        Conveyor2 = new Conveyor("transport","slide","010");

        Conveyor[] conveyorArray = new Conveyor[2]; 
        conveyorArray[0]=Conveyor1;
        conveyorArray[1]=Conveyor2;
        
        factoryMonitor.updateTransportConveyors(conveyorArray);

        */

        // ************  HOW TO PLACE A BLOCK IN FACTORY ****************
        
        /*
        // creating a bit vector of size 8
        BitVector b = new BitVector(8);
        
        //b.setBit(3, true);
        
        // setting all bits to 1

        
        // prints the bit vector
        System.out.println(b.toString());
        
        // prints the result of the function writeModbus (Write Multiple Coils) 
        System.out.println(protocolToPLC.writeModbus(144,b));
        */
        

        // checks if factory is ready
        //if((simulatedFactory.isReady));
  

// TESTE 17 DEZEMBRO
