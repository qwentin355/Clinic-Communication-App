import io.javalin.http.Handler;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatMessage {
    /** Validates the user, then sends the given message to the DAO to be added to the database. Also sends a new chat history
     * @param username      The user's username for login validation
     * @param sessionID     The user's session ID for login validation
     * @param cardNum       The health card number this message is meant for
     * @param sentByStaff   A boolean value, wether or not the message was sent by the staff (if false, the client sent it)
     * @param message       The body of the message
     * @param time          The time the message was sent, in milliseconds since the Unix epoch
     * @return              The updated chat history of the health card number in the message
     * @throws 500          If there is a server or database connection error
     * @throws 401          If any part of the message is in an invalid format, or if the username/sessionID is invalid
     */
    public static Handler SendMessage = ctx -> {
        //Validate login first before doing anything
        int validateStatus = Validate.ValidateLogin(ctx.formParam("username"), ctx.formParam("sessionID"));

        if (validateStatus == 200)
        {
            //Setup connection to DAO
            HttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost("http://" + Network.readIP() + ":7001/addChat");

            //Validate all parameters
            String error = "";
            if (!validateCardNum(ctx.formParam("cardNum")))
                error = "Invalid Health Card";
            else if (!validateSender(ctx.formParam("sentByStaff")))
                error = "Invalid Sender Specification";
            else if (!validateMessage(ctx.formParam("message")))
                error = "Invalid Message";
            else if (!validateTime(ctx.formParam("time")))
                error = "Invalid Time";
            //If error isn't "", then there was something invalid and the error code is in error, so we return that
            if (!error.equals(""))
            {
                ctx.status(401);
                ctx.json(error);
                return;
            }
            //Otherwise, all values are valid and we can send the data to the DAO
            //Preparing parameters to send to DAO
            List<NameValuePair> messageInfo = new ArrayList<>(1);
            messageInfo.add(new BasicNameValuePair("cardNum", ctx.formParam("cardNum")));
            messageInfo.add(new BasicNameValuePair("staff", ctx.formParam("sentByStaff")));
            messageInfo.add(new BasicNameValuePair("message", ctx.formParam("message")));
            messageInfo.add(new BasicNameValuePair("sentTime", ctx.formParam("time")));

            post.setEntity(new UrlEncodedFormEntity(messageInfo, "UTF-8"));
            //DAO request sent
            HttpResponse response = client.execute(post);
            //If request returned a 200, message was added to the database
            if (response.getStatusLine().getStatusCode() == 200)
            {
                try {
                    //Run GetChatHistory to get an updated list of chat messages to send to the front end
                    HttpResponse history = GetChatHistory(ctx.formParam("cardNum"));
                    ctx.status(history.getStatusLine().getStatusCode());
                    ctx.json(EntityUtils.toString(history.getEntity()).trim());
    //Steps below are for error handling, should something go wrong in any of these connection steps
                } catch (IOException e) {
                    ctx.status(500);
                    ctx.json(e.getMessage());
                }
            }
            else if (response.getStatusLine().getStatusCode() == 406)
            {
                ctx.status(401);
                ctx.json("Health Card Does Not Exist In Database");
            }
            else
                ctx.status(response.getStatusLine().getStatusCode());
        }
        else
            ctx.status(validateStatus);
    };

    /** Validates the user, then retrieves all chat messages related to a specific health card number
     * @param username      The user's username for login validation
     * @param sessionID     The user's session ID for login validation
     * @param cardNum       The health card number the user wants the chat history for
     * @return              The chat history of the specified health card number
     * @throws 500          If there is a server or database connection error
     * @throws 401          If the health card number is in an invalid format, or if the username/sessionID is invalid
     */
    public static Handler RetrieveChat = ctx -> {
        //Validate login first before doing anything
        int validateStatus = Validate.ValidateLogin(ctx.formParam("username"), ctx.formParam("sessionID"));

        if (validateStatus == 200)
            //Make sure the health card number is valid
            if (validateCardNum(ctx.formParam("cardNum")))
            {
                try {
                    //Use GetChatHistory to get the messages from the DAO, then return it to the front end
                    HttpResponse history = GetChatHistory(ctx.formParam("cardNum"));
                    ctx.status(history.getStatusLine().getStatusCode());
                    ctx.json(EntityUtils.toString(history.getEntity()).trim());
    //Steps below are for error handling, should something go wrong in any of these connection steps
                } catch (IOException e) {
                    ctx.status(500);
                    ctx.json(e.getMessage());
                }
            }
            else
            {
                ctx.status(401);
                ctx.json("Invalid Health Card");
            }
        else
            ctx.status(validateStatus);
    };

    /** Connects to the DAO to get all chat messages related to the given health card number
     * @param cardNum       The health card number we're getting the messages for
     * @return              The response object from the DAO
     * @throws IOException  If there is a connection error between us and the DAO, or the DAO and the database
     */
    private static HttpResponse GetChatHistory(String cardNum) throws IOException {
        //Setup connection to the DAO
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost("http://" + Network.readIP() + ":7001/getChat");
        //Insert parameters
        List<NameValuePair> searchParam = new ArrayList<>(1);
        searchParam.add(new BasicNameValuePair("cardNum", cardNum));

        //Send request and return the response
        post.setEntity(new UrlEncodedFormEntity(searchParam, "UTF-8"));
        return client.execute(post);
    }

    /*Validation was split into separate functions to allow for multi-step validation to be done easily, and so if
     *validation requirements change over time they can be altered without needing to change the main function */

    /** Checks if the given health card number is valid
     * @param cardNum       The heath card number to check
     * @return              Whether the card is valid or not
     */
    private static boolean validateCardNum(String cardNum)
    {
        //Check that it's not empty, then use the isHealthCard function in the Staff class for actual validation
        if (cardNum != null)
            return Staff.isHealthCard(cardNum);
        return false;
    }

    /** Checks if who sent the message is in a valid format
     * @param sender        The value specifying if the sender was a staff or not
     * @return              If sender is in a valid format or not
     */
    private static boolean validateSender(String sender)
    {
        //Check that it's not empty, then make sure that it's in a boolean format (true or false)
        if (sender != null)
            return (sender.equals("true") || sender.equals("false"));
        return false;
    }

    /** Checks if the message is valid or not
     * @param message       The message to check
     * @return              Whether the message is valid or not
     */
    private static boolean validateMessage(String message)
    {
        //Check that it's not empty, then make sure it's less than the database's maximum size
        if (message != null)
            return message.length() < 255;
        return false;
        //Currently, max size is the only real requirement. We aren't checking for special characters or bad words or anything
    }

    /** Checks that the time is valid or not
     * @param time          The time the message was sent
     * @return              Whether the time is valid or not
     */
    private static boolean validateTime(String time)
    {
        //Check that it's not empty
        if (time != null)
        {
            try {
                //This checks that the time is both a number, and that it's not negative
                if (Long.parseLong(time) < 0) throw new NumberFormatException();
                //If either is true, it throws an exception (parseLong throws NumberFormatException normally)
                return true;
            } catch (NumberFormatException e) { return false; }
        }
        return false;
    }
}
