import io.javalin.http.Handler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;

public class SearchAppointments {
    public static long DAY_IN_MILLIS = 86399;
    //search appointment
    public static Handler SearchAppointment = ctx -> {
        try {
            ArrayList<Appointment> searchList = new ArrayList<Appointment>();
            String[] values = {ctx.formParam("cardNum"), ctx.formParam("name"), ctx.formParam("doctor"), ctx.formParam("time")};
            String[] columns = {"username", "patientName", "doctorName", "aptDate"};
            String header = "SELECT username, patientName, doctorName, aptDate FROM appointment";
            StringBuilder sql = new StringBuilder();

            for(int i = 0; i < 4; i++)
                if (values[i] != null && !values[i].trim().equals(""))
                {
                    if (sql.length() > 0)
                        sql.append(" AND ");
                    if (i == 3)
                        sql.append(columns[i]).append(" >= ? AND ").append(columns[i]).append(" <= ?");
                    else
                        sql.append(columns[i]).append(" = ?");
                }
            if (sql.length() > 0)
                header += " WHERE ";
            Connection con = JDBCHelper.connectToDB();
            PreparedStatement stmt = con.prepareStatement(header + sql.toString());
            int searchCount = 1;
            for(int i = 0; i < 4; i++)
                if (values[i] != null && !values[i].trim().equals(""))
                {
                    if (i == 3) {
                        stmt.setLong(searchCount, Long.parseLong(values[i]));
                        stmt.setLong(++searchCount, Long.parseLong(values[i]) + DAY_IN_MILLIS);
                    }
                    else
                        stmt.setString(searchCount, values[i]);
                    searchCount++;
                }
            ResultSet rs = stmt.executeQuery();


            //cardNum not working, other parameters are
            System.out.println(ctx.formParam("cardNum"));
            if (ctx.formParam("cardNum") != null && !ctx.formParam("cardNum").trim().equals("")) {
                sql.append("username = '").append(ctx.formParam("cardNum")).append("'");
            }
            if (ctx.formParam("name") != null && !ctx.formParam("name").trim().equals("")) {
                if (sql.length() != 0) sql.append(" AND ");
                sql.append("patientName = '").append(ctx.formParam("name")).append("'");
            }
            if (ctx.formParam("doctor") != null && !ctx.formParam("doctor").trim().equals("")) {
                if (sql.length() != 0) sql.append(" AND ");
                sql.append("doctorName = '").append(ctx.formParam("doctor")).append("'");
            }
            if (ctx.formParam("time") != null && !ctx.formParam("time").trim().equals("")) {
                if (sql.length() != 0) sql.append(" AND ");
                sql.append("aptDate >= ").append(ctx.formParam("time")).append(" AND aptDate <= ").append(Long.parseLong(ctx.formParam("time")) + DAY_IN_MILLIS);
            }
            if (sql.length() != 0) header += " WHERE ";

            rs = stmt.executeQuery(header + sql.toString() + ";");


            while (rs.next()) {
                String healthCardNum = rs.getString("username");
                String patientName = rs.getString("patientName");
                String doctorName = rs.getString("doctorName");
                Calendar aptTime = Calendar.getInstance();
                aptTime.setTimeInMillis(rs.getLong("aptDate"));
                searchList.add(new Appointment(healthCardNum, patientName, doctorName, aptTime));
            }
            if(searchList.size() == 0)
            {
                ctx.json(searchList);
                ctx.status(204);
            }
            else
            {
                ctx.json(searchList);
                ctx.status(200);
            }
            stmt.close();
            rs.close();
            con.close();
        } catch (Exception e) {
            ctx.status(400);
            ctx.json(e);
            System.out.println(e.getMessage());
        }
    };
}