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
    Modbus protocolToPLC;
    Factory virtualFactory;
    
    /**
     * 
     * @param protocol
     * @param currentFactory 
     */
    public DecisionMaker(Modbus protocol, Factory currentFactory)
    {
        if(null == protocol)
        {
            System.out.println("No protocol was given.\n");
            System.exit(-1);
        }
        else if (null == currentFactory)
        {
            System.out.println("No factory was given.\n");
            System.exit(-1);
        }
        else 
        {
            protocolToPLC = protocol;
            virtualFactory = currentFactory;
        }
    }
    
    /**
     * 
     * @return 
     */
    public boolean makeDecision()
    {
        // Creating Block in MES
            
            //Creating Block Object with type P8 and position 0.0 and destination 0.5 and ID "Teste"
            Block testBlock = new Block("P8", "0.2", "Teste");
        
            // Adding the block to the virtual factory
            if(!virtualFactory.addBlock(testBlock))
            {
                System.out.println("No block was created\n");
                System.exit(-1);
            }
                  
            // creating a bit vector of size 16 to set orders to "enter cell" 
            // and "keep going" orders to zero
            BitVector settingEverythingToZero = new BitVector(16);
                
            System.out.println(settingEverythingToZero);
            
            protocolToPLC.writeModbus(0, settingEverythingToZero);
            
            
            
            // Needs to wait before sending consecutive packets to PLC
            try
            {
                TimeUnit.SECONDS.sleep(2);
            }
            catch(Exception Ex)
            {
                System.out.println("error in sleep");
            }
            

            // Setting "Blocks to add in factory" to zero
            
            BitVector setBlock = new BitVector(8);
            
            System.out.println(protocolToPLC.writeModbus(144, setBlock));
            
            
            // Needs to wait before sending consecutive packets to PLC
            try
            {
                TimeUnit.SECONDS.sleep(2);
            }
            catch(Exception Ex)
            {
                System.out.println("error in sleep");
            }
            
            
            
            // Creating the block in the factory
        
            // creating a bit vector of size 8

            // putting a P8 block in factory
            setBlock.setBit(3, true);
            
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
            

        boolean permissao = true;    
       
        //TO DO
        while(!testBlock.isDestination())
        {  
           String newPosition = virtualFactory.getNewPosition(testBlock);
           testBlock.setPosition(newPosition);
           System.out.println(testBlock.getPosition());
           
           if (testBlock.getPosition().equals("0.2") && permissao == true)
           {
               permissao = false;
               System.out.println("Digo para entrar na C1");
               
               BitVector keepGoingC1 = new BitVector(8);
               keepGoingC1.setBit(0, true);
               
               
               BitVector enterC1 = new BitVector(8);
               enterC1.setBit(0, true);
               
               
               protocolToPLC.writeModbus(0, enterC1);
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
