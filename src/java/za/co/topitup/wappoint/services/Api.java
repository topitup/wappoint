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
import static java.lang.Integer.parseInt;
import java.net.URLEncoder;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
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


import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.xml.sax.InputSource;
@Path("Api")
public class Api extends Resource {
    
    private static final String DATE_FORMAT_D = "%1$td/%1$tm/%1$tY";
    private static final String DATE_FORMAT_T = "%1$tH:%1$tM:%1$tS";
    private String data,result,usergroup,username,timestamp,token,applicationid;
    private  String UniqueID,BatchNumber,Amount,UserReference,OperatorID,Terminal,Settled,BankResponse,BankReference,CycleNumber,CardType,TransactionDate,random,CardNumber,ExpiryMonth,ExpiryYear;
   int pid,pidiveri;
   String JsonResponse;
   String sdt,edt,sdt1,edt1;
        int stmtCounter = 0;
           
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
         String sql = "select sum(r.tx_amnt) as tx_amnt,sum(settled_amnt) as settled_amnt,COUNT(id) as tx \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and YEAR(r.dt) = ?\n"+
                    " and MONTH(r.dt) = ?\n"+
                    " and r.sts = ?\n"+
                    " GROUP BY r.customer_id";
         //System.out.println(sql);
            try (Connection con = getConnection(); 
                PreparedStatement ps = con.prepareStatement(sql);) {
                
                
                ps.setInt(1, customer_id);
                ps.setInt(2, y);
                ps.setInt(3, m);
                ps.setInt(4, sts);
               //  
              //   ps.setInt(4, m);
        // System.out.println(ps);
        // slip +=sql;
        String mth=Month.of(m).name()+" "+y;
            try (ResultSet rs = ps.executeQuery();) {
                while(rs.next()) {
                    result += mth + "^";
                  
               
        slip += "1"+mth+"          "+rs.getString("tx")+" Tx's\n";
        slip += "1Amount Sold"+"       R"+rs.getString("tx_amnt")+"\n";
        slip += "1Funds Settled"+"     R"+rs.getString("settled_amnt")+"\n";
       //System.out.print("month name"+result);
                }
            }
            
            
           
            
            
            
        } catch (SQLException e) {
           throw new MyException("could not get details1",e,showDebug);
           /* slip += "1txn's not found\n";
        slip += "1\n";
        slip += "1         Top it Up\n";
           slip += "1     0860 111 723\n";
        slip += "1Whatsapp 064 121 9970\n";
          slip += "1     www.itopitup.co.za\n";
        slip += "1\n";
        return slip;*/
         } 
        slip += "1---------------------------\n";
        slip += "1---------------------------\n";
               String sql1 = "select date(dt) as dt,sum(tx_amnt) as tx_amnt,sum(settled_amnt) as settled_amnt,COUNT(id) as tx \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and YEAR(r.dt) = ?\n"+
                    " and MONTH(r.dt) = ?\n"+
                    " and r.sts = ?"+
                   " group by 1";

            try (Connection con = getConnection(); 
                PreparedStatement ps1 = con.prepareStatement(sql1);) {
                
                
                ps1.setInt(1, customer_id);
                ps1.setInt(2, y);
                ps1.setInt(3, m);
                ps1.setInt(4, sts);
               //  
              //   ps.setInt(4, m);
           String mth=Month.of(m).name();
            try (ResultSet rs1 = ps1.executeQuery();) {
                while(rs1.next()) {
                    result += rs1.getString("dt") + "^";
                  
               
        slip += "1"+rs1.getString("dt")+"          "+rs1.getString("tx")+" Tx's\n";
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
               slip += "1   0860 111 723\n";
                  slip += "1 Whatsapp 064 121 9970\n";
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
         String mth=Month.of(m).name();
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
         String sql3 = "select date(r.dt),sum(tx_amnt) as tx_amnt,sum(settled_amnt) as settled_amnt,COUNT(id) as tx \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and YEAR(r.dt) = ?\n"+
                    " and MONTH(r.dt) = ?\n"+
                     " and DAY(r.dt) = ?\n"+
                    " and r.sts = ?"+
                    " group by 1";

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
                    result += mth + "^";
                
               if(rs3.getString("tx").matches("0")){
                      slip += "1txn's not found\n";
               }
               else{
        slip += "1"+mth+"          "+rs3.getString("tx")+" Tx's\n";
        slip += "1Amount Sold"+"       R"+rs3.getString("tx_amnt")+"\n";
        slip += "1Funds Settled"+"     R"+rs3.getString("settled_amnt")+"\n";
         slip += "1---------------------------\n";
        slip += "1---------------------------\n";
                       }
       //System.out.print("month name"+result);
                }
            }
        } catch (SQLException e) {
      throw new MyException("could not get details",e,showDebug);
         /* slip += "1txn's not found\n";
        slip += "1\n";
        slip += "1         Top it Up\n";
        slip += "1     0860 111 723\n";
        slip += "1Whatsapp 064 121 9970\n";
          slip += "1     www.itopitup.co.za\n";
        slip += "1\n";
        return slip;*/
         } 
       
         /*    */        String sql1 = "select tx_amnt,settled_amnt, DATE_FORMAT(dt,'%d %b %Y %H:%i') as mth \n" +
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
    public String print_real_time_slip(@QueryParam("customer_id") int customer_id,@DefaultValue("C")  @QueryParam("recpttype") String recpttype,@QueryParam("currDate") String currDate,@DefaultValue("60")  @QueryParam("sec") String sec) {
        
        
           int pid = 0;
           int pidprv=0;
           int check=0;
           String lastTxdt="",lastAmnt="",lastUID="";
           
        java.util.Date dtmnow = new java.util.Date();


DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

 LocalDateTime currentdt=LocalDateTime.parse(currDate,formatter1);
//LocalDateTime prvdt=currentdt.minusMinutes(10);
LocalDateTime prvdt=currentdt.minusMinutes(180);
           
            
         
if(parseInt(sec)==60){
           
           
            String sqlprv = "select MAX(id),round(tx_amnt,2) as tx_amnt,round(settled_amnt,2) as settled_amnt,DATE_FORMAT(dt,'%d %b %Y') as mth,uid,customer_tx,DATE_FORMAT(`acquirer_dtm`,'%d-%m-%Y %H:%i') AS acquirer_dtm,pan,bankresponse \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                  " and id = (SELECT MAX(id) AS id\n" +
"  FROM wap_point_realtime_txns rt WHERE rt.`customer_id`=?) and r.sts in (1,2) and updt=1\n"+
                   " and r.sts in (1,2) and updt=1";

            try (Connection conprv = getConnection(); 
                PreparedStatement psprv = conprv.prepareStatement(sqlprv);) {
                
                
                psprv.setInt(1, customer_id);
                    psprv.setInt(2, customer_id);
                System.out.println(psprv);
                   try (ResultSet rsprv = psprv.executeQuery();) {
                while(rsprv.next()) {
              
                             lastTxdt=rsprv.getString("acquirer_dtm");
                    lastAmnt="R "+rsprv.getString("tx_amnt");
                    lastUID=rsprv.getString("uid");
                    
                } 
                   }
                   
            }catch (SQLException e) {
            //throw new MyException("could not get details1234",e,showDebug);
                
                
            }
}
            // get_txns_appid(prvdt.format(formatter1),currentdt.format(formatter),ckey,appID); 
      
         get_txns_appid(currentdt.format(formatter),currentdt.format(formatter),ckey,appID); 
          
        String sqlpid = "SELECT MAX(id) AS pid FROM wap_point_realtime_txns WHERE updt=0 and customer_id=?";

            try (Connection con = getConnection(); 
                PreparedStatement ps12 = con.prepareStatement(sqlpid);) {
                
                
                ps12.setInt(1, customer_id);
                   try (ResultSet rs12 = ps12.executeQuery();) {
                while(rs12.next()) {
                    pid=rs12.getInt("pid");
                    if(pid>0)
                   check=1;
                            String sql2 = "update wap_point_realtime_txns set updt=1 where updt='0' and customer_id=?";
                          //  System.out.println(sql2);
                            
                    PreparedStatement ps2 = con.prepareStatement(sql2);
                      ps2.setInt(1, customer_id);
                 ps2.executeUpdate();
                    
                } 
                   }
                   
            }catch (SQLException e) {
            //throw new MyException("could not get details1234",e,showDebug);
                
                
            }
        
            
           
            
            
            
            
            
        String slip = "";
      
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
             String sql1="";       
        if(check==1)    {        
       sql1 = "select round(tx_amnt,2) as tx_amnt,round(settled_amnt,2) as settled_amnt,DATE_FORMAT(dt,'%d %b %Y') as mth,uid,customer_tx,DATE_FORMAT(`acquirer_dtm`,'%d-%m-%Y %H:%i') AS acquirer_dtm,pan,bankresponse \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and id = ?\n"+
                   " and r.sts in (1,2)";
        }
        else{
                  String sqlpid1 = "SELECT MAX(id) AS pid FROM wap_point_realtime_txns WHERE updt=1 and customer_id=?";

            try (Connection con = getConnection(); 
                PreparedStatement ps12 = con.prepareStatement(sqlpid1);) {
                
                
                ps12.setInt(1, customer_id);
                   try (ResultSet rs12 = ps12.executeQuery();) {
                while(rs12.next()) {
                    pid=rs12.getInt("pid");
                   
                    
                } 
                   }
                   
            }catch (SQLException e) {
            //throw new MyException("could not get details1234",e,showDebug);
                
                
            }
              sql1 = "select round(tx_amnt,2) as tx_amnt,round(settled_amnt,2) as settled_amnt,DATE_FORMAT(dt,'%d %b %Y') as mth,uid,customer_tx,DATE_FORMAT(`acquirer_dtm`,'%d-%m-%Y %H:%i') AS acquirer_dtm,pan,bankresponse \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and id = ?\n"+
                   " and r.sts in (1,2)";
        }
            try (Connection con = getConnection(); 
                PreparedStatement ps1 = con.prepareStatement(sql1);) {
                
                
                ps1.setInt(1, customer_id);
                 ps1.setInt(2, pid);
                //  ps1.setInt(3, 1);
                   try (ResultSet rs1 = ps1.executeQuery();) {
                while(rs1.next()) {
                   // pid=rs1.getString("pid");
                 
                   
                /* 
                   
                   lastTxdt=rs1.getString("acquirer_dtm");
                    lastAmnt="R "+rs1.getString("tx_amnt");
                    lastUID=rs1.getString("uid");
                      */
                    
                    slip += "2"+rs1.getString("bankresponse")+":R"+rs1.getString("tx_amnt")+ "\n";
                             slip += "1\n";
                    
                    slip1 += "2"+rs1.getString("bankresponse")+":R"+rs1.getString("tx_amnt")+ "\n";
                             slip1 += "1\n";
                             
                             
                             
                           slip += "1Tx. Date:" +rs1.getString("acquirer_dtm")+"\n";       
                             
                          slip1 += "1Tx. Date:" +rs1.getString("acquirer_dtm")+"\n";    
                             
                              slip += "1PAN:"+rs1.getString("pan")+ "\n";
                               slip1 += "1\n";
                              slip1 += "1PAN:"+rs1.getString("pan")+ "\n";
                                slip1 += "1\n";
                               slip1 += "1AUTHORIZED BY PIN\n";
                                slip1 += "1NO SIGNATURE REQUIRED\n";
                                  slip1 += "1\n";
                                  
                              slip += "1UID:"+rs1.getString("uid")+ "\n";
                              slip1 += "1UID:"+rs1.getString("uid")+ "\n";
                    if(rs1.getString("bankresponse").equals("Approved")){
                              slip += "1Txn. No.:"+rs1.getString("customer_tx")+ "\n";
                              slip1 += "1Txn. No.:"+rs1.getString("customer_tx")+ "\n";
                }
                              
                String sql2 = "update wap_point_realtime_txns set updt=1 where updt='0' and customer_id=?";        
                 PreparedStatement ps2 = con.prepareStatement(sql2);
                 ps2.setInt(1, customer_id);
                 ps2.executeUpdate();
                              
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
        
        
        if(check==0){
           slip1="";
           slip="";
            
        }
        
        
       // System.out.print(prvdt.format(formatter1));
       //  System.out.print(currentdt.format(formatter));
       
      
       
       
       
   


          
               //System.out.print(data);
        
                    return "{ \"pid\": \"" + check+ "\",\"stDate\": \"" + prvdt.format(formatter1) +"\",\"endDate\": \"" + currentdt.format(formatter) +"\",\"appID\": \"" + appID + "\",\"ckey\": \"" + ckey + "\",\"lastUID\": \"" + lastUID + "\",\"lastAmnt\": \"" + lastAmnt + "\",\"lastTxdt\": \"" + lastTxdt + "\",\"print_data\": \"" + slip + "\",\"print_data1\": \"" + slip1 +"\" }";

           
            
        
    }
    

    
    
        @GET
    @Path("get-iveri-txns")
    @Produces({"application/json"})
    public String get_iveri_txns( @QueryParam("currDate") String currDate,@DefaultValue("NA") @QueryParam("posusername") String posusername) {
        String res="";
        
        random=randomString(10);
        
        int customer_id;
     String sql1;
     if(posusername.equals("NA"))
       sql1 = "SELECT customer_id,pos_ckey,app_id,pos_ckey FROM customer_extra WHERE realtime_settle=1";
      else
      sql1 = "SELECT customer_id,pos_ckey,app_id,pos_ckey FROM customer_extra WHERE (pos_ckey='" + String.valueOf(posusername) +"' or pos_username='" + String.valueOf(posusername) +"') and realtime_settle=1";   
DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
System.out.print(sql1);
            try (Connection con = getConnection(); 
                PreparedStatement ps1 = con.prepareStatement(sql1);) {
         
                   try (ResultSet rs1 = ps1.executeQuery();) {
                while(rs1.next()) {
                    customer_id=rs1.getInt("customer_id");
                      appID=rs1.getString("app_id");
                        ckey=rs1.getString("pos_ckey");
               System.out.print(appID);
    
               
               
                try {
    
            
			 //URL url = new URL("https://portal.nedsecure.co.za/api/merchant/authenticate");
URL url = new URL("https://portal.nedsecure.co.za/api/transactions?applicationid={"+appID+"}");
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

                     //data=data+output;
                   data=output;
                         // System.out.println("data"+data);
                   //   JSONObject obj = new JSONObject(data);
                   
                   
                    JSONArray marr = new JSONArray(output);
  // System.out.println(obj.get("Transaction"));
  
  System.out.println("len="+marr.length());
  
  
   

        
       int cnt=marr.length();
  int j=0;
  
  if(cnt>10)
    j=cnt-10;
/*
  */
  
  
for(int i = j; i < cnt; i++)
 //for(int i = cnt-1; i >= (cnt-10); i--)
  {
    System.out.println("length="+i);
    JSONObject arr = marr.getJSONObject(i).getJSONObject("Transaction"); // notice that `"posts": [...]`.getJSONObject("Transaction")
    JSONObject arr1=arr.getJSONObject("Result");
    /*
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
                ps.setString(14, ExpiryMonth+ExpiryYear);*/
    UniqueID = arr.getString("AuthorisationCode");
    Terminal = arr.getString("Terminal");
    
    TransactionDate = arr.getString("AcquirerDate");
   // System.out.println("currDate"+currDate);

//System.out.println("TransactionDate"+TransactionDate);
    String year        = TransactionDate.substring(0,4);
String month       = TransactionDate.substring(4,6);
String day         = TransactionDate.substring(6,8);

 String hour=arr.getString("AcquirerTime").substring(0,2);
 String min=arr.getString("AcquirerTime").substring(2,4);
 String sec=arr.getString("AcquirerTime").substring(4,6);
 
 
    TransactionDate = year+"-"+month+"-"+day+" "+hour+":"+min+":"+sec;
    CardType = get_card_type(arr.getString("BIN"));
    System.out.println(CardType);
     Amount = arr.getString("DisplayAmount");
     Amount=Amount.substring(2);
  BatchNumber = "";
  CycleNumber = "";
  if((arr1.get("Description").toString()).equals("Successful"))
  BankResponse = "Approved";
  else
  BankResponse = "Declined";
 // BankResponse = arr1.get("Description").toString();
  
   BatchNumber = "";
  BankReference = arr.getString("ReconReference");
Integer BankReferenceint = arr.getInt("ReconReference");
       OperatorID = arr.getString("MerchantUSN");
  UserReference = arr.getString("MerchantReference");
  CardNumber = arr.getString("PAN")+" ("+arr.getString("BIN")+")";
  ExpiryMonth = arr.getString("ExpiryDate");
 ExpiryYear="";
  
//  System.out.println("BankResponse="+BankResponse);
 // System.out.println("TransactionDate="+TransactionDate);     
//System.out.println("BankReference="+BankReference);
  int checkcnt=0;
        String sqlcheck = "select count(bankreference) as crosscnt \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where (r.bankreference = ? or r.bankreference = ?)";
          
            try (Connection con1 = getConnection(); 
                PreparedStatement pscheck = con1.prepareStatement(sqlcheck);) {
                
              
                pscheck.setString(1, BankReference);
          pscheck.setString(2, BankReferenceint.toString());
            System.out.println("sqlcheck="+pscheck);
                   try (ResultSet rscheck = pscheck.executeQuery();) {
                while(rscheck.next()) {
                 checkcnt=rscheck.getInt("crosscnt");
                  
                }
                   }
            }catch (SQLException e) {
            throw new MyException("Duplicate Txns.",e,showDebug);
           
         } 
        
       
        
        if(checkcnt==0){
        System.out.print("Not Duplicate="+BankReference);
        String sql2 = "insert into wap_txns_updated (pid, cnt,api_sdt,api_edt,json_response,ckey,st_dtm) values ( ?, ?,?,?,?,?,now()) ";
System.out.println(sql2);
        try (Connection con2 = getConnection();
            PreparedStatement ps = con2.prepareStatement(sql2,Statement.RETURN_GENERATED_KEYS);) 
        {
            
            
        ps.setString(1, random);
        ps.setInt(2, marr.length());
        ps.setString(3, currDate);
        ps.setString(4, currDate);
        ps.setString(5, arr.toString());
        ps.setString(6, ckey);
        ps.executeUpdate();
               ResultSet rs = ps.getGeneratedKeys();
if (rs.next()) {
    pidiveri = rs.getInt(1);
}

            } catch (SQLException e) {

            }
        updateDB();
        
        sql1 = "UPDATE wap_txns_updated SET end_dtm=NOW() WHERE id=?";
       
      //  String sql = "SELECT cnt FROM wap_txns_updated WHERE api_sdt=? AND api_edt=?";
        try (Connection con3 = getConnection();
            PreparedStatement ps = con3.prepareStatement(sql1);) 
            
        {
               ps.setInt(1, pidiveri);
           // ps.setString(2, edt1);
          // System.out.print(ps);
           ps.executeUpdate();
        } catch (SQLException e) {
            throw new MyException("Unable to update",e,showDebug);
        }
        
        }else{
              System.out.print("Duplicate="+BankReference);
        }

  
  }  
  
 
      //  JSONArray obj1 = obj.getJSONArray("Transaction");
        
//if (obj instanceof JSONObject)
//iveri(obj);
				//System.out.println(obj.getJSONArray("Transaction"));
			}
			     
			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
			
		}   
               
               
               
               
               
               
               
               
                        
                }
                   }
            }catch (SQLException e) {
            //throw new MyException("could not get details1234",e,showDebug);
           
         } 
        
        
        
        
        
        
        
        
        return data;
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
    
    
    
    @GET
    @Path("get-txns-appid")
    @Produces({"application/json"})
    public String get_txns_appid( @DefaultValue("2021-07-12") @QueryParam("sdt") String sdt,@DefaultValue("2021-07-12") @QueryParam("edt") String edt,@DefaultValue("2021-07-12") @QueryParam("ckey") String ckey,@DefaultValue("2021-07-12") @QueryParam("appid") String appid) {
  String result="";

  if(ckey.equals("WG6124") || ckey.equals("WG6593") || ckey.equals("WG6660")){
      get_iveri_txns(sdt,ckey);
  }else{
   generateTokenAppid();
   waplivetxAppid(sdt,edt,ckey,appid);
  }
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
    private void generateTokenAppid(){
        
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
       //check for dupllcates if no call procedure
        
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
    System.out.println(CardType);
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
              //  ps.setString(15, JsonResponse);
                try (ResultSet rs = ps.executeQuery();) {
                    while (rs.next()) {
                  //int id = rs.getInt("response");
               
                    }
                }
          
            } catch (SQLException e) {
                throw new MyException("Realtime Insert Failed!!!", e, showDebug);
            }
           
      
            
                 // System.out.print(result);
    }
    private void waplivetx(@QueryParam("sdt") String sdta,@QueryParam("edt") String edta){
        
    random=randomString(10);
    sdt=sdta;edt=edta;
        try {
     sdt1=sdt;
             edt1=edt;
            
            sdt=sdt.replaceAll(" ", "%20");
             edt=edt.replaceAll(" ", "%20");
            
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
       private void waplivetxAppid(@QueryParam("sdt") String sdta,@QueryParam("edt") String edta,@QueryParam("ckey") String ckey,@QueryParam("appid") String appid){
        
    random=randomString(10);
    sdt=sdta;edt=edta;
        try {
     sdt1=sdt;
             edt1=edt;
            
            sdt=sdt.replaceAll(" ", "%20");
             edt=edt.replaceAll(" ", "%20");
            
			 //URL url = new URL("https://portal.nedsecure.co.za/api/merchant/authenticate");
URL url = new URL("http://77.68.15.76:3400/api/Transactions/"+sdt+"/"+edt+"/"+ckey+"/"+appid+"/getrecenttransactions?access_token="+token);
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
                          JsonResponse=output;
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
        /*cooment here */
   //System.out.println("ckey="+ckey);
              System.out.println("Live Txn's Count="+arr.length());
             int cnt = 0; 
      // String sql = "SELECT cnt FROM wap_txns_updated WHERE id = ( SELECT MAX(id) FROM wap_txns_updated where ckey=?) and ckey=?";
       // System.out.println("ckey="+sql);
        String sql = "SELECT cnt FROM wap_txns_updated WHERE api_sdt=? AND api_edt=? and ckey=?";
        try (Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql);) 
            
        {
            ps.setString(1, sdt1);
           ps.setString(2, edt1);
           ps.setString(3, ckey);
        //   System.out.println("ps="+ps);
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
              
   /*    */      
              
        for(int j=0;j<arr.length(); j++){
/* */
      if(j==0){

        String sql1 = "insert into wap_txns_updated (pid, cnt,api_sdt,api_edt,json_response,ckey,st_dtm) values ( ?, ?,?,?,?,?,now()) ";
System.out.println(sql1);
        try (Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql1,Statement.RETURN_GENERATED_KEYS);) 
        {
            
            
            ps.setString(1, random);
              ps.setInt(2, count);
    ps.setString(3, sdt1);
     ps.setString(4, edt1);
          ps.setString(5, JsonResponse);
               ps.setString(6, ckey);
                ps.executeUpdate();

               ResultSet rs = ps.getGeneratedKeys();
if (rs.next()) {
    pid = rs.getInt(1);
}

            } catch (SQLException e) {

            }
       
      }
        System.out.println("j="+j);
        System.out.println("count="+count);
      if((j+1)==count){
        
    
          
          
      String sql1 = "UPDATE wap_txns_updated SET end_dtm=NOW() WHERE id=?";
       
      //  String sql = "SELECT cnt FROM wap_txns_updated WHERE api_sdt=? AND api_edt=?";
        try (Connection con = getConnection();
            PreparedStatement ps = con.prepareStatement(sql1);) 
            
        {
               ps.setInt(1, pid);
           // ps.setString(2, edt1);
          // System.out.print(ps);
           ps.executeUpdate();
        } catch (SQLException e) {
            throw new MyException("Unable to update",e,showDebug);
        }
          
          
          
      }
       
   /* */
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
            System.out.println("Terminal Vallllllllllll->"+Terminal);  
        if(Terminal.startsWith("D")){
          String[] parts=Terminal.split(":");
         // System.out.print(parts[2]);
          Terminal=parts[2];
      }    
     System.out.println("Terminal Valsssssssssssssssssssss->"+Terminal);  
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
//if(Terminal.equals("wpp1954") ||  Terminal.equals("WPP1954")){
System.out.println(j+"="+BankReference);
  int checkcnt=0;
        String sqlcheck = "select count(bankreference) as crosscnt \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.bankreference = ?\n";
        
            try (Connection con = getConnection(); 
                PreparedStatement pscheck = con.prepareStatement(sqlcheck);) {
                
                
                pscheck.setString(1, BankReference);
          
                   try (ResultSet rscheck = pscheck.executeQuery();) {
                while(rscheck.next()) {
                 checkcnt=rscheck.getInt("crosscnt");
                  
                }
                   }
            }catch (SQLException e) {
            throw new MyException("Duplicate Txns.",e,showDebug);
           
         } 
        
       
        
        if(checkcnt==0){
           System.out.print("Not Duplicate="+BankReference);
        updateDB();
        }else{
              System.out.print("Duplicate="+BankReference);
        }
        
//}
    

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
    
    
    /****************
     * 
     * Reprint Section
     * 
     */
    
    private String getVoucherActivationMessage(int service_provider_id) {

        String sql = "select terminal_message from service_provider where service_provider_id = ?;";

        String terminal_massage = "";
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setInt(1, service_provider_id);

            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    terminal_massage = rs.getString("terminal_message");
                }
            }
        } catch (SQLException e) {
            //
        }

        return terminal_massage;

    }
    public String getXMLValue(String path, String xml) {
        try {
            return XPathFactory
                    .newInstance()
                    .newXPath()
                    .evaluate(path, new InputSource(
                            new StringReader(xml)));
        } catch (XPathExpressionException ex) {
            //errorMsg = ex.getMessage();
            return ex.getMessage();
        }
    }
     private String str_wrap(String wrapText, int wrapLength, boolean center, String strSize) {
        String[] words = wrapText.split(" ");

        String newSentence = "";

        String line = "";
        for (String word : words) {

            if ((line + word).length() > wrapLength) {

                if (center) {
                    line = line.trim();
                    int amountToPad = wrapLength - line.length();
                    if (amountToPad > 0) {
                        amountToPad = amountToPad / 2;
                    }
                    line = StringUtils.leftPad(line, line.length() + amountToPad, ' ');
                }

                //newSentence.AppendLine(line,'\n');
                newSentence = newSentence + strSize + line + "\n";
                line = "";
            }

            line += word + " ";
        }

        if (line.trim().length() > 0) {
            if (center) {
                line = line.trim();
                int amountToPad = wrapLength - line.length();
                if (amountToPad > 0) {
                    amountToPad = amountToPad / 2;
                }
                line = StringUtils.leftPad(line, line.length() + amountToPad, ' ');
                //newSentence.AppendLine(line, "\n");
                newSentence = newSentence + strSize + line.trim() + "\n";
            } else {
                newSentence = newSentence + strSize + line.trim() + "\n";
            }
        }

        return newSentence;

    }
      @GET
    @Path("get-voucher-reprint-json")
    @Produces({"application/json", "application/xml;"})
    public String get_voucher_reprint_json(
            @QueryParam("uid") int stock_uid
    ) {

        String result = "";
        int service_provider_id = 0;

        if (String.valueOf(stock_uid).length() == 0) {
            throw new MyException("Incorrect data request. Aborting.");
        }

        java.util.Date now = new java.util.Date();
        String strDate = String.format("%1$td/%1$tm/%1$tY", now);
        String strTime = String.format("%1$tH:%1$tM:%1$tS", now);

        int service_provider_item_id = 0;
        String pin_number = "";
        String slip;

        slip = "2" + company_name + "\n";
        slip += "1\n";

        String sql = "select s.*, 0 as sale_count, curdate() as tx_date from stock s where s.stock_uid = ?;";
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setInt(1, stock_uid);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {

                    pin_number = rs.getString("pin_number");
                    slip += "1REPRINT\n";
                    slip += "1Trx   # " + String.valueOf(rs.getInt("stock_uid")) + "\n";
                    slip += "1Serial# " + rs.getString("serial_number") + "\n";
                    slip += "1Date       Time     POS User \n";
                    slip += "1" + strDate + " " + strTime + " " + posuser_name + "\n";

                    result += "\"uid\":\"" + rs.getInt("stock_uid") + "\",";
                    result += "\"p\":\"" + rs.getString("pin_number") + "\",";
                    result += "\"s\":\"" + rs.getString("serial_number") + "\",";
                    result += "\"sp\":\"" + rs.getInt("service_provider_id") + "\",";
                    result += "\"spi\":\"" + rs.getInt("service_provider_item_id") + "\",";
                    result += "\"n\":\"" + rs.getInt("sale_count") + "\",";
                    result += "\"dtm\":\"" + rs.getString("tx_date") + "\",";

                    service_provider_item_id = rs.getInt("service_provider_item_id");

                    result += "\"balance\":\"" + new DecimalFormat("0.00").format(balance) + "\",";
                    result += "\"balance_cash\":\"" + new DecimalFormat("0.00").format(balance_cash) + "\",";
                    result += "\"available_balance\":\"" + new DecimalFormat("0.00").format(available_balance) + "\",";
                    result += "\"credit_limit\":\"" + new DecimalFormat("0.00").format(credit_limit) + "\",";
                    result += "\"credit_extended\":\"" + new DecimalFormat("0.00").format(credit_extended) + "\",";
                    result += "\"low_balance\":\"" + low_balance + "\",";
                    result += "\"dtmd\":\"" + strDate + "\",";
                    result += "\"dtmt\":\"" + strTime + "\",";

                    service_provider_id = rs.getInt("service_provider_id");

                    if (rs.getInt("service_provider_id") == 15) {
                        result += "\"xtra1\"=\"Instructions:|1. Go to https://www.siyavula.com|2. If you have an account, Sign In.|3. If you don't have, Sign Up.|4. Go to 'Voucher code'|5. Enter voucher code as payment\",";
                        result += "\"xtra2\"=\"For help call 021 469 4771\",";
                    }

                    if (rs.getInt("service_provider_id") == 17) {
                        result += "\"^xtra1\"=\"Talk360 customers can redeem a|purchased Top it Up vouchers to|receive Talk360 airtime in-app:|1. Head to More - Redeem Voucher|2. Enter the voucher PIN|3. Select 'redeem voucher'|The pre-paid airtime via|Top it Up will be added directly|to the Talk360 account.\",";
                        result += "\"^xtra2\"=\"\",";
                    }

                }
            }

        } catch (SQLException e) {
            throw new MyException("this voucher is currently unavailable", e, showDebug);
        }

        String activationMessage = getVoucherActivationMessage(service_provider_id);

        if (service_provider_item_id == 256) {

            sql = "select xml_send from airtime_international where stock_uid =  " + String.valueOf(stock_uid) + ";";
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(sql);) {
                try (ResultSet rs = ps.executeQuery();) {
                    while (rs.next()) {

                        String xml_send = rs.getString("xml_send");

                        String tokenType = getXMLValue("/ipayMsg/cellMsg/vendReq/tokenType", xml_send);
                        String network = getXMLValue("/ipayMsg/cellMsg/vendReq/network", xml_send);

                        slip += "1\n";
                        slip += "1" + network + "\n";
                        slip += "1" + tokenType + "\n";

                    }
                }
            } catch (SQLException e) {
                //
            }

        } else {
            sql = "select item_description from service_provider_item where service_provider_item_id = " + String.valueOf(service_provider_item_id) + ";";
            try (Connection con = getConnection();
                    PreparedStatement ps = con.prepareStatement(sql);) {
                try (ResultSet rs = ps.executeQuery();) {
                    while (rs.next()) {

                        slip += "1\n";
                        slip += str_wrap(rs.getString("item_description"), 32, true, "2");

                    }
                }
            } catch (SQLException e) {
                //
            }
        }

        slip += "1\n";

        if (service_provider_item_id == 256) {
            //
        } else {
            
            String decryptedPin = za.co.topitup.wappoint.utilities.Blowfish.decryptBlowfish(pin_number);

            //slip +=  str_format_pin(decryptedPin.replaceAll("[^\\x20-\\x7e]", ""));
            String pin_formatted = "";
            for (String part : getParts(decryptedPin.replaceAll("[^\\x20-\\x7e]", ""), 4)) {
                pin_formatted += " " + part;
            }

            slip += str_wrap(pin_formatted, 32, true, "2") + "\n";

        }

        slip += "1\n";
        
        if (service_provider_item_id == 256) {
            //
        } else {
            slip += "1" + activationMessage + "\n";
        }
        

        slip += "1\n";
        slip += "1         Top it Up\n";
        slip += "1     www.topitup.co.za\n";
        slip += "1\n";

        return "{ " + result + "\"print_data\": \"" + slip + "\" }";

    }
    
    
      @GET
    @Path("reprint-voucher")
    @Produces({"application/json", "application/xml;"})
    public String reprint_voucher(@QueryParam("t") int reprint_type, @QueryParam("s") String param) {

        String ret = "";
        int stock_uid = 0;

        int service_provider_item_id = 0;
        String stock_sold_date = "";

        String sql = "call terminal_reprint(?,?,?,?,?);";
        try (Connection con = getConnection();
                PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setInt(1, license_id);
            ps.setInt(2, customer_id);
            ps.setInt(3, posuser_id);
            ps.setInt(4, reprint_type);
            ps.setString(5, param);
            try (ResultSet rs = ps.executeQuery();) {
                while (rs.next()) {
                    stock_uid = rs.getInt("stock_uid");

                    ret = "uid=" + rs.getInt("stock_uid");
                    ret += "^p=" + rs.getString("pin_number");
                    ret += "^s=" + rs.getString("serial_number");
                    ret += "^l=" + low_balance;
                    ret += "^b=" + available_balance;
                    ret += "^sp=" + rs.getInt("service_provider_id");
                    ret += "^spi=" + rs.getInt("service_provider_item_id");
                    ret += "^c=" + rs.getInt("reprint_count");
                    ret += "^t=" + rs.getString("reprint_dtm");
                    ret += "^dtm=" + rs.getString("tx_date");

                    String cat = "";
                    if (rs.getInt("service_provider_item_id") == 163 || rs.getInt("service_provider_item_id") == 107) {
                        cat = "^cat=7";
                    } else if (rs.getInt("service_provider_item_id") == 51) {
                        cat = "^cat=2";
                        //} else if (rs.getInt("service_provider_item_id") == 256) {
                        //     cat = "^cat=9";
                    } else {
                        cat = "^cat=1";
                    }
                    //if (rs.getInt("service_provider_id") == 15) cat = "^cat=9";

                    ret += cat;

                    service_provider_item_id = rs.getInt("service_provider_item_id");
                    stock_sold_date = rs.getString("tx_date");

                }
            }
            if (stock_uid == 0) {
                String err_desc = "Could not find Voucher for Reprint";
                if (reprint_type == 3) {
                    err_desc = "No previous voucher has been sold by this user.";
                }

                throw new MyException(err_desc);
            }
        } catch (SQLException e) {
            throw new MyException("Error Fetching Voucher For Reprint", e, showDebug);
        }

        try {
            if (stock_sold_date == null && service_provider_item_id == 51) {
                try (Connection con = getConnection();
                        PreparedStatement ps = con.prepareStatement("call ConfirmElectricity(?)");) {
                    ps.setInt(1, stock_uid);
                    try (ResultSet rs = ps.executeQuery();) {
                        while (rs.next()) {
                            //
                        }
                    }
                } catch (SQLException e) {
                    throw new MyException("This voucher is currently unavailable for reprint", e, showDebug);
                }

            }
        } catch (Exception e) {
            //
        }

        return ret;

    }
    
    
    @GET
    @Path("reprint-swipe")
    @Produces({"application/json", "application/xml;"})
    public String reprint_swipe(@QueryParam("t") int reprint_type, @QueryParam("pid") int pid) {

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
                    
        String sql1 = "select round(tx_amnt,2) as tx_amnt,round(settled_amnt,2) as settled_amnt,DATE_FORMAT(dt,'%d %b %Y') as mth,uid,customer_tx,DATE_FORMAT(`acquirer_dtm`,'%d-%m-%Y %H:%i') AS acquirer_dtm,pan,bankresponse \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and id = ?\n";

            try (Connection con = getConnection(); 
                PreparedStatement ps1 = con.prepareStatement(sql1);) {
                
                
                ps1.setInt(1, customer_id);
                 ps1.setInt(2, pid);
            
                   try (ResultSet rs1 = ps1.executeQuery();) {
                while(rs1.next()) {
                   // pid=rs1.getString("pid");
                    
                    slip += "2"+rs1.getString("bankresponse")+":R"+rs1.getString("tx_amnt")+ "\n";
                             slip += "1\n";
                             
                         slip1 += "2"+rs1.getString("bankresponse")+":R"+rs1.getString("tx_amnt")+ "\n";
                             slip1 += "1\n";
                             
                             
                           slip += "1Tx. Date:" +rs1.getString("acquirer_dtm")+"\n";       
                             
                          slip1 += "1Tx. Date:" +rs1.getString("acquirer_dtm")+"\n";    
                             
                              slip += "1PAN:"+rs1.getString("pan")+ "\n";
                               slip1 += "1\n";
                              slip1 += "1PAN:"+rs1.getString("pan")+ "\n";
                                slip1 += "1\n";
                               slip1 += "1AUTHOROZED BY PIN\n";
                                slip1 += "1NO SIGNATURE REQUIRED\n";
                                  slip1 += "1\n";
                              slip += "1UID:"+rs1.getString("uid")+ "\n";
                              if(rs1.getString("bankresponse").equals("Approved"))
                              slip += "1Txn. No.:"+rs1.getString("customer_tx")+ "\n";
                                     slip1 += "1UID:"+rs1.getString("uid")+ "\n";
                              if(rs1.getString("bankresponse").equals("Approved"))
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
    @Path("get-store-info")
    @Produces({"application/json", "application/xml;"})
    public String get_store_info(@QueryParam("accno") String accno,@QueryParam("passcode") String passcode) {
        
        String result = null;
        String passwrd;
        String country;
         String store_acc_no;
  
        // licenseCode="DEMO99c4-1999-11e9-84ad-001e6779cd30";
          
        String sql1 = "select user_name,passwrd,store_id,country,store_acc_no \n" +
                        " from tbl_retailer_app_users r\n" +
                        " where store_acc_no = ?"+
                    " and passwrd = ?\n";

            try (Connection con = getConnection(); 
                PreparedStatement ps12 = con.prepareStatement(sql1);) {
                ps12.setString(1, accno);
                ps12.setString(2, passcode);
                
                   try (ResultSet rs12 = ps12.executeQuery();) {
                while(rs12.next()) {
                     System.out.print(rs12);
                    passwrd=rs12.getString("passwrd");
                    country=rs12.getString("country");
                    username=rs12.getString("user_name");
                    store_acc_no=rs12.getString("store_acc_no");
                     customer_id=rs12.getInt("store_id");
                     
                    
          String sq12 = "SELECT license_code FROM license  WHERE customer_id='" + String.valueOf(customer_id) + "' AND enabled=1 AND license_type_id=1 LIMIT 0,1";
            try (Connection con1 = getConnection();
                    PreparedStatement ps = con1.prepareStatement(sq12);) {
                try (ResultSet rs = ps.executeQuery();) {
                    while (rs.next()) {

                     licenseCode=rs.getString("license_code");

                    }
                }
            } catch (SQLException e) {
                //
            } 
                                
          String sq13 = "SELECT * FROM posuser t1 WHERE t1.`customer_id`='" + String.valueOf(customer_id) + "' AND t1.`posuser_isadmin`=1 AND t1.`posuser_status`=1";
            try (Connection con1 = getConnection();
                    PreparedStatement ps = con1.prepareStatement(sq13);) {
                try (ResultSet rs = ps.executeQuery();) {
                    while (rs.next()) {

                     posuser_id=rs.getInt("posuser_id");

                    }
                }
            } catch (SQLException e) {
                //
            }            
                     
            result= "{ \"store_acc_no\": \"" + store_acc_no+ "\",\"posuser_id\": \"" + posuser_id + "\",\"license\": \"" + licenseCode + "\",\"username\": \"" + username + "\",\"passwrd\": \"" + passwrd + "\",\"country\": \"" + country +"\" }";

            
      
                     
                } 
                   }
                   
            }catch (SQLException e) {
            throw new MyException("could not get details",e,showDebug);
                
                
            }
            
    return result;   
            
           
    }
            
            
            
 
    
    
    
    
    
    
     @GET
    @Path("get-last-sales-filter")
    @Produces({"application/json","application/xml;"})
    public String get_last_sales_filter(
            @DefaultValue("0") @QueryParam("q") int is_admin,
            @DefaultValue("0") @QueryParam("t") int filter_type,
            @DefaultValue("") @QueryParam("f") String filter_value
            )
    {
        
        String result = "";
        String prov_desc = "";
        //filter_type = 0 - all
        //filter_type = 1 - airtime
        //filter_type = 2 - electricity
        //filter_type = 7 - bills

        //filter_type = 100 - swipecard
        int stock_uid = 0;
        
//        try {
//           // if (filter_value.length() > 0)
//            //{
//               stock_uid = Integer.parseInt(filter_value);
//            //}
//        } catch() {
//                //
//        }

        if(filter_type==100){
            
            
            
              String sql_main = "select id as stock_uid,tx_amnt,settled_amnt,pan_type,terminal,bankresponse as pname,bankreference, DATE_FORMAT(acquirer_dtm,'%d/%m/%Y, %H:%i') as acquirer_dtm \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?";
               if (filter_value.length() > 0) {
                                    if (isNumeric(filter_value)) {
                                   
                                        sql_main += " and (r.customer_tx = ? or r.uid = ? )";
                                    }
                                } 
                         sql_main += " order by id desc limit 25;";
            
          
                       try (Connection con = getConnection(); 
                PreparedStatement ps = con.prepareStatement(sql_main);) {
           
            ps.setInt(1, customer_id);
        
             if (filter_value.length() > 0) {
                    if (isNumeric(filter_value)) {
                 
                        ps.setString(2, filter_value);
                        ps.setString(3, filter_value);
                        System.out.print(stmtCounter);
                    
                    }
                }
            
             // System.out.print(ps.toString());
            
             
                
            try (ResultSet rs = ps.executeQuery();) {
                while(rs.next()) {
                   
                    
                 /*   String terminal=rs.getString("terminal");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");        
                    LocalDateTime currentdt=LocalDateTime.now();
                    do_auto_recon(currentdt.format(formatter),terminal);
                    */
                    
                   prov_desc="DEBIT CARD";
                    if(rs.getString("pan_type").equals("MPOSDebit"))
                         prov_desc="DEBIT CARD";
                    else  if(rs.getString("pan_type").equals("credit") || rs.getString("pan_type").equals("MPOSVisa"))
                      prov_desc="CREDIT CARD";
                         else  if(rs.getString("pan_type").equals("Foreign Card") || rs.getString("pan_type").equals("foreign"))
                      prov_desc="FOREIGN CARD";
                    
                     result += rs.getInt("stock_uid") + "^";
                      result += rs.getString("acquirer_dtm") + "^";
                result += prov_desc + "^";
                      result += rs.getDouble("tx_amnt") + "^";
                        result += rs.getString("bankreference") + "^";
                        result += rs.getString("pname") + "\n";
                   /* if (rs.getInt("tx_status") == 1 || (rs.getInt("tx_status") == 2 && rs.getInt("stock_category_id") == 2 && rs.getInt("vendor_result") == 1))
                    {
                        prov_desc = rs.getString("service_provider_desc");
                        if (rs.getInt("tx_status") == 2) prov_desc = prov_desc + " (P)";
                        
                        result += rs.getInt("stock_uid") + "^";
                        result += rs.getString("tx_dtm") + "^";
                        result += prov_desc + "^";
                        result += rs.getDouble("stockvalue") + "^";
                        result += rs.getString("serial_number") + "^";
                        result += rs.getString("pname") + "\n";
                    }*/
                }
            }
        } catch (SQLException e) {
            throw new MyException("Could not get sales history!",e,showDebug);
        }  
           
            
            
            
            
            
            
              
        }else{
            
               
            
        String sql_main = "select txd.stock_uid,txd.tx_dtm as sortby, \n" +
                        "	   date_format(txd.tx_dtm,'%d/%m/%Y, %H:%i') as tx_dtm,\n" +
                        "	   sp.service_provider_desc,\n" +
                                    "	   case "
                               + "      when spi.stock_category_id = 2 or spi.stock_category_id = 7 or spi.service_provider_item_id = 256 then round(txd.list_cost,2) "
                                + "  else round(spi.real_value,2) "
                        + "        end as stockvalue,\n" +
                        "	   s.serial_number, \n" +
                        "          concat(pu.posuser_firstname,' ',pu.posuser_surname) as pname \n" +
                        "       ,spi.stock_category_id, txd.tx_status \n";

                if (filter_type == 1) sql_main += ",0 as vendor_result \n";
                if (filter_type == 2) sql_main += ",case when sq.vend_dtm > date_sub(now(), interval 24 hour) then sq.vendor_result else 0 end as vendor_result \n";
                if (filter_type == 7) sql_main += ",0 as vendor_result \n";
                    
                if (filter_type != 1 && filter_type != 2 && filter_type != 7) {
                    sql_main += ",ifnull((select sq.vendor_result from stock_electricity_seq sq where sq.stock_uid = s.stock_uid and sq.vend_dtm > date_sub(now(), interval 24 hour)),0) as vendor_result \n";
                }
                
                sql_main += " from (select s.stock_uid, s.service_provider_item_id, s.serial_number " +
                                " from stock s  " +
                                " where s.stock_sold_to = " + String.valueOf(customer_id) +
                                " and s.stock_uid > 471500000 ";
                                if (filter_value.length() > 0) {
                                    if (isNumeric(filter_value) && (filter_value.length() == 9 || filter_value.length() == 8)) {
                                        sql_main += " and s.stock_uid = ? ";
                                    } else {
                                        sql_main += " and s.serial_number = ? ";
                                    }
                                } 
                
                sql_main += " ) as s ";
                                
                sql_main += " inner join tx_detail txd on txd.stock_uid = s.stock_uid \n" +
                            " inner join service_provider_item spi on spi.service_provider_item_id = s.service_provider_item_id \n" +
                            " inner join service_provider sp on sp.service_provider_id = spi.service_provider_id \n" +
                            " inner join posuser pu on pu.posuser_id = txd.posuser_id  \n";

                if (filter_type == 7) {
                    sql_main += " inner join billpayments_payat bp on bp.stock_uid = s.stock_uid \n";
                }
                if (filter_type == 2) {
                     sql_main += " inner join stock_electricity_seq sq on sq.stock_uid = s.stock_uid \n";
                }
                if (filter_type == 1) {
                     sql_main += " and spi.stock_category_id = 1 \n";
                } 
                
                if (filter_type != 1 && filter_type != 2 && filter_type != 7) {
                    sql_main += " and spi.stock_category_id in (1,2,7) \n";
                }

                // sql_main += " where txd.customer_id = " + String.valueOf(customer_id) + " \n";
                sql_main += " where (1=1) \n";
                
                if (filter_type == 1 || filter_type == 2 || filter_type == 7) {
                    //
                } else {
                    if (filter_value.length() == 0) {
                        sql_main += " and txd.license_id = " + String.valueOf(license_id) + " \n";
                    }
                }
                if (is_admin == 0) sql_main += " and txd.posuser_id = " + String.valueOf(posuser_id) + " ";
                
                sql_main += " order by sortby desc limit 25;";

//result = sql_main;

//if (1==2) {
                
            try (Connection con = getConnection(); 
                PreparedStatement ps = con.prepareStatement(sql_main);) {
                
                if (filter_value.length() > 0) {
                    if (isNumeric(filter_value) && filter_value.length() == 9) {
                        ps.setInt(getStmt(), Integer.parseInt(filter_value));
                    } else {
                        ps.setString(getStmt(), filter_value);
                    }
                }
                
            try (ResultSet rs = ps.executeQuery();) {
                while(rs.next()) {
                    if (rs.getInt("tx_status") == 1 || (rs.getInt("tx_status") == 2 && rs.getInt("stock_category_id") == 2 && rs.getInt("vendor_result") == 1))
                    {
                        prov_desc = rs.getString("service_provider_desc");
                        if (rs.getInt("tx_status") == 2) prov_desc = prov_desc + " (P)";
                        
                        result += rs.getInt("stock_uid") + "^";
                        result += rs.getString("tx_dtm") + "^";
                        result += prov_desc + "^";
                        result += rs.getDouble("stockvalue") + "^";
                        result += rs.getString("serial_number") + "^";
                        result += rs.getString("pname") + "\n";
                    }
                }
            }
        } catch (SQLException e) {
            throw new MyException("Could not get sales history!",e,showDebug);
        }         
            
        }
     
//}    

   return result;
     

        
    }
      private int getStmt() 
    {
        stmtCounter++;
        return stmtCounter;
    }
      private static boolean isNumeric(String str)  
    {  
      try  
      {
        int i = Integer.parseInt(str);  
      }  
      catch(NumberFormatException nfe)  
      {  
        return false;  
      }  
      return true;  
    }
    
    private static List<String> getParts(String string, int partitionSize) {
        List<String> parts = new ArrayList<String>();
        int len = string.length();
        for (int i = 0; i < len; i += partitionSize) {
            parts.add(string.substring(i, Math.min(len, i + partitionSize)));
        }
        return parts;
    }
    
    
    
    @GET
    @Path("do-auto-recon")
    @Produces({"application/json"})
    public void do_auto_recon(@QueryParam("currDate") String currDate,@DefaultValue("NA") @QueryParam("posusername") String posusername) {
     int customer_id;
     String sql1;
     if(posusername.equals("NA"))
       sql1 = "SELECT customer_id,pos_ckey,app_id FROM customer_extra WHERE realtime_settle=1";
      else
      sql1 = "SELECT customer_id,pos_ckey,app_id FROM customer_extra WHERE pos_username='" + String.valueOf(posusername) +"' and realtime_settle=1";   

System.out.print(sql1);
            try (Connection con = getConnection(); 
                PreparedStatement ps1 = con.prepareStatement(sql1);) {
         
                   try (ResultSet rs1 = ps1.executeQuery();) {
                while(rs1.next()) {
                    customer_id=rs1.getInt("customer_id");
                      appID=rs1.getString("app_id");
                        ckey=rs1.getString("pos_ckey");
               System.out.print(customer_id);
                           /* java.util.Date dtmnow = new java.util.Date();


DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

 LocalDateTime currentdt=LocalDateTime.parse(currDate,formatter1);
LocalDateTime prvdt=currentdt.minusMinutes(10);*/
                        get_txns_appid(currDate,currDate,ckey,appID);
                        if(data.contains("TransactionDate")){
                     System.out.print("customer_id="+customer_id);
                     String sql2 = "update wap_point_realtime_txns set updt=1 where updt='0' and customer_id=?";
                          //  System.out.println(sql2); 
                      PreparedStatement ps2 = con.prepareStatement(sql2);
                      ps2.setInt(1, customer_id);
                      ps2.executeUpdate();
                        }
                        
                }
                   }
            }catch (SQLException e) {
            //throw new MyException("could not get details1234",e,showDebug);
           
         } 
    }
    
    
    
        @GET
    @Path("get-card-type")
    @Produces({"application/json"})
    public String get_card_type( @QueryParam("binno") String binno) {
          String cardType="credit";
          
     String sql1 = "SELECT product_type FROM wap_point_binno t1 WHERE t1.`from_bin`>='" + String.valueOf(binno) +"'  AND t1.`to_bin`<='" + String.valueOf(binno) +"'";   
     System.out.println(sql1);      
     try (Connection con = getConnection(); 
                PreparedStatement ps1 = con.prepareStatement(sql1);) {
         
                   try (ResultSet rs1 = ps1.executeQuery();) {
                while(rs1.next()) {
              
                      
                        cardType=rs1.getString("product_type");
        if((cardType.toLowerCase(Locale.ENGLISH).equals("debit"))){
            
            cardType="MPOSDebit";
        }
        else{
            cardType="MPOSVisa";
        }
                        
                }
                   }
            }catch (SQLException e) {
            //throw new MyException("could not get details1234",e,showDebug);
           
         } 
        
        /*
        
      

                try {
URL url = new URL("https://lookup.binlist.net/"+binno+"");
System.out.println(url);
			HttpsURLConnection conn = ((HttpsURLConnection)url.openConnection());
			//conn.setRequestMethod("GET");
			//conn.setRequestProperty("Accept", "application/json");

                        
                        
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
		
			while ((output = br.readLine()) != null) {

                     //data=data+output;
                   data=output;
                         // System.out.println("data"+data);
                   //   JSONObject obj = new JSONObject(data);
                   
                   
                    JSONArray marr = new JSONArray(output);
  // System.out.println(obj.get("Transaction"));
  
  //System.out.println("marr="+marr);
  for(int i = 0; i < marr.length(); i++)
  
  {
    
       
       

  
  }  
  
 
      //  JSONArray obj1 = obj.getJSONArray("Transaction");
        
//if (obj instanceof JSONObject)
//iveri(obj);
				//System.out.println(obj.getJSONArray("Transaction"));
			}
			     
			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
			
		}   
               
               */
        
        return cardType;
    }
    
    
     @GET
    @Path("get-swipe-sales")
    @Produces({"application/json"})
    public String get_swipe_sales(@QueryParam("y") String y,@QueryParam("m") String m,@QueryParam("d") String d) {
    JSONObject jsonObject = new JSONObject();
JSONArray array = new JSONArray();
String sql1;
    if(Integer.valueOf(d)>0){
    sql1 = "select '1' as cnt,settled_amnt, DATE_FORMAT(dt,'%d %b %Y') AS mth \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and YEAR(r.acquirer_dtm) = ?\n"+
                    " and MONTH(r.acquirer_dtm) = ?\n"+
                  " and DAY(r.acquirer_dtm) = ?\n"+
                    " and r.sts = 1";
    }else{
        
          sql1 = "select DAY(r.`acquirer_dtm`),COUNT(*) as cnt,SUM(settled_amnt) as settled_amnt, DATE_FORMAT(dt,'%d %b %Y') AS mth \n" +
                        " from wap_point_realtime_txns r\n" +
                        " where r.customer_id = ?\n"+
                    " and YEAR(r.acquirer_dtm) = ?\n"+
                    " and MONTH(r.acquirer_dtm) = ?\n"+
                    " and r.sts = 1 GROUP BY 1";
        
    }
//    " and DAY(r.dt) = ?\n"+
            try (Connection con = getConnection(); 
                PreparedStatement ps1 = con.prepareStatement(sql1);) {
                
           
                ps1.setInt(1, customer_id);
                ps1.setString(2, y);
                ps1.setString(3, m);
        if(Integer.valueOf(d)>0)
             ps1.setString(4, d);
            
               NumberFormat cf1 = NumberFormat.getCurrencyInstance(new Locale("en", "ZA"));  
         int k=1;     
         System.out.println(ps1);
            try (ResultSet rs1 = ps1.executeQuery();) {
                while(rs1.next()) {
 
                k++;
       //System.out.print("month name"+));
       
       JSONObject response = new JSONObject();
    response.put("date", rs1.getString("mth") );
    response.put("amount", cf1.format((rs1.getFloat("settled_amnt"))) );
    response.put("not", rs1.getString("cnt") );
    //if you are using JSON.simple do this
   // array.add(response);

    //and if you use json-jena
   array.put(response);
       
       
                }
            }
        } catch (SQLException e) {
            throw new MyException("could not get details1234",e,showDebug);
         
         }
    
  //array.put("manoj");
   
   jsonObject.put("data" , array);
    return jsonObject.toString(); 
    }
    
    
    

    }
    
    
    
    
    
    
    
    

