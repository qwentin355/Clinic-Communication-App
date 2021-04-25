import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBCHelper {
    private static final String url = "jdbc:mysql://localhost:3306/cosaproject";
    private static final String username = "COSAInc";
    private static final String password = "P@ssw0rd123";

    public static Connection connectToDB() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
