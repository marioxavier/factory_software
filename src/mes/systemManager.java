/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import mes.graph.exception.InvalidConstructionException;
import net.wimpi.modbus.util.BitVector;


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
         // creates a database object
        Database db = new Database();
        
        // if database is initialized
       if(db.initDatabase("org.postgresql.Driver", 
                "jdbc:postgresql://dbm.fe.up.pt/sinf16g67"))
       {
          // if credentials are right
          if(db.setCredentials("sinf16g67","manueljoaofraga"))
           {
               // if a databased connection is opened
               if(db.openConnection())
               {
                   // executes a query
                   //db.executeQuery("CREATE TABLE mes.TEST_FOUR();");
              }
          }
       }
        // creates an instance of manager
        systemManager manager = new systemManager(db);
        // initializes a linked list to implement the queue
        manager.orderQueue = new LinkedList<>();
        
        Controller controlUnit = new Controller();
        
          // creates UDP protocol object
        UDP protocolToERP = new UDP(manager);
        // initializes UDP
        protocolToERP.initUDP();
        // starts UDP thread
        protocolToERP.start();
        
     }     
        /*
        // creates Modbus protocol object 
        Modbus protocolToPLC = new Modbus(controlUnit);
        
        // setting the Modbus Connection   
        if (protocolToPLC.setModbusConnection())
            System.out.println("Modbus connection on.\n");
        else
            System.out.println("Modbus connection failed.\n");
        
        if(protocolToPLC.openConnection())
            System.out.println("Connection not opened.\n");
        else
            System.out.println("Connection opened.\n");
        
        
        // creates UDP protocol object
        UDP protocolToERP = new UDP(manager);
        // initializes UDP
        protocolToERP.initUDP();
        // starts UDP thread
        protocolToERP.start();
          
        // creates a virtual factory
        Factory virtualFactory = new Factory(protocolToPLC, manager);
        // initializes factory
        virtualFactory.initFactory();
        // starting factory thread
        virtualFactory.start();
        
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
