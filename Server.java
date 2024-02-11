package src;

import java.io.*;
import java.net.ServerSocket;

public class Server {

    final int port;
    StringBuilder map;

        public void start(){
        try( ServerSocket server = new ServerSocket(port))
        {
            System.out.println("Server started");


            try (Play play = new Play(server)) {
                while (true) {

                    String message = play.read();

                    String answer = play.processingMes(message);

                    System.out.println(answer);
                    play.write( answer );
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        }
    public Server(int port, String map){
        this.port = port;
        this.map = new StringBuilder(map);
    }
}
