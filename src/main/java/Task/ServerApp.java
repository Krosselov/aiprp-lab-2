package Task;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApp extends Frame implements Runnable {
    private static int balance = 0;  // Общий счет
    private TextArea textArea = new TextArea();  // Для отображения баланса
    private ServerSocket serverSocket;
    private Button button = new Button("Add Client");

    public ServerApp() {
        // Создаем окно
        super("Server Balance");
        setLayout(null);
        setSize(300, 200);
        textArea.setEditable(false);
        textArea.setBounds(50, 50, 200, 100);
        add(button);
        button.setBounds(50, 200, 150, 30);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Thread(()->{
                    ClientApp1 clientApp1 = new ClientApp1();
                    clientApp1.run();
                }).start();
            }
        });
        add(textArea);



        updateBalance();
        setVisible(true);
        setLocationRelativeTo(null);

        try {
            serverSocket = new ServerSocket(3001);  // Создаем сервер на порту 3001
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для обновления отображаемого баланса
    private void updateBalance() {
        textArea.setText("Current balance: " + balance);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();  // Ожидание подключения клиента
                new ClientHandler(clientSocket).start();  // Запуск потока для каждого клиента
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Класс для обработки каждого клиента
    private class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (DataInputStream dis = new DataInputStream(socket.getInputStream());
                 DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

                while (true) {
                    // Чтение суммы от клиента
                    String input = dis.readUTF();
                    int amount = Integer.parseInt(input);

                    // Обновление баланса
                    synchronized (ServerApp.class) {
                        balance += amount;
                    }

                    // Обновляем отображение баланса
                    updateBalance();

                    // Отправляем обновленный баланс клиенту
                    dos.writeUTF("Updated balance: " + balance);
                    dos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ServerApp serverApp = new ServerApp();
        new Thread(serverApp).start();  // Запуск сервера в отдельном потоке
    }
}