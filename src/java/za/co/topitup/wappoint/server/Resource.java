package za.co.topitup.wappoint.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.core.HttpHeaders;

public class Resource {
      
    @javax.ws.rs.core.Context
    protected HttpHeaders headers;
   
    public static final String CONST_CUSTOMER_STATUS2 = "Your account has been suspended, please contact Top it Up on 0860 111 723";
    public static final String CONST_CUSTOMER_STATUS = "Your account has been suspended, please contact Top it Up on 0860 111 723";
    
    protected boolean showDebug = false;
    protected int ignoreLevel = 0;
    protected String licenseCode = "";
    protected String posuserID = "0";
    protected String blowfishHash = "";
    
    protected boolean cash_customer = false;
    protected int enable_airtime = 0;
    protected int enable_unipin = 0;
    protected int customer_status = 0;
    protected int license_id = 0;
    protected int license_type_id = 0;
    protected int customer_id = 0;
    protected String account_number = "";
    protected String company_name = "";
    protected String posuser_name = "";
    protected int posuser_id = 0;
    protected int pricelist_id = 0;
    protected int customer_balanced = 0;
    protected int enable_remote_ext_credit = 0;
    protected int allow_transfer_cash = 0;
    protected int allow_transfer_interstore = 0;
    
    protected int device_type_id = 0;
    
    protected double available_balance = 0d;
    protected double balance = 0d;
    protected double balance_cash = 0d;
    protected double credit_limit = 0d;
    protected double credit_extended = 0d;
    protected double cashms_limit = 0d;
    protected String low_balance = "0";
    protected String low_balance_cash = "0";
    
    protected String ckey = "0";
    protected String appID = "0";
     
    public Resource() {
          
        if (Settings.system_paused) {
            throw new MyException("System Maintenance. Please Wait.");
        }
        
    }
    
    
    
    @PostConstruct
    public void Resource() {
        
  
        
        if (headers.getRequestHeader("debug") != null){
            if (headers.getRequestHeader("debug").get(0).equalsIgnoreCase("true"))
                showDebug = true;
        }
        if (headers.getRequestHeader("ignore") != null){
            ignoreLevel = Integer.parseInt(headers.getRequestHeader("ignore").get(0));
        }
      if (headers.getRequestHeader("license") != null){
        //if(ignoreLevel==4)
         //licenseCode="DEMO99c4-1999-11e9-84ad-001e6779cd30";
          //else
            licenseCode = headers.getRequestHeader("license").get(0);
        }
        if (headers.getRequestHeader("posuser") != null){
            posuserID = headers.getRequestHeader("posuser").get(0);
        }
       
        
         if (headers.getRequestHeader("hash") != null){
            blowfishHash = headers.getRequestHeader("hash").get(0);
        }
        
        if (headers.getRequestHeader("device") != null){
            if (headers.getRequestHeader("device").get(0).equals("pc"))
                device_type_id = 1;
        }
        // System.out.print("test1");
        if (ignoreLevel < 2 ) {
         checkLicense();
          
        }
        else if(ignoreLevel==100){
            
             // fetchRealtimeCust();
        }
        
    }
 
     public int disignore(){
      
      return ignoreLevel;
  }
    public void checkLicense() {
  
        String sql = "";

            if (ignoreLevel == 1) {
                sql += " select ce.pos_ckey,ce.app_id,lic.customer_id, c.company_name, c.account_number, c.customer_balanced, '' as posuser_name, 0 as posuser_id, lic.license_id,  lic.license_type_id, c.customer_status, c.enable_airtime, c.enable_unipin, c.customer_pricelist_id, c.enable_remote_ext_credit, c.allow_transfer_cash, c.allow_transfer_interstore, c.enable_easyairtime, cc.credit_warning, ";
             } else {
                sql += " select ce.pos_ckey,ce.app_id,lic.customer_id, c.company_name, c.account_number, c.customer_balanced, (select posuser_firstname from posuser where posuser_id = lic.posuser_id) as posuser_name, lic.posuser_id,  lic.license_id, 0 as license_type_id, c.customer_status, c.enable_airtime, c.enable_unipin, c.customer_pricelist_id, c.enable_remote_ext_credit, c.allow_transfer_cash, c.allow_transfer_interstore, c.enable_easyairtime, cc.credit_warning, ";
            }        
                
            sql += " c.cashms_limit, ";
            
            sql += "  round(0-cc.balance + (cc.credit_limit + cc.credit_extended),2) as available_balance, "
                + "   round(0-cc.balance,2) as balance, "
                + "   round(0-cc.balance_cash,2) as balance_cash, "
                + "   round(cc.credit_limit,2) as credit_limit, "
                + "   round(cc.credit_extended,2) as credit_extended ";

              if (ignoreLevel == 1) {
                  sql += " from license lic ";
              } 
              else if (ignoreLevel == 100) {
                  sql += " from license lic ";
              }else {
                  sql += " from tx_customer_lookup lic ";
              }

            sql += " inner join customer_credit cc on cc.customer_id = lic.customer_id "
                + "  inner join customer c on c.customer_id = lic.customer_id "
                 + "  inner join customer_extra ce on ce.customer_id = c.customer_id ";
              if (ignoreLevel == 1) {
                     sql += " where lic.enabled = 1 and lic.license_code = ? limit 1;";
                     
              }else {
                     sql += " where lic.license_code = ? and lic.posuser_id = ? limit 1;";
              }

        int rowCount = 0;
        try (Connection con = getConnection();) {

            PreparedStatement ps = con.prepareStatement(sql);
            
            if (ignoreLevel == 1) {
                ps.setString(1, licenseCode);
            } else {
                ps.setString(1, licenseCode);
                ps.setInt(2, Integer.parseInt(posuserID));
            }
            //System.out.print(ps);
            try (ResultSet rs = ps.executeQuery();) {
                while(rs.next()) {
                    rowCount++;
                    
                    account_number = rs.getString("account_number");
                   
                    company_name = rs.getString("company_name");
                    posuser_name  = rs.getString("posuser_name");
                    posuser_id = rs.getInt("posuser_id");
                    enable_airtime = rs.getInt("enable_airtime");
                    enable_unipin = rs.getInt("enable_unipin");
                    customer_status = rs.getInt("customer_status");
                    license_id = rs.getInt("license_id");
                    license_type_id = rs.getInt("license_type_id");
                    customer_id = rs.getInt("customer_id");
                    pricelist_id = rs.getInt("customer_pricelist_id");
                    customer_balanced = rs.getInt("customer_balanced");
                    
                    available_balance = rs.getDouble("available_balance");
                    balance = rs.getDouble("balance");
                    balance_cash = rs.getDouble("balance_cash");
                    credit_limit = rs.getDouble("credit_limit");
                    credit_extended = rs.getDouble("credit_extended");
                    
                    enable_remote_ext_credit = rs.getInt("enable_remote_ext_credit");
                    allow_transfer_cash = rs.getInt("allow_transfer_cash");
                    allow_transfer_interstore = rs.getInt("allow_transfer_interstore");
                    
                    cashms_limit = rs.getDouble("cashms_limit");
                    
                    appID = rs.getString("app_id");
     ckey = rs.getString("pos_ckey");
    // System.out.print(ckey);
                    if (rs.getDouble("available_balance") <= rs.getDouble("credit_warning"))
                    {
                        low_balance = "1";
                    }
                    
                    if (credit_limit == 0d) {
                        cash_customer = true;
                        enable_remote_ext_credit = 0;
                    }
                    
                }
            }

            if (rowCount == 0) {
                throw new MyException("license not authorised");
            }

        } catch (SQLException e) {
            throw new MyException("license not authorised",e ,showDebug );
        }
        
        if (available_balance < 0) {
            available_balance = 0.00;
        }

        
    } 
    public void fetchRealtimeCust() {
  
        String sql = "";

            if (ignoreLevel == 1) {
                sql += " select ce.pos_ckey,ce.app_id,lic.customer_id, c.company_name, c.account_number, c.customer_balanced, '' as posuser_name, 0 as posuser_id, lic.license_id,  lic.license_type_id, c.customer_status, c.enable_airtime, c.enable_unipin, c.customer_pricelist_id, c.enable_remote_ext_credit, c.allow_transfer_cash, c.allow_transfer_interstore, c.enable_easyairtime, cc.credit_warning, ";
             } else {
                sql += " select ce.pos_ckey,ce.app_id,lic.customer_id, c.company_name, c.account_number, c.customer_balanced, (select posuser_firstname from posuser where posuser_id = lic.posuser_id) as posuser_name, lic.posuser_id,  lic.license_id, 0 as license_type_id, c.customer_status, c.enable_airtime, c.enable_unipin, c.customer_pricelist_id, c.enable_remote_ext_credit, c.allow_transfer_cash, c.allow_transfer_interstore, c.enable_easyairtime, cc.credit_warning, ";
            }        
                
            sql += " c.cashms_limit, ";
            
            sql += "  round(0-cc.balance + (cc.credit_limit + cc.credit_extended),2) as available_balance, "
                + "   round(0-cc.balance,2) as balance, "
                + "   round(0-cc.balance_cash,2) as balance_cash, "
                + "   round(cc.credit_limit,2) as credit_limit, "
                + "   round(cc.credit_extended,2) as credit_extended ";

              if (ignoreLevel == 1) {
                  sql += " from license lic ";
              } 
              else if (ignoreLevel == 100) {
                  sql += " from license lic ";
              }else {
                  sql += " from tx_customer_lookup lic ";
              }

            sql += " inner join customer_credit cc on cc.customer_id = lic.customer_id "
                + "  inner join customer c on c.customer_id = lic.customer_id "
                 + "  inner join customer_extra ce on ce.customer_id = c.customer_id ";
              if (ignoreLevel == 1) {
                     sql += " where lic.enabled = 1 and lic.license_code = ? limit 1;";
                     
              } else if (ignoreLevel == 100) {
                     sql += " where lic.enabled = 1  limit 1;";
                     
              }else {
                     sql += " where lic.license_code = ? and lic.posuser_id = ? limit 1;";
              }

        int rowCount = 0;
        try (Connection con = getConnection();) {

            PreparedStatement ps = con.prepareStatement(sql);
            
            if (ignoreLevel == 1) {
                ps.setString(1, licenseCode);
            } else {
                ps.setString(1, licenseCode);
                ps.setInt(2, Integer.parseInt(posuserID));
            }
            System.out.print(ps);
            try (ResultSet rs = ps.executeQuery();) {
                while(rs.next()) {
                    rowCount++;
                    
                    account_number = rs.getString("account_number");
                   
                    company_name = rs.getString("company_name");
                    posuser_name  = rs.getString("posuser_name");
                    posuser_id = rs.getInt("posuser_id");
                    enable_airtime = rs.getInt("enable_airtime");
                    enable_unipin = rs.getInt("enable_unipin");
                    customer_status = rs.getInt("customer_status");
                    license_id = rs.getInt("license_id");
                    license_type_id = rs.getInt("license_type_id");
                    customer_id = rs.getInt("customer_id");
                    pricelist_id = rs.getInt("customer_pricelist_id");
                    customer_balanced = rs.getInt("customer_balanced");
                    
                    available_balance = rs.getDouble("available_balance");
                    balance = rs.getDouble("balance");
                    balance_cash = rs.getDouble("balance_cash");
                    credit_limit = rs.getDouble("credit_limit");
                    credit_extended = rs.getDouble("credit_extended");
                    
                    enable_remote_ext_credit = rs.getInt("enable_remote_ext_credit");
                    allow_transfer_cash = rs.getInt("allow_transfer_cash");
                    allow_transfer_interstore = rs.getInt("allow_transfer_interstore");
                    
                    cashms_limit = rs.getDouble("cashms_limit");
                    
                    appID = rs.getString("app_id");
     ckey = rs.getString("pos_ckey");
     System.out.print(ckey);
                    if (rs.getDouble("available_balance") <= rs.getDouble("credit_warning"))
                    {
                        low_balance = "1";
                    }
                    
                    if (credit_limit == 0d) {
                        cash_customer = true;
                        enable_remote_ext_credit = 0;
                    }
                    
                }
            }

            if (rowCount == 0) {
                throw new MyException("license not authorised");
            }

        } catch (SQLException e) {
            throw new MyException("license not authorised",e ,showDebug );
        }
        
        if (available_balance < 0) {
            available_balance = 0.00;
        }

        
    }
    
    private String stripStart(final String str, final String stripChars) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }
        int start = 0;
        if (stripChars == null) {
            while (start != strLen && Character.isWhitespace(str.charAt(start))) {
                start++;
            }
        } else if (stripChars.isEmpty()) {
            return str;
        } else {
            while (start != strLen && stripChars.indexOf(str.charAt(start)) != -1) {
                start++;
            }
        }
        return str.substring(start);
    }
    
    
       
    
    protected Connection getConnection(){

      Connection result = null;
      try {

        Context initialContext = new InitialContext();

        DataSource datasource = (DataSource)initialContext.lookup("jdbc/glassfish");
        if (datasource != null) {
            result = datasource.getConnection();
        }
        else {
            System.err.println("failed to lookup datasource");
            throw new MyException("failed to lookup datasource");
        }

      }
      catch ( NamingException | SQLException e ) {
          throw new MyException("cannot get connection", e, showDebug);
      }

      return result;

    }   
 
     
   
    
    
}
