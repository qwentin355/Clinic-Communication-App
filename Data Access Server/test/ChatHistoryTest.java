import com.fasterxml.jackson.core.JsonProcessingException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//Health card correct
//Health card not correct
//Message correct
//Message not correct
//time sent correct
//time sent not correct

public class ChatHistoryTest {
    public static ArrayList<ChatMessage> appList;
    private static final String HEALTHCARD = "123456789";
    private static final String INVALIDHEALTHCARD = "12345678";
    private static final String MESSAGE = "test message";
    private static final String INVALIDMESSAGE = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
            "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
            "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest" +
            "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest";
    private static final boolean ISSTAFF = true;
    private static final Calendar TIME = Calendar.getInstance();


    @BeforeAll
    public static void TestSetup() throws Exception {

        appList = new ArrayList<>();
        String line, cvsSplitBy = ",";
        BufferedReader br = new BufferedReader(new FileReader(new File("test/data/testChatMessage.csv")));
        while ((line = br.readLine()) != null) {
            String[] Messages = line.split(cvsSplitBy);
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(Long.parseLong(Messages[3]));
            ChatMessage Message = new ChatMessage(Messages[0], Messages[2].equals("true"), Messages[1], date);
            appList.add(Message);
        }
    }

    @Test
    public void GetMessage() throws JsonProcessingException {
        HttpResponse<String> response = Unirest.post("http://localhost:7001/getChat")
                .field("cardNum", HEALTHCARD)
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    public void NewValidMessage() throws JsonProcessingException {
        HttpResponse<String> response = Unirest.post("http://localhost:7001/addChat")
                .field("cardNum", HEALTHCARD)
                .field("staff", "true")
                .field("message", MESSAGE)
                .field("sentTime", String.valueOf(TIME.getTimeInMillis()))
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);

    }

    @Test
    public void NewInValidMessage() throws JsonProcessingException {
        HttpResponse<String> response = Unirest.post("http://localhost:7001/addChat")
                .field("cardNum", HEALTHCARD)
                .field("staff", "true")
                .field("message", INVALIDMESSAGE)
                .field("sentTime", String.valueOf(TIME.getTimeInMillis()))
                .asString();
        assertThat(response.getStatus()).isEqualTo(500);

    }

    @Test
    public void ValidHealthCard() throws JsonProcessingException {
        HttpResponse<String> response = Unirest.post("http://localhost:7001/addChat")
                .field("cardNum", HEALTHCARD)
                .field("staff", "true")
                .field("message", MESSAGE)
                .field("sentTime", String.valueOf(TIME.getTimeInMillis()))
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);

    }

    @Test
    public void InvalidHealthCard() throws JsonProcessingException {
        HttpResponse<String> response = Unirest.post("http://localhost:7001/addChat")
                .field("cardNum", INVALIDHEALTHCARD)
                .field("staff", "true")
                .field("message", MESSAGE)
                .field("sentTime", String.valueOf(TIME.getTimeInMillis()))
                .asString();
        assertThat(response.getStatus()).isEqualTo(406);

    }

    @Test
    public void ValidTime() throws JsonProcessingException {
        HttpResponse<String> response = Unirest.post("http://localhost:7001/addChat")
                .field("cardNum", HEALTHCARD)
                .field("staff", "true")
                .field("message", MESSAGE)
                .field("sentTime", String.valueOf(TIME.getTimeInMillis()))
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);

    }

    @Test
    public void InvalidTime() throws JsonProcessingException {
        HttpResponse<String> response = Unirest.post("http://localhost:7001/addChat")
                .field("cardNum", HEALTHCARD)
                .field("staff", "true")
                .field("message", MESSAGE)
                .field("sentTime", "test")
                .asString();
        assertThat(response.getStatus()).isEqualTo(500);

    }

    private ArrayList<ChatMessage> SearchMessages(ArrayList<ChatMessage> appList, String cardNum) {
        ArrayList<ChatMessage> newList = new ArrayList<>();
        appList.stream().filter(app -> app.cardNum.equals(cardNum)).forEach(newList::add);
        return newList;
    }
}
