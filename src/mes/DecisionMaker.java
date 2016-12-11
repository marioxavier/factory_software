/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import net.wimpi.modbus.util.BitVector;

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
            Block testBlock = new Block("P8","1.2");
            testBlock.setPosition("0.0");
        
            // Adding the block to the virtual factory
            virtualFactory.addBlock(testBlock);
        
            
        // Creating the block in the factory
        
            // creating a bit vector of size 8
            BitVector setBlock = new BitVector(8);
            setBlock.setBit(3, true);
            // prints the result of the function writeModbus (Write Multiple Coils) 
            System.out.println(protocolToPLC.writeModbus(144, setBlock));
      
        //TO DO
        while(!testBlock.isDestination());
            //testBlock.setPosition(virtualFactory.getNewPosition(testBlock));

        
        return true;
    }
   
    
}
