import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import com.mycompany.chat.MainJFrame;

public class ServerChat {
    private static final Vector<PrintWriter> clientOutputs = new Vector<>();
    private static MainJFrame serverWindow;

    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(2020);
            System.out.println("Serwer działa na porcie 2020...");

            // tworzymy główne okno serwera
            serverWindow = new MainJFrame();
            serverWindow.setVisible(true);
            serverWindow.setServerSender(ServerChat::broadcastFromServer);

            // akceptowanie klientów
            while (true) {
                Socket client = server.accept();
                System.out.println("Nowe połączenie: " + client.getInetAddress());

                PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
                clientOutputs.add(out);

                // uruchamiamy wątek do czytania od tego klienta
                new Thread(() -> handleClient(client, out)).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket client, PrintWriter out) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                String msg = "[" + client.getInetAddress() + "]: " + line;
                System.out.println(msg);
                serverWindow.appendMessage(msg);
                broadcast(msg); 
            }
        } catch (Exception e) {
            System.out.println("Klient rozłączony: " + client.getInetAddress());
            clientOutputs.remove(out);
        }
    }

    private static void broadcast(String message) {
        synchronized (clientOutputs) {
            for (PrintWriter writer : clientOutputs) {
                writer.println(message);
            }
        }
    }
    
    private static void broadcastFromServer(String message) {
        String fullMessage = "[SERVER]: " + message;
        System.out.println(fullMessage);
        serverWindow.appendMessage(fullMessage);
        broadcast(fullMessage);
    }
}
