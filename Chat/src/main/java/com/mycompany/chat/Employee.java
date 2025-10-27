import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class Employee {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 2020);
             BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Połączono z serwerem! Możesz pisać wiadomości:");

            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    System.out.println("Rozłączono z serwerem.");
                }
            }).start();

            String line;
            while ((line = console.readLine()) != null) {
                out.println(line);
                if (line.equalsIgnoreCase("exit")) break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
