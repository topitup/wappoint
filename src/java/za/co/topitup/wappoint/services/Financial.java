package za.co.topitup.wappoint.services;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import za.co.topitup.wappoint.server.Resource;
import za.co.topitup.wappoint.server.MyException;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Connection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import javax.ws.rs.DefaultValue;
import static org.apache.logging.log4j.core.util.NameUtil.md5;
import za.co.topitup.wappoint.server.Base64;


@Path("financial")
public class Financial extends Resource {
    
    private static final String DATE_FORMAT_D = "%1$td/%1$tm/%1$tY";
    private static final String DATE_FORMAT_T = "%1$tH:%1$tM:%1$tS";
    
    public Financial() {
        
    }
    
    private String randomString( int len ) 
    {
       Random rnd = new Random();
       String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
       StringBuilder sb = new StringBuilder( len );
       for( int i = 0; i < len; i++ ) 
          sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
       return sb.toString();
    }
  
    @GET
    @Path("get-random")
    @Produces({"application/json"})
    public String get_random(@QueryParam("customer_id") int customer_id) {
        
        
        final String url="https://portal.nedsecure.co.za/";
       // String password="?gSF#R1Qmw";
        String password="Wappoint123*";
        try {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
                md5.update(password.getBytes());
        byte[] md5Pass = md5.digest();
        

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String formattedDate = dateFormat.format(new Date()); //formatted in UTC/GMT time
        byte[] urlDateBytes = (url + formattedDate).getBytes();
        byte[] tokenBytes = new byte[md5Pass.length + urlDateBytes.length];
        System.arraycopy(urlDateBytes, 0, tokenBytes, 0, urlDateBytes.length);
        System.arraycopy(md5Pass, 0, tokenBytes, urlDateBytes.length, md5Pass.length);
             MessageDigest sha256 = null;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //  Toasty.success(getApplicationContext(), "token="+tokenBytes, 25000).show();
        sha256.update(tokenBytes);
        byte[] tokenHash = sha256.digest();
        String token = Base64.encodeToString(tokenHash, Base64.NO_WRAP);
        
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
   

        
        
        
        
        
       // customer_id=1110;
        String result = "";
         String sql = "select date_format(r.capture_dtm,'%Y-%m-%d, %H:%i') as capture_dtm, concat(r.fname,' ',r.lname) as customername, \n" +
                        "	concat(pu.posuser_firstname,' ', pu.posuser_surname) as posuser,\n" +
                        "    r.register_number,\n" +
                        "	sp.service_provider_desc\n" +
                        " from rica r\n" +
                        " inner join service_provider sp on sp.service_provider_id = r.service_provider_item_id\n" +
                        " inner join posuser pu on pu.posuser_id = r.posuser_id\n" +
                        " where r.customer_id = ?\n";

            try (Connection con = getConnection(); 
                PreparedStatement ps = con.prepareStatement(sql);) {
                
                ps.setInt(1, customer_id);
         //  System.out.print(sql);     
            try (ResultSet rs = ps.executeQuery();) {
                while(rs.next()) {
                    result += rs.getString("capture_dtm") + "^";
                    result += rs.getString("posuser") + "^";
                    result += rs.getString("register_number") + "^";
                    result += rs.getString("service_provider_desc") + "^";
                    result += rs.getString("customername") + "\n";
                }
            }
        } catch (SQLException e) {
            throw new MyException("could not get rica list",e,showDebug);
         }        

        return result;
    }
   
}
