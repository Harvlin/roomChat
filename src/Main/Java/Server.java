import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 6666;
    private static final Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server Started");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket);
                ClientHandler clientHandler = new ClientHandler(socket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private String nickname;
        public ClientHandler(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true))
            {
                out.print("Enter your nickname:");
                nickname = in.readLine();
                synchronized (clients) {
                    clients.put(nickname, out);
                }
                broadcastMessage(nickname + " joined the chat");
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("quit")) {
                        break;
                    }
                    broadcastMessage(nickname + ": " + message);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                synchronized (clients) {
                    clients.remove(nickname);
                }
                broadcastMessage(nickname + " left the chat");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcastMessage(String message) {
            synchronized (clients) {
                for (PrintWriter writer : clients.values()) {
                    writer.println(message);
                }
            }
        }
    }
}
