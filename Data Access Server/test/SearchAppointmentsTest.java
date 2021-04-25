import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Stream;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
public class SearchAppointmentsTest {
    public static ArrayList<Appointment> appList;
    private static final Calendar TIME = Calendar.getInstance();
    //Test for recieing valid patient name(Kohl Hendrikson)
    //if that patient has an appointment = JSON array
    //if that patient doesnt have an appoint = Null
//Test for recieving an invalid Patient Name (KohlHendrikson)
    //expected: error
//Test for recieing valid Doctor name(Kohl Hendrikson)
    //if that patient has an appointment = JSON array
    //if that patient doesnt have an appoint = Null
//Test for recieving an invalid Doctor Name (KohlHendrikson)
    //expected: error
    //Test for Healthcard
    //if that patient has an appointment = JSON array
    //if that patient doesnt have an appoint = Null
    // Test for Date
    //if that date has an appointment = JSON array
    //if that date doesnt have an appoint = Null
    @BeforeAll
    public static void TestSetup() throws Exception {
        TIME.setTimeInMillis(1608055200);
        appList = new ArrayList<>();
        String line, cvsSplitBy = ",";
        BufferedReader br = new BufferedReader(new FileReader(new File("test/data/testList.csv")));
        while ((line = br.readLine()) != null) {
            String[] appointments = line.split(cvsSplitBy);
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            Calendar date = Calendar.getInstance();
            date.setTime(dateFormat.parse(appointments[3] + " " + appointments[4]));
            Appointment app = new Appointment(appointments[0], appointments[1], appointments[2], date);
            appList.add(app);
        }
    }
    @Test
    public void ValidHealthCardNum() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(SearchList(appList, "123456789", null, null, null));
        HttpResponse<String> response = Unirest.post("http://localhost:7001/searchApts")
                .field("cardNum", "123456789")
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }
    @Test
    public void InValidShortHealthCardNum() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(SearchList(appList, "12345678", null, null, null));
        HttpResponse<String> response = Unirest.post("http://localhost:7001/searchApts")
                .field("cardNum", "12345678")
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }
    @Test
    public void InValidLongHealthCardNum() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(SearchList(appList, "1234567890", null, null, null));
        HttpResponse<String> response = Unirest.post("http://localhost:7001/searchApts")
                .field("cardNum", "1234567890")
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }
    @Test
    public void ValidPatientName() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(SearchList(appList, null, null, "Anuj Patel", null));
        HttpResponse<String> response = Unirest.post("http://localhost:7001/searchApts")
                .field("name", "Anuj Patel")
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }
    @Test
    public void InValidPatientName() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(SearchList(appList, null, null, "Anunjjpatel", null));
        HttpResponse<String> response = Unirest.post("http://localhost:7001/searchApts")
                .field("name", "Anunjjpatel")
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }
    @Test
    public void ValidDoctorName() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(SearchList(appList, null, "Dr. Who", null, null));
        HttpResponse<String> response = Unirest.post("http://localhost:7001/searchApts")
                .field("doctor", "Dr. Who")
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }
    @Test
    public void InValidDoctorName() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(SearchList(appList, null, "Dr. Raju Rastogi", null, null));
        HttpResponse<String> response = Unirest.post("http://localhost:7001/searchApts")
                .field("doctor", "Dr. Raju Rastogi")
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }
    @Test
    public void ValidDate() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(SearchList(appList, null, null, null, TIME));
        HttpResponse<String> response = Unirest.post("http://localhost:7001/searchApts")
                .field("time", String.valueOf(TIME.getTimeInMillis()))
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }
    private static class Appointment {
        public String healthCardNum;
        public String patientName;
        public String doctorName;
        public Calendar appointmentTime;
        public Appointment (String cardNum, String name, String doctor, Calendar time)
        {
            this.healthCardNum = (cardNum.equals(""))?null:cardNum;
            this.patientName = (name.equals(""))?null:name;
            this.doctorName = (doctor.equals(""))?null:doctor;
            this.appointmentTime = Calendar.getInstance();
            this.appointmentTime.setTimeInMillis(time.getTimeInMillis() / 1000);
        }
    }
    private ArrayList<Appointment> SearchList(ArrayList<Appointment> appList, String healthcard, String doctor, String patient, Calendar time) {
        Stream<Appointment> stream = appList.stream();
        if(healthcard != null)
            stream = stream.filter(app -> app.healthCardNum != null).filter(app -> app.healthCardNum.equals(healthcard));
        if(doctor != null)
            stream = stream.filter(app -> app.doctorName != null).filter(app -> app.doctorName.equals(doctor));
        if(patient != null)
            stream = stream.filter(app -> app.patientName != null).filter(app -> app.patientName.equals(patient));
        if(time != null) {
            stream = stream.filter(app -> app.appointmentTime != null);
            stream = stream.filter(app -> app.appointmentTime.getTimeInMillis() >= time.getTimeInMillis() && app.appointmentTime.getTimeInMillis() <= (time.getTimeInMillis() + 86399));
        }
        ArrayList<Appointment> newList = new ArrayList<>();
        stream.forEach(newList::add);
        return newList;
    }
}