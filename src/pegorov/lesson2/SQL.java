package pegorov.lesson2;

import java.sql.*;

public class SQL {

    private Connection con;
    private Statement st;
    private PreparedStatement ps;

    public SQL() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:chatdatabase.db");
            con.setAutoCommit(false);
            st = con.createStatement();
            st.executeQuery("CREATE TABLE IF NOT EXISTS Users\n" +
                    "(\n" +
                    "  Name TEXT PRIMARY KEY NOT NULL,\n" +
                    "  Password TEXT NOT NULL,\n" +
                    ");\n");
            con.commit();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            con.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
            con.rollback();
        }
    }

    public void changeName(String name, String oldName) throws SQLException {
        try {
            ps = con.prepareStatement("UPDATE Users SET Name=? WHERE Name=?;");
            ps.setString(1, name);
            ps.setString(2, oldName);
            ps.executeQuery();

            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            con.rollback();
        }
    }

    public void changePWD(String pwd, String name) throws SQLException {
        try {
            ps = con.prepareStatement("UPDATE Users SET Password=? WHERE Name=?;");
            ps.setString(1, pwd);
            ps.setString(2, name);
            ps.executeQuery();

            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            con.rollback();
        }
    }

    public boolean findName(String name) throws SQLException {
        try {
            ps = con.prepareStatement("SELECT * FROM Users WHERE Name=?;");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            return (rs.next()?true:false);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addUser(String name, String pwd) throws SQLException {
        try {
            ps = con.prepareStatement("INSERT INTO Users (Name, Password) VALUES (?, ?);");
            ps.setString(1, name);
            ps.setString(2, pwd);
            ResultSet rs = ps.executeQuery();

            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
           con.rollback();
        }
    }

    public void Stop() {
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
