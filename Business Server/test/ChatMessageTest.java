import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ChatMessageTest {

    private static final String SESSIONID = "1";
    private static final String USERNAME = "admin";
    private static final String HEALTHCARD = "123456789";
    private static final String MESSAGE = "test message";
    private static final String INVALIDMESSAGE = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
                                                    "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
                                                    "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
                                                    "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest";
    private static final String ISSTAFF = "true";
    private static final Calendar TIME = Calendar.getInstance();
    public static ArrayList<ChatMessage> chatList;

    //Make sure to start the server before running tests
    private static class ChatMessage {
        public String message;
        public boolean staff;
        public String cardNum;
        public Calendar sentTime;



        public ChatMessage (String message, String staff, String cardNum, Calendar sentTime){
            this.message = message;
            this.staff = staff.equals("true");
            this.cardNum = cardNum;
            this.sentTime = sentTime;
        }
    }

    @BeforeAll
    public static void TestSetup() throws Exception {
        TIME.setTimeInMillis(1608055200);
        chatList = new ArrayList<>();
        String line, cvsSplitBy = ",";
        BufferedReader br = new BufferedReader(new FileReader(new File("test/data/chatAddList.csv")));
        while ((line = br.readLine()) != null) {
            String[] messages = line.split(cvsSplitBy);
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(Long.parseLong(messages[3]));
            ChatMessage msg = new ChatMessage(messages[0], messages[2], messages[1], date);
            chatList.add(msg);
        }
    }

    @AfterEach
    public void ResetDatabase() throws Exception {
        TestSetup();
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/cosaproject", "COSAInc", "P@ssw0rd123");
        Statement resetTable = con.createStatement();
        resetTable.execute("DROP TABLE chatMessage;");
        resetTable.execute("CREATE TABLE chatMessage ( messageID INT PRIMARY KEY AUTO_INCREMENT, messageContent VARCHAR(255), username VARCHAR(10) NOT NULL, sentByStaff BOOL DEFAULT true, timeSent LONG NOT NULL, FOREIGN KEY (username) REFERENCES user(username));");
        PreparedStatement addToTable = con.prepareStatement("INSERT INTO chatMessage (timeSent, username, messageContent, sentByStaff) VALUES (?, ?, ?, ?)");
        for(ChatMessage c : chatList)
        {
            addToTable.setLong(1, c.sentTime.getTimeInMillis());
            addToTable.setString(2, c.cardNum);
            addToTable.setString(3, c.message);
            addToTable.setBoolean(4, c.staff);
            addToTable.execute();
        }
        con.close();
    }

    @Test
    public void GetValidHealthCardNumber() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(mapper.writeValueAsString(SearchList(chatList, null, null, "123456789", null)));
        HttpResponse<String> response = Unirest.post("http://localhost:7000/getMessages")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("cardNum", HEALTHCARD)
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void GetInvalidHealthCardNumber() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(mapper.writeValueAsString(SearchList(chatList, null, null, "552489006", null)));
        HttpResponse<String> response = Unirest.post("http://localhost:7000/getMessages")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("cardNum", "552489006")
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void NewValidMessage() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        chatList.add(new ChatMessage(MESSAGE, ISSTAFF, HEALTHCARD, TIME));
        String expected = mapper.writeValueAsString(mapper.writeValueAsString(SearchList(chatList, null, null, "123456789", null)));
        HttpResponse<String> response = Unirest.post("http://localhost:7000/newMessage")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("cardNum", HEALTHCARD)
                .field("sentByStaff", ISSTAFF)
                .field("message", MESSAGE)
                .field("time", String.valueOf(TIME.getTimeInMillis()))
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void NewInvalidHealthCardToShort() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        chatList.add(new ChatMessage(MESSAGE, ISSTAFF, HEALTHCARD, TIME));
        String expected = mapper.writeValueAsString("Invalid Health Card");
        HttpResponse<String> response = Unirest.post("http://localhost:7000/newMessage")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("cardNum", "1234")
                .field("sentByStaff", ISSTAFF)
                .field("message", MESSAGE)
                .field("time", String.valueOf(TIME.getTimeInMillis()))
                .asString();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void NewInvalidHealthCardToLong() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        chatList.add(new ChatMessage(MESSAGE, ISSTAFF, HEALTHCARD, TIME));
        String expected = mapper.writeValueAsString("Invalid Health Card");
        HttpResponse<String> response = Unirest.post("http://localhost:7000/newMessage")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("cardNum", "1234567890")
                .field("sentByStaff", ISSTAFF)
                .field("message", MESSAGE)
                .field("time", String.valueOf(TIME.getTimeInMillis()))
                .asString();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void NewInvalidIsStaff() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        chatList.add(new ChatMessage(MESSAGE, ISSTAFF, HEALTHCARD, TIME));
        String expected = mapper.writeValueAsString("Invalid Sender Specification");
        HttpResponse<String> response = Unirest.post("http://localhost:7000/newMessage")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("cardNum", HEALTHCARD)
                .field("sentByStaff", "test")
                .field("message", MESSAGE)
                .field("time", String.valueOf(TIME.getTimeInMillis()))
                .asString();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void NewInvalidMessage() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        chatList.add(new ChatMessage(MESSAGE, ISSTAFF, HEALTHCARD, TIME));
        String expected = mapper.writeValueAsString("Invalid Message");
        HttpResponse<String> response = Unirest.post("http://localhost:7000/newMessage")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("cardNum", HEALTHCARD)
                .field("sentByStaff", ISSTAFF)
                .field("message", INVALIDMESSAGE)
                .field("time", String.valueOf(TIME.getTimeInMillis()))
                .asString();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void NewInvalidUser() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        chatList.add(new ChatMessage(MESSAGE, ISSTAFF, HEALTHCARD, TIME));
        String expected = mapper.writeValueAsString("Health Card Does Not Exist In Database");
        HttpResponse<String> response = Unirest.post("http://localhost:7000/newMessage")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("cardNum", "552136547")
                .field("sentByStaff", ISSTAFF)
                .field("message", INVALIDMESSAGE)
                .field("time", String.valueOf(TIME.getTimeInMillis()))
                .asString();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo(expected);
    }



    private ArrayList<ChatMessageTest.ChatMessage> SearchList(ArrayList<ChatMessageTest.ChatMessage> msgList, String message, Boolean staff, String healthcard, Calendar time) {
        Stream<ChatMessageTest.ChatMessage> stream = msgList.stream();
        if(healthcard != null)
            stream = stream.filter(app -> app.cardNum != null).filter(app -> app.cardNum.equals(healthcard));
        if(message != null)
            stream = stream.filter(app -> app.message != null).filter(app -> app.message.equals(message));
        if(staff != null)
            stream = stream.filter(app -> app.staff == staff);
        if(time != null) {
            stream = stream.filter(app -> app.sentTime != null);
            stream = stream.filter(app -> app.sentTime.getTimeInMillis() >= time.getTimeInMillis() && app.sentTime.getTimeInMillis() <= (time.getTimeInMillis() + 86399));
        }
        ArrayList<ChatMessageTest.ChatMessage> newList = new ArrayList<>();
        stream.forEach(newList::add);
        return newList;
    }
}
