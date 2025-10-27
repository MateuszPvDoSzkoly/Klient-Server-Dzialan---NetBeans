import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import com.mycompany.chat.EmployeeChat;


public class ServerJobChat {
    public static void main(String[] args) {
        Vector<EmployeeChat> v = new Vector<>();
        try {
            ServerSocket s = new ServerSocket(2025);
            System.out.println("Serwer uruchomiony...");

            while (true) {
                Socket incoming = s.accept();
                System.out.println("Nowe połączenie: " + incoming);
                EmployeeChat thread = new EmployeeChat(incoming);
                v.addElement(thread);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
