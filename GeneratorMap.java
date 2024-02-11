package src;

import java.util.Random;

public class GeneratorMap {
    StringBuilder plansza = BuildPlansza();

    public String generateMap() {


        AddCzteroMasztowiec();

        AddTrojmastowiec();

        AddDwoMastowiec();

        AddJednoMastowiec();


        for (int i = 10; i < plansza.length(); i += 10 + 1) {
            plansza.insert(i, "\n");
        }

        return String.valueOf(plansza);
    }

    private void AddJednoMastowiec() {
        for (int i = 0; i <= 3; i++) {
            int p1 = GeneratePoint();
            while (!VerifyPoint(p1)) {
                p1 = GeneratePoint();
            }
            plansza.setCharAt(p1, '#');
        }

    }

    int iloscDwomast = 0;

    private void AddDwoMastowiec() {
        if (iloscDwomast == 3) return;
        for (int i = 0; i <= 2; i++) {
            int p1 = GeneratePoint();
            while (!VerifyPoint(p1)) {
                p1 = GeneratePoint();
            }
            int p2 = choseNextPoint(-1, p1, 1);
            if (p2 == -3) {
                AddDwoMastowiec();

            } else if (iloscDwomast < 3) {

                plansza.setCharAt(p1, '#');
                plansza.setCharAt(p2, '#');
                iloscDwomast++;
            }
        }

    }

    int iloscTroj = 0;

    private void AddTrojmastowiec() {
        if (iloscTroj == 2) return;
        for (int i = 0; i <= 1; i++) {
            int p1 = GeneratePoint();
            while (!VerifyPoint(p1)) {
                p1 = GeneratePoint();
            }

            int p2 = choseNextPoint(-1, p1, 1);
            if (p2 == -3) {
                AddTrojmastowiec();
            } else {

                int p3 = choseNextPoint(p1, p2, 2);
                if (p3 == -3) {
                    AddTrojmastowiec();
                } else if (iloscTroj < 2) {

                    plansza.setCharAt(p1, '#');
                    plansza.setCharAt(p2, '#');
                    plansza.setCharAt(p3, '#');
                    iloscTroj++;
                }
            }
        }

    }


    void AddCzteroMasztowiec() {


        int p1 = GeneratePoint();

        int p2 = choseNextPoint(-1, p1, 1);

        int p3 = choseNextPoint(p1, p2, 1);

        int p4 = choseNextPoint(p2, p3, 1);

        plansza.setCharAt(p1, '#');
        plansza.setCharAt(p2, '#');
        plansza.setCharAt(p3, '#');
        plansza.setCharAt(p4, '#');

    }

    private int choseNextPoint(int grany, int p, int i) {

        int counter = 1;

        while (true) {

            switch (i) {
                case 1 -> {
                    if (p + 1 <= 99 && p % 10 != 9 && p + 1 != grany && VerifyPoint(p + 1)) {
                        return p + 1;
                    }
                    i = 2;
                }
                case 2 -> {
                    if (p - 10 >= 0 && p - 10 != grany && VerifyPoint(p - 10)) return p - 10;
                    i = 3;
                }
                case 3 -> {
                    if (p - 1 >= 0 && p % 10 != 0 && p - 1 != grany && VerifyPoint(p - 1)) return p - 1;
                    i = 4;
                }
                case 4 -> {
                    if (p + 10 <= 99 && p + 10 != grany && VerifyPoint(p + 10)) return p + 10;
                    i = 1;
                }
            }
            counter++;
            if (counter == 5) return -3;
        }

    }

    Boolean VerifyPoint(int id) {

        if (isAMaszt(id)) return false;

        if (areDiagonalMaszty(id)) return false;

        return !areShips(id);
    }

    private boolean areShips(int id) {
        boolean answer;
        if (id - 1 >= 0 && id % 10 != 0) {
            answer = isAMaszt(id - 1);
            if (answer) return true;
        }

        if (id - 10 >= 0) {
            answer = isAMaszt(id - 10);
            if (answer) return true;
        }

        if (id + 1 <= 99 && id % 10 != 9) {
            answer = isAMaszt(id + 1);
            if (answer) return true;
        }

        if (id + 10 <= 99) {
            answer = isAMaszt(id + 10);
            return answer;
        }

        return false;
    }

    private boolean areDiagonalMaszty(int id) {
        boolean answer;
        if (id - 11 >= 0 && id % 10 != 0) {
            answer = isAMaszt(id - 11);
            if (answer) return true;
        }
        if (id - 9 >= 0 && id % 10 != 9) {
            answer = isAMaszt(id - 9);
            if (answer) return true;
        }
        if (id + 11 <= 99 && id % 10 != 9) {
            answer = isAMaszt(id + 11);
            if (answer) return true;
        }
        if (id + 9 <= 99 && id % 10 != 0) {
            answer = isAMaszt(id + 9);
            return answer;
        }

        return false;
    }

    private Boolean isAMaszt(int id) {
        return plansza.charAt(id) == '#';
    }

    private int GeneratePoint() {
        Random rand = new Random();
        return rand.nextInt(100);
    }

    private StringBuilder BuildPlansza() {
        StringBuilder plansza = new StringBuilder("");
        plansza.append(".".repeat(100));
        return plansza;
    }


}



