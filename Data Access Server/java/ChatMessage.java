import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

public class ChatMessage {
    public String message  ;
    public boolean staff = false;
    public String cardNum;
    public Calendar sentTime;

    public ChatMessage (String message, boolean staff, String cardNum, Calendar sentTime){
        this.message = message;
        this.staff = staff;
        this.cardNum = cardNum;
        this.sentTime = sentTime;
    }

    public long GetRawTime()
    {
        return sentTime.getTimeInMillis();
    }

    public String toString()
    {
        return "(" + GetRawTime() + ", '" + cardNum + "', '" + message + "', " + staff + ")";
    }

}
