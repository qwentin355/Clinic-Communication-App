
import java.util.Calendar;
import java.util.Date;

public class Appointment {

    public String healthCardNum;
    public String patientName;
    public String doctorName;
    public Calendar appointmentTime;


    public Appointment (String cardNum, String name, String doctor, Calendar time)
    {
        this.healthCardNum = cardNum;
        this.patientName = name;
        this.doctorName = doctor;
        this.appointmentTime = time;
    }

    public long GetRawTime()
    {
        return appointmentTime.getTimeInMillis();
    }

    public String toString()
    {
        return "(" + GetRawTime() + ", " +
                ((patientName==null)?null:"'" + patientName + "'") + ", " +
                ((doctorName==null)?null:"'" + doctorName + "'") + ", " +
                ((healthCardNum==null)?null:"'" + healthCardNum + "'") + ")";
    }

    public String getUser()
    {
        return healthCardNum;
    }
}