import io.javalin.http.Handler;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Staff {
    //SearchAppointment handler requires search parameters along with username and sessionID
    public static Handler SearchAppointment = ctx -> {
        //Creating the HTTP Client
        HttpClient httpclient = HttpClients.createDefault();

        //Creating two different posts, one for searching, and one for validating a valid user is logged in
        HttpPost httpPostSearch = new HttpPost("http://" + Network.readIP() + ":7001/searchApts");

        //Runs Validation function to check if the user is valid and gets status code
        int validateStatus = Validate.ValidateLogin(ctx.formParam("username"), ctx.formParam("sessionID"));

        //If user is valid, continue with search
        if (validateStatus == 200) {
            List<NameValuePair> SearchParams = new ArrayList<NameValuePair>(1);

            //validating doctor name parameter
            if(ctx.formParam("Doctor") != null) {
                if(ctx.formParam("Doctor").length() <= 30) {
                    //if all is valid add the doctor name to the parameter list
                    SearchParams.add(new BasicNameValuePair("doctor", ctx.formParam("Doctor")));

                } else {
                    //if the parameter is invalid return code 401
                    ctx.status(401);
                    ctx.json("Invalid Doctor Parameter");
                    return;


                }

            } else {
                //if doctors name is empty add a null parameter to the parameter list
                SearchParams.add(new BasicNameValuePair("doctor", null));

            }

            //validating health card number parameter
            if(ctx.formParam("HealthCard") != null) {
                if(isHealthCard(ctx.formParam("HealthCard"))) {
                    //if all is valid add the Health Card to the parameter list
                    SearchParams.add(new BasicNameValuePair("cardNum", ctx.formParam("HealthCard")));

                } else {
                    //if the parameter is invalid return code 401
                    ctx.status(401);
                    ctx.json("Invalid Health Card Parameter");
                    return;
                }

            } else {
                //if health card is empty add a null parameter to the parameter list
                SearchParams.add(new BasicNameValuePair("cardNum", null));

            }

            //Validating Date parameter
            if(ctx.formParam("Date") != null) {
                //If the date is in an invalid format (not a long), output an error
                 try
                 {
                     long time = Long.parseLong(ctx.formParam("Date"));
                     //If the date is less than 0, output an error
                     if (time < 0) throw new NumberFormatException();
                     //If the date isnt null add its value to the parameter list
                     SearchParams.add(new BasicNameValuePair("time", ctx.formParam("Date")));
                 }
                 catch (NumberFormatException e)
                 {
                     ctx.status(401);
                     ctx.json("Invalid Date Parameter");
                     return;
                 }

            } else {
                //if date is null add a null value to the parameter list
                SearchParams.add(new BasicNameValuePair("time", null));

            }

            //Validating patient name parameter
            if(ctx.formParam("Patient") != null) {
                if(isName(ctx.formParam("Patient"))) {
                    if (ctx.formParam("Patient").length() <= 30) {
                        //If the patient name isnt null, its a name, and its less than 30 characters add its value to the parameter list
                        SearchParams.add(new BasicNameValuePair("name", ctx.formParam("Patient")));

                    } else {
                        //if the parameter is invalid return code 401
                        ctx.status(401);
                        return;

                    }

                } else {
                    //if the parameter is invalid return code 401
                    ctx.status(401);
                    ctx.json("Invalid Patient Parameter");
                    return;
                }

            } else {
                //if patient name is null add a null value to the parameter list
                SearchParams.add(new BasicNameValuePair("name", null));

            }

            //setting the search entity
            httpPostSearch.setEntity(new UrlEncodedFormEntity(SearchParams, "UTF-8"));

            //Executing the search, getting the result, and sending back to the front end
            HttpResponse SearchResponse = httpclient.execute(httpPostSearch);
            HttpEntity SearchEntity = SearchResponse.getEntity();
            ctx.status(SearchResponse.getStatusLine().getStatusCode());
            ctx.json(EntityUtils.toString(SearchEntity).trim());

        } else {
            //returning the status code sent back from the DAO by validate to the front end
            ctx.status(validateStatus);

        }

    };

    //uses regex to verify that valid data is being used
    public static boolean isName(String value) {
        java.util.regex.Pattern pattern = Pattern.compile("^[a-zA-Z]+ [a-zA-Z]+$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(value);

        return matcher.find();
    }

    //checks the health card numbers to verify they are all numbers and its 9 digits
    public static boolean isHealthCard(String value) {
        java.util.regex.Pattern pattern = Pattern.compile("^\\d\\d\\d\\d\\d\\d\\d\\d\\d$");
        Matcher matcher = pattern.matcher(value);

        return matcher.find();
    }

    //get appointment count to check if user trying to log in has appointments in the system.
    //gets only username as input parameters
    public static Handler getAppointmentCount = ctx -> {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPostSearch = new HttpPost("http://" + Network.readIP() + ":7001/searchApts");
        List<NameValuePair> SearchParams = new ArrayList<NameValuePair>(1);

        //validating health card number parameter
        if(ctx.formParam("HealthCard") != null) {
            if(isHealthCard(ctx.formParam("HealthCard"))) {
                //if all is valid add the Health Card to the parameter list
                System.out.println(ctx.formParam("HealthCard"));
                SearchParams.add(new BasicNameValuePair("cardNum", ctx.formParam("HealthCard")));

            } else {
                //if the parameter is invalid return code 401
                ctx.status(401);
                ctx.json("Invalid Health Card Parameter");
                return;
            }
        } else {
            //if health card is empty add a null parameter to the parameter list
            ctx.status(401);
            ctx.json("Invalid Health Card");
            return;
        }

        httpPostSearch.setEntity(new UrlEncodedFormEntity(SearchParams, "UTF-8"));

        //execute post request with username as search parameter and send the response back to frontend.
        HttpResponse SearchResponse = httpclient.execute(httpPostSearch);
        System.out.println("After execute");
        HttpEntity SearchEntity = SearchResponse.getEntity();
        ctx.status(SearchResponse.getStatusLine().getStatusCode());
        System.out.println(SearchResponse.getStatusLine().getStatusCode());
        ctx.json(EntityUtils.toString(SearchEntity).trim());
    };
}
