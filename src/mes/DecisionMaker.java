/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;



import net.wimpi.modbus.util.BitVector;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Utilizador
 */
public class DecisionMaker {
    
    public int ID;
    
    
    public boolean makeDecision(Modbus protocolToPLC, Factory virtualFactory)
    {
        // Creating Block in MES
            
            //Creating Block Object with type P8 and position 0.0 and destination 1.2
            Block testBlock = new Block("P8","1.2","Teste");
            testBlock.setPosition("0.0");
        
            // Adding the block to the virtual factory
            virtualFactory.addBlock(testBlock);
        
            
        // Creating the block in the factory
        
        
            // creating a bit vector of size 8
            BitVector setBlock = new BitVector(8);
            
            // resets the memory
            System.out.println(setBlock);
            
            System.out.println(protocolToPLC.writeModbus(144, setBlock));
            
            
            // Needs to wait 2 seconds before sending the byte with block type
            try{
                TimeUnit.SECONDS.sleep(2);
            }
            catch(Exception Ex)
            {
                System.out.println("error in sleep");
            }
            
            
            
            // Sends the byte with information about block type to add 
            
            setBlock.setBit(1, true);
            
            System.out.println(setBlock);
            
            System.out.println(protocolToPLC.writeModbus(144, setBlock));
          
            
            
            // Bug in reading actuators -> read Modbus
            virtualFactory.readFactory();
            
            
            String test = virtualFactory.getFactoryData();
            
            System.out.println(test);
            
            
            
            
            
            
            
            /*
        //TO DO
        while(!testBlock.isDestination())
        {
           String newPosition = virtualFactory.getNewPosition(testBlock);
           testBlock.setPosition(newPosition);
           System.out.println(testBlock.getPosition());
        }
            */
        
        return true;
    }
   
    
}
