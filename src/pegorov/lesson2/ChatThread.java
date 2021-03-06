package pegorov.lesson2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

public class ChatThread {
    private Socket socket;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;

    private String name;
    private boolean haveName;

    public ChatThread(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;

        in = new DataInputStream(this.socket.getInputStream());
        out = new DataOutputStream(this.socket.getOutputStream());
    }

    public void start() throws IOException {
          /*
         Первым делом нужно спросить кто к нам подключается.
         */
        server.sendlogInfo("Клиент подключился требуется аутентификация");
        Thread thread = new Thread(() -> {
            try {
                server.sendlogInfo("Создаем поток чтения сообщений от клиента: " + name);

                readMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        haveName = false;

        Thread aut = new Thread(()->{
            try {
                haveName = authentication();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        aut.start();
        try {
            aut.join(120000);/// Ожидаем 120 сек
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (haveName) {
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        closeConnection();
    }

    private boolean authentication() throws IOException, SQLException {
        String str = in.readUTF();

        if (str.equalsIgnoreCase("/end")) {
            sendMessage("/end");
            server.sendlogInfo("Клиент отключился на этапе аутентификации");

            return false;
        }

        this.name = str;

        String msg = "";
        if (server.addClient(name, this)) {
            do{
                str = in.readUTF();
            }while (!server.isPwdCorrect(name, str));

            msg = "Добро пожаловать в чат! " + name;

            sendMessageName(true);

            server.sendAll(msg);

            server.sendlogInfo("Клиент добавлен в список. Имя нового клиента: " + name);

            return true;
        } else {
            msg = "Клиент уже есть в чате, введите новый ник.";

            out.writeUTF(msg);
            server.sendlogInfo("Клиент не добавлен в список. Имя нового клиента: " + name + " сообщение: " + msg);

            return false;
        }
    }

    public void sendMessageName(boolean t) throws IOException {
        if (t){
            sendMessage("/name " + name);
        }else{
            sendMessage("/changename " + name);
        }
    }

    private void readMessage() throws IOException {

        while (server.isRunning()) {
            if (!socket.isConnected()) {
                server.sendlogInfo("Клиент: " + this.name + " отключился");
                closeConnection();
                break;
            }

            String str = in.readUTF();

            server.sendlogInfo("Клиент: " + name + " написал сообщение: " + str);

            if (str.equalsIgnoreCase("/end")) {
                closeConnection();
                break;
            }

            if (str.equalsIgnoreCase("/stopServer")) {
                server.sendlogInfo("Поступила команды выключения сервера!");

                server.stopServer();
                break;
            }

            String[] msg = str.split(" ", 3);
            if (msg[0].equalsIgnoreCase("/w")) {
                String nameOut = msg[1];
                server.sendPrivateMsg(msg[2], name, nameOut);
            }else if(msg[0].equalsIgnoreCase("/setpassword")){
                server.sendlogInfo("Поступила команды смены пароля!");

                String pwd = msg[1];
                server.changePWD(pwd, name);
            }else if(msg[0].equalsIgnoreCase("/setname")){
                server.sendlogInfo("Поступила команды смены имени!");

                String pwd = msg[1];
                server.changeName(pwd, name);
            } else {
                server.sendAll(this.name, str);
            }
        }
    }

    private void stopSocket() throws IOException {
            in.close();
            out.close();

            if (!socket.isClosed()) {

                    socket.close();
            }
    }

    public void closeConnection() {
        try {
            if (!server.closeClient(this.name)) return;

            if (!(this.name == null)) {
                server.sendAll("Клиент: " + this.name + " отключился.");
            }
            sendMessage("/end");

            stopSocket();
            server.sendlogInfo(this.name + " отключился");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) throws IOException {
        if (!socket.isConnected()) {
            closeConnection();
            return;
        }

        out.writeUTF(msg);
        out.flush();

        server.sendlogInfo("Отправлено сообщение клиенту: " + this.name + " " + msg);

    }

    public void setName(String name) {
        this.name = name;
    }
}
