package com.mycompany.chat;

import java.io.*;
import java.net.Socket;
import javax.swing.*;

public class EmployeeChat extends javax.swing.JFrame implements Runnable {

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private static int employeeCounter = 0;
    private String employeeName;
    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(EmployeeChat.class.getName());

    public EmployeeChat(Socket socket) {
        this.socket = socket;
        initComponents();

        employeeCounter++;
        employeeName = "Employee" + employeeCounter;

        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setupChatUI();
        new Thread(this).start();
    } // <- zamknięcie konstruktora

    private static int localCounter = 0;

    public EmployeeChat() {
        try {
            socket = new Socket("localhost", 5000); // łączy z serwerem
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Nie można połączyć z serwerem: " + e.getMessage());
            System.exit(1);
        }

        initComponents();

        // 🔹 nadaj automatyczną nazwę
        localCounter++;
        employeeName = "Employee" + localCounter;

        setupChatUI();
        new Thread(this).start(); // odbiór wiadomości
    }



    
    
    // Tutaj zaczyna się metoda setupChatUI()
    private void setupChatUI() {
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String message = jTextField2.getText().trim();
                if (!message.isEmpty()) {
                    String formatted = employeeName + ": " + message;

                    // wysyłanie przez sieć
                    try {
                        if (out != null) {
                            PrintWriter writer = new PrintWriter(out, true);
                            writer.println(formatted);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    // 🔹 DODAJ TO:
                    String current = jTextArea1.getText();
                    jTextArea1.setText(formatted + "\n" + current);

                    // 🔹 Wyczyść pole wejściowe
                    jTextField2.setText("");


                    jTextField2.setText("");
                }
            }
        });
    }


    @Override
    public void run() {
        if (in == null) return;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final String msg = line;
                SwingUtilities.invokeLater(() -> {
                    if (jTextArea1 != null) {
                        String current = jTextArea1.getText();
                        jTextArea1.append(msg + "\n");
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));

        jButton1.setBackground(new java.awt.Color(0, 153, 255));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Wyślij");

        jScrollPane1.setBackground(new java.awt.Color(204, 204, 204));

        jList1.setBackground(new java.awt.Color(0, 102, 204));
        jList1.setForeground(new java.awt.Color(255, 255, 255));
        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                            .addComponent(jButton1)))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new EmployeeChat().setVisible(true));
    }
      


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
