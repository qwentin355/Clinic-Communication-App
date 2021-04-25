import io.javalin.Javalin;

import io.javalin.core.util.Header;
import org.eclipse.jetty.server.Authentication;

import io.javalin.core.JavalinConfig;


public class Start {
    public static void main(String[] args)
    {
        Javalin app = Javalin.create(JavalinConfig::enableCorsForAllOrigins).start(Network.readIP(), 7000);

        //app.before(ctx->ctx.header(Header.ACCESS_CONTROL_ALLOW_ORIGIN, "*"));
        app.post("/login", User.LoginRequest);
        app.post("/logout", User.LogoutRequest);
        app.post("/uploadApp", UploadAppointment.uploadFile);
        app.post("/updateWaitingRoom",UpdateWaitingRoom.uploadImage);
        app.post("/retrieveImage", UpdateWaitingRoom.retrieveImage);
        app.post("/search", Staff.SearchAppointment);
        app.post("/newMessage", ChatMessage.SendMessage);
        app.post("/getMessages", ChatMessage.RetrieveChat);
        app.post("/clearWaitingRoomTable", UpdateWaitingRoom.clearWaitingRoom);

        app.post("/getAppointmentCount", Staff.getAppointmentCount);
        //App functions go here
    }
}
