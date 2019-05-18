package istia.ei4.ProjetISTIA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Alain on 04/02/2015.
 */
public class MapGenerator {

    Random rand;
    int boardSize=16; // TODO: still crashes on other than 16
    int boardXSize=16; // TODO
    int boardYSize=16; // TODO


    int carrePosX = boardSize/2-1; // horizontal position of the top wall of carré, starting with 0
    int carrePosY = boardSize/2-1; // vertical position the left wall of the carré

    Boolean targetMustBeInCorner = true;
    Boolean allowMulticolorTarget = true;
    Boolean generateNewMapEachTime = false; // TODO: add option in settings

    int maxWallsInOneVerticalCol = 2;
    int maxWallsInOneHorizontalRow = 2;

    Boolean loneWallsAllowed = false; // TODO: walls that are not attached in a 90 deg. angle


    public MapGenerator(){
        rand = new Random();

        if(GridGameScreen.getLevel()!="Beginner"){
            // random position of carré in the middle
            carrePosX=getRandom(3,boardSize-5);
            carrePosY=getRandom(3,boardSize-5);

            allowMulticolorTarget = false;

            maxWallsInOneVerticalCol = 3;
            maxWallsInOneHorizontalRow = 3;

            loneWallsAllowed = true;
        }

        if(GridGameScreen.getLevel()=="Insane") {
            targetMustBeInCorner = false;

            maxWallsInOneVerticalCol = 5;
            maxWallsInOneHorizontalRow = 5;
        }
    }

    public ArrayList<GridElement> removeGameElementsFromMap(ArrayList<GridElement> data) {
        String[] gameElementTypes = {"rv", "rj", "rr", "rb", "cv", "cj", "cr", "cb", "cm"};
        for (GridElement e: data){
            if(Arrays.asList(gameElementTypes).contains(e.getType())){
                data.remove(e);
            }
        }
        return data;
    }

    public ArrayList<GridElement> translateArraysToMap(int[][] horizontalWalls, int[][] verticalWalls) {
        ArrayList<GridElement> data = new ArrayList<GridElement>();

        for(int x=0; x<=boardSize; x++)
            for(int y=0; y <= boardSize; y++)
            {
                if(horizontalWalls[x][y]== 1) {
                    // add all horizontal walls
                    data.add(new GridElement(x,y,"mh"));
                }
                if(verticalWalls[x][y]== 1) {
                    // add all vertical walls
                    data.add(new GridElement(x,y,"mv"));
                }
            }
        return data;
    }

    public int getRandom(int min, int max) {
        return rand.nextInt((max - min) + 1) + min;
    }

    public ArrayList<GridElement> addGameElementsToGameMap(ArrayList<GridElement> data ,int[][] horizontalWalls, int[][]verticalWalls){

        Boolean abandon;
        int cibleX;
        int cibleY;
        Boolean tempTargetMustBeInCorner;

        tempTargetMustBeInCorner = targetMustBeInCorner;
        if(!targetMustBeInCorner && getRandom(0,1) != 1){
            // 50% probability that the target is in a corner
            tempTargetMustBeInCorner=true;
        }
        do{
            abandon = false;
            cibleX = getRandom(0, boardSize-1);
            cibleY = getRandom(0, boardSize-1);

            if(tempTargetMustBeInCorner && horizontalWalls[cibleX][cibleY] == 0 && horizontalWalls[cibleX][cibleY+1] == 0)
                abandon = true;
            if(tempTargetMustBeInCorner && verticalWalls[cibleX][cibleY] == 0 && verticalWalls[cibleX+1][cibleY] == 0)
                abandon = true;

            if((cibleX == carrePosX && cibleY == carrePosY)
                    || (cibleX == carrePosX && cibleY == carrePosY+1)
                    || (cibleX == carrePosX+1 && cibleY == carrePosY)
                    || (cibleX == carrePosX+1 && cibleY == carrePosY+1))
                abandon = true; // target was in carré

        }while(abandon);

        String typesOfCibles[] = {"cj","cr","cb", "cv", "cm"};

        if(allowMulticolorTarget) {
            data.add(new GridElement(cibleX, cibleY, typesOfCibles[getRandom(0,4)]));
        } else {
            data.add(new GridElement(cibleX, cibleY, typesOfCibles[getRandom(0,3)]));
        }

        String typesOfRobots[] = {"rr", "rb", "rj", "rv"};

        ArrayList<GridElement> robotsTemp = new ArrayList<>();

        int cX;
        int cY;

        for(String currentRobotType : typesOfRobots)
        {
            do {
                abandon = false;
                cX = getRandom(0, boardSize-1);
                cY = getRandom(0, boardSize-1);

                for(GridElement robot:robotsTemp) {
                    if (robot.getX() == cX && robot.getY() == cY)
                        abandon = true;
                }

                if((cX == carrePosX && cY == carrePosY) || (cX == carrePosX && cY == carrePosY+1) || (cX == carrePosX+1 && cY == carrePosY) || (cX == carrePosX+1 && cY == carrePosY+1))
                    abandon = true; // robot was inside carré
                if(cX == cibleX && cY == cibleY)
                    abandon = true; // robot was target

            }while(abandon);
            robotsTemp.add(new GridElement(cX, cY, currentRobotType));
        }
        data.addAll(robotsTemp);

        return data;
    }

    /**
     * generates a new map
     * @return Arraylist with all grid elements that belong to the map
     */
    public ArrayList<GridElement> getGeneratedGameMap() {
        int[][] horizontalWalls = new int[boardSize+1][boardSize+1];
        int[][] verticalWalls = new int[boardSize+1][boardSize+1];

        int temp = 0;
        int countX = 0;
        int countY = 0;

        Boolean restart;

        do {
            restart = false;

            //On initialise avec aucun mur
            for (int x = 0; x < boardSize; x++)
                for (int y = 0; y < boardSize; y++)
                    horizontalWalls[x][y] = verticalWalls[x][y] = 0;

            //Création des bords
            for (int x = 0; x < boardSize; x++) {
                horizontalWalls[x][0] = 1;
                horizontalWalls[x][boardSize] = 1;
            }
            for (int y = 0; y < boardSize; y++) {
                verticalWalls[0][y] = 1;
                verticalWalls[boardSize][y] = 1;
            }

            //Murs près de la bordure gauche
            horizontalWalls[0][getRandom(2, 7)] = 1;
            do {
                temp = getRandom(8, boardSize-1);
            }
            while (horizontalWalls[0][temp - 1] == 1 || horizontalWalls[0][temp] == 1 || horizontalWalls[0][temp + 1] == 1);
            horizontalWalls[0][temp] = 1;

            //Murs près de la bordure droite
            horizontalWalls[boardSize-1][getRandom(2, 7)] = 1;
            do {
                temp = getRandom(8, boardSize-1);
            }
            while (horizontalWalls[boardSize-1][temp - 1] == 1 || horizontalWalls[boardSize-1][temp] == 1 || horizontalWalls[boardSize-1][temp + 1] == 1);
            horizontalWalls[boardSize-1][temp] = 1;

            //Murs près de la bordure haut
            verticalWalls[getRandom(2, 7)][0] = 1;
            do {
                temp = getRandom(8, boardSize-1);
            }
            while (verticalWalls[temp - 1][0] == 1 || verticalWalls[temp][0] == 1 || verticalWalls[temp + 1][0] == 1);
            verticalWalls[temp][0] = 1;

            //Murs près de la bordure bas
            verticalWalls[getRandom(2, 7)][boardSize-1] = 1;
            do {
                temp = getRandom(8, boardSize-1);
            }
            while (verticalWalls[temp - 1][boardSize-1] == 1 || verticalWalls[temp][boardSize-1] == 1 || verticalWalls[temp + 1][boardSize-1] == 1);
            verticalWalls[temp][boardSize-1] = 1;

            //Dessin du carré du milieu
            horizontalWalls[carrePosX][carrePosY] = horizontalWalls[carrePosX + 1][carrePosY] = 1;
            horizontalWalls[carrePosX][carrePosY+2] = horizontalWalls[carrePosX + 1][carrePosY+2] = 1;
            verticalWalls[carrePosX][carrePosY] = verticalWalls[carrePosX][carrePosY + 1] = 1;
            verticalWalls[carrePosX+2][carrePosY] = verticalWalls[carrePosX+2][carrePosY + 1] = 1;

            for (int k = 0; k <= boardSize; k++) {
                Boolean abandon = false;
                int tempX = 0;
                int tempY = 0;
                int tempXv = 0;
                int tempYv = 0;

                long compteLoop1 = 0;
                do {
                    compteLoop1++;
                    abandon = false;

                    //Choix de coordonnées aléatoires dans chaque quart de terrain de jeu
                    if (k < 4) {
                        tempX = getRandom(1, 7);
                        tempY = getRandom(1, 7);
                    } else if (k < 8) {
                        tempX = getRandom(8, boardSize-1);
                        tempY = getRandom(1, 7);
                    } else if (k < 12) {
                        tempX = getRandom(1, 7);
                        tempY = getRandom(8, boardSize-1);
                    } else if (k < 16) {
                        tempX = getRandom(8, boardSize-1);
                        tempY = getRandom(8, boardSize-1);
                    } else {
                        tempX = getRandom(1, boardSize-1);
                        tempY = getRandom(1, boardSize-1);
                    }

                    if (horizontalWalls[tempX][tempY] == 1 // already chosen
                        || horizontalWalls[tempX - 1][tempY] == 1 // left
                        || horizontalWalls[tempX + 1][tempY] == 1 // right
                        || horizontalWalls[tempX][tempY - 1] == 1 // directly above
                        || horizontalWalls[tempX][tempY + 1] == 1 // directly below
                        ) abandon = true;

                    if (verticalWalls[tempX][tempY] == 1 // already chosen
                        || verticalWalls[tempX + 1][tempY] == 1 // left
                        || verticalWalls[tempX][tempY - 1] == 1 // above
                        || verticalWalls[tempX + 1][tempY - 1] == 1 // diagonal right-above
                        ) abandon = true;

                    if (!abandon) {
                        //On compte le nombre de murs dans la même ligne/colonne
                        countX = countY = 0;
                        
                        for (int x = 1; x < boardSize-1; x++) {
                            if (horizontalWalls[x][tempY] == 1)
                                countX++;
                        }
                        
                        for (int y = 1; y < boardSize-1; y++) {
                            if (horizontalWalls[tempX][y] == 1)
                                countY++;
                        }
                        
                        if (tempY == carrePosY || tempY == carrePosY+2) {
                            countX -= 2;
                        }
                        if (countX >= maxWallsInOneHorizontalRow || countY >= maxWallsInOneVerticalCol) //Si il y a trop de murs dans la même ligne/colonne, on abandonne
                            abandon = true;
                    }

                    if (!abandon) {
                        //Choix du 2ème mur du coin en cours de dessin
                        tempXv = tempX + getRandom(0, 1);
                        tempYv = tempY - getRandom(0, 1);

                        //On vérifie qu'il ne tombe pas dessus ou près de murs déja existant
                        if (verticalWalls[tempXv][tempYv] == 1 || verticalWalls[tempXv - 1][tempYv] == 1 || verticalWalls[tempXv + 1][tempYv] == 1)
                            abandon = true;
                        if (verticalWalls[tempXv][tempYv - 1] == 1 || verticalWalls[tempXv][tempYv + 1] == 1)
                            abandon = true;

                        if (horizontalWalls[tempXv][tempYv] == 1 || horizontalWalls[tempXv - 1][tempYv] == 1)
                            abandon = true;

                        if (horizontalWalls[tempXv][tempYv - 1] == 1 || horizontalWalls[tempXv - 1][tempYv - 1] == 1)
                            abandon = true;

                        if (verticalWalls[tempXv - 1][tempYv - 1] == 1 || verticalWalls[tempXv - 1][tempYv + 1] == 1)
                            abandon = true;

                        if (verticalWalls[tempXv + 1][tempYv + 1] == 1 || verticalWalls[tempXv + 1][tempYv - 1] == 1)
                            abandon = true;

                        if (!abandon) {
                            //On compte le nombre de murs dans la même ligne/colonne
                            countX = countY = 0;

                            for (int x = 1; x < boardSize-1; x++) {
                                if (verticalWalls[x][tempYv] == 1)
                                    countX++;
                            }

                            for (int y = 1; y < boardSize-1; y++) {
                                if (verticalWalls[tempXv][y] == 1)
                                    countY++;
                            }

                            if (tempXv == carrePosX || tempXv == carrePosX+2) {
                                countY -= 2;
                            }
                            if (countX >= maxWallsInOneHorizontalRow || countY >= maxWallsInOneVerticalCol) //Si il y a trop de murs dans la même ligne/colonne, on abandonne
                                abandon = true;
                        }

                    }

                    if (compteLoop1 > 1000) {
                        restart = true;
                    }

                } while (abandon && !restart);
                horizontalWalls[tempX][tempY] = 1;
                verticalWalls[tempXv][tempYv] = 1;
            }
        }while(restart);

        ArrayList<GridElement> data = GridGameScreen.getMap();

        if(data == null || generateNewMapEachTime){
            data = translateArraysToMap(horizontalWalls, verticalWalls);
            GridGameScreen.setMap(data);
        } else{
            data = removeGameElementsFromMap(data);
        }

        data = addGameElementsToGameMap(data, horizontalWalls, verticalWalls);

        return data;
    }
}
