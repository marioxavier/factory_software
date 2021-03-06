/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import java.net.DatagramSocket;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extends Protocol class 
 * @author Mário Xavier
 */
public class UDP extends Thread
{
    private DatagramSocket serverSocket;
    private byte[] receivedData ;
    systemManager manager;
    
    /**
     * Constructor
     * @param systemManager
     */
    public UDP(systemManager systemManager)
    {
        // no manager was given
        if (null == systemManager)
        {
            System.out.println("No manager was given.\n");
            System.exit(-1);
        }

        else
        {
            manager = systemManager;
        }
            
    }
    
    @Override
    public void run()
    {
        while(true)
        {
            // creates new packet to receive data
            DatagramPacket receivePacket 
                    = new DatagramPacket(receivedData, receivedData.length);
            try 
            {
                // waits for and receives a packet from port 54321
                serverSocket.receive(receivePacket);
            } 
            catch (IOException ex) 
            {       
                Logger.getLogger(UDP.class.getName()).log(Level.SEVERE, null, ex);
            }
          
            manager.addToQueue(manager.convertToOrder(new String(receivePacket.getData())));
            manager.printQueue(manager.orderQueue);
            //manager.systemDatabase.executeQuery(query);
            
            // resets the received data
            receivedData = new byte[1024]; 
            
            // DEBUG - prints the sentence
            //System.out.println("DEBUG:: RECEIVED: " + sentence);
        }
    }
    
    /**
     * Initializes UDP protocol 
     * @return 
     */
    public boolean initUDP()
    {
        try
        {
            // creates new datagram socket (Port: 54321)  
            serverSocket = new DatagramSocket(54321);
            // creates array of bytes (receiveData)
            receivedData = new byte[1024]; 
            return true;
        }
        catch(Exception e)
        {
            System.out.println("Error in UDP Protocol " + e);
            return false;
        }
    }
}
