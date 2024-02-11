package src;

import java.io.*;
import java.util.Scanner;


public class Player {
    final String ip;
    final int port;
    StringBuilder map;

    static Scanner sc = new Scanner(System.in);


    public void start(){


        try( Play play = new Play(ip, port)){
            System.out.println("Connected to server");
            play.write( Play.commends.start + play.createCoordinate());

            while(true) {
                /*для игры вручную
                String command = sc.nextLine();
                play.write(command);

                String message = play.read();
                play.processingMes(message);*/
                String message = play.read();

                String answer = play.processingMes(message);

                System.out.println(answer);
                play.write( answer );
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public Player(String ip, int port, String map){
        this.ip = ip;
        this.port = port;
        this.map = new StringBuilder(map);
    }
}