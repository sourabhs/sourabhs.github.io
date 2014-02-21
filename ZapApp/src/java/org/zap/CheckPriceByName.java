/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.zap;

import java.net.URL;
import java.util.TimerTask;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

/**
 *
 * @author Sourabh
 */
public class CheckPriceByName extends TimerTask {

    //API URL
    String prefix = "http://api.zappos.com/Search";

    //Key to send in URL
    String keyValue = "52ddafbe3ee659bad97fcce7c53592916a6bfd73";

    //ProductID and Subscriber Email
    String pid, email;
    
    //Limit results returned to a maximum of this value
    String resultLimit = "10";

    //Discount value which triggers email if reached
    int discountValue = 20;

    public CheckPriceByName(String pid, String email) {
        this.pid = pid;
        this.email = email;
    }

    @Override
    public void run() {
        String prod = getDiscountedProduct();
        //if product has the expected discount, send email to 
        //subscriber and terminate current task process
        try {
            if (prod != null) {
                Mail.send(email, prod);
                this.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.cancel();
        }
    }

    //function to check if the product has expected discount
    //returns the productName to send in mail, if discount is reached,
    //else returns null
    public String getDiscountedProduct() {
        try {
            //create complete URL for API call along with request params
            URL url = new URL(byPNameCreateURL());

            //declare and init Json Reader to read response as JSON object
            JsonReader jr = Json.createReader(url.openStream());
            JsonObject obj = jr.readObject();

            //if response has success status code, proceed
            if (obj.getString(APILiterals.statusCode).equals(APILiterals.successStatus)) {

                JsonArray products = obj.getJsonArray(APILiterals.searchResult);

                for (JsonObject prod : products.getValuesAs(JsonObject.class)) {

                    //get the discount value from the object
                    String percentOff = prod.getString(APILiterals.percentOff);
                    String productName = prod.getString(APILiterals.productName);

                    //compare product names of user entered name and name fetched in current product object
                    //if searched name is completely contained in the fetched one, continue,
                    //else ignore this product
                    if (productName.toLowerCase().trim().contains(pid.toLowerCase().trim())) {
                        //remove % sign from retrieved discount value and parse int
                        //eg. 20% will become 20
                        int discount = Integer.parseInt(percentOff.substring(0, (percentOff.length() - 1)));

                        //if discount is greater than expected, return productName
                        if (discount >= discountValue) {
                            return productName;
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            this.cancel();
        }
        //return null if product with expected discount is not found
        return null;

    }

    //creates the URL with request params to create URL connection
    public String byPNameCreateURL() {
        StringBuffer b = new StringBuffer();
        b.append(prefix);
        b.append("?");
        //search term
        b.append(APILiterals.searchTerm).append("=").append(pid);
        //limit results since we're interested in only the most relevant search results
        b.append("&").append("limit=").append(resultLimit);
        //only include items on sale
        b.append("&").append(APILiterals.includes).append("=[\"onSale\"]");
        b.append("&filters={\"onSale\":[\"true\"]}");
        //append API key
        b.append("&").append(APILiterals.key).append("=").append(keyValue);
        return b.toString();
    }

}
