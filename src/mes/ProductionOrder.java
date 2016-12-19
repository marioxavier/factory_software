/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mes;

import java.util.*;
   
    
/**
 *
 * @author Utilizador
 */
public class ProductionOrder 
{
     private String orderID;
     public String originalType;
     public String finalType;
     private Date orderDate;
     private String orderStatus;
     public String blockOperation;
     private String quantity; 
     
     
     public ProductionOrder(String datagram)
     {
         String[] datagramTokens = datagram.split(":");
         
         this.blockOperation = datagramTokens[0];
         this.orderID = datagramTokens[1];
         this.originalType = datagramTokens[2];
         this.finalType = datagramTokens[3];
         this.quantity = datagramTokens[4];
         
         //this.orderDate = today();
     }
     
     
     public ProductionOrder()
     {
         
     }
     
     
     
     /**
      * Gets order ID
      * @return 
      */
     public String getOrderID()
     {
         return orderID;
     }
     
     /**
      * Gets order date
      * @return 
      */
     public Date getOrderDate()
     {
         return orderDate;
     }
     
     /**
      * 
      * @return 
      */
     public String getOrderStatus()
     {
         return orderStatus;
     }
     
     /**
      * 
      * @return 
      */
     public boolean setOrderStatus()
     {
         return true;
     }
}
