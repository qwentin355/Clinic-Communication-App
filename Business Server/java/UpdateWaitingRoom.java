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
import org.apache.http.util.EntityUtils;
import java.net.URI;

public class UpdateWaitingRoom {
    public static Handler uploadImage = ctx -> {
        System.out.println("In upload function");

        //get the uploaded file
        UploadedFile file = ctx.uploadedFile("myFile");

        String fileExt = file.getExtension();
        if(!(fileExt.equals(".png")) && !(fileExt.equals(".jpg")) && !(fileExt.equals(".jpeg")) && !(fileExt.equals(".jfif")))
        {
            ctx.status(415);
            ctx.json("File type is not supported");
        }
        else
        {
            //create body as multipart entity
            MultipartEntityBuilder entitybuilder = MultipartEntityBuilder.create();
            entitybuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            String filename = "file" + fileExt;
            entitybuilder.addBinaryBody("myFile", file.getContent(), ContentType.MULTIPART_FORM_DATA, filename);

            //create a request to back end
            HttpClient httpclient = HttpClients.createDefault();

            URI uri = new URIBuilder().setScheme("http")
                    .setHost(Network.readIP())
                    .setPort(7001)
                    .setPath("/updateWaitingRoom").build();
            HttpPost httppost = new HttpPost(uri);

            //set mulitpart entity to the request
            HttpEntity multiPartHttpEntity = entitybuilder.build();
            httppost.setEntity(multiPartHttpEntity);

            HttpResponse response = httpclient.execute(httppost);
            System.out.println(response.getStatusLine());

            HttpEntity entity = response.getEntity();

            ctx.status(response.getStatusLine().getStatusCode());
            ctx.json(EntityUtils.toString(entity));
        }
    };

    public static Handler retrieveImage = ctx -> {
        HttpClient httpclient = HttpClients.createDefault();
        URI uri = new URIBuilder().setScheme("http")
                .setHost(Network.readIP())
                .setPort(7001)
                .setPath("/retrieveImage").build();
        HttpPost httppost = new HttpPost(uri);

        HttpResponse response = httpclient.execute(httppost);

        HttpEntity entity = response.getEntity();

        ctx.status(response.getStatusLine().getStatusCode());
        ctx.json(EntityUtils.toString(entity));
    };

    public static Handler clearWaitingRoom = ctx -> {
        HttpClient httpclient = HttpClients.createDefault();
        URI uri = new URIBuilder().setScheme("http")
                .setHost(Network.readIP())
                .setPort(7001)
                .setPath("/clearWaitingRoomTable").build();
        HttpPost httppost = new HttpPost(uri);

        HttpResponse response = httpclient.execute(httppost);

        HttpEntity entity = response.getEntity();

        ctx.status(response.getStatusLine().getStatusCode());
        ctx.json(EntityUtils.toString(entity).trim());

    };
}
