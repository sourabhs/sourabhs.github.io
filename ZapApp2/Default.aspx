<%@ Page Language="C#" %>

<%@ Import Namespace="org.zap" %>
<%@ Import Namespace="System" %>

<!DOCTYPE html>

<html>
<head>
    <title>ZapApp 2</title>
</head>
<script runat="server">
    void Search_API(object sender, EventArgs e)
    {
        // if the page is being loaded in response to a postback
        if (IsPostBack)
        {
            try
            {
                // Get the no if items and desired amount info from form
                int num = Convert.ToInt32(TxtNum.Text);
                float amt = (float) Convert.ToDecimal(TxtAmt.Text);

                // Call helper CS class to process info and get the product combinations to display
                ZapHelper zh = new ZapHelper();
                List<ResultCombo> l2 = zh.mainExecute(num,amt);
                List<Result> l = new List<Result>();

                ResultSet.InnerHtml = "";
                
                // If combination returned, show error.
                if (l2 == null) {
                    ResultSet.InnerHtml = "An error occured or no results retrieved. Please retry.";
                    return;
                }
                
                // Display all product combinations
                for (int i = 0; i < l2.Count;i++ ) {
                    l = l2.ElementAt(i).comb;
                    ResultSet.InnerHtml += "<li>"+"Total Amount: $"+Convert.ToString(l2.ElementAt(i).sum);
                    for (int j = 0; j < l.Count; j++) {
                        ResultSet.InnerHtml += "<ul><li>Product ID: "+l[j].productId+
                                               "<br/>Product Name: "+l[j].productName+
                                               "<br/>Amount: "+l[j].price+"</li></ul>";
                    }
                    ResultSet.InnerHtml += "</li>";
                }                    
                
            }
            catch(Exception excp){
                ResultSet.InnerHtml = "An error occured. Please retry.";
                return;
            }
        }
    }
</script>
<body>
    <div class="container">

        <h1>Gift Suggestions</h1>
            
        <div>
            <div>

                <p>
                    Enter the Number of Products you want to gift and the Maximum Amount 
                    you are willing to spend, and we will suggest the best combinations for you.
                </p>
                <div>
                    <form id="form1" runat="server">
                        <asp:TextBox ID="TxtNum" required placeholder="No of Items" runat="server" />
                        <asp:TextBox ID="TxtAmt" required placeholder="Desired Amount in $" runat="server" />
                        <span>
                            <asp:button ID="Submit" Text="Search" OnClick="Search_API" runat="server"></asp:button>
                        </span>
                    </form>
                </div>
                
                <h2>Suggestions</h2>

                <ul id="ResultSet" runat="server">
                </ul>

            </div>

        </div>
    </div>


</body>
</html>
