import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;

public class Start {
    public static void main(String[] args)
    {
        Javalin app = Javalin.create(JavalinConfig::enableCorsForAllOrigins).start(Network.readIP(), 7001);
        app.post("/appUpdate", UploadAppointments.ListFromFile);
        app.post("/searchApts", SearchAppointments.SearchAppointment);
        app.post("/login", Login.isLoginValid);
        app.post("/validate", Login.isSessionValid);
        app.delete("/logout", Login.logout);
        app.post("/getChat", ChatHistory.GetHistory);
        app.post("/addChat", ChatHistory.AddMessage);
        app.post("/updateWaitingRoom", UpdateWaitingRoom.UpdateImage);
        app.post("/retrieveImage", UpdateWaitingRoom.RetrieveImage);
        app.post("/clearWaitingRoomTable", UpdateWaitingRoom.ClearTable);
    }
}