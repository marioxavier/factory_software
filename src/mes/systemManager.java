/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import java.sql.SQLException;
import java.util.*;
import javax.swing.JOptionPane;
import net.wimpi.modbus.util.BitVector;


/**
 * Classe para gerir o arranque e funcionamento do sistema
 * @author Mário Xavier
 */
public class systemManager 
{    
    private String ID;
    private PriorityQueue taskQueue;
    private int status;
    
     public static void main (String[] args) throws SQLException
     {     
        // creates Modbus protocol object 
        Modbus protocolToPLC = new Modbus();
        // creates UDP protocol object
        //UDP protocolToERP = new UDP();
        // creates a factory monitor
        Monitor factoryMonitor = new Monitor(protocolToPLC);
        // creates a virtual factory
        Factory virtualFactory = new Factory(factoryMonitor);
        // creates a decision unit
        DecisionMaker decisionUnit = new DecisionMaker(protocolToPLC, 
                virtualFactory);
        // creates a database object
        Database db = new Database();
        
              
        // setting the Modbus Connection   
        if (protocolToPLC.setModbusConnection())
            System.out.println("Modbus connection on.\n");
        else
            System.out.println("Modbus connection failed.\n");
        
        protocolToPLC.openConnection();
        
       // runs Monitor thread
       factoryMonitor.start();
       
       // runs Database thread
       db.start();
       
 
       decisionUnit.makeDecision();
        
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
     
     public boolean addToQueue(ProductionOrder newOrder)
     {
         //TO DO
         return true;
     }
     
     public boolean removeFromQueue()
     {
         //TO DO
         return true;
     }  
     
     public ProductionOrder convertToOrder(String receivedOrder)
     {
         ProductionOrder newOrder = new ProductionOrder();
         //TO DO
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
  