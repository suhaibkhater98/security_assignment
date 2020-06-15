/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author sohaib
 */
public class Server {

     /**
      * @param args the command line arguments
      */
     
     public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
          ServerSocket server = new ServerSocket(1998);
          while (true)  
         { 
            Socket client = null; 
              
            try 
            { 
                client = server.accept();                 
               // System.out.println("A new client is connected : " + client);                   
                DataInputStream in = new DataInputStream(client.getInputStream()); 
                DataOutputStream out = new DataOutputStream(client.getOutputStream());                   
                System.out.println("Assigning new thread for new client");    
                Thread t = new Thread(new ClientHandler(client,in,out));
                t.start(); 
                  
            } 
            catch (Exception e){ 
                client.close(); 
                e.printStackTrace(); 
            } 
        } 
     
      
     }

}
