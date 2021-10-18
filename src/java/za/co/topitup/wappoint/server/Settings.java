package za.co.topitup.wappoint.server;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import za.co.topitup.wappoint.services.Api;
@Startup
@Singleton
public class Settings {
 
    private static InetAddress ip;
    private static String _live_ip = "41.203.10.186";
    
    public static boolean IS_DEMO = false;
    
    public static boolean system_paused = false;
    
    public static String ding_balance = "";
    
     @Resource
    TimerService timerService;

    private Date lastProgrammaticTimeout;
    private Date lastAutomaticTimeout;
    
    private static final Logger logger = 
            Logger.getLogger("timersession.ejb.TimerSessionBean");
    
    public void setTimer(long intervalDuration) {
        logger.log(Level.INFO,
                "Setting a programmatic timeout for {0} milliseconds from now.",
                intervalDuration);
        Timer timer = timerService.createTimer(intervalDuration, 
                "Created new programmatic timer");
    }
    
    @Timeout
    public void programmaticTimeout(Timer timer) {
        this.setLastProgrammaticTimeout(new Date());
        logger.info("Programmatic timeout occurred.");
    }

//     @Schedule(second="*",minute="*/4",hour = "*", persistent = false)
//   //@Schedule(minute="*",hour = "*/12", persistent = false)
//    public void automaticTimeout() {
//        this.setLastAutomaticTimeout(new Date());
//       logger.info("<<<<<<<<<<<<<<<<<<<New Session is Created>>>>>>>>>>>>>>>>>>>>>");
//       Api api=new Api();
//       java.util.Date dtmnow = new java.util.Date();
//        String strDate = String.format("%1$tY-%1$tm-%1$td ", dtmnow);
//         //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//           DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//           LocalDateTime currentdt=LocalDateTime.now();
//            LocalDateTime prvdt=currentdt.minusMinutes(2);
//    //api.get_txns(prvdt.format(formatter),currentdt.format(formatter));
// 
//    //api.get_txns(prvdt.format(formatter1),currentdt.format(formatter));
//  
//    api.get_txns(currentdt.format(formatter),currentdt.format(formatter));
// }
//    
  
    @Schedule(dayOfMonth="6",second="0", minute="42", hour="16", persistent = false)
    public void automaticTimeout1() {
        this.setLastAutomaticTimeout(new Date());
        logger.info("<<<<<<<<<<<<<<<<<<<System Initiated2>>>>>>>>>>>>>>>>>>>>>");
        Api api=new Api();
  
    }
     @Schedule(minute="30", hour="00", persistent = false)
    public void automaticTimeout2() throws ParseException {
        this.setLastAutomaticTimeout(new Date());
        logger.info("<<<<<<<<<<<<<<<<<<<System Initiated2>>>>>>>>>>>>>>>>>>>>>");
      java.util.Date dtmnow = new java.util.Date();
String strDate = String.format("%1$tY-%1$tm-%1$td ", dtmnow);
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
LocalDateTime currentdt=LocalDateTime.now();
 LocalDateTime yesterday=currentdt.minusDays(1);
        Api api=new Api();
    api.do_auto_recon(yesterday.format(formatter),"NA");
  
    }
  
//    @Schedule(minute="00", hour="*/3", persistent = false)
//    public void automaticTimeout3() throws ParseException {
//        this.setLastAutomaticTimeout(new Date());
//        logger.info("<<<<<<<<<<<<<<<<<<<System Initiated2>>>>>>>>>>>>>>>>>>>>>");
//      java.util.Date dtmnow = new java.util.Date();
//String strDate = String.format("%1$tY-%1$tm-%1$td ", dtmnow);
//DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//LocalDateTime currentdt=LocalDateTime.now();
// LocalDateTime yesterday=currentdt.minusDays(1);
//        Api api=new Api();
//        api.do_auto_recon(currentdt.format(formatter),"NA");
//  
//    }
    public String getLastProgrammaticTimeout() {
        if (lastProgrammaticTimeout != null) {
            return lastProgrammaticTimeout.toString();
        } else {
            return "never";
        }
    }

    public void setLastProgrammaticTimeout(Date lastTimeout) {
        this.lastProgrammaticTimeout = lastTimeout;
    }

    public String getLastAutomaticTimeout() {
        if (lastAutomaticTimeout != null) {
            return lastAutomaticTimeout.toString();
        } else {
            return "never";
        }
    }

    public void setLastAutomaticTimeout(Date lastAutomaticTimeout) {
   this.lastAutomaticTimeout = lastAutomaticTimeout;
    }
   public static String wappoint_server_url = "https://portal.nedsecure.co.za/api/merchant/authenticate";
   //  public static String wappoint_server_url = "168.142.240.191";
     public static String wappoint_authenticate = "/api/merchant/authenticate";
    @PostConstruct
    void Singletoon()
    {

        System.out.println("Startup Constructor - WAPPOINT");
        
        try {
          ip = InetAddress.getLocalHost();
          System.out.println("Current IP address : " + ip.getHostAddress());
        } catch (Exception e) {
            System.out.println("IP Error: " + e.getMessage());
        }

        if (ip.getHostAddress().equals(_live_ip)) 
        {
            System.out.println("Live Server Settings");
            IS_DEMO = false;
        } else {
            System.out.println("Test Server Settings");
            IS_DEMO = true;
        }

    }
    
    
    public static boolean get_system_status()
    {
        return system_paused;
    }
    
    public static void set_system_paused(boolean state)
    {
        system_paused = state;
    }
    
//    public static String get_ding_balance()
//    {
//        return _ding_balance;
//    }
//     
//    public static void set_ding_balance(String ding_balance)
//    {
//        _ding_balance = ding_balance;
//    }
   

    
}
