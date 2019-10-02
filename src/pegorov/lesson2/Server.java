package pegorov.lesson2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;

public class Server {
    private int PORT;
    private boolean isRunnable;
    private ServerSocket srv;
    private HashMap<String, ChatThread> listClient;
    private SQL sql;

    public Server() {
        this.PORT = 8181;
        this.listClient = new HashMap<String, ChatThread>();
    }

    public Server(int port) {
        this.PORT = port;
        this.listClient = new HashMap<String, ChatThread>();
    }

    public synchronized boolean isRunning() {
        return isRunnable;
    }

    public void startServer() throws IOException, SQLException {
        srv = new ServerSocket(this.PORT);
        this.sql = new SQL();
        this.isRunnable = true;

        System.out.println("Сервер запущен, ожидаем подключения...");

        while (isRunning()) {
            Socket socket = srv.accept();
            ChatThread thread = new ChatThread(this, socket);
            thread.start();
        }
    }

    public boolean closeClient(String name) {
        if (name == null){
            System.out.println("Клиент не успел авторизоватья! ");

            return true;
        }
        if (!listClient.isEmpty()) {
            listClient.remove(name);

            System.out.println("Клиент удален из чата! " + name);

            return true;
        }

        System.out.println("Клиента уже нет в чате! " + name);
        return false;
    }

    public boolean addClient(String name, ChatThread cTh) throws SQLException, IOException {
        if (!listClient.containsKey(name)) {
            listClient.put(name, cTh);

            if (!sql.findName(name)){
                sql.addUser(name);
                sendNeedPwd(cTh, true);
            }else{
                sendNeedPwd(cTh, false);
                System.out.println("Клиент уже есть в SQL базе! " + name);
            }

            System.out.println("В чате новый клиент! " + name);
            return true;
        }

        System.out.println("Клиент попытался подключиться под уже заведенным ником! " + name);

        return false;
    }

    private void sendNeedPwd(ChatThread cTh, boolean newPwd) throws IOException {
        if (newPwd) {
            cTh.sendMessage("Установите пароль:");
        } else {
            cTh.sendMessage("Введите пароль:");
        }
    }

    public void stopServer() throws IOException {
        this.isRunnable = false;

        if (!listClient.isEmpty()) {
            for (String name : listClient.keySet()) {
                ChatThread map = listClient.get(name);
                map.closeConnection();

                System.out.println("Клиент: " + name + " был отключен сервером.");
            }
            listClient.clear();
        }

        if (!srv.isClosed()) {
            srv.close();
        }

        sql.Stop();
        System.out.println("Сервер остановлен");
    }

    public void sendAll(String nameClient, String msg) throws IOException {
        if (!listClient.isEmpty()) {
            for (ChatThread map : listClient.values()) {
                map.sendMessage("" + nameClient + ": " + msg);
            }
       }
    }

    public void sendAll(String msg) throws IOException {
        if (!listClient.isEmpty()) {
            for (ChatThread map : listClient.values()) {
                map.sendMessage(msg);

                System.out.println("Сообщение сервера: " + msg);
            }
        }
    }

    public void sendPrivateMsg(String str, String nameIn, String nameOut) throws IOException {
        ChatThread clientIn = listClient.get(nameIn);
        ChatThread clientOut = listClient.get(nameOut);

        if (clientOut == null){
            clientIn.sendMessage("Сообщение сервера: нет такого пользователя [" + nameOut + "]");
            System.out.println("Нет такого клиента! " + nameOut);
        }else{
            clientIn.sendMessage("[" + nameIn + " -> " + nameOut + "]: " + str);
            clientOut.sendMessage("[" + nameIn + " -> " + nameOut + "]: " + str);
            System.out.println("Отправлено приватное сообщение: [" + nameIn + " -> " + nameOut + "] " + str);
        }
    }

    public boolean isPwdCorrect(String name, String str) {
        return sql.pwdIsCorrect(name, str);
    }

    public void changePWD(String s, String name) {
        try {
            sql.changePWD(s, name);

            ChatThread clientIn = listClient.get(name);
            clientIn.sendMessage("Пароль успешно изменен!");
        } catch (SQLException | IOException e) {
            e.printStackTrace();

            ChatThread clientIn = listClient.get(name);
            try {
                clientIn.sendMessage("При смене пароля возникла ошибка!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void changeName(String s, String name) {
        try {
            ChatThread clientIn = listClient.get(name);

            if (sql.findName(s)){
                clientIn.sendMessage("Имя уже занято!");

                return;
            }

            sql.changeName(s, name);

            clientIn.setName(s);
            clientIn.sendMessage("Имя успешно изменено!");
            clientIn.sendMessageName(false);
        } catch (SQLException | IOException e) {
            e.printStackTrace();

            ChatThread clientIn = listClient.get(name);
            try {
                clientIn.sendMessage("При смене имени возникла ошибка!");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
