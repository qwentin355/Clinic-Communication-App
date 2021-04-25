import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchAppointmentTest {

    private static final String SESSIONID = "1";
    private static final String USERNAME = "admin";
    private static final String HEALTHCARD = "101010101";
    private static final String DOCTOR = "Dr. Who";
    private static final String PATIENT = "Kohl Henrikson";
    private static final Calendar TIME = Calendar.getInstance();
    public static ArrayList<Appointment> appList;

    //Make sure to start the server before running tests

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
    public void ValidLoginNoParameters() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(mapper.writeValueAsString(SearchList(appList, null, null,null,null)));
        HttpResponse<String> response = Unirest.post("http://localhost:7000/search")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void ValidLoginValidHealthcard() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(mapper.writeValueAsString(SearchList(appList, HEALTHCARD, null,null,null)));
        HttpResponse<String> response = Unirest.post("http://localhost:7000/search")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("HealthCard", HEALTHCARD)
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void ValidLoginCharacteredHealthcard() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HttpResponse<String> response = Unirest.post("http://localhost:7000/search")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("HealthCard", "123abc789")
                .asString();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo(mapper.writeValueAsString("Invalid Health Card Parameter"));
    }

    @Test
    public void ValidLoginShortHealthcard() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HttpResponse<String> response = Unirest.post("http://localhost:7000/search")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("HealthCard", "123")
                .asString();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo(mapper.writeValueAsString("Invalid Health Card Parameter"));
    }

    @Test
    public void ValidLoginLongHealthcard() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HttpResponse<String> response = Unirest.post("http://localhost:7000/search")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("HealthCard", "123456789000")
                .asString();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo(mapper.writeValueAsString("Invalid Health Card Parameter"));
    }

    @Test
    public void ValidLoginValidDoctor() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(mapper.writeValueAsString(SearchList(appList, null, DOCTOR,null,null)));
        HttpResponse<String> response = Unirest.post("http://localhost:7000/search")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("Doctor", DOCTOR)
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void ValidLoginLongDoctor() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HttpResponse<String> response = Unirest.post("http://localhost:7000/search")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("Doctor", "Dr. ThereArentAnyFamousDoctorsWithReallyLongNamesSoIMadeOneUp")
                .asString();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo(mapper.writeValueAsString("Invalid Doctor Parameter"));
    }

    @Test
    public void ValidLoginValidPatient() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(mapper.writeValueAsString(SearchList(appList, null, null,PATIENT,null)));
        HttpResponse<String> response = Unirest.post("http://localhost:7000/search")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("Patient", PATIENT)
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void ValidLoginLongPatient() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HttpResponse<String> response = Unirest.post("http://localhost:7000/search")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("Patient", "Esteban Julio Ricardo Montoya de la Rosa Ramirez")
                .asString();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo(mapper.writeValueAsString("Invalid Patient Parameter"));
    }

    @Test
    public void ValidLoginValidTime() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(mapper.writeValueAsString(SearchList(appList, null, null,null,TIME)));
        HttpResponse<String> response = Unirest.post("http://localhost:7000/search")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("Date", String.valueOf(TIME.getTimeInMillis()))
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void ValidLoginWrongFormatTime() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HttpResponse<String> response = Unirest.post("http://localhost:7000/search")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("Date", TIME.getTime().toString())
                .asString();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo(mapper.writeValueAsString("Invalid Date Parameter"));
    }

    @Test
    public void ValidLoginInvalidTime() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        HttpResponse<String> response = Unirest.post("http://localhost:7000/search")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("Date", String.valueOf(-1))
                .asString();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getBody()).isEqualTo(mapper.writeValueAsString("Invalid Date Parameter"));
    }

    @Test
    public void ValidLoginMultipleParamters() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String expected = mapper.writeValueAsString(mapper.writeValueAsString(SearchList(appList, HEALTHCARD, DOCTOR, PATIENT,null)));
        HttpResponse<String> response = Unirest.post("http://localhost:7000/search")
                .field("sessionID", SESSIONID)
                .field("username", USERNAME)
                .field("HealthCard", HEALTHCARD)
                .field("Patient", PATIENT)
                .field("Doctor", DOCTOR)
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void InvalidLogin() {
        HttpResponse<String> response = Unirest.post("http://localhost:7000/search")
                .field("sessionID", 420)
                .field("username", "snoopdogg")
                .asString();
        assertThat(response.getStatus()).isEqualTo(401);
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
