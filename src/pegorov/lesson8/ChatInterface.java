package pegorov.lesson2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

public class ChatInterface extends JFrame {
    private int width = 500;
    private int sizeX = 150;
    private int sizeY = 250;

    private Client client;
    private JTextArea dialogField;

    public void initField(Client cl) {
        this.client = cl;

        setTitle("Chat");
        setDefaultCloseOperation(this.EXIT_ON_CLOSE);

        ChatWindowListener chatWindowListener = new ChatWindowListener();
        this.addWindowListener(chatWindowListener);

        setBounds(150, 150, width, sizeY);

        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        JPanel desk = new JPanel();

        dialogField = new JTextArea();
        dialogField.setEditable(false);
        dialogField.setLineWrap(true);
        dialogField.setWrapStyleWord(true);
        dialogField.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        JScrollPane jsp = new JScrollPane(dialogField,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JTextField textField = new JTextField();
        textField.addActionListener(action -> {
            sendMessage(textField);
        });

        JButton sendMessage = new JButton("Send");
        sendMessage.addActionListener(action -> {
            sendMessage(textField);
        });

        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        controlPanel.add(textField);
        controlPanel.add(sendMessage);
        controlPanel.setSize(width - sizeX, sizeY);

        desk.setLayout(new BoxLayout(desk, BoxLayout.X_AXIS));
        desk.add(jsp);
        desk.setSize(sizeX, sizeY);

        getContentPane().add(controlPanel, BorderLayout.SOUTH);
        getContentPane().add(desk, BorderLayout.CENTER);

        updateDialog("Чтобы выйти из чата введите: /end");
        updateDialog("Чтобы написать приватное сообщение введите: /w имя сообщение");
        updateDialog("Чтобы установить/изменить пароль введите: /setpassword пароль");
        updateDialog("Чтобы изменить имя: /setname имя");
        updateDialog("У вас есть 120сек на ввод имени!");
        updateDialog("Введите свое имя:");
        setVisible(true);
    }

    private void sendMessage(JTextField textField) {
        if (textField.getText().equals("")) {
            return;
        }

        client.sendMessage(textField.getText());
        textField.setText("");
    }

    public void updateDialog(String msg) {
        dialogField.append("\n" + msg);
    }

    public void clear() {
        dialogField.setText("");
    }

    public class ChatWindowListener implements WindowListener {

        @Override
        public void windowOpened(WindowEvent e) {

        }

        @Override
        public void windowClosing(WindowEvent e) {
            client.sendMessage("/end");
            client.safeChat(dialogField.getText());

            /*try {
                client.closeConnection();
            } catch (IOException ex) {
                ex.printStackTrace();
            }*/
        }

        @Override
        public void windowClosed(WindowEvent e) {

        }

        @Override
        public void windowIconified(WindowEvent e) {

        }

        @Override
        public void windowDeiconified(WindowEvent e) {

        }

        @Override
        public void windowActivated(WindowEvent e) {

        }

        @Override
        public void windowDeactivated(WindowEvent e) {

        }
    }
}
