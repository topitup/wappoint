package za.co.topitup.wappoint.services;

import java.security.SecureRandom;
import za.co.topitup.wappoint.server.MyException;
import za.co.topitup.wappoint.server.Resource;
import za.co.topitup.wappoint.server.Base64;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import javax.ws.rs.Consumes;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringEscapeUtils;
import za.co.topitup.wappoint.server.Settings;

@Path("server")
public class Server {
  
    
    public Server() {
        
    }
    
        
    @GET
    @Path("set_system_paused")
    @Produces({"application/json", "application/xml;"})
    public String set_system_paused(@QueryParam("pause") Boolean pause) 
    {
        
        Settings.set_system_paused(pause);
        
        return "ok";
        
    }

    
   
    
    
    @GET
    @Path("get_system_status")
    @Produces({"application/json", "application/xml;"})
    public String get_system_status() 
    {
        
        //System.out.println("Test: get all status values");
        
        return String.valueOf(Settings.get_system_status());
        
    }
    
    
    
    
    @GET
    @Path("get_ding_balance")
    @Produces({"application/json", "application/xml;"})
    public String get_ding_balance() 
    {
        
        return String.valueOf(Settings.ding_balance);
        
    }
    
    
            
}
