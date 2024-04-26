import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int PORT = 6666;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println(in.readLine());
            String nickname = userInput.readLine();
            out.println(nickname);

            new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            String userInputMessage;
            while ((userInputMessage = userInput.readLine()) != null) {
                out.println(userInputMessage);
                if (userInputMessage.equalsIgnoreCase("quit")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
