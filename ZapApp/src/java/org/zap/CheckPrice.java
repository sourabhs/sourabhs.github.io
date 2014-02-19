package org.zap;

import java.net.URL;
import java.util.TimerTask;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;


/*
This is the main class which checks for changes in price of a product
by its ProductID and send a mail if expected discount is met.
Extends TimerTask to be able to pull information periodically to check 
for updates in price.
*/
public class CheckPrice extends TimerTask{
    
    //API URL
    String prefix = "http://api.zappos.com/Product/";
    
    //Key to send in URL
    String keyValue = "52ddafbe3ee659bad97fcce7c53592916a6bfd73";
    
    //ProductID and Subscriber Email
    String pid,email;
    
    //Discount value which triggers email if reached
    int discountValue = 20;
    
    public CheckPrice(String pid, String email){
        this.pid=pid;
        this.email=email;
    }
    
    @Override
    public void run(){
        String prod = getDiscountedProduct();
        //if product has the expected discount, send email to 
        //subscriber and terminate current task process
        try {
            if (prod != null) {
                Mail.send(email, prod);
                this.cancel();
            }
        } catch (Exception e) {
            this.cancel();
        }
    }
    
    //function to check if the product has expected discount
    //returns the productName to send in mail, if discount is reached,
    //else returns null
    public String getDiscountedProduct(){
        try{
            //create complete URL for API call along with request params
            URL url = new URL(byPidCreateURL());
            
            //declare and init Json Reader to read response as JSON object
            JsonReader jr = Json.createReader(url.openStream());
            JsonObject obj = jr.readObject();
            
            //if response has success status code, proceed
            if(obj.getString(APILiterals.statusCode).equals(APILiterals.successStatus)){
                JsonArray products = obj.getJsonArray(APILiterals.product);
                
                //searching by productID should return only one product in result
                if(products.size()==1){
                    //get the first and only product object
                    obj = (JsonObject) products.get(0);
                    
                    //get ProductName to send in email
                    String productName = obj.getString(APILiterals.productName);
                    
                    //a product can have many styles
                    //pull all styles and save in array
                    JsonArray styles = obj.getJsonArray(APILiterals.styles);
                    for(JsonObject styleObj : styles.getValuesAs(JsonObject.class)){
                        
                        //get the discount value from the object
                        String percentOff = styleObj.getString(APILiterals.percentOff);
                        
                        //remove % sign from retrieved discount value and parse int
                        //eg. 20% will become 20
                        int discount = Integer.parseInt(percentOff.substring(0, (percentOff.length()-1)));
                        
                        //if discount is greater than expected, return productName
                        if(discount>=discountValue)
                            return productName;
                    }
                    
                }
            }
        }catch(Exception e){
            this.cancel();
        }
        //return null if product with expected discount is not found
        return null;
        
    }
    
    //creates the URL with request params to create URL connection
    public String byPidCreateURL(){
        StringBuffer b = new StringBuffer();
        b.append(prefix).append(pid);
        b.append("?");
        b.append(APILiterals.includes).append("=[");
        b.append("\"").append(APILiterals.styles).append("\"");
        b.append("]");
        b.append("&").append(APILiterals.key).append("=").append(keyValue);
        return b.toString();
    }
}
