<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="org.zap.CheckPrice"%>
<%@page import="org.zap.CheckPriceByName"%>
<%@page import="java.util.Timer"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Wishlist</title>
    </head>
    <body>
        <%
            //check if this page load is caused by submit button or not
            if (request.getParameter("btnSubmit") != null) {
                //check search criteria
                if(request.getParameter("searchby").equals("prodid")){
                try{
                    //create timer to check for price update repeatedly at some intervals
                    Timer timer = new Timer();
                    timer.schedule(new CheckPrice(request.getParameter("pid"),request.getParameter("email")),0,10000);
                    out.println("Email " + request.getParameter("email") + " successfully subscribed.");
                }catch(Exception e){
                    out.println("An error occured. Please retry on the <a href=\"wishlist.html\">Wishlist</a> page.");
                }
            }
                else if(request.getParameter("searchby").equals("pname")){
                    try{
                    //create timer to check for price update repeatedly at some intervals
                    Timer timer = new Timer();
                    timer.schedule(new CheckPriceByName(request.getParameter("pid"),request.getParameter("email")),0,10000);
                    out.println("Email " + request.getParameter("email") + " successfully subscribed.");
                }catch(Exception e){
                    out.println("An error occured. Please retry on the <a href=\"wishlist.html\">Wishlist</a> page.");
                }
                }
            }else {
                out.println("Please go to <a href=\"wishlist.html\">Wishlist</a> page.");
            }
        %>
    </body>
</html>
