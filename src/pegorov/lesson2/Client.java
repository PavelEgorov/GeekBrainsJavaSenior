package pegorov.lesson2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final static String SERVER_ADDR = "localhost";
    private final static int SERVER_PORT = 8181;

    private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;

    private static boolean isRunning;
    private ChatInterface clientInterface;

    private ChatFileWork file;
    private String name;
    private String newName;

    public void startClient(ChatInterface inr) {
        this.clientInterface = inr;
        this.name = "";
        this.newName = "";

        try {
            connectToServer(inr);
            closeConnection();
        } catch (IOException e) {
            clientInterface.updateDialog("Не удалось подключиться к серверу");
            e.printStackTrace();
        }
    }

    private void connectToServer(ChatInterface inr) throws IOException {

        socket = new Socket(SERVER_ADDR, SERVER_PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        isRunning = true;

        Thread tr1in = new Thread(() -> {
            try {
                while (isRunning) {
                    if (!socket.isConnected()) {
                        System.out.println("Server is disconnected");
                        isRunning = false;
                        break;
                    }

                    String strFromServer = in.readUTF();

                    if (strFromServer.equalsIgnoreCase("/end")) {
                        System.out.println("Отключились от сервера");
                        isRunning = false;
                        break;
                    }

                    String[] msg = strFromServer.split(" ");
                    if (msg[0].equalsIgnoreCase("/name")) {
                        System.out.println("Сервер прислал наше имя: " + msg[1]);

                        name = msg[1];
                        file = new ChatFileWork(name);

                        clientInterface.clear();

                        inr.updateDialog(this.loadChat());
                    }else if (msg[0].equalsIgnoreCase("/changename")) {
                        System.out.println("Сервер прислал наше новое имя: " + msg[1]);

                        newName = msg[1];
                    }else {

                        inr.updateDialog(strFromServer);
                        System.out.println(strFromServer);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        /*
        Thread trOut = new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            try {
                while (isRunning) {
                    if (!socket.isConnected()) {
                        isRunning = false;
                        break;
                    }

                    String str = sc.nextLine();
                    out.writeUTF(str);
                    out.flush();

                    if (str.equalsIgnoreCase("/end")) {
                        isRunning = false;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        */
        tr1in.start();
        //trOut.start();

        try {
            tr1in.join();
            //trOut.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() throws IOException {
        isRunning = false;

        in.close();
        out.close();

        if (!socket.isClosed()) {
            socket.close();
        }

        file.close();
    }

    public void sendMessage(String text) {
        try {
            if (!isRunning) {
                return;
            }

            if (!socket.isConnected()) {
                isRunning = false;
                return;
            }

            if (socket == null) {
                isRunning = false;
                return;
            }

            out.writeUTF(text);
            out.flush();

            if (text.equalsIgnoreCase("/end")) {
                isRunning = false;
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void safeChat(String toString) {
        try {
            file.safeFile(toString, newName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String loadChat(){
        try {
            return file.loadFile100(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
