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
    public PriorityQueue<ProductionOrder> orderQueue;
    private int status;
    
     public static void main (String[] args) throws SQLException, InvalidConstructionException
     {  
        systemManager manager = new systemManager();
        // creates Modbus protocol object 
        Modbus protocolToPLC = new Modbus();
        
        // creates UDP protocol object
        UDP protocolToERP = new UDP(manager);
          
        // creates a virtual factory
        Factory virtualFactory = new Factory(protocolToPLC, manager);
        // creates a decision unit
        DecisionMaker decisionUnit = new DecisionMaker(protocolToPLC, virtualFactory);
        // creates a database object
        Database db = new Database();
        
        
        ProductionOrder productionOrder = new ProductionOrder();
        
        
        // TO DO
        // creates UDP protocol object
        //UDP protocolToERP = new UDP(this, productionOrder);
        
        //protocolToERP.init();
        
        // starts protocol to ERP thread
        //protocolToERP.start();
        
        
        
        
        
        
              
        // setting the Modbus Connection   
        if (protocolToPLC.setModbusConnection())
            System.out.println("Modbus connection on.\n");
        else
            System.out.println("Modbus connection failed.\n");
        
        protocolToPLC.openConnection();
        
        
       // runs Monitor thread
       //factoryMonitor.start();
       
       // initializing factory
       virtualFactory.initFactory();
       
       

       
       // starts factory Thread
      // virtualFactory.start();
       
       // runs Database thread
       db.start();
       
  
       

       
       
       /*
            try
            {
                TimeUnit.SECONDS.sleep(2);
            }
            catch(Exception Ex)
            {
                System.out.println("error in sleep");
            }
       */

       /*
       inputTransport.addBlockToControl(block2);
       inputTransport.start();
       
       try
            {
                TimeUnit.SECONDS.sleep(2);
            }
            catch(Exception Ex)
            {
                System.out.println("error in sleep");
            }
       
       inputTransport.addBlockToControl(block3);
       inputTransport.start();

            */
       
       //decisionUnit.makeDecision();
        
       /*
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
                   db.executeQuery("CREATE TABLE mes.TEST_FOUR();");
              }
          }
       }
       
       */ 
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
         ProductionOrder newOrder = new ProductionOrder();
         return newOrder;
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
