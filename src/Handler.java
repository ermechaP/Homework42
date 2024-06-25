import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Handler {
    static void handle(Socket socket) {
        try (var input = new InputStreamReader(socket.getInputStream(), "UTF-8");
             var scanner = new Scanner(input)) {
            while (true) {
                var message = scanner.nextLine().strip();
                System.out.printf("Got: %s%n", message);
                if (message.equalsIgnoreCase("bye")) {
                    System.out.println("Bye bye");
                    break;
                }
                msgSender(message, socket);
            }
        } catch (NoSuchElementException | IOException ex) {
            System.out.println("Клиент разорвал соединение:" + socket.getRemoteSocketAddress());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            EchoServer.getClients().remove(socket);
        }
    }


    private static void msgSender(String message, Socket sender) {
        synchronized (EchoServer.getClients()) {
            for (Socket client : EchoServer.getClients()) {
                if (client != sender) {
                    try {
                        var out = new PrintWriter(client.getOutputStream(), true);
                        out.println(message);
                    } catch (IOException e) {
                        System.out.println("Ошибка отправки сообщения клиенту:" + client.getRemoteSocketAddress());
                        EchoServer.getClients().remove(client);
                        try {
                            client.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
