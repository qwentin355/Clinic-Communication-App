import io.javalin.http.Handler;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class User {

    public static Handler LoginRequest = ctx -> {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://" + Network.readIP() + ":7001/login");

        List<NameValuePair> params = new ArrayList<NameValuePair>(1);
        params.add(new BasicNameValuePair("username", ctx.formParam("username")));
        params.add(new BasicNameValuePair("password", ctx.formParam("password")));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (response.getStatusLine().getStatusCode() == 200) {
            ctx.json(EntityUtils.toString(entity).replace('"', ' ').trim());

        } else {
            ctx.status(response.getStatusLine().getStatusCode());
        }
    };


    public static Handler LogoutRequest = ctx -> {

            HttpClient httpclient = HttpClients.createDefault();
            HttpDelete httpDelete = new HttpDelete("http://" + Network.readIP() + ":7001/logout");

            List<NameValuePair> params = new ArrayList<NameValuePair>(1);
            params.add(new BasicNameValuePair("sessionID", ctx.formParam("sessionID")));
            //Needs to add params to http request

            HttpResponse response = httpclient.execute(httpDelete);
            ctx.status(response.getStatusLine().getStatusCode());

        };
}
