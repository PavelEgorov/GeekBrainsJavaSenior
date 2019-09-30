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

            //st = con.createStatement();
            //st.executeQuery("CREATE DATABASE IF NOT EXISTS chatdatabase;");

            st = con.createStatement();
            st.execute("CREATE TABLE IF NOT EXISTS Users(Name TEXT PRIMARY KEY NOT NULL, Password TEXT NOT NULL);");
            con.commit();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            con.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
            con.rollback();
        }

        System.out.println("Соединение с SQL базой установлено!");
    }

    public void changeName(String name, String oldName) throws SQLException {
        try {
            ps = con.prepareStatement("UPDATE Users SET Name=? WHERE Name=?;");
            ps.setString(1, name);
            ps.setString(2, oldName);
            ps.execute();

            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            con.rollback();
        }

        System.out.println("Имя изменено в SQL для клиента: " + oldName + " новое имя: " + name);
    }

    public void changePWD(String pwd, String name) throws SQLException {
        try {
            ps = con.prepareStatement("UPDATE Users SET Password=? WHERE Name=?;");
            ps.setString(1, pwd);
            ps.setString(2, name);
            ps.execute();

            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            con.rollback();
        }

        System.out.println("Пароль изменен в SQL для клиента: " + name + " новый пароль: " + pwd);
    }

    public boolean findName(String name) throws SQLException {
        try {
            ps = con.prepareStatement("SELECT * FROM Users WHERE Name=?;");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            boolean res = ((rs.next() == false) ? false : true);

            rs.close();
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addUser(String name) throws SQLException {
        try {
            ps = con.prepareStatement("INSERT INTO Users (Name, Password) VALUES (?, '');");
            ps.setString(1, name);
            ps.execute();

            con.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            con.rollback();
        }

        System.out.println("Клиент добавлен в базу! " + name);
    }

    public void Stop() {
        try {
            if (!st.isClosed()){
                st.close();
            }
            if (!ps.isClosed()){
                ps.close();
            }
            if (!con.isClosed()) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Сервер отключился от SQL!");
    }

    public boolean pwdIsCorrect(String name, String str) {
        try {
            ps = con.prepareStatement("SELECT * FROM Users WHERE Name=?;");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (rs.getString(2).equalsIgnoreCase("")) {
                    rs.close();

                    changePWD(str, name);
                    System.out.println("У клиента: " + name + "не было пароля. Пароль установлен!");
                    return true;
                } else if (rs.getString(2).equalsIgnoreCase(str)) {
                    rs.close();

                    System.out.println("Клиент: " + name + " ввсел пароль верно!");
                    return true;
                } else {
                    rs.close();

                    System.out.println("Клиент: " + name + " ввсел пароль не верно!");
                    return false;
                }
            }
            System.out.println("Не удалось выбрать данные из базы для проверки пароля!");

            return  false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
