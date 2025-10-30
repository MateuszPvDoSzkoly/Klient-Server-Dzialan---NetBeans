package com.mycompany.chat;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ServerJobChat {

    private static final int PORT = 2011;
    private static final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private static int employeeCounter = 0;

    public static void main(String[] args) {
        System.out.println("Chat Server uruchomiony na porcie " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();

                employeeCounter++;
                String userName = "Employee" + employeeCounter;
                ClientHandler handler = new ClientHandler(socket, userName);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.send(message);
        }
        System.out.println("" + message);
    }

    static void updateUserLists() {
        StringBuilder sb = new StringBuilder("[USERS]");
        for (ClientHandler c : clients) {
            sb.append(" ").append(c.getUserName());
        }
        String listMsg = sb.toString();
        for (ClientHandler c : clients) {
            c.send(listMsg);
        }
    }

    static void sendPrivate(String recipient, String message) {
        for (ClientHandler client : clients) {
            if (client.getUserName().equals(recipient)) {
                client.send(message);
                break;
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private final String userName;

        ClientHandler(Socket socket, String userName) {
            this.socket = socket;
            this.userName = userName;
        }

        String getUserName() {
            return userName;
        }

        void send(String msg) {
            if (out != null) out.println(msg);
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                clients.add(this);
                broadcast("[SERVER] " + userName + " dołączył do czatu.");
                updateUserLists();

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("[PRIVATE]")) {
                        String[] parts = line.split(":", 2);
                        if (parts.length == 2) {
                            String[] header = parts[0].split(" ");
                            String recipient = header[1];
                            String content = parts[1].trim();
                            sendPrivate(recipient, "[PRYWATNA] od " + userName + ": " + content);
                        }
                    } else {
                        broadcast(userName + ": " + line);
                    }
                }
            } catch (IOException e) {
                System.out.println("Klient rozłączony: " + userName);
            } finally {
                clients.remove(this);
                broadcast("[SERVER] " + userName + " opuścił czat.");
                updateUserLists();
                try {
                    socket.close();
                } catch (IOException ignored) {}
            }
        }
    }
}
