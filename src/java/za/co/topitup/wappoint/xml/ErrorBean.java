package za.co.topitup.wappoint.xml;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "error")
public class ErrorBean {
    
    public String err = "";

    private ErrorBean() {} // for JAXB
    
    public ErrorBean(String message) {
        err = message;
    }
    
     public ErrorBean(String message, String getError, Boolean showError) {
        
         err = message;
         if (showError) {
             err = "[" + message + "] " + getError;
         }
    }

}