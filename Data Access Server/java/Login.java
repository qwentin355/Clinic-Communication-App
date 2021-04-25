import io.javalin.http.Handler;

import java.sql.*;

public class Login {

    public static Handler isLoginValid = ctx -> {
        try {
            Connection con = JDBCHelper.connectToDB();
            PreparedStatement stmt = con.prepareStatement("SELECT username, userPass FROM user WHERE username = ?");
            boolean isValid = false;
            String username = ctx.formParam("username"), password = ctx.formParam("password");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String user = rs.getString("username");
                String pass = rs.getString("userPass");
                isValid = (user.equals(username) && pass.equals(password));

            }
            rs.close();
            stmt.close();
            con.close();
            if (isValid) {
                logoutOthers(username);
                ctx.json(createID(username));
                ctx.status(200);
            }
            else
                ctx.status(401);
        } catch (Exception e) {
            ctx.status(500);
        }
    };

    public static Handler isSessionValid = ctx -> {
        Connection test = JDBCHelper.connectToDB();
        try (PreparedStatement stmt = test.prepareStatement("SELECT sessionID FROM session WHERE sessionID = ? AND username = ?")) {
            stmt.setString(1, ctx.formParam("sessionID"));
            stmt.setString(2, ctx.formParam("username"));
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                ctx.status(200);
            else
                ctx.status(401);
            rs.close();
            stmt.close();
        } catch (Exception se) {
            ctx.status(500);
        }
        test.close();
    };

    public static Handler logout = ctx -> {
        Connection con = JDBCHelper.connectToDB();
        try (PreparedStatement stmt = con.prepareStatement("DELETE FROM session WHERE sessionID = ?")) {
            stmt.setString(1, ctx.formParam("sessionID"));
            stmt.executeUpdate();
            stmt.close();
            ctx.status(200);
        } catch (Exception se) {
            ctx.status(500);
        }
        con.close();
    };

    public static String createID(String username) throws SQLException {
        Connection con = JDBCHelper.connectToDB();
        Statement stmt = con.createStatement();
        stmt.executeUpdate("INSERT INTO session (username) VALUES ('" + username + "');");
        ResultSet id = stmt.executeQuery("SELECT * FROM session WHERE username = '" + username + "' ORDER BY sessionID DESC");
        id.next();
        int sId = id.getInt("sessionID");
        id.close();
        stmt.close();
        con.close();
        return sId + "";
    }

    public static void logoutOthers(String username) throws SQLException {
        Connection con = JDBCHelper.connectToDB();
        PreparedStatement stmt = con.prepareStatement("DELETE FROM session WHERE username = ?");
        stmt.setString(1, username);
        stmt.executeUpdate();
        stmt.close();
        con.close();
    }
}

