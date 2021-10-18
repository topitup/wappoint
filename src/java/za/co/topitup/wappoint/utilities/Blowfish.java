package za.co.topitup.wappoint.utilities;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import za.co.topitup.wappoint.server.MyException;

public class Blowfish {

   final static String IVString = "12345678";
   
   private static String bytesToHexString(byte[] bytes){ 
        StringBuilder sb = new StringBuilder(); for(byte b : bytes)
        { sb.append(String.format("%02x", b&0xff)); } 
        return sb.toString(); 
   }    
     
   private static String hexToString(String input, int groupLength)
   {
       try
       {
           StringBuilder sb = new StringBuilder(input.length() / groupLength);
           for (int i = 0; i < input.length() - groupLength + 1; i += groupLength)
           {
               String hex = input.substring(i, i + groupLength);
               sb.append((char) Integer.parseInt(hex, 16));
           }
           return sb.toString();
       } catch (NumberFormatException e) {
           return null;
       }
   }

   private static byte[] getBytes(String toGet)
   {
       try
       {
           byte[] retVal = new byte[toGet.length()];
           for (int i = 0; i < toGet.length(); i++)
           {
               char anychar = toGet.charAt(i);
               retVal[i] = (byte)anychar;
           }
           return retVal;
       }catch(Exception e)
       {
           return null;
       }
   }
     

    public static String decryptBlowfish(String to_decrypt)
    {
        try
        {
            
            byte[] encryptedpin = getBytes(hexToString(to_decrypt, 2));
            
            javax.crypto.spec.IvParameterSpec IV = new javax.crypto.spec.IvParameterSpec(getBytes(IVString));
            SecretKeySpec key = new SecretKeySpec(getBytes("p1zz@p1zz@p1zz@p1zz@p1zz@p1zz@p1"), "Blowfish");

            Cipher cipher = Cipher.getInstance("Blowfish/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, IV);
            
            byte[] decrypted = cipher.doFinal(encryptedpin);
            
            return new String(decrypted);

        } catch (Exception e) {
            throw new MyException("voucher decrypt",e,true);
        }
    }   
        
       
    public static String getNewPin(String pin, String key) {
        String ret = "";
        try{
        
            javax.crypto.spec.IvParameterSpec iv = new javax.crypto.spec.IvParameterSpec(IVString.getBytes("UTF8"));
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF8"), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] cipherText = cipher.doFinal(pin.getBytes("UTF8"));
            ret = bytesToHexString(cipherText);        
        } catch (Exception e) {
            throw new MyException("voucher encrypt",e,true);
        }
        return ret;
    }
    
   
}
