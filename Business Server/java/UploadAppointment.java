import io.javalin.http.Handler;
import io.javalin.http.UploadedFile;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import java.net.URI;
import org.apache.http.util.EntityUtils;

public class UploadAppointment {
    public static Handler uploadFile = ctx -> {

        //get the uploaded file
        UploadedFile file = ctx.uploadedFile("myFile");

        if(!(file.getExtension().equals(".csv")))
        {
            ctx.status(415);
            ctx.json("File type is not supported");
        }
        else
        {
            //create body as multipart entity
            MultipartEntityBuilder entitybuilder = MultipartEntityBuilder.create();
            entitybuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            entitybuilder.addBinaryBody("myFile", file.getContent(), ContentType.MULTIPART_FORM_DATA, "file.csv");

            //create a request to back end
            HttpClient httpclient = HttpClients.createDefault();

            URI uri = new URIBuilder().setScheme("http")
                    .setHost(Network.readIP())
                    .setPort(7001)
                    .setPath("/appUpdate").build();
            HttpPost httppost = new HttpPost(uri);

            //set mulitpart entity to the request
            HttpEntity mutiPartHttpEntity = entitybuilder.build();
            httppost.setEntity(mutiPartHttpEntity);

            HttpResponse response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();

            ctx.status(response.getStatusLine().getStatusCode());
            ctx.json(EntityUtils.toString(entity));
        }
    };
}