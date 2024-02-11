package src;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class Play implements Closeable{

    enum commends{
        start, pudlo, trafiony, zatopiony, ostatni
    }


    private final Socket socket;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    static char letter = '@';
    static int num = 1;
    static int shipCounter = 10;

    public Play(String ip, int port ){
        try {
            this.socket = new Socket(ip, port);
            this.reader = createRider();
            this.writer = createWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Play(ServerSocket servet) throws IOException {
        this.socket = servet.accept();
        this.reader = createRider();
        this.writer = createWriter();
    }

    private BufferedReader createRider() throws IOException {
            return new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private BufferedWriter createWriter() throws IOException {
            return new BufferedWriter( new OutputStreamWriter(socket.getOutputStream()));

    }

    @Override
    public void close() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }

    public void write(String command) throws IOException {
        writer.write(command);
        writer.newLine();
        writer.flush();
    }

    public String read() throws IOException {
        return reader.readLine();
    }

    public String processingMes(String message) throws IOException {
        System.out.println(message);
        String[] data = message.split(";");

        if (data.length == 1){
            System.out.println("Przegrana\n");
            printMap( Start.pathEnemyMap );
            printMap( Start.pathMap );
            close();
        }
        commends c = null;
        while (c == null) {
            try {
                if (data[0] == "pudło") c = commends.pudlo;
                c = commends.valueOf(data[0]);
            } catch (Exception ignored) {
                System.out.println("Is not a commend:\t" + c);
            }
        }

        if (c == commends.pudlo) {
            changeInMap('.', Start.pathEnemyMap, num, letter - 65);
        }else if (c == commends.trafiony){
            changeInMap('#', Start.pathEnemyMap, num, letter - 65);
        } else if (c == commends.zatopiony) {
            changeInMap('#', Start.pathEnemyMap, num, letter - 65);
            changeAroundShip('?', '.','#', Start.pathEnemyMap, num, letter - 65, -1, -1 );
        } else if (c == commends.ostatni) {
            System.out.println("Wygrana\n");
            printMap(Start.pathEnemyMap);
            System.out.println("\n");
            printMap(Start.pathMap);
        }

        String answer = String.valueOf(check(data[1]));
        if (Objects.equals(answer, "pudlo")) {
            ByteBuffer buffer = StandardCharsets.UTF_8.encode("pudło");
            answer = StandardCharsets.UTF_8.decode(buffer).toString();
        }
        answer += ";";
        answer += createCoordinate();
        answer += "\n";

        return answer;
    }

    private void printMap(Path pathMap) throws IOException {
        List<String> map = Files.readAllLines(pathMap);
        for (String line : map) { System.out.println(line); }
        System.out.println();

    }

    public String createCoordinate() throws IOException {
        while(true) {
            letter++;

            if (letter == 'K') { letter = 'A'; num++; }

            List<String> lines = Files.readAllLines(Start.pathEnemyMap);
            String numLine = lines.get(num);

            if (numLine.charAt(letter - 65) == '?') return String.valueOf(letter) + num;
        }

    }

    private commends check(String coordinates) throws IOException {
        char letterCoordinates = coordinates.charAt(0);
        int numberCoordinates = Integer.parseInt(coordinates.substring(1)) - 1;

        List<String> lines = Files.readAllLines(Start.pathMap);
        String numLine = lines.get(numberCoordinates);

        int lCoordinate = letterCoordinates - 65;

        if (numLine.charAt( lCoordinate ) == '.' ||
                numLine.charAt( lCoordinate ) == '~'){

            changeInMap('~', Start.pathMap, numberCoordinates, lCoordinate);
            return commends.pudlo;
        }
        else if (numLine.charAt( lCoordinate ) == '#' ||
                    numLine.charAt( lCoordinate ) == '@') {

            changeInMap('@', Start.pathMap, numberCoordinates, lCoordinate);

            if (isSunk(lCoordinate, numberCoordinates, lines, -1, -1)){
                changeAroundShip('.', '~','@',Start.pathMap, numberCoordinates, lCoordinate, -1, -1);

                if (--shipCounter == 0) return commends.ostatni;

                return commends.zatopiony;
            }
            return commends.trafiony;
        }

        return null;
    }

    private void changeAroundShip(char old, char neew, char shipChar, Path pathMap, int nCoordinates, int lCoordinate, int alreadySeenN, int alreadySeenL) throws IOException {
        List<String> lines = Files.readAllLines(pathMap);
        if (nCoordinates == -1 || nCoordinates == 11 ) return;
        String numLine = lines.get(nCoordinates);

        if (nCoordinates != 0 && lines.get(nCoordinates - 1).charAt(lCoordinate) == old)
            changeInMap(neew, pathMap, nCoordinates - 1, lCoordinate);
        if (nCoordinates != 9 && lines.get(nCoordinates + 1).charAt(lCoordinate) == old)
            changeInMap(neew, pathMap, nCoordinates + 1, lCoordinate);
        if (lCoordinate != 0 && numLine.charAt( lCoordinate - 1) == old)
            changeInMap(neew, pathMap, nCoordinates, lCoordinate - 1);
        if (lCoordinate != 9 && numLine.charAt( lCoordinate + 1) == old)
            changeInMap(neew, pathMap, nCoordinates, lCoordinate + 1);


        if (nCoordinates != 0 && lines.get(nCoordinates - 1).charAt(lCoordinate) == shipChar) {
            if (nCoordinates - 1 == alreadySeenN && lCoordinate == alreadySeenL) return;
            changeAroundShip(old, neew, shipChar, pathMap, nCoordinates - 1, lCoordinate, nCoordinates, lCoordinate);
        }
        if (nCoordinates != 9 && lines.get(nCoordinates + 1).charAt(lCoordinate) == shipChar)
            changeAroundShip(old, neew, shipChar, pathMap , nCoordinates + 1, lCoordinate, nCoordinates, lCoordinate);
        if (lCoordinate != 0 && numLine.charAt( lCoordinate - 1) == shipChar) {
            if (nCoordinates == alreadySeenN && lCoordinate - 1 == alreadySeenL) return;
            changeAroundShip(old, neew, shipChar,pathMap, nCoordinates, lCoordinate - 1, nCoordinates, lCoordinate);
        }
        if (lCoordinate != 9 && numLine.charAt( lCoordinate + 1) == shipChar)
            changeAroundShip(old, neew, shipChar,pathMap , nCoordinates, lCoordinate + 1, nCoordinates, lCoordinate);
    }

    private void changeInMap(char c, Path pathMap, int numberCoordinates, int lCoordinate) throws IOException {
        List<String> lines = Files.readAllLines(pathMap);
        String line = lines.get(numberCoordinates);
        String newLine = line.substring(0, lCoordinate) + c + line.substring(lCoordinate+1);

        lines.set(numberCoordinates, newLine);

        StringBuilder newMap = new StringBuilder();
        for (String l : lines){ newMap.append(l).append("\n");}

        Files.writeString(pathMap, newMap.toString());
    }

    private boolean isSunk(int lCoordinate, int nCoordinates, List<String> lines, int alreadySeenL, int alreadySeenN) {
        if (nCoordinates == -1 || nCoordinates == 11 ) return false;
        String numLine = lines.get(nCoordinates);

        if ( nCoordinates != 0 && lines.get(nCoordinates - 1).charAt(lCoordinate) == '#' ||
                nCoordinates != 9 && lines.get(nCoordinates + 1).charAt(lCoordinate) == '#' ||
                    lCoordinate != 0 && numLine.charAt( lCoordinate - 1) == '#' ||
                        lCoordinate != 9 && numLine.charAt( lCoordinate + 1) == '#' )
            return false;

        boolean answer = true;
        if (nCoordinates != 0 && lines.get(nCoordinates - 1).charAt(lCoordinate) == '@') {
            if (nCoordinates - 1 == alreadySeenN && lCoordinate == alreadySeenL) return true;
            answer = isSunk(lCoordinate, nCoordinates - 1, lines, lCoordinate, nCoordinates);
        }
        if (nCoordinates != 9 && lines.get(nCoordinates + 1).charAt(lCoordinate) == '@')
            answer = isSunk(lCoordinate, nCoordinates + 1, lines, lCoordinate, nCoordinates);

        if (lCoordinate != 0 && numLine.charAt( lCoordinate - 1) == '@') {
            if (nCoordinates == alreadySeenN && lCoordinate - 1 == alreadySeenL) return true;
            answer = isSunk(lCoordinate - 1, nCoordinates, lines, lCoordinate, nCoordinates);
        }
        if (lCoordinate != 9 && numLine.charAt( lCoordinate + 1) == '@')
            answer = isSunk(lCoordinate + 1, nCoordinates, lines, lCoordinate, nCoordinates);
        return answer;
    }


}