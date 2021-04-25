import io.javalin.http.Handler;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatHistory {

    public static Handler GetHistory = ctx -> {
        try {
            ctx.json(SearchMessages(ctx.formParam("cardNum")));
            ctx.status(200);
        } catch (Exception e) {
            ctx.json(e);
            ctx.status(500);
        }
    };

    private static ArrayList<ChatMessage> SearchMessages(String searchParam) throws Exception {
        Connection con = JDBCHelper.connectToDB();
        PreparedStatement stmt = con.prepareStatement("SELECT messageContent, username, sentByStaff, timeSent FROM chatMessage WHERE username = ?");
        stmt.setString(1, searchParam);
        ArrayList<ChatMessage> objMessages = new ArrayList<ChatMessage>();
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String messageContent = rs.getString("messageContent");
            String username = rs.getString("username");
            boolean sentByStaff = rs.getBoolean("sentByStaff");
            Calendar timeSent = Calendar.getInstance();
            timeSent.setTimeInMillis(rs.getLong("timeSent"));
            objMessages.add(new ChatMessage(messageContent, sentByStaff, username, timeSent));
        }

        rs.close();
        stmt.close();
        con.close();
        return objMessages;
    }

    public static Handler AddMessage = ctx -> {
        try
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(ctx.formParam("sentTime")));
            ChatMessage newMessage = new ChatMessage(ctx.formParam("message"), ctx.formParam("staff").equals("true"),
                                                     ctx.formParam("cardNum"), calendar);
            Connection con = JDBCHelper.connectToDB();
            PreparedStatement addMessage = con.prepareStatement("INSERT INTO chatMessage (timeSent, username, messageContent, sentByStaff) VALUES (?, ?, ?, ?)");
            addMessage.setString(3, newMessage.message);
            addMessage.setBoolean(4, newMessage.staff);
            addMessage.setString(2, newMessage.cardNum);
            addMessage.setLong(1, newMessage.GetRawTime());
            addMessage.executeUpdate();
            ctx.status(200);
            con.close();
            addMessage.close();
        }
        catch(SQLIntegrityConstraintViolationException se) {
            ctx.status(406);
        } catch(Exception e) {
            ctx.status(500);
            ctx.json(e.getMessage());
        }
    };
}
