package Task;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ClientApp extends Frame implements ActionListener, Runnable {
    private TextField inputField = new TextField();  // Поле для ввода суммы
    private TextArea outputArea = new TextArea();    // Поле для вывода информации
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public ClientApp() {
        // Создаем окно
        super("Client");
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
            // Получаем введенное значение
            String amountStr = inputField.getText();
            int amount = Integer.parseInt(amountStr);

            // Проверяем, что сумма отрицательная
            if (amount < 0) {
                // Отправляем введенную сумму на сервер
                dos.writeUTF(amountStr);
                dos.flush();
            } else {
                // Выводим предупреждение, если введено не отрицательное число
                outputArea.append("Введите отрицательное число!\n");
            }

            // Очищаем поле ввода
            inputField.setText("");
        } catch (NumberFormatException ex) {
            // Если ввод не является числом, выводим сообщение об ошибке
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