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
    private boolean switchFlag;
    
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
    
    /*
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
                return "0.5";
            default:
                return "0.2";
        }        
    }
*/
    
    public String decideDestination(Block blockToDecide)
    {
        switch("P" + blockToDecide.getType() + "P" + blockToDecide.getFinalType())
        {
            case "P1P7":
                if (switchFlag)
                {
                    switchFlag = false;
                    return "0.2";
                }                    
                else
                {
                    switchFlag = true;
                    return "0.7";
                }
                    
            case "P1P2":
                if (switchFlag)
                {
                    switchFlag = false;
                    return "0.2";
                }                    
                else
                {
                    switchFlag = true;
                    return "0.7";
                }
            case "P2P3":
                if (switchFlag)
                {
                    switchFlag = false;
                    return "0.2";
                }                    
                else
                {
                    switchFlag = true;
                    return "0.7";
                }
            case "P3P4":
               if (switchFlag)
                {
                    switchFlag = false;
                    return "0.2";
                }                    
                else
                {
                    switchFlag = true;
                    return "0.7";
                }
            case "P3P6":
                if (switchFlag)
                {
                    switchFlag = false;
                    return "0.2";
                }                    
                else
                {
                    switchFlag = true;
                    return "0.7";
                }
            case "P4P6":
                if (switchFlag)
                {
                    switchFlag = false;
                    return "0.2";
                }                    
                else
                {
                    switchFlag = true;
                    return "0.7";
                }
            case "P4P7":
                if (switchFlag)
                {
                    switchFlag = false;
                    return "0.2";
                }                    
                else
                {
                    switchFlag = true;
                    return "0.7";
                }
            case "P5P7":
               if (switchFlag)
                {
                    switchFlag = false;
                    return "0.2";
                }                    
                else
                {
                    switchFlag = true;
                    return "0.7";
                }
            case "P6P7":
                if (switchFlag)
                {
                    switchFlag = false;
                    return "0.2";
                }                    
                else
                {
                    switchFlag = true;
                    return "0.7";
                }
            case "P1P6":
                if (switchFlag)
                {
                    switchFlag = false;
                    return "0.2";
                }                    
                else
                {
                    switchFlag = true;
                    return "0.7";
                }
            case "P3P7":
                if (switchFlag)
                {
                    switchFlag = false;
                    return "0.2";
                }                    
                else
                {
                    switchFlag = true;
                    return "0.7";
                }                           
            default:
                if (switchFlag)
                {
                    switchFlag = false;
                    return "0.5";
                }                    
                else
                {
                    switchFlag = true;
                    return "0.10";
                }
        }        
    }
    
    public String decideTransformation(Block blockToTransform)
    {
        return "P1P2 C1";
    }
    
}
