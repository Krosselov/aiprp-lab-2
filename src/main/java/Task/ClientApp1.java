package Task;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ClientApp1 extends Frame implements ActionListener, Runnable  {
    private TextField inputField = new TextField();  // Поле для ввода суммы
    private TextArea outputArea = new TextArea();    // Поле для вывода информации
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

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

        inputField.addActionListener(this);  // При нажатии Enter обрабатываем ввод

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
                // Чтение ответа от сервера
                String response = dis.readUTF();
                outputArea.append(response + "\n");  // Выводим ответ в текстовое поле
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ClientApp clientApp = new ClientApp();
        new Thread(clientApp).start();  // Запуск клиента в отдельном потоке
    }
}
