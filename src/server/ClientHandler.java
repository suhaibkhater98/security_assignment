/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64DecoderStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author sohaib
 */
public class ClientHandler implements Runnable{
          Socket client ;
          DataInputStream in ;
          DataOutputStream out ;
          Cipher dcipher; 
          SecretKeySpec key;
          static int counter =0;
          int clientnumber ;
     public ClientHandler(Socket client, DataInputStream in, DataOutputStream out) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
               this.client = client;
               this.in = in;
               this.out = out; 
               counter++;
               this.clientnumber = counter;
               
     }

     @Override
     public void run()  
     {
  
               try {
                    System.out.println("Client "+counter + " Welcome to our System..");
                    String name = in.readUTF();
                    String key1 = readkey(Integer.toString(name.hashCode()));
                    key = new SecretKeySpec(key1.getBytes(), "DES");
                    dcipher = Cipher.getInstance("DES");
                    dcipher.init(Cipher.DECRYPT_MODE, key);
                    String password = decrypt(in.readUTF());
                    String result = checkUser(name , password);
                    out.writeUTF(result);
                    String message = "";
                    
                    if(result.equals("Successfull sign in."))
                         {
                              message = in.readUTF();
                              System.out.println("Message befor Decrypted from Client : "+this.clientnumber); 
                              System.out.println(message);
                              System.out.println("Message after Decrypted from Client : "+this.clientnumber);
                              System.out.println(decrypt(message));                         
                              out.writeUTF("Chose a file to inter : A , B , C");
                              String file = in.readUTF();
                              String result1 = checkaccess(file.toUpperCase() , name);
                              out.writeUTF(result1);
                         }
                    else
                         {
                              while(!result.equals("Successfull sign in."))
                              {
                                   name = in.readUTF();
                                   password = decrypt(in.readUTF());
                                   result = checkUser(name , password);
                                   out.writeUTF(result);
                              }
                              message = in.readUTF();
                              System.out.println("Message befor Decrypted :"); 
                              System.out.println(message);
                              System.out.println("Message after Decrypted :");
                              System.out.println(decrypt(message));                         
                              out.writeUTF("Chose a file to inter : A , B , C");
                              String file = in.readUTF();
                              String result1 = checkaccess(file.toUpperCase() , name);
                              out.writeUTF(result1);
                         }
                         
                    System.out.println("Client "+this.clientnumber+" Destroyed ...");
                    
               } catch (IOException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
               } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
               } catch (NoSuchPaddingException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
               } catch (InvalidKeyException ex) {
                    Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
               }
     
     }
     public static String checkUser(String name , String password) throws FileNotFoundException, IOException
     {
          String filename = "users.txt";
          String line = null;
          FileReader reader = new FileReader(filename);
          BufferedReader buffer = new BufferedReader(reader);
          while(buffer.ready())
          {               
               line = buffer.readLine();
               String [] split = line.split("\\s+");
               for(int i=0 ; i<split.length;i++)
               {                   
                    if(split[i].equals(name))
                    {
                         if(split[i+1].equals(password))
                              return "Successfull sign in.";
                    }
               }
          }
          return "Worng Password...";
     }
     public static String checkaccess(String file , String name) throws FileNotFoundException, IOException
     {
          String filename = "accessfile.txt";
          String line = null;
          FileReader reader = new FileReader(filename);
          BufferedReader buffer = new BufferedReader(reader);
          while(buffer.ready())
          {               
               line = buffer.readLine();
               String [] split = line.split(",|:|\\s");
               for(int i=0 ; i<split.length;i++)
               {
                    if(split[i].equals(file))
                    {
                         for(int j=1; j <split.length;j++)
                         {
                              if(split[j].equals(name))
                                   return "You have permision .";
                              else
                                   return "you Can't access the file .";
                         }
                    }
               }
               System.out.println();
          }
          buffer.close();
          reader.close();
          
          return "No Such as this File .";
     }
     public  String decrypt(String str) 
     {           
     try { 
          // decode with base64 to get bytes
          byte[] dec = BASE64DecoderStream.decode(str.getBytes());
          byte[] utf8 = dcipher.doFinal(dec); 
          // create new string based on the specified charset 
          return new String(utf8, "UTF8"); 
               }  
          catch (Exception e) {
          e.printStackTrace();
          } 
          return null; 
     }
     public static String readkey(String name) throws FileNotFoundException, IOException
      {
          String filename = "key.txt";
          String line = null;
          FileReader reader = new FileReader(filename);
          BufferedReader buffer = new BufferedReader(reader);
          while(buffer.ready())
          {               
               line = buffer.readLine();
               String [] split = line.split("\\s+");
               for(int i=0 ; i<split.length;i++)
               {                   
                    if(split[i].equals(name))
                    { 
                              return split[i+1];
                    }
               }
          }
          buffer.close();
          reader.close();
           return null;
      }
}
