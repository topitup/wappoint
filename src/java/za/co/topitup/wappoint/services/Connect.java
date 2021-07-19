package za.co.topitup.wappoint.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.xml.sax.InputSource;

import java.util.zip.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static org.apache.logging.log4j.core.util.NameUtil.md5;
import za.co.topitup.wappoint.server.Base64;

import za.co.topitup.wappoint.server.MyException;
import za.co.topitup.wappoint.server.Resource;
import za.co.topitup.wappoint.server.Settings;

@Path("connect")
public class Connect extends Resource {

    private static final Logger logger = LogManager.getLogger();

    public static String KS_PASSWORD = "p1zz@p1zz@";

    String iPay_ks = "/vs/certnew/keystore.jks";
 
    String CLIENT_ID = "TopItUp";
    String TERM_ID = "TopItUp01";
  int wappoint_port = 80;
    public static int ding_faultChecker = 0;
    private int ding_socket_timeout = 60000;

    protected final String HEADER_LINES = "<ipayMsg client=\"" + CLIENT_ID + "\" term=\"" + TERM_ID + "\" seqNum=\"%s\" time=\"%s\"><cellMsg ver=\"2.20\">";
    protected final String FOOTER_LINES = "</cellMsg></ipayMsg>";

    private Socket socket = null;
    private BufferedWriter bufferedWriter = null;
    private BufferedReader bufferedReader = null;

    public String errorMsg = "";
    
    
    
    

    
    
    
    
    
    
    
    

    public Connect() {


    }

    private Socket getASocket(String ipAddress, int port) {
        SSLSocket skt = null;
        try {

            InetAddress addr = InetAddress.getByName(ipAddress);
            SocketAddress sockaddr = new InetSocketAddress(addr, port);

            KeyStore localKeyStore = KeyStore.getInstance("JKS");
            //KeyStore localKeyStore = KeyStore.getInstance("PKCS12");
            char[] localKeyStorePassword = KS_PASSWORD.toCharArray();
            //localKeyStore.load(new FileInputStream(iPay_ks), localKeyStorePassword);
            localKeyStore.load(new FileInputStream(iPay_ks), localKeyStorePassword);

            //not in pdf doc
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(localKeyStore);

            //pdf doc resumes here
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(localKeyStore, localKeyStorePassword);

            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextInt();  // Force initialisation to occur now.

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom);
            SSLSocketFactory sf = sslContext.getSocketFactory();

            skt = (SSLSocket) sf.createSocket();//create the socket but dont connect yet
            skt.connect(sockaddr, 5000);

            skt.setSoTimeout(ding_socket_timeout);

//        try {
//            System.out.println("Starting SSL handshake...");
//            skt.startHandshake();
//            System.out.println("SSL handshake started...");
//            //socket.close();
//        } catch (Exception e) {
//            System.out.println();
//            e.printStackTrace(System.out);
//        }
            return skt;

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            errorMsg = "ERR:Socket Connection Time Out.";
            closeSocket(skt);
            return null;
        } catch (ConnectException e) {
            e.printStackTrace();
            errorMsg = "CON ERROR " + e.getMessage();
            closeSocket(skt);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = "OTHER ERROR " + e.getMessage();
            //errorMsg = ErrorHandler.submitError(901, e, "setupSocket");
            closeSocket(skt);
            return null;
        }
    }

    private Socket setupSocket(String ipAddress, int port) {

        SSLSocket skt;
        skt = (SSLSocket) getASocket(ipAddress, port);

        return skt;

    }

    protected void closeSocket(Socket skt) {
        try {
            if (skt != null) {
                skt.close();
            }
        } catch (Exception e) {
            System.err.append("closeSocket : " + e.getMessage());
        }
    }

    public byte[] wrap(byte[] msg) throws Exception {
        int len = msg.length;
        if (len > 65535) {
            throw new IllegalArgumentException("Exceeds 65535 bytes.");
        }
        byte firstByte = (byte) (len >>> 8);
        byte secondByte = (byte) len;
        ByteArrayOutputStream baos = new ByteArrayOutputStream(len + 2);
        baos.write(firstByte);
        baos.write(secondByte);
        baos.write(msg);
        return baos.toByteArray();
    }

    public byte[] unWrap(InputStream inputStream) throws Exception {
        int firstByte = inputStream.read();
        if (firstByte == -1) {
            throw new IOException("End of Stream while trying to read vli byte1");
        }
        int firstByteValue = firstByte << 8;
        int secondByteValue = inputStream.read();
        if (secondByteValue == -1) {
            throw new IOException("End of Stream reading vli byte 2.");
        }
        int len = firstByteValue + secondByteValue;
        byte[] message = new byte[len];
        int requestLen;
        int readLen;
        int currentIndex = 0;
        while (true) {
            requestLen = len - currentIndex;
            readLen = inputStream.read(message, currentIndex, requestLen);
            if (readLen == requestLen) {
                break; // Message is complete.
            }
// Either data was not yet available, or End of Stream.
            currentIndex += readLen;
            int nextByte = inputStream.read();
            if (nextByte == -1) {
                throw new IOException("End of Stream at " + currentIndex);
            }
            message[currentIndex++] = (byte) nextByte;
        }
        return message;
    }

    private void sendReq(String req, Socket skt) {

        try {

            DataOutputStream output = new DataOutputStream(skt.getOutputStream());

            if (Settings.IS_DEMO) {

                output.writeUTF(req);

            } else {

                byte[] dataToCompress = req.getBytes("UTF-8");
                byte[] compressedData = null;

                try {
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream(dataToCompress.length);
                    try {
                        GZIPOutputStream zipStream = new GZIPOutputStream(byteStream);
                        try {
                            zipStream.write(dataToCompress);
                        } finally {
                            zipStream.close();
                        }
                    } finally {
                        byteStream.close();
                    }

                    compressedData = byteStream.toByteArray();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                output.write(wrap(compressedData));

            }

            //byte[] b = req.getBytes();      //Charset.forName("UTF-8")
            // logger.info("req" + req );
            //  logger.info("hex" + bytesToHex(wrap(b)) );
            //DataOutputStream output = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            //output.writeUTF(req);
            output.flush();

            //output.write(wrap(b));
            //output.flush();
        } catch (SocketTimeoutException e) {
            errorMsg = e.getMessage();
            //errorMsg = ErrorHandler.submitError(215, null, "sendReq");
        } catch (Exception e) {
            e.printStackTrace();
            errorMsg = e.getMessage();
            //errorMsg = ErrorHandler.submitError(901, e, "sendReq");
        }
    }

    private String getRes(Socket skt) {
        String toRet = "";

        try {

            DataInputStream input = new DataInputStream(skt.getInputStream());
            Short length = input.readShort();
            byte b[] = new byte[length];
            input.read(b, 0, length);

            if (Settings.IS_DEMO) {

                toRet = new String(b);

            } else {

                java.io.ByteArrayInputStream bytein = new java.io.ByteArrayInputStream(b);
                java.util.zip.GZIPInputStream gzin = new java.util.zip.GZIPInputStream(bytein);
                java.io.ByteArrayOutputStream byteout = new java.io.ByteArrayOutputStream();

                int res = 0;
                byte buf[] = new byte[1024];
                while (res >= 0) {
                    res = gzin.read(buf, 0, buf.length);
                    if (res > 0) {
                        byteout.write(buf, 0, res);
                    }
                }
                byte uncompressed[] = byteout.toByteArray();

                toRet = new String(uncompressed);

            }

            return toRet;

        } catch (Exception e) {
            //errorMsg = ErrorHandler.submitError(901, null, "getRes");
            e.printStackTrace();
            errorMsg = e.getMessage();
            return "";
        }
    }
    
    public String getToken(){
           MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
         final String url="https://portal.nedsecure.co.za/";
       // String password="?gSF#R1Qmw";
        String password="Wappoint123*";
        md5.update(password.getBytes());
        byte[] md5Pass = md5.digest();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String formattedDate = dateFormat.format(new Date()); //formatted in UTC/GMT time
      //  System.out.print(formattedDate);
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
        String token = Base64.encodeToString(tokenHash,true);
        return token;
    }

    public String getXMLValue(String path, String xml) {
        try {
            return XPathFactory
                    .newInstance()
                    .newXPath()
                    .evaluate(path, new InputSource(
                            new StringReader(xml)));
        } catch (XPathExpressionException ex) {
            errorMsg = ex.getMessage();
            return ex.getMessage();
        }
    }

 

//    @GET
//    @Path("test")
//    @Produces({"application/json","application/xml;"})
//    public String test() {
//    
//        Socket socket = null;
//        
//        try {
//        
//            socket = setupSocket(ding_ip, ding_port);
//            
//            
//            //SSLContext.getDefault().getSupportedSSLParameters().getProtocols()
//            
//            
//        } catch (Exception e) {
//            socket = null;
//            
//            return e.getMessage();
//        }
//        
//        return "test";
//        
//    }
 
    
     @GET
    @Path("getserver")
    @Produces("text/plain")
    public String get_server(){
        
 String response = "";
    
   
      
       try {
            int port = 443;
             String params="";
            InetAddress addr = InetAddress.getByName(Settings.wappoint_server_url);
            Socket socket = new Socket(addr, port);
            String path = "/api/merchant/authenticate";
            // Send headers
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            wr.write("GET "+path+" HTTP/1.0rn");
         //   wr.write("Content-Length: "+params.length()+"rn");
           // wr.write("Content-Type: application/x-www-form-urlencodedrn");
          //  wr.write("rn");
 
            // Send parameters
           // wr.write(params);
          //  wr.flush();
 
            // Get response
            BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
              System.out.println("res="+rd);
            while ((line = rd.readLine()) != null) {
               
                //response+=line;
            }
             
            wr.close();
            rd.close();
        
       } catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				
				try {
					if(socket!= null)
						socket.close();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
        
    
        
        
        
     return getToken();
        
    }

 

}