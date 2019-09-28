package pegorov.lesson2;

import java.io.IOException;
import java.sql.SQLException;

public class Lesson7 {
    public static void main(String[] args) {
        Server srv = new Server();
        try {
            srv.startServer();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
