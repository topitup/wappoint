/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.topitup.wappoint.server;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;
import za.co.topitup.wappoint.xml.ErrorBean;

@Provider
public class MyException extends WebApplicationException {
 
     public MyException() {
         super(Response.status(Status.BAD_REQUEST).entity(new ErrorBean("Unknown")).build());
     }

     public MyException(String message) {
         super(Response.status(Response.Status.OK).entity(new ErrorBean(message)).build());
     }
     
     public MyException(String message, Exception e, boolean showError) {
         super(Response.status(Status.OK).entity(new ErrorBean(message, e.getMessage(), showError)).build());
         System.err.println(message);
         e.printStackTrace();
     }
     
//     public MyException(String message, Exception e) {
//         super(Response.status(Status.OK).entity(new ErrorBean(message)).build());
//         System.err.println(message);
//         e.printStackTrace();
//     }     
     
     
}