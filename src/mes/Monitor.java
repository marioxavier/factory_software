/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

/**
 *
 * @author Utilizador
 */
public class Monitor extends Thread
{
    private String inputData;
    private String outputData;
    private Modbus protocolToPLC;
    private Factory virtualFactory;
    // variable used to stop the thread
    private volatile boolean killThread;
    
    // method called to stop Thread
    public void stopThread()
    {
        killThread = true;
    }
            
    // method to run in a Thread
    @Override
    public void run()
    {
        while (!killThread)
        {
            this.readSensors();
            this.readActuators();
            virtualFactory.updateConveyorStatus(inputData + outputData);
            virtualFactory.updateBlockStatus(inputData + outputData);
            virtualFactory.updateMachineStatus(inputData + outputData);
            
            
        }   
    }
    
    
    /**
     * 
     * @param protocol
     * @param currentFactory 
     */
    public Monitor(Modbus protocol, Factory currentFactory)
    {
        // if protocolModbus is empty
        if (null == protocol)
        {
            System.out.println("No protocol was given.\n");
            System.exit(-1);
        }
        // if some protocol was given
        else if (null == currentFactory)
        {
            System.out.println("No factory was given.\n");
            System.exit(-1);
        }
        // all input arguments are OK
        else
        {
            // initializes variables
            virtualFactory = currentFactory;
            protocolToPLC = protocol;
            inputData = null;
            outputData = null;
        }
        
    }
    
    /**
     * 
     * @return 
     */
    public Modbus getProtocol()
    {
        return protocolToPLC;
    }
    
    
    /**
     * gets inputData
     * @return 
     */
    public String getInputData()
    {
        return inputData;
    }
    
    /**
     * Gets outputData 
     * @return 
     */
    public String getOutputData()
    {
        return outputData;
    }
    
    /**
     * Updates inputData with value from PLC sensors
     * @return 
     */
    public boolean readSensors()
    {        
        // dataReceived contains a string with the value of all sensors, separated by a space on each byte
        String dataReceived = protocolToPLC.readModbus(0,146);
        
        // if no data was received
        if (null == dataReceived)
        {
            System.out.println("No data was received from PLC.\n");
            return false;
        }
            
        else
        {
            // invertedByteArray is an array of bytes
            String[] invertedByteArray = dataReceived.split(" ");
        
            // cycle to invert the order of each byte
            int i = 0;
            do
            {
                invertedByteArray[i] = new StringBuilder(invertedByteArray[i]).
                        reverse().toString();
                i++;
            }while(i < 19);
        
            // emptying dataReceived
            dataReceived = "";
        
            // placing a string on dataReceived with the data in the intended format
            for (String tool : invertedByteArray)
                dataReceived = dataReceived + tool;
        
            // inputData contains an array in which each position has the value 
            //of the corresponding sensor on the PLC
            inputData = dataReceived;
            
            // if some error ocurred
            if(null == inputData)
            {
                System.out.println("No data was stored.\n");
                return false;
            }
        
            // if no error ocurred
            return true;
        }  
    }

    /**
     * Updates outputData with value from PLC actuators
     * @return 
     */
    public boolean readActuators()       
        {
            // dataReceived contains a string with the value of all actuators, separated by a space on each byte
            String dataReceived = protocolToPLC.readModbus(160,200);

            // if no data was received
            if (null == dataReceived)
            {
                System.out.println("No data was received from PLC.\n");
                return false;
            }
            
            // if data was received
            else
            {
                // s is a string array in which each position has a byte
                String[] invertedByteArray = dataReceived.split(" ");
        
                // cycle to invert the order of each byte
                int i = 0;
                do
                {
                    invertedByteArray[i] = new StringBuilder(invertedByteArray[i]).
                        reverse().toString();
                    i++;
                }while(i < 25);
        
                // emptying dataReceived
                dataReceived = "";
        
                // placing a string on dataReceived with the data in the intended format
                for (String tool : invertedByteArray)
                    dataReceived = dataReceived + tool;
        
                // outputData contains an array in which each position has 
                //the value of the corresponding actuator on the PLC
                outputData = dataReceived;

                // if some error ocurred
                if (null == outputData)
                {
                    System.out.println("No data was stored.\n");
                    return false;
                }
                // if no error ocurred
                return true;
            }
            
        }
}
