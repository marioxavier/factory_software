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
            Block testBlock = new Block("P8","0.5","Teste");
        
            // Adding the block to the virtual factory
            virtualFactory.addBlock(testBlock);
        
            
            
            // creating a bit vector of size 16
            BitVector settingAllToZero = new BitVector(16);
                
            System.out.println(settingAllToZero);
            
            protocolToPLC.writeModbus(0, settingAllToZero);
            

            
        // Creating the block in the factory
        
        
            // creating a bit vector of size 8
            BitVector setBlock = new BitVector(8);
            
            // resets the memory
            System.out.println(setBlock);
            
            System.out.println(protocolToPLC.writeModbus(144, setBlock));
            
            
            // Needs to wait 2 seconds before sending the byte with block type
            try
            {
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

            
            
        boolean permissao=true;    
            
        //TO DO
        while(!testBlock.isDestination())
        {
           String newPosition = virtualFactory.getNewPosition(testBlock);
           testBlock.setPosition(newPosition);
           System.out.println(testBlock.getPosition());
           
           if (testBlock.getPosition().equals("0.2") && permissao==true)
           {
               permissao=false;
               System.out.println("Digo para não entrar na C1");
               BitVector keepGoingC1 = new BitVector(8);
               keepGoingC1.setBit(0, true);
               protocolToPLC.writeModbus(8, keepGoingC1);
           }
           
           
        }
        

            // creating a bit vector of size 8
            BitVector cellTwoOrder = new BitVector(8);
            
            // allows entry on C2
            cellTwoOrder.setBit(1, true);
            System.out.println(cellTwoOrder);
            
            protocolToPLC.writeModbus(0, cellTwoOrder);

        return true;
    }
   
    
}
