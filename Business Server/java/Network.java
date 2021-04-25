import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Network {
    public static String readIP() {
        try {
            String address = "";
            BufferedReader br = new BufferedReader(new FileReader(new File("ip.csv")));
            address = br.readLine();
            return address;
        } catch(Exception e) {
            System.out.println("ERROR: Unable to read ip address. Defaulting to 127.0.0.1");
            return "127.0.0.1";
        }
    }
}
