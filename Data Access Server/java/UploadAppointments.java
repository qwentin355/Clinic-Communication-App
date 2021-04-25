import com.mysql.cj.protocol.Resultset;
import io.javalin.http.Handler;
import io.javalin.http.UploadedFile;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class UploadAppointments {
    public static Handler ListFromFile = ctx -> {

        UploadedFile file = ctx.uploadedFile("myFile");
        String ext = file.getExtension();

        String pathname = "file" + ext;
        byte[] buffer = new byte[file.getContent().available()];
        file.getContent().read(buffer);

        File targetFile = new File(pathname);
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);

        try {
            Connection con = JDBCHelper.connectToDB();
            Statement stmt = con.createStatement();
            File csvFile = new File("file.csv");
            ArrayList<Appointment> appList = FileUpload(csvFile);
            update(stmt);
            PopulateAppointments(appList, stmt);
            ctx.status(201);
            stmt.close();
        } catch (SQLException e) {
            ctx.status(500);
            System.out.println(e.getMessage());
            ctx.json(e);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            ctx.status(415);
            System.out.println(ctx.status());
        }

    };

    private static ArrayList<Appointment> FileUpload(File csvFile) {
        ArrayList<Appointment> appList = new ArrayList<>();
        BufferedReader br = null;
        String line;
        String cvsSplitBy = ",";
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] appointments = line.split(cvsSplitBy);
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                Calendar date = Calendar.getInstance();
                date.setTime(dateFormat.parse(appointments[3] + " " + appointments[4]));
                Appointment app = new Appointment(appointments[0], appointments[1], appointments[2], date);
                appList.add(app);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return appList;
    }

    //delete the entries in appointment table in database and add new list to it
    private static void update(Statement stmt) throws SQLException {
        String sql1 = "DROP TABLE IF EXISTS appointment";
        String sql2 = "CREATE TABLE appointment (appointmentID INT PRIMARY KEY AUTO_INCREMENT,  " +
                "    aptDate   LONG   NOT NULL,  " +
                "    patientName  VARCHAR(30)  NOT NULL,  " +
                "    doctorName  VARCHAR(30)  ,  " +
                "    username  VARCHAR(10)  ,  " +
                "    FOREIGN KEY (username) REFERENCES user(username)  " +
                ")";
        stmt.executeUpdate(sql1);
        stmt.executeUpdate(sql2);
    }

    private static void PopulateAppointments(ArrayList<Appointment> appList, Statement stmt) throws SQLException {
        //add users before adding appointments to avoid the issue of foreign key violation
        ArrayList<Appointment> AppointmentList = new ArrayList<>(appList);

        ArrayList<Appointment> toRemove = new ArrayList();
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        //check if user exists already and remove it from appList if it does

        for (Appointment a : AppointmentList) {
            sb2.append("SELECT * FROM user WHERE username = ");
            sb2.append(a.getUser());
            sb2.append(";");
            ResultSet rs = stmt.executeQuery(sb2.toString());
            int i=0;
            while(rs.next())
            {
                i++;
            }
            if(i>0)
                toRemove.add(a);
            sb2 = new StringBuilder();
        }
        AppointmentList.removeAll(toRemove);
        
        if(AppointmentList.size()>0) {
            sb2.append("INSERT INTO user (username, userPass) VALUES ");
            for (Appointment a : AppointmentList) {
                sb2.append("('")
                        .append(a.getUser())
                        .append("', 'password')")
                        .append(", ");
            }
            sb2.delete(sb2.length() - 2, sb2.length());
            sb2.append(";");
            stmt.executeUpdate(sb2.toString());
        }
        sb.append("INSERT INTO appointment (aptDate, patientName, doctorName, username) VALUES ");
        for (Appointment a : appList)
            sb.append(a.toString()).append(", ");
        sb.delete(sb.length() - 2, sb.length());
        sb.append(";");
        stmt.execute(sb.toString());
    }
}