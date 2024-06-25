import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class EchoServer {
    private final int port;
    private static final Set<Socket> clients = new HashSet<>();

    private EchoServer(int port) {
        this.port = port;
    }

    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public void run() {
        try (var server = new ServerSocket(port)) {
            System.out.printf("EchoServer  запущен на порту %d%n", port);
            while (true) {
                var clientSocket = server.accept();
                System.out.println("Клиент подключен: " + clientSocket.getRemoteSocketAddress());
                clients.add(clientSocket);
                new Thread(() -> Handler.handle(clientSocket)).start();
            }
        } catch (IOException e) {
            var formatMsg = "Порт %s, скорее всего, занят.%n";
            System.out.printf(formatMsg, port);
            e.printStackTrace();
        }
    }

    public static Set<Socket> getClients() {
        return clients;
    }
}
