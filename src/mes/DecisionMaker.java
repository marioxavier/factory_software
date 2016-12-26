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
     * Constructor
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
    
    public String decideDestination(Block blockToDecide)
    {
        switch(blockToDecide.ID)
        {
            case "000":
                return "0.5";
            case "001":
                return "0.2";
            case "002":
                return "0.7";
            case "003":
                return "0.10";
            case "004":
                return "0.10";
            default:
                return "0.2";
        }        
    }
    
    public String decideTransformation(Block blockToTransform)
    {
        return "P1P2 C1";
    }
    
}
