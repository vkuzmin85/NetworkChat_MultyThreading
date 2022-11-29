package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Scanner;

public class Server {
    public static LinkedList<ServeClient> serverList = new LinkedList<>();
    static Logger log = Logger.getInstance();
    private static String logMessage;

    public static void main(String[] args) throws IOException {
        Scanner scanner1 = new Scanner(new File("settings.txt"));
        int port = Integer.parseInt(scanner1.nextLine());
        scanner1.close();
        ServerSocket server = new ServerSocket(port);
        logMessage = "Сервер запущен!";
        System.out.println(logMessage);
        log.logging(logMessage);
        try {
            while (true) {
                Socket socket = server.accept();
                try {
                    serverList.add(new ServeClient(socket));
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }

    public static class ServeClient extends Thread {

        private Socket socket;
        private BufferedReader in;
        private BufferedWriter out;

        public ServeClient(Socket socket) throws IOException {
            this.socket = socket;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            start();
        }

        @Override
        public void run() {
            String word;
            try {
                while (true) {
                    word = in.readLine();
                    logMessage = "На сервере получено сообщение от " + word;
                    System.out.println(logMessage);
                    log.logging(logMessage);
                    for (ServeClient sc : Server.serverList) {
                        if (!sc.equals(this)) {
                            sc.send(word);
                        }
                    }
                    if (word.contains("/exit")) {
                        logMessage = word.split(":")[0] + " вышел из чата";
                        System.out.println(logMessage);
                        log.logging(logMessage);
                        close();
                        break;
                    }
                }

            } catch (IOException e) {
                try {
                    close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

        private void send(String msg) throws IOException {
            try {
                out.write(msg + "\n");
                out.flush();
            } catch (IOException ignored) {
                close();
            }
        }

        private void close() throws IOException {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (ServeClient sc : Server.serverList) {
                    if (sc.equals(this)) {
                        sc.interrupt();
                        Server.serverList.remove(this);
                    }
                }
            }
        }
    }
}

