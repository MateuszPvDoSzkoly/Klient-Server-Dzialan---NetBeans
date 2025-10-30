package com.mycompany.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Employee {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 2011;

    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private DefaultListModel<String> userListModel;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private Map<String, PrivateChatWindow> privateChats = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Employee().startChat();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void startChat() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        frame = new JFrame("Chat");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane chatScroll = new JScrollPane(chatArea);

        messageField = new JTextField();
        sendButton = new JButton("Wyślij");

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        userListModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(userListModel);
        userList.setBorder(BorderFactory.createTitledBorder("Pracownicy"));
        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setPreferredSize(new Dimension(150, 0));

        frame.add(chatScroll, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(userScroll, BorderLayout.EAST);
        frame.setVisible(true);

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());

        userList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedUser = userList.getSelectedValue();
                    if (selectedUser != null) {
                        openPrivateMessageDialog(selectedUser);
                    }
                }
            }
        });

        new Thread(this::receiveMessages).start();

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try { socket.close(); } catch (IOException ignored) {}
            }
        });
    }

    private void sendMessage() {
        String text = messageField.getText().trim();
        if (!text.isEmpty()) {
            out.println(text);
            messageField.setText("");
        }
    }

    private void receiveMessages() {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                final String receivedMsg = msg;

                if (receivedMsg.startsWith("[USERS]")) {
                    updateUserList(receivedMsg);
                } else if (receivedMsg.startsWith("[PRYWATNA]")) {
                    SwingUtilities.invokeLater(() -> {
                        String sender = extractSender(receivedMsg);
                        if (sender != null) {
                            PrivateChatWindow chat = privateChats.get(sender);
                            if (chat == null) {
                                chat = new PrivateChatWindow(sender);
                                privateChats.put(sender, chat);
                            }
                            chat.appendMessage(receivedMsg);
                        } else {
                            chatArea.append("Nie można odczytać nadawcy wiadomości prywatnej.\n");
                        }
                    });
                } else {
                    chatArea.append(receivedMsg + "\n");
                }
            }
        } catch (IOException e) {
            chatArea.append("Połączenie z serwerem utracone.\n");
        }
    }

    private String extractSender(String msg) {
        try {
            int start = msg.indexOf("od ");
            int end = msg.indexOf(":", start);
            if (start != -1 && end != -1) {
                return msg.substring(start + 3, end).trim();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private void updateUserList(String msg) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            String[] users = msg.split(" ");
            for (int i = 1; i < users.length; i++) {
                userListModel.addElement(users[i]);
            }
        });
    }

    private void openPrivateMessageDialog(String recipient) {
        if (privateChats.containsKey(recipient)) {
            privateChats.get(recipient).bringToFront();
            return;
        }

        PrivateChatWindow chatWindow = new PrivateChatWindow(recipient);
        privateChats.put(recipient, chatWindow);
    }

    private class PrivateChatWindow {
        private JFrame window;
        private JTextArea chatHistory;
        private JTextField inputField;
        private String recipient;

        PrivateChatWindow(String recipient) {
            this.recipient = recipient;
            window = new JFrame("Prywatny czat z " + recipient);
            window.setSize(400, 300);
            window.setLayout(new BorderLayout(5, 5));

            chatHistory = new JTextArea();
            chatHistory.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(chatHistory);

            inputField = new JTextField();
            JButton sendButton = new JButton("Wyślij");

            JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
            bottomPanel.add(inputField, BorderLayout.CENTER);
            bottomPanel.add(sendButton, BorderLayout.EAST);

            window.add(scrollPane, BorderLayout.CENTER);
            window.add(bottomPanel, BorderLayout.SOUTH);
            window.setVisible(true);

            sendButton.addActionListener(e -> sendPrivateMessage());
            inputField.addActionListener(e -> sendPrivateMessage());

            window.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    privateChats.remove(recipient);
                }
            });
        }

        void bringToFront() {
            window.toFront();
            window.repaint();
        }

        void appendMessage(String msg) {
            chatHistory.append(msg + "\n");
        }

        private void sendPrivateMessage() {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                out.println("[PRIVATE] " + recipient + ": " + text);
                appendMessage("Ja: " + text);
                inputField.setText("");
            }
        }
    }
}
