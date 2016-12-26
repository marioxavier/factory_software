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
            
    /**
     * Constructor
     * @param currentFactory 
     */
    public Monitor(Factory currentFactory)
    {        
        // if some protocol was given
        if (null == currentFactory)
        {
            System.out.println("No factory was given.\n");
            System.exit(-1);
        }
        // all input arguments are OK
        else
        {
            // initializes variables
            virtualFactory = currentFactory;
            protocolToPLC = new Modbus();
            
            // inicializes Monitor protocol with PLC
            if (protocolToPLC.setModbusConnection())
            {
                System.out.println("Modbus Monitor connection on.\n");
                // opens the modbus connection
                if(protocolToPLC.openConnection())
                {
                    System.out.println("Connection with factory for reading opened.\n");
                }
            }

            inputData = null;
            outputData = null;
        } 
    }
    
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
            try
            {
                // reads sensors from the factory
                if (!this.readSensors())
                {
                    System.out.println("MONITOR_THREAD:: Error reading sensors.\n");
                    System.exit(-1);
                }
                // reads actuators from the factory
                if (!this.readActuators())
                {
                    System.out.println("MONITOR_THREAD:: Error reading actuators.\n");
                    System.exit(-1);
                }
                
                // updates conveyors
                virtualFactory.updateConveyors(inputData + outputData);
                
                virtualFactory.updateFactoryData(inputData + outputData);
                
                /*
                // updates block position
                if (!this.virtualFactory.updateBlockPositions(inputData + outputData))
                {
                    System.out.println("MONITOR_THREAD:: Error updating block position.\n");
                    System.exit(-1);
                }
                        */      
                // checks if factory is ready to receive a new block
                this.virtualFactory.isReady(inputData + outputData);    
                
                
                
            }
            catch(Exception s)
            {
               //TO DO
            }
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
        //System.out.println("DEBUG:: Entro no readSensors.\n");
        
        // dataReceived contains a string with the value of all sensors, separated by a space on each byte
        String dataReceived = protocolToPLC.readModbus(0, 146);
        
        // if no data was received
        if (null == dataReceived)
        {
            System.out.println("No data was received from PLC.\n");
            System.exit(-1);
            return false;
        }
        // if data was received    
        else
        {
            
            // splits the received array of bytes 
            String[] invertedByteArray = dataReceived.split(" ");
        
            // the bits come in the opposite order, so we need to sort them
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
            for (String toolByte : invertedByteArray)
                dataReceived = dataReceived + toolByte;
        
            // inputData contains an array in which each position has the value 
            //of the corresponding sensor on the PLC
            this.inputData = dataReceived;
            
            
            // if some error ocurred
            if (null == this.inputData)
            {
                System.out.println("No data was stored.\n");
                System.exit(-1);
                return false;
            }
            
            //System.out.println("DEBUG:: Retorno true em readSensors.\n");
            
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
            //System.out.println("DEBUG:: Entro no readActuactors.\n");
            
            // dataReceived contains a string with the value of all actuators,
            //separated by a space on each byte
            String dataReceived = protocolToPLC.readModbus(160, 200);

            // if no data was received
            if (null == dataReceived)
            {
                System.out.println("No data was received from PLC.\n");
                System.exit(-1);
                return false;
            }
            
            // if data was received
            else
            {
                // dataReceived is a string array 
                //in which each position has a byte
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
                for (String toolByte : invertedByteArray)
                    dataReceived = dataReceived + toolByte;
        
                // outputData contains an array in which each position has 
                //the value of the corresponding actuator on the PLC
                this.outputData = dataReceived;

                // if some error ocurred
                if (null == this.outputData)
                {
                    System.out.println("No data was stored.\n");
                    System.exit(-1);
                    return false;
                }
                
                //System.out.println("DEBUG:: Retorno true em readActuators.\n");
                
                // if no error ocurred
                return true;
            }
        }
}
