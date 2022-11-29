package org.example;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String name;
    private static Socket clientSocket;
    private static BufferedReader reader;
    private static BufferedReader in;
    private static BufferedWriter out;
    private Logger log = Logger.getInstance();
    private static String logMessage;

    public Client() throws IOException {
        try {
            Scanner scanner1 = new Scanner(new File("settings.txt"));
            int port = Integer.parseInt(scanner1.nextLine());
            String ip = scanner1.nextLine();
            scanner1.close();
            clientSocket = new Socket(ip, port);
            reader = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            System.out.println("Введите имя:");
            name = reader.readLine();
            logMessage = "Клиент " + name + " зашел в чат";
            log.logging(logMessage);
            System.out.println(logMessage);
            new ReadMessage().start();
            new WriteMessage().start();
        } catch (IOException e) {
            System.err.println(e);
            close();
        }
    }

    private void close() throws IOException {
        if (!clientSocket.isClosed()) {
            clientSocket.close();
            in.close();
            out.close();
        }
    }

    private class ReadMessage extends Thread {
        @Override
        public void run() {
            String str;
            try {
                while (true) {
                    str = in.readLine();
                    if (str.contains("/exit")) {
                        str = str.split(":")[0] + ": вышел из чата";
                    }
                    logMessage = "Сообщение от пользователя " + str;
                    System.out.println(logMessage);
                    log.logging(logMessage);
                }
            } catch (IOException e) {
            }
        }
    }

    public class WriteMessage extends Thread {
        @Override
        public void run() {
            while (true) {
                String userWord;
                try {
                    userWord = reader.readLine();
                    if (userWord.contains("/exit")) {
                        out.write(name + ": " + "/exit" + "\n");
                        out.flush();
                        close();
                        break;
                    } else {
                        out.write(name + ": " + userWord + "\n");
                    }
                    out.flush();
                } catch (IOException e) {

                }
            }
        }
    }
}
