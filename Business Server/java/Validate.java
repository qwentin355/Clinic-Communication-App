import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class Validate {
    public static int ValidateLogin(String username, String sessionID)
    {
        try {
            //Creating the HTTP Client and a post for validating
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httpPostValidate = new HttpPost("http://" + Network.readIP() + ":7001/validate");

            //Setting Parameters for user validation
            List<NameValuePair> ValidationParams = new ArrayList<NameValuePair>(1);
            ValidationParams.add(new BasicNameValuePair("sessionID", sessionID));
            ValidationParams.add(new BasicNameValuePair("username", username));
            httpPostValidate.setEntity(new UrlEncodedFormEntity(ValidationParams, "UTF-8"));

            //getting the response from the DAO and outputting whether the user is valid or not
            HttpResponse ValidateResponse = httpclient.execute(httpPostValidate);
            return ValidateResponse.getStatusLine().getStatusCode();
        } catch (Exception e) { return 500; }
    }
}
