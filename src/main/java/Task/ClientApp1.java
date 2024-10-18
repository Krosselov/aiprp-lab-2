package Task;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ClientApp1 extends Frame implements ActionListener, Runnable  {
    private TextField inputField = new TextField();
    private TextArea outputArea = new TextArea();
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Button button_exit = new Button("Exit");

    public ClientApp1() {
        // Создаем окно
        super("Client1");
        setLayout(null);
        setSize(300, 300);
        inputField.setBounds(50, 50, 200, 30);
        outputArea.setBounds(50, 100, 200, 150);
        outputArea.setEditable(false);
        add(inputField);
        add(outputArea);
        add(button_exit);
        button_exit.setBounds(100, 260, 100, 30);
        button_exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == button_exit)
                    System.exit(0);
            }
        });

        inputField.addActionListener(this);  // При нажатии Enter

        setVisible(true);
        setLocationRelativeTo(null);

        try {
            socket = new Socket("127.0.0.1", 3001);  // Подключение к серверу
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            // Отправляем введенную сумму на сервер
            String amount = inputField.getText();
            dos.writeUTF(amount);
            dos.flush();

            // Очищаем поле ввода
            inputField.setText("");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String response = dis.readUTF();
                outputArea.append(response + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClientApp clientApp = new ClientApp();
        new Thread(clientApp).start();
    }
}
