package Task;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;


public class ClientApp extends Frame implements ActionListener, Runnable {
    private TextField inputField = new TextField();
    private TextArea outputArea = new TextArea();
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Button button_exit = new Button("Exit");

    public ClientApp() {
        // Создаем окно
        super("Client");
        setLayout(null);
        setSize(300, 300);
        inputField.setBounds(50, 50, 200, 30);
        outputArea.setBounds(50, 100, 200, 150);
        outputArea.setEditable(false);
        add(button_exit);
        button_exit.setBounds(100, 260, 100, 30);
        button_exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == button_exit)
                    System.exit(0);
            }
        });


        add(inputField);
        add(outputArea);

        inputField.addActionListener(this);  // При нажатии Enter

        setVisible(true);
        setLocationRelativeTo(null);

        try {
            socket = new Socket("192.168.170.91", 3001);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            String amountStr = inputField.getText();
            int amount = Integer.parseInt(amountStr);

            if (amount < 0) {
                dos.writeUTF(amountStr);
                dos.flush();
            } else {
                outputArea.append("Введите отрицательное число!\n");
            }

            inputField.setText("");
        } catch (NumberFormatException ex) {
            outputArea.append("Введите корректное число!\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Чтение ответа от сервера
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