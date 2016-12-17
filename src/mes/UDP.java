/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import java.net.DatagramSocket;

import java.io.*;
import java.net.*;
import net.wimpi.modbus.*;
import net.wimpi.modbus.msg.*;
import net.wimpi.modbus.io.*;
import net.wimpi.modbus.net.*;
import net.wimpi.modbus.util.*;
import java.util.*;
import javax.swing.JOptionPane;

/**
 * Extends Protocol class 
 * @author Mário Xavier
 */
public class UDP 
{
    
    private DatagramSocket serverSocket;

    private String type;
    
    private int port;
    
    
    public UDP() 
    {
        
        
        // starts UDP protocol
        if(type == "UDP")
        {
            try
            {
                // creates new datagram socket (Port: 54321)  
                DatagramSocket serverSocket = new DatagramSocket(port);
                // creates array of bytes (receiveData)
                byte[] receiveData = new byte[1024]; 
       
                while(true)
                {
            
                    // creates new packet to receive data
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    // receives a packet from port 54321
                    serverSocket.receive(receivePacket);
                    // retrieves the sentence from the packet
                    String sentence = new String( receivePacket.getData());
                    // prints the sentence
                    System.out.println("RECEIVED: " + sentence);
                }
            }
            catch(Exception e)
            {
                System.out.println("Error in UDP Protocol");
            }
        }
    }
    
}
