/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.BASE64EncoderStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author sohaib
 */
public class Client {

     private static Cipher ecipher;
     private static SecretKeySpec key;
     static DataInputStream in;
     static DataOutputStream out;
     public static void main(String [] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
     {
          
          Socket client = new Socket("localhost",1998);
           in = new DataInputStream(client.getInputStream());
           out = new DataOutputStream(client.getOutputStream());
          Scanner input = new Scanner(System.in);
          System.out.println("Inter your Username :");
          String name = input.nextLine();
          System.out.println("Inter a Password :");
          String password = input.nextLine();
          String key1 = "";
          if(readkey(Integer.toString(name.hashCode())).equals("No such as this UserName"))
          {
          while(readkey(Integer.toString(name.hashCode())).equals("No such as this UserName"))
               {         
               System.out.println("Wrong UserName , Try a again :");
               System.out.println("Inter your Username :");
               name = input.nextLine();
               System.out.println("Inter a Password :");
               password = input.nextLine();         
               }
                    key1 = readkey(Integer.toString(name.hashCode()));
          }
          else
               key1 = readkey(Integer.toString(name.hashCode()));
          
          key = new SecretKeySpec(key1.getBytes(), "DES");
          ecipher = Cipher.getInstance("DES");
          ecipher.init(Cipher.ENCRYPT_MODE, key);
          out.writeUTF(name);
          out.writeUTF(encrypt(password));
          String result = in.readUTF();
          System.out.println(result);
          
          
          if(result.equals("Successfull sign in."))
          {
               dofinal();
          }
          else
          {
               while(!result.equals("Successfull sign in."))
               {
              System.out.println("try again ...");
              System.out.println("Inter your Username :");
              name = input.nextLine();
              System.out.println("Inter a Password :");
              password = input.nextLine();
              out.writeUTF(name);
              out.writeUTF(encrypt(password));
              result = in.readUTF();
              System.out.println(result);
               }
               dofinal();
          }
             

         
     }
     public static void dofinal() throws IOException
     {
          Scanner input = new Scanner(System.in);
          String message;
          System.out.println("Inter A message for Server :");
               message = input.nextLine();
               out.writeUTF(encrypt(message));
               System.out.println(in.readUTF());
               String file = input.nextLine();
               out.writeUTF(file);
               System.out.println(in.readUTF());
     }
      public static String encrypt(String str) {
          try { 
          // encode the string into a sequence of bytes using the named charset 
           // storing the result into a new byte array.  
          byte[] enc = ecipher.doFinal(str.getBytes("UTF8")); 
          // encode to base64 
          enc = BASE64EncoderStream.encode(enc);
               return new String(enc);
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
          return "No such as this UserName";
      }

}
