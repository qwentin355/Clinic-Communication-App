import io.javalin.http.Handler;
import io.javalin.http.UploadedFile;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;

public class UpdateWaitingRoom {
    public static Handler UpdateImage = ctx -> {
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
            update(stmt);
            File imgFile = new File(pathname);
            String base64 = encodeImage(imgFile);
            populate(base64, stmt);
            ctx.status(200);
            stmt.close();
        } catch (SQLException e) {
            ctx.status(500);
            ctx.json(e);
        } catch (NullPointerException e) {
            ctx.status(415);
        }
    };

    public static Handler RetrieveImage = ctx -> {
        try {
            ArrayList<String> base64 = new ArrayList<String>();
            Connection con = JDBCHelper.connectToDB();
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM waitingroom";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next())
            {
                base64.add(rs.getString("base64string"));
            }
            ctx.json(base64.toArray());
            ctx.status(200);
        }
        catch (SQLException e) {
            ctx.status(500);
            ctx.json(e);
        }
        catch (Exception e) {
            ctx.status(500);
        }
    };

    public static Handler ClearTable = ctx -> {
        try {
            Connection con = JDBCHelper.connectToDB();
            Statement stmt = con.createStatement();
            clear(stmt);
        }
        catch (SQLException e) {
            ctx.status(500);
            ctx.json(e);
        }
        catch (Exception e) {
            ctx.status(500);
        }
    };

    public static String encodeImage(File img) throws Exception
    {
        try
        {
            FileInputStream stream = new FileInputStream(img);

            int bufLength = 2048;
            byte[] buffer = new byte[2048];
            byte[] data;

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int readLength;
            while ((readLength = stream.read(buffer, 0, bufLength)) != -1)
            {
                out.write(buffer, 0, readLength);
            }

            data = out.toByteArray();
            String imageString = Base64.getEncoder().encodeToString(data);
            out.close();
            stream.close();

            return imageString;
        }
        catch(FileNotFoundException e)
        {
            System.out.println("File not found.");
        }
        return "";
    }

    private static void update(Statement stmt) throws SQLException {
        String sql2 = "CREATE TABLE IF NOT EXISTS waitingroom (pKey INT PRIMARY KEY AUTO_INCREMENT,  " +
                "    base64string   MEDIUMBLOB   NOT NULL)";
        stmt.executeUpdate(sql2);
    }
    private static void clear(Statement stmt) throws SQLException {
        String sql1 = "DROP TABLE IF EXISTS waitingroom";
        String sql2 = "CREATE TABLE waitingroom (pKey INT PRIMARY KEY AUTO_INCREMENT,  " +

                "    base64string   MEDIUMBLOB   NOT NULL)";
        stmt.executeUpdate(sql1);
        stmt.executeUpdate(sql2);
    }

    private static void populate(String base64, Statement stmt) throws SQLException {
        String qry = "INSERT INTO waitingroom (base64string) VALUES ('" + base64 + "');";
        stmt.executeUpdate(qry);
    }
}
