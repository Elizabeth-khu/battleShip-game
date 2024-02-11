package src;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
public class Start {
    static Scanner sc = new Scanner(System.in);
    static Path pathMap;
    static Path pathEnemyMap;

    public static void main(String[] args) {
        System.out.println("Who are you today? ... ");
        String mode = sc.nextLine();

        System.out.println("Please give me a port ... ");
        int port = Integer.parseInt(sc.nextLine());

        GeneratorMap generatorMap = new GeneratorMap();
        String map = generatorMap.generateMap();

        createMapFile(map);



        if (mode.equals("Player")){
            System.out.println("Please give me a server to connect ... ");
            String server = sc.nextLine();
            Player player = new Player(server, port, map);
            player.start();
        }else if(mode.equals("Server")){
            Server server = new Server(port, map);
            server.start();

        }
    }

    private static void createMapFile(String map) {
        try {
            File myMap = new File("map.txt");
            Files.writeString(myMap.toPath(), map);
            pathMap = Path.of(myMap.getAbsolutePath());

            File enemyMap = new File("enemyMap.txt");

            StringBuilder data = new StringBuilder();
            data.append("??????????\n".repeat(10));

            Files.writeString(enemyMap.toPath(), data );
            pathEnemyMap = Path.of(enemyMap.getAbsolutePath());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
