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

    public Client(String name) throws IOException {
        try {
            this.name = name;
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

    private static String setName() {
        String s = "";
        System.out.println("Введите имя:");
        try {
            s = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return s;
    }

    public static void main(String[] args) {
        String name;
        Scanner scanner;
        try {
            scanner = new Scanner(new File("settings.txt"));
            int port = Integer.parseInt(scanner.nextLine());
            String ip = scanner.nextLine();
            scanner.close();
            reader = new BufferedReader(new InputStreamReader(System.in));
            name = setName();
            if (!name.contains("/exit")) {
                try {
                    clientSocket = new Socket(ip, port);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    new Client(name);
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
        }
    }

    private static void close() throws IOException {
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
                try {
                    close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
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
