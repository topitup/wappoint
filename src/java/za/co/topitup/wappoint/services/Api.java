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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.Schedule;


import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.stream.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;
import sun.rmi.runtime.Log;

@Path("Api")
public class Api extends Resource {
    
    private static final String DATE_FORMAT_D = "%1$td/%1$tm/%1$tY";
    private static final String DATE_FORMAT_T = "%1$tH:%1$tM:%1$tS";
    private String data,result,usergroup,username,timestamp,token,applicationid;
    private  String UniqueID,BatchNumber,Amount,UserReference,OperatorID,Terminal,Settled,BankResponse,BankReference,CycleNumber,CardType,TransactionDate,random,CardNumber,ExpiryMonth,ExpiryYear;
   int pid;
   String sdt,edt;
               
    public Api() {
        
    }
    
    private String randomString( int len ) 
    {
       Random rnd = new Random();
       String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
       StringBuilder sb = new StringBuilder( len );
       for( int i = 0; i < len; i++ ) 
          sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
          java.util.Date dtmnow = new java.util.Date();
                String strDate2 = String.format("%1$td%1$tm%1$tY", dtmnow);
                String strTime2 = String.format("%1$tH%1$tM%1$tS", dtmnow);
       return sb.toString()+strDate2+strTime2;
    }
    
        @GET
    @Path("swipe-monthly-sales")
    @Produces({"application/json"})
    public String swipe_monthly_sales(@QueryParam("customer_id") int customer_id,@QueryParam("y") int y,@QueryParam("m") int m) {
        
        int sts=1;//settled
        String result = "";
              String slip = "";
               java.util.Date dtmnow = new java.util.Date();
                String strDate2 = String.format("%1$td/%1$tm/%1$tY", dtmnow);
                String strTime2 = String.format("%1$tH:%1$tM:%1$tS", dtmnow);

          
                slip += "2" + company_name + "\n";
                 slip += "2" + account_number + "\n";
                slip += "1\n";
                slip += "1Date       Time     \n";
                slip += "1" + strDate2 + " " + strTime2+"\n";
                    slip += "1Pos User   "+posuser_name+"\n";
               // slip += "1" + strDate2 + " " + strTime2 + " " + posuser_name + "\n";
                
                    slip += "1\n";
     /*    String sql = "select DATE_FORMAT(dt,'%b') as mth,COUNT(id) as tx,sum(tx_amnt) as amount_sold,sum(settled_amnt) \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and r.sts = ?\n"+ 
                 " and MONTH(dt)=?\n"+
                 " and YEAR(dt)=?\n";*/
         String sql = "select sum(tx_amnt) as tx_amnt,sum(settled_amnt) as settled_amnt,COUNT(id) as tx, DATE_FORMAT(dt,'%b %Y') as mth \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and YEAR(r.dt) = ?\n"+
                    " and MONTH(r.dt) = ?\n"+
                    " and r.sts = ?";
         System.out.println(sql);
            try (Connection con = getConnection(); 
                PreparedStatement ps = con.prepareStatement(sql);) {
                
                
                ps.setInt(1, customer_id);
                ps.setInt(2, y);
                ps.setInt(3, m);
                ps.setInt(4, sts);
               //  
              //   ps.setInt(4, m);
         
            try (ResultSet rs = ps.executeQuery();) {
                while(rs.next()) {
                    result += rs.getString("mth") + "^";
                  
               
        slip += "1"+rs.getString("mth")+"          "+rs.getString("tx")+" Tx's\n";
        slip += "1Amount Sold"+"       R"+rs.getString("tx_amnt")+"\n";
        slip += "1Funds Settled"+"     R"+rs.getString("settled_amnt")+"\n";
       //System.out.print("month name"+result);
                }
            }
            
            
           
            
            
            
        } catch (SQLException e) {
          //  throw new MyException("could not get details1",e,showDebug);
            slip += "1txn's not found\n";
        slip += "1\n";
        slip += "1         Top it Up\n";
           slip += "1     0860 111 723\n";
        slip += "1Whatsapp 064 121 9970\n";
          slip += "1     www.itopitup.co.za\n";
        slip += "1\n";
        return slip;
         } 
        slip += "1---------------------------\n";
        slip += "1---------------------------\n";
               String sql1 = "select sum(tx_amnt) as tx_amnt,sum(settled_amnt) as settled_amnt,COUNT(id) as tx,   DATE_FORMAT(dt, '%d %b %Y')  as mth \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and YEAR(r.dt) = ?\n"+
                    " and MONTH(r.dt) = ?\n"+
                    " and r.sts = ?"+
                   " group by date(dt)";

            try (Connection con = getConnection(); 
                PreparedStatement ps1 = con.prepareStatement(sql1);) {
                
                
                ps1.setInt(1, customer_id);
                ps1.setInt(2, y);
                ps1.setInt(3, m);
                ps1.setInt(4, sts);
               //  
              //   ps.setInt(4, m);
         
            try (ResultSet rs1 = ps1.executeQuery();) {
                while(rs1.next()) {
                    result += rs1.getString("mth") + "^";
                  
               
        slip += "1"+rs1.getString("mth")+"          "+rs1.getString("tx")+" Tx's\n";
 slip += "1Amount Sold"+"       R"+rs1.getString("tx_amnt")+"\n";
        slip += "1Funds Settled"+"     R"+rs1.getString("settled_amnt")+"\n";
               slip += "1---------------------------\n";
  
       //System.out.print("month name"+result);
                }
            }
        } catch (SQLException e) {
            throw new MyException("could not get details2",e,showDebug);
         } 
               
            
    
 slip += "1\n";

        slip += "1\n";
        slip += "1         Top it Up\n";
      slip += "1   0860 111 723\n";
      slip += "1Whatsapp 064 121 9970\n";
               slip += "1   0860 111 723|Whatsapp 064 121 9970\n";
                 slip += "1     www.itopitup.co.za\n";
        slip += "1\n";

        return slip;
        
        
    }
          @GET
    @Path("swipe-daily-sales")
    @Produces({"application/json"})
    public String swipe_daily_sales(@QueryParam("customer_id") int customer_id,@QueryParam("y") int y,@QueryParam("m") int m,@DefaultValue("30")  @QueryParam("d") int d) {
        
         int sts=1;//settled
        String result = "";
              String slip = "";
               java.util.Date dtmnow = new java.util.Date();
                String strDate2 = String.format("%1$td/%1$tm/%1$tY", dtmnow);
                String strTime2 = String.format("%1$tH:%1$tM:%1$tS", dtmnow);

                slip += "2" + company_name + "\n";
                 slip += "2" + account_number + "\n";
                slip += "1\n";
                slip += "1Date       Time     \n";
                slip += "1" + strDate2 + " " + strTime2+"\n";
                    slip += "1Pos User   "+posuser_name+"\n";
               // slip += "1" + strDate2 + " " + strTime2 + " " + posuser_name + "\n";
                
                    slip += "1\n";
     /*    String sql = "select DATE_FORMAT(dt,'%b') as mth,COUNT(id) as tx,sum(tx_amnt) as amount_sold,sum(settled_amnt) \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and r.sts = ?\n"+ 
                 " and MONTH(dt)=?\n"+
                 " and YEAR(dt)=?\n";*/
         String sql3 = "select sum(tx_amnt) as tx_amnt,sum(settled_amnt) as settled_amnt,COUNT(id) as tx, DATE_FORMAT(dt,'%d %b %Y') as mth \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and YEAR(r.dt) = ?\n"+
                    " and MONTH(r.dt) = ?\n"+
                     " and DAY(r.dt) = ?\n"+
                    " and r.sts = ?";

            try (Connection con = getConnection(); 
                PreparedStatement ps3 = con.prepareStatement(sql3);) {
                
                
                ps3.setInt(1, customer_id);
                ps3.setInt(2, y);
                ps3.setInt(3, m);
                ps3.setInt(4, d);
                ps3.setInt(5, sts);
               //  
              //   ps.setInt(4, m);
         
            try (ResultSet rs3 = ps3.executeQuery();) {
                while(rs3.next()) {
                    result += rs3.getString("mth") + "^";
                  System.out.print("live");
               if(rs3.getString("tx").matches("0")){
                      slip += "1txn's not found\n";
               }
               else{
        slip += "1"+rs3.getString("mth")+"          "+rs3.getString("tx")+" Tx's\n";
        slip += "1Amount Sold"+"       R"+rs3.getString("tx_amnt")+"\n";
        slip += "1Funds Settled"+"     R"+rs3.getString("settled_amnt")+"\n";
         slip += "1---------------------------\n";
        slip += "1---------------------------\n";
                       }
       //System.out.print("month name"+result);
                }
            }
        } catch (SQLException e) {
          //  throw new MyException("could not get details",e,showDebug);
            slip += "1txn's not found\n";
        slip += "1\n";
        slip += "1         Top it Up\n";
        slip += "1     0860 111 723\n";
        slip += "1Whatsapp 064 121 9970\n";
          slip += "1     www.itopitup.co.za\n";
        slip += "1\n";
        return slip;
         } 
       
         /*    */        String sql1 = "select tx_amnt,settled_amnt, DATE_FORMAT(dt,'%d %b %Y %H:%m') as mth \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and YEAR(r.dt) = ?\n"+
                    " and MONTH(r.dt) = ?\n"+
                     " and DAY(r.dt) = ?\n"+
                    " and r.sts = ?";

            try (Connection con = getConnection(); 
                PreparedStatement ps1 = con.prepareStatement(sql1);) {
                
                
                ps1.setInt(1, customer_id);
                ps1.setInt(2, y);
                ps1.setInt(3, m);
                ps1.setInt(4, d);
                ps1.setInt(5, sts);
               //  
              //   ps.setInt(4, m);
         int k=1;
            try (ResultSet rs1 = ps1.executeQuery();) {
                while(rs1.next()) {
                    result += rs1.getString("mth") + "^";
                  
               
        slip += "1"+rs1.getString("mth")+"        TX #"+k+"\n";
 slip += "1Amount Sold"+"       R"+rs1.getString("tx_amnt")+"\n";
        slip += "1Funds Settled"+"     R"+rs1.getString("settled_amnt")+"\n";
               slip += "1---------------------------\n";
  k++;
       //System.out.print("month name"+result);
                }
            }
        } catch (SQLException e) {
            //throw new MyException("could not get details1234",e,showDebug);
            slip += "1txn's not found\n";
        slip += "1\n";
        slip += "1         Top it Up\n";
       slip += "1   0860 111 723\n";
        slip += "1Whatsapp 064 121 9970\n";
            slip += "1     www.itopitup.co.za\n";
        slip += "1\n";
        return slip;
         } 
               
            
    
 slip += "1\n";

        slip += "1\n";
        slip += "1         Top it Up\n";
        slip += "1     0860 111 723\n";
        slip += "1Whatsapp 064 121 9970\n";
          slip += "1     www.itopitup.co.za\n";
        slip += "1\n";

        return slip;
        
    }
    
    
    
    @GET
    @Path("get-realtime-sales-id")
    @Produces({"application/json"})
    public String get_realtime_sales_id(@QueryParam("customer_id") int customer_id) {
        
        String pid = null;
        String sql1 = "SELECT MAX(id) AS pid FROM wap_point_realtime_txns WHERE updt=0 and customer_id=?";

            try (Connection con = getConnection(); 
                PreparedStatement ps1 = con.prepareStatement(sql1);) {
                
                
                ps1.setInt(1, customer_id);
                   try (ResultSet rs1 = ps1.executeQuery();) {
                while(rs1.next()) {
                    pid=rs1.getString("pid");
                            String sql2 = "update wap_point_realtime_txns set updt=1 where updt='0'";
                            System.out.println(sql2);
                            
                    PreparedStatement ps2 = con.prepareStatement(sql2);
                    //ps2.setString(1, pid);
                   // ps2.executeUpdate();
                   
                    
                    
                }
                   }
            }catch (SQLException e) {
            //throw new MyException("could not get details1234",e,showDebug);
           
         } 
            
            return pid;
            
        
    }
    
    
    
        @GET
    @Path("print-real-time-slip")
    @Produces({"application/json"})
    public String print_real_time_slip(@QueryParam("customer_id") int customer_id,@DefaultValue("C")  @QueryParam("recpttype") String recpttype) {
        
        
           int pid = 0;
        String sqlpid = "SELECT MAX(id) AS pid FROM wap_point_realtime_txns WHERE updt=0 and customer_id=?";

            try (Connection con = getConnection(); 
                PreparedStatement ps12 = con.prepareStatement(sqlpid);) {
                
                
                ps12.setInt(1, customer_id);
                   try (ResultSet rs12 = ps12.executeQuery();) {
                while(rs12.next()) {
                    pid=rs12.getInt("pid");
                   
                            String sql2 = "update wap_point_realtime_txns set updt=1 where updt='0'";
                            System.out.println(sql2);
                            
                    PreparedStatement ps2 = con.prepareStatement(sql2);
                 ps2.executeUpdate();
                    
                } 
                   }
                   
            }catch (SQLException e) {
            //throw new MyException("could not get details1234",e,showDebug);
                
                
            }
        
        String slip = "";
        java.util.Date dtmnow = new java.util.Date();
                String strDate2 = String.format("%1$td/%1$tm/%1$tY", dtmnow);
                String strTime2 = String.format("%1$tH:%1$tM:%1$tS", dtmnow);
          slip += "2" + company_name + "\n";
                 slip += "2" + account_number + "\n";
                slip += "1\n";
               // slip += "1Date       Time     \n";
              
                                         slip += "1CUSTOMER RECEIPT"+ "\n";

               
                             slip += "1\n";
                             
                             
                slip += "1" + strDate2 + " " + strTime2+"\n";
                 //   slip += "1Pos User   "+posuser_name+"\n";
               // slip += "1" + strDate2 + " " + strTime2 + " " + posuser_name + "\n";
                
                    slip += "1\n";
                    
                    String slip1="";
                    
                    slip1 += "2" + company_name + "\n";
                 slip1 += "2" + account_number + "\n";
                slip1 += "1\n";
               // slip += "1Date       Time     \n";
               
                      slip1 += "1MERCHANT RECEIPT"+ "\n";
                             slip1 += "1\n";
                             
                             
                slip1 += "1" + strDate2 + " " + strTime2+"\n";
                 //   slip += "1Pos User   "+posuser_name+"\n";
               // slip += "1" + strDate2 + " " + strTime2 + " " + posuser_name + "\n";
                
                    slip1 += "1\n";
                    
        String sql1 = "select round(tx_amnt,2) as tx_amnt,round(settled_amnt,2) as settled_amnt,DATE_FORMAT(dt,'%d %b %Y') as mth,uid,customer_tx,pan \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and id = ?\n"+
                    " and r.sts = ?";

            try (Connection con = getConnection(); 
                PreparedStatement ps1 = con.prepareStatement(sql1);) {
                
                
                ps1.setInt(1, customer_id);
                 ps1.setInt(2, pid);
                  ps1.setInt(3, 1);
                   try (ResultSet rs1 = ps1.executeQuery();) {
                while(rs1.next()) {
                   // pid=rs1.getString("pid");
                    slip += "2APPROVED:R"+rs1.getString("tx_amnt")+ "\n";
                             slip += "1\n";
                             
                         slip1 += "2APPROVED:R"+rs1.getString("tx_amnt")+ "\n";
                             slip1 += "1\n";          
                             
                             
                             
                              slip += "1PAN:"+rs1.getString("pan")+ "\n";
                               slip1 += "1\n";
                              slip1 += "1PAN:"+rs1.getString("pan")+ "\n";
                                slip1 += "1\n";
                               slip1 += "1AUTHOROZED BY PIN\n";
                                slip1 += "1NO SIGNATURE REQUIRED\n";
                                  slip1 += "1\n";
                              slip += "1UID:"+rs1.getString("uid")+ "\n";
                              slip += "1Txn. No.:"+rs1.getString("customer_tx")+ "\n";
                                     slip1 += "1UID:"+rs1.getString("uid")+ "\n";
                              slip1 += "1Txn. No.:"+rs1.getString("customer_tx")+ "\n";
                   
                }
                   }
            }catch (SQLException e) {
            //throw new MyException("could not get details1234",e,showDebug);
           
         } 
                        
    
 slip += "1\n";

        slip += "1\n";
        slip += "1         Top it Up\n";
        slip += "1     0860 111 723\n";
        slip += "1Whatsapp 064 121 9970\n";
          slip += "1     www.itopitup.co.za\n";
        slip += "1\n";
        
        
        slip1 += "1\n";

        slip1 += "1\n";
        slip1 += "1         Top it Up\n";
         slip1 += "1     0860 111 723\n";
        slip1 += "1Whatsapp 064 121 9970\n";
          slip1 += "1     www.itopitup.co.za\n";
        slip1 += "1\n";
        
        
        
        
        
        
        
        
                    return "{ \"pid\": \"" + pid + "\",\"print_data\": \"" + slip + "\",\"print_data1\": \"" + slip1 +"\" }";

           
            
        
    }
    
    
    
    
    
    
    
    
    
    
    @GET
    @Path("get-txns")
    @Produces({"application/json"})
    public String get_txns( @DefaultValue("2021-07-12") @QueryParam("sdt") String sdt,@DefaultValue("2021-07-12") @QueryParam("edt") String edt) {
         String result="";
        /*sd
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
           timestamp=formattedDate;
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
      token = Base64.encodeToString(tokenHash, Base64.NO_WRAP);
       
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        
        usergroup="10004225";
        username="WPP1977";
       
    
        result=token;
        */
        

   /*
       // customer_id=1110;
        String result = "";
         String sql = "select tx_amnt, dt \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and r.sts = ?";

            try (Connection con = getConnection(); 
                PreparedStatement ps = con.prepareStatement(sql);) {
                
                ps.setInt(1, customer_id);
                 ps.setInt(2, sts);
         //  System.out.print(sql);     
            try (ResultSet rs = ps.executeQuery();) {
                while(rs.next()) {
                    result += rs.getString("tx_amnt") + "^";
                    result += rs.getString("dt");
               
                }
            }
        } catch (SQLException e) {
            throw new MyException("could not get rica list",e,showDebug);
         }   */     
   generateToken();
   
 waplivetx(sdt,edt);
 System.out.print("Sesion Stopped..............");
        return data;
    }
    private void generateToken(){
        
        token="Y25WFSWTME4gKVdqt3dXowp2po7pbubAsbzmH6CiTadY8NFTRD9zcb0gsECnEMC1";
         System.out.print("getting Token..............");
        try {
         URL url = new URL("http://77.68.15.76:3400/api/Users/login");
        Map<String,Object> params = new LinkedHashMap<>();
        params.put("email", "zamier.seidel@gmail.com");
        params.put("password", "2z4E9nFqfhf&hBzx");
     
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
            postData.append('=');
            postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
        }
        byte[] postDataBytes = postData.toString().getBytes("UTF-8");

        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
        conn.setDoOutput(true);
        conn.getOutputStream().write(postDataBytes);
        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
          StringBuilder sb = new StringBuilder();
        for (int c; (c = in.read()) >= 0;)
            sb.append((char)c);
        String response = sb.toString();
           JSONObject obj = new JSONObject(response);
           token=obj.get("id").toString();
      System.out.print("Fetched Token..............");
        } catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
			
		}
        
        
        
        
        
        
        
        
    }

    private void updateDB(){
 
        String result = "";
        String sql = "call wap_realtime_tx(?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(sql);) {
                /*
               int minimum=100;
               int maximum=50;
int randomNum1 = minimum + (int)(Math.random() * maximum);
int minimum1=10000;int maximum1=5000;
int randomNum2 = minimum1 + (int)(Math.random() * maximum1);
	String[] arr={"credit", "ebit", "foreign"};
      int rnd = new Random().nextInt(arr.length);
                */
      //	String pantype=arr[rnd];
                ps.setString(1, Terminal);
                ps.setString(2, UniqueID);
                ps.setString(3, TransactionDate);
                ps.setString(4, random);
                ps.setString(5, CardType);//randomNum2
                ps.setString(6, Amount);
                ps.setString(7, BatchNumber);
                ps.setString(8, CycleNumber);
                ps.setString(9, BankResponse);
                ps.setString(10, BankReference);
                ps.setString(11, OperatorID);
                ps.setString(12, UserReference);
                ps.setString(13, CardNumber);
                ps.setString(14, ExpiryMonth+ExpiryYear);

                try (ResultSet rs = ps.executeQuery();) {
                    while (rs.next()) {
                  //int id = rs.getInt("response");
               
                    }
                }
              
            } catch (SQLException e) {
                throw new MyException("Could not request new payment", e, showDebug);
            }
           
                 // System.out.print(result);
    }
    private void waplivetx(@QueryParam("sdt") String sdta,@QueryParam("edt") String edta){
        
    random=randomString(10);
    sdt=sdta;edt=edta;
        try {

			 //URL url = new URL("https://portal.nedsecure.co.za/api/merchant/authenticate");
URL url = new URL("http://77.68.15.76:3400/api/Transactions/"+sdt+"/"+edt+"/WG5933/getrecenttransactions?access_token="+token);
System.out.print("Session Started Fetching Txn's API Server..............");
System.out.println(url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

              /*          
                        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("usergroup", usergroup);
        parameters.put("username", username);
        parameters.put("timestamp", timestamp);
        parameters.put("token", token);
        conn.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        out.flush();
        out.close();*/

        //conn.setConnectTimeout(5000);
        //conn.setReadTimeout(5000);
                        
                        
                        
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
		
			while ((output = br.readLine()) != null) {

                          data=output;
                             JSONObject obj = new JSONObject(output);
    //System.out.println(obj);
 if (obj instanceof JSONObject)
printJsonObject(obj);
				//System.out.println(output);
			}
			
			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
			
		}


    }
    public  void printJsonObject(JSONObject jsonObj) {
        
        
    for (Object key : jsonObj.keySet()) {
        //based on you key types
        String keyStr = (String)key;
        Object value = jsonObj.get(keyStr);
        JSONArray arr=jsonObj.getJSONArray(keyStr);
   //System.out.println(keyStr+"->"+value);
   
     // System.out.println("length---------->"+keyStr.length());
        //Print key and value
   int count=arr.length();
        
              System.out.println("Live Txn's Count="+arr.length());
                int cnt = 0; 
        String sql = "SELECT cnt FROM wap_txns_updated WHERE id = ( SELECT MAX(id) FROM wap_txns_updated ) ";
        try (Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);) 
        {
           
            try (ResultSet rs = ps.executeQuery();) {
                while(rs.next()) {
                    cnt = rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
            throw new MyException("Unable to get customer detail.",e,showDebug);
        }
        System.out.println("Database Count="+cnt);
              if(arr.length()==cnt){
                   System.out.println("No New Txn. System Halt>>>>>>>>>>>>>>>");
                  break;
              }
        for(int j=0;j<arr.length(); j++){

      if(j==0){

        String sql1 = "insert into wap_txns_updated (pid, cnt,api_sdt,api_edt,st_dtm) values ( ?, ?,?,?,now()) ";

        try (Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql1,Statement.RETURN_GENERATED_KEYS);) 
        {
            
            
            ps.setString(1, random);
              ps.setInt(2, count);
    ps.setString(3, sdt);
     ps.setString(4, edt);
                ps.executeUpdate();

               ResultSet rs = ps.getGeneratedKeys();
if (rs.next()) {
    pid = rs.getInt(1);
}

            } catch (SQLException e) {

            }
       
      }
       
   
JSONObject jObj = new JSONObject(arr.get(j).toString());
JSONArray keyc = jObj.names ();
//System.out.println(keyc);
for (int i = 0; i < keyc.length (); i++) {

  String keycc = keyc.getString (i); // Here's your key
  Object valuecc = jObj.get (keycc); // Here's your value
   
   // System.out.println(keycc+"->"+valuecc);    
   if(keycc.matches("UniqueID"))
       UniqueID=valuecc.toString();
     if(keycc.matches("BatchNumber"))
       BatchNumber=valuecc.toString();
       if(keycc.matches("Amount"))
       Amount=valuecc.toString();
          if(keycc.matches("CardNumber"))
       CardNumber=valuecc.toString(); 
               if(keycc.matches("ExpiryMonth"))
       ExpiryMonth=valuecc.toString(); 
                        if(keycc.matches("ExpiryYear"))
       ExpiryYear=valuecc.toString(); 
          if(keycc.matches("UserReference"))
       UserReference=valuecc.toString();
     if(keycc.matches("OperatorID"))
       OperatorID=valuecc.toString();
       if(keycc.matches("Terminal")){
       Terminal=valuecc.toString();
        // System.out.println("Terminal Valsssssssssssssssssssss->"+lastInsertId);  
       }
          if(keycc.matches("BankResponse"))
       BankResponse=valuecc.toString();
     if(keycc.matches("BankReference"))
       BankReference=valuecc.toString();
       if(keycc.matches("CycleNumber"))
       CycleNumber=valuecc.toString();
              if(keycc.matches("CardType"))
       CardType=valuecc.toString();
                  if(keycc.matches("TransactionDate"))
       TransactionDate=valuecc.toString();
                  

}
 updateDB();
        }
//JSONArray arr2=jgetJSONArray(arr.get(0).toString());

//obj1.getString("BatchNumber");

//obj1.get("TransactionDate");
        //JSONObject vals = new JSONObject(value.toString());
        
        //for nested objects iteration if required
        //if (vals instanceof JSONObject)
          //  printJsonObject((JSONObject)vals);
    }
}
    private void get(){
        
        	try {

			 //URL url = new URL("https://portal.nedsecure.co.za/api/merchant/authenticate");
                       URL url = new URL("https://portal.nedsecure.co.za/api/transactions?applicationid=38BC3819-A2B0-4FAC-A9E5-EC7C4CC1C29D");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");

              /*          
                        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("usergroup", usergroup);
        parameters.put("username", username);
        parameters.put("timestamp", timestamp);
        parameters.put("token", token);
        conn.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
        out.flush();
        out.close();*/

        //conn.setConnectTimeout(5000);
        //conn.setReadTimeout(5000);
                        
                        
                        
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {

                            result=output;
				//System.out.println(output);
			}
			
			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
			
		}
          

	}
    
    
    
        
    @GET
    @Path("get-fintx")
    @Produces({"application/json"})
    public String get_fintx(@DefaultValue("0") @QueryParam("alltx") int all_transactions) {

        String result = "";
        String tx_type = "";
        
        String sql = "select customer_tx_id, date_format(ctx.customer_tx_date,'%d-%m-%Y, %H:%i') as customer_tx_date, date_format(ctx.customer_tx_date,'%d-%m-%Y') as customer_tx_dated, ctx.customer_tx_type, ifnull(ctx.customer_order_no,'') as customer_order_no, ctx.customer_tx_amount, (0-ctx.customer_tx_amount) as amount , ctx.customer_tx_reference, b.bank_description \n";
                    sql +=  " from customer_tx ctx \n";
                    sql +=  "inner join lk_bank b on b.bank_id = ctx.bank_id\n" +
                            " inner join user u on u.user_id = ctx.user_id\n" +
                            " where ctx.customer_id = ? \n";
                if (all_transactions == 0) {
                    sql +=  "   and ctx.customer_tx_type = 2 \n";
                }
                sql +=  " order by ctx.customer_tx_date desc ,ctx.customer_tx_id desc \n" +
                        " limit 10";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);) 
        {
            
            ps.setInt(1, customer_id);
                    
            try (ResultSet rs = ps.executeQuery();) {
                
                int rowcnt = 0;
                
                while(rs.next()) {
                    
                    rowcnt++;
                    if (rowcnt > 1) {
                        result += ",";
                    }
                    
                    tx_type = "Unknown";
                    switch (rs.getInt("customer_tx_type")) {
                        case 1 : tx_type = "Invoice";break;
                        case 2 : tx_type = "Payment";
                                 if (rs.getDouble("customer_tx_amount") > 0) tx_type = "Payment Reversal";
                                 break;
                        case 3 : tx_type = "Journal Debit";break;
                        case 4 : tx_type = "Journal Credit";break;
                        case 5 : tx_type = rs.getString("customer_tx_reference"); break;
                        case 7 : tx_type = "Credit Note";break;
                        case 8 : tx_type = "Transfer To";break;
                        case 9 : tx_type = "Transfer From";break;
                        case 10: tx_type = "Wallet Xfer";break;
                    }
                      LocalDateTime myDateObj = LocalDateTime.now();
                       DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy");  
    
    String formattedDate = myDateObj.format(myFormatObj);
   // System.out.print(formattedDate);

                     result += "{";
                     String customer_order_no= rs.getString("customer_order_no");
                    if(rs.getString("customer_tx_dated").equals(formattedDate) && customer_order_no.contains("TRF FROM"))
                        customer_order_no="Swipe Intraday";
                 //System.out.print(rs.getString("customer_tx_dated"));       
                   
                    result += "\"tx_id\":\"" + rs.getInt("customer_tx_id") + "\",";
                    result += "\"tx_type\":\"" + tx_type + "\",";
                    result += "\"customer_tx_date\":\"" + rs.getString("customer_tx_date") + "\",";
                    result += "\"customer_tx_type\":\"" + rs.getString("customer_tx_type") + "\",";
                    result += "\"customer_order_no\":\"" + customer_order_no + "\",";
                    result += "\"customer_tx_amount\":\"R " + new DecimalFormat("0.00").format(rs.getDouble("amount")) + "\",";
                    result += "\"bank_description\":\"" + rs.getString("bank_description") + "\"";
                    result += "}";
                    
                }
            }
        } catch (SQLException e) {
            throw new MyException("Could not retrieve financial transactions!",e,showDebug);
        }
        
        return "{ \"arr\": [" + result + "]}";

    }    
    
    
    @GET
    @Path("print-fintx")
    @Produces({"application/json"})
    public String print_fintx(@DefaultValue("0") @QueryParam("tx_id") int tx_id) {

        String tx_type = "";
        
        if (tx_id == 0){
            throw new MyException("Incorrect data request.");
        }
        
        String result = "2Financial Transactions\n";
        result += "1\n";

        String sql = "select customer_tx_id, date_format(ctx.customer_tx_date,'%d-%m-%Y, %H:%i') as customer_tx_date, ctx.customer_tx_type, ifnull(ctx.customer_order_no,'') as customer_order_no, ctx.customer_tx_amount, (0-ctx.customer_tx_amount) as amount , ctx.customer_tx_reference, b.bank_description, " +
                    " date_format(now(),'%d-%m-%Y, %H:%i') as cur_dtm " +
                    " from customer_tx ctx \n" +
                    " inner join lk_bank b on b.bank_id = ctx.bank_id\n" +
                    " inner join user u on u.user_id = ctx.user_id\n" +
                    " where ctx.customer_id = ? \n" +
                    " and ctx.customer_tx_id = ?";
        
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);) 
        {
            
            ps.setInt(1, customer_id);
            ps.setInt(2, tx_id);
            
            try (ResultSet rs = ps.executeQuery();) {
                
                while(rs.next()) {
                    
                    tx_type = "Unknown";
                    switch (rs.getInt("customer_tx_type")) {
                        case 1 : tx_type = "Invoice";break;
                        case 2 : tx_type = "Payment";
                                 if (rs.getDouble("customer_tx_amount") > 0) tx_type = "Payment Reversal";
                                 break;
                        case 3 : tx_type = "Journal Debit";break;
                        case 4 : tx_type = "Journal Credit";break;
                        case 5 : tx_type = rs.getString("customer_tx_reference"); break;
                        case 7 : tx_type = "Credit Note";break;
                        case 8 : tx_type = "Transfer To";break;
                        case 9 : tx_type = "Transfer From";break;
                        case 10: tx_type = "Wallet Xfer";break;
                    }
                    
                    result += "1Print Date: " + rs.getString("cur_dtm") + "\n";
                    result += "1\n";
                    result += "1Description: " + tx_type + "\n";
                    result += "1Tx Date: " + rs.getString("customer_tx_date") + "\n";
                    result += "1Order #: " + rs.getString("customer_order_no") + "\n";
                    result += "1Bank: " + rs.getString("bank_description") + "\n";
                    result += "1Amount: R " + new DecimalFormat("0.00").format(rs.getDouble("amount")) + "\n";
                    
                }
            }
        } catch (SQLException e) {
            throw new MyException("Could not retrieve financial transaction!",e,showDebug);
        }
        
        return result;

    }    
    
    
    
    
    
    
    }
    
    
    
    
    
    
    
    

