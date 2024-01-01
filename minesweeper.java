import java.util.Scanner;
import java.util.Random;

/* GAME DESCRIPTION:
for each value of the board, use binary operators to determine whether the square at that position
is hidden, marked, or revealed. when a mine is overturned, the game ends. player wins when all
non-mines have been revealed.
*/

class minesweeper{
    //variable declaration
    private static final int marked = 0x10;
    private static final int value = 0x0F; 
    private static final int hidden = 0x20; 
    private static final int xMax = 39;
    private static final int yMax = 39;
    public static int currXDim;
    public static int currYDim;
    public static int currNumMines;

    public static void main(String[] args){
        System.out.println("");
        System.out.println("Welcome to Minesweeper!");
        System.out.println("The goal of this game is to locate all the mines by revealing the locations");
        System.out.println("of all non-mine squares. Once the player clicks on a mine, the game ends. ");
        System.out.println("");
        System.out.println("* Represents a square of hidden value.");
        System.out.println("M Represents a square that has been marked.");
        System.out.println("B Represents an unveiled mine.");
        System.out.println("An unveiled number represents the number of mines adjacent to that square.");
        System.out.println("");
        playGame();
    }
    
    /*
    at the start of each round, the user inputs the x-dimension, y-dimension, and the number of mines. 
    once a board has been successfully created, mine locations are randomized. board is then hidden.
    */
    public static int[] startGame(){
        Scanner dimX1 = new Scanner(System.in);
        System.out.println("Please enter the x-dimension (" + xMax + "): ");
        int xDim = dimX1.nextInt();
        while (xDim < 1 || xDim > xMax){
            System.out.println("This x-dimension is not within the bounds. Please try again: ");
            Scanner dimX2 = new Scanner(System.in);
            System.out.println("Please enter the x-dimension (" + xMax + "): ");
            xDim = dimX2.nextInt();
        }
        Scanner dimY1 = new Scanner(System.in);
        System.out.println("Please enter the y-dimension (" + yMax + "): ");
        int yDim = dimY1.nextInt();
        while (yDim < 1 || yDim > yMax){
            System.out.println("This y-dimension is not within the bounds. Please try again: ");
            Scanner dimY2 = new Scanner(System.in);
            System.out.println("Please enter the y-dimension (" + yMax + ")");
            yDim = dimY2.nextInt();
        }
        Scanner numMines1 = new Scanner(System.in);
        System.out.println("Please enter the number of mines: ");
        int numMines = numMines1.nextInt();
        while (numMines > xDim * yDim){
            Scanner numMines2 = new Scanner(System.in);
            System.out.println("The number of mines exceed the slots open on the board. Please try again: ");
            numMines = numMines2.nextInt();
        }
        //sets the public variables to their respective user input, so that the values are accessible elsewhere
        currXDim = xDim;
        currYDim = yDim;
        currNumMines = numMines;
        //creates board
        int[] gameBoard = createBoard(xDim, yDim);
        spreadMines(gameBoard, xDim, yDim, numMines);
        computeNeighbours(gameBoard, xDim, yDim);
        hideBoard(gameBoard, xDim, yDim);
        printBoard(gameBoard, xDim, yDim);
        //if the number of mines is equal to the size of the board, start another round
        if (currNumMines == xDim * yDim){
            for (int i = 0; i < currYDim; i++){
                for (int j = 0; j < currXDim; j++){
                    reveal(gameBoard, currXDim, currYDim, j+1, i+1);
                }
            }
            deleteBoard(gameBoard);
            return startGame();
        }
        //returns board array
        else{
            return gameBoard;
        }
    }

    //gets user action: whether they want to mark or show the location of the spot, or restart/quit
    public static int userAction(){
        Scanner userInput = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Actions available:");
        System.out.println("1 to Mark");
        System.out.println("2 to Show");
        System.out.println("3 to Restart");
        System.out.println("4 to Quit.");
        System.out.println("Please enter the corresponding number of your action: ");
        int playerAction = userInput.nextInt();  // Read user input

        if (playerAction != 1 && playerAction != 2 && playerAction != 3 && playerAction != 4){
            System.out.println("Input invalid! Please try again.");
            return userAction();
        }
        else{
            return playerAction;
        }
    }

    
    public static boolean playGame(){
        int[] gameBoard = startGame();
        int currAction = userAction();
        int xLoc;
        int yLoc;
        
        //continues prompting the user until game is won or user quits
        while (currAction != 4 && isWon(gameBoard, currXDim, currYDim) == false){
            if (currAction == 1){
                xLoc = getXLoc(currXDim);
                yLoc = getYLoc(currYDim);
                actionMark(gameBoard, currXDim, currYDim, xLoc, yLoc);
                printBoard(gameBoard, currXDim, currYDim);
            }
            else if (currAction == 2){
                xLoc = getXLoc(currXDim);
                yLoc = getYLoc(currYDim);
                actionShow(gameBoard, currXDim, currYDim, xLoc, yLoc);
                printBoard(gameBoard, currXDim, currYDim);
            }
            else if (currAction == 3){
                gameBoard = null;
                playGame();
            }
            if (isWon(gameBoard, currXDim, currYDim)){
                break;
            }
            currAction = userAction();
        }
        //quits
        if (currAction == 4){
            return false;
        }
        //if user has not quit but game is won, reveal all values of the board
        for (int i = 0; i < currYDim; i++){
            for (int j = 0; j < currXDim; j++){
                gameBoard[i * currXDim + j] &= ~hidden;
            }
        }
        printBoard(gameBoard, currXDim, currYDim);
        deleteBoard(gameBoard);
        playGame();
        return true;
    }

    //helper function to prompt user for the x location
    public static int getXLoc(int xDim){
        Scanner input1 = new Scanner(System.in);
        System.out.println("Please enter your desired x-location (max " + (xDim - 1) + "): ");
        int xLoc = input1.nextInt();
        while (xLoc > xDim){
            System.out.println("This input is out of bounds. Please try again.");
            Scanner input2 = new Scanner(System.in);
            System.out.println("Please enter your desired x-location (max " + (xDim - 1) + "): ");
            xLoc = input2.nextInt();
        }
        return xLoc;
    }

    //helper function to prompt user for the y location
    public static int getYLoc(int yDim){
        Scanner input1 = new Scanner(System.in);
        System.out.println("Please enter your desired y-location (max " + (yDim - 1) + "): ");
        int yLoc = input1.nextInt();
        while (yLoc > yDim){
            System.out.println("This input is out of bounds. Please try again.");
            Scanner input2 = new Scanner(System.in);
            System.out.println("Please enter your desired y-location (max " + (yDim - 1) + "): ");
            yLoc = input2.nextInt();
        }
        return yLoc;
    }

    //mark the value of a specified location
    public static void actionMark(int[] board, int xDim, int yDim, int xLoc, int yLoc){
        if (markLoc(board, xDim, yDim, xLoc, yLoc) == 1){
            System.out.println("This location is already revealed. Please try again.");
        }
    }

    //show the value of a specified location
    public static void actionShow(int[] board, int xDim, int yDim, int xLoc, int yLoc){
        if ((board[yLoc * xDim + xLoc] & marked) == marked){
            System.out.println("Location is marked, and cannot be revealed.");
            System.out.println("Use Mark on a location to unmark.");
        }
        else if (reveal(board, xDim, yDim, xLoc, yLoc) == 2){
            System.out.println("You have disturbed a mine! Game over.");
            printBoard(board, xDim, yDim);
            deleteBoard(board);
            startGame();
            return;
        }
    }

    //generate a random number
    public static int randnum(int max) {
        Random rand = new Random();
        return rand.nextInt(max);
      }

    //spread specified number of mines through the board
    public static void spreadMines(int[] board, int xDim, int yDim, int numMines){
        for (int mines = 0 ; mines < numMines; mines++){
            int genRan = randnum(xDim * yDim - 1);
            //if the value is not already a goose
            if ((board[genRan] & value) != 9){
                board[genRan] = 9;
            }
        }
    }

    //create an empty board
    public static int[] createBoard(int xDim, int yDim){
        int[] board = new int[xDim * yDim];
        return board;
    }

    //delete a board
    public static void deleteBoard(int[] board){
        board = null;
    }

    //hide all values of the board
    public static void hideBoard(int[] board, int xDim, int yDim){
        for (int i = 0; i < xDim * yDim; i++){
            board[i] |= hidden;
        }
    }

    //print the whole board
    public static void printBoard(int[] board, int xDim, int yDim){
        System.out.println("Printing Board: ");
        //printing column numbers
        for (int colNum = -1; colNum < xDim; colNum ++){
            if (colNum == -1){
                System.out.print("    ");
            }
            else if (colNum < 9){
                System.out.print(colNum + "  ");
            }
            else{
                System.out.print(colNum + " ");
            }
        }
        //printing row numbers
        System.out.println("");
        int rowNum = 0;
        for (int row = 0; row < yDim; row++){
            if (rowNum < 10){
                System.out.print(rowNum + "  ");
            }
            else{
                System.out.print(rowNum + " ");
            }
            rowNum++;
            for(int col = 0; col < xDim; col++){
                int indexNum = row * xDim + col;
                if ((board[indexNum] & marked) == marked){
                System.out.print("[M]");
                }
                else if ((board[indexNum] & hidden) == hidden){
                    System.out.print("[*]");
                }
                else{
                    if ((board[indexNum] & value) == 9){
                        System.out.print("[B]");
                    } 
                    else{
                        System.out.print("[" + (board[indexNum] & value) + "]");
                    }
                }
            }
        System.out.println();
        }
        
    }
    
    //checking whether or not the game is won
    public static boolean isWon(int[] board, int xDim, int yDim){
        for (int i = 0; i < xDim * yDim; i++){
            //if there a value on the board that is not 9 and is hidden
            if ((board[i] & value) != 9 && (board[i] & hidden) == hidden){
                return false;
            }
            //if there is a value on the board that is 9 and revealed
            else if ((board[i] & value) == 9 && (board[i] ^ hidden) == hidden){
                return false;
            }
        }
        System.out.println("You have revealed all the fields without disturbing a mine!");
        System.out.println("Congratulations! You have won!");
        return true;
    }

    //mark location
    public static int markLoc(int[] board, int xDim, int yDim, int xLoc, int yLoc){
        int indexNum = yLoc * xDim + xLoc;
        //if the location is already revealed, it cannot be marked
        if ((board[indexNum] ^ hidden) == hidden){
            return 1;
        }
        //toggle mark
        else{
            board[indexNum] ^= marked;
            return 0;
        }
    }

    //compute all the neighbouring mines to each square
    public static void computeNeighbours(int[] board, int xDim, int yDim){
        for(int row = 0; row < yDim; row++){
            for (int col = 0; col < xDim; col++){
                int indexNum = row * xDim + col;
                int neighbourCount = 0;
                if ((board[indexNum] & value) != 9){
                    //numbers above/below/beside, has to be within bounds of the set 3x3 matrix
                    for (int numAbove = 0; numAbove < 3; numAbove++){
                        int numAboveStart = indexNum - xDim - 1 + numAbove;
                        if (numAboveStart >= ((row - 1) * xDim) && numAboveStart < row * xDim && numAboveStart >= 0
                        && (board[numAboveStart] & value) == 9){
                            neighbourCount++;
                        }
                    }
                    for (int numBeside = 0; numBeside < 3; numBeside++){
                        int numBesideStart = indexNum - 1 + numBeside;
                        if (numBesideStart >= (row * xDim) && numBesideStart < (row + 1) * xDim
                        && (board[numBesideStart] & value) == 9 ){
                            neighbourCount++;
                        }
                    }
                    for (int numBelow = 0; numBelow < 3; numBelow++){
                        int numBelowStart = indexNum + xDim - 1 + numBelow;
                        if (numBelowStart >= (row + 1) * xDim && numBelowStart < (row + 2) * xDim
                        && numBelowStart < xDim * yDim && (board[numBelowStart] & value) == 9){
                            neighbourCount++;
                        }
                    }
                    board[indexNum] += neighbourCount;
                }
                else{
                    continue;
                }
            }
        }
    }

    //reveal specified location
    public static int reveal(int[] board, int xDim, int yDim, int xLoc, int yLoc){
        int indexNum = yLoc * xDim + xLoc;
        //if already marked, nothing happens
        if ((board[indexNum] & marked) == marked){
            return 1;
        }
        //if a mine, reveal and return
        else if ((board[indexNum] & value) == 9){
            board[indexNum] &= ~hidden;

            return 2;
        }
        else if((board[indexNum] & value) != 9){
            //if not mine and not 0, reveal and return
            if((board[indexNum] & value) != 0){
                board[indexNum] &= ~hidden;
                return 3;
            }
            //if not mine and 0, reveal all surrounding mines (recursively for 0 neighbours) and return
            else if((board[indexNum] & value) == 0){
                board[indexNum] &= ~hidden;
                for (int numAbove = 0; numAbove < 3; numAbove++){
                    int numAboveStart = indexNum - xDim - 1 + numAbove;
                    if (numAboveStart >= (yLoc - 1) * xDim && numAboveStart < yLoc * xDim && numAboveStart >= 0
                    && (board[numAboveStart] & value) != 9){
                        if ((board[numAboveStart] & value) == 0){
                            if ((board[numAboveStart] ^ hidden) == hidden){
                                continue;
                            }
                            else{
                                reveal(board, xDim, yDim, xLoc - 1 + numAbove, yLoc - 1);
                            }
                        }
                        board[numAboveStart] &= ~hidden;
                    }
                }
                for (int numBeside = 0; numBeside < 3; numBeside++){
                    int numBesideStart = indexNum - 1 + numBeside;
                    if ((numBesideStart >= yLoc * xDim) && numBesideStart < (yLoc + 1) * xDim
                    && (board[numBesideStart] & value) != 9 && numBesideStart < xDim * yDim && numBesideStart >= 0){
                        if ((board[numBesideStart] & value) == 0){
                            if ((board[numBesideStart] ^ hidden) == hidden){
                                continue;
                            }
                            else{
                                reveal(board, xDim, yDim, xLoc - 1 + numBeside, yLoc);
                            }
                        }
                        board[numBesideStart] &= ~hidden;
                    }
                }
                for (int numBelow = 0; numBelow < 3; numBelow++){
                    int numBelowStart = indexNum + xDim - 1 + numBelow;
                    if (numBelowStart >= ((yLoc + 1) * xDim) && numBelowStart < (yLoc + 2) * xDim && numBelowStart < xDim * yDim
                    && (board[numBelowStart] & value) != 9){
                        if ((board[numBelowStart] & value) == 0){
                            if ((board[numBelowStart] ^ hidden) == hidden){
                                continue;
                            }
                            else{
                                reveal(board, xDim, yDim, xLoc - 1 + numBelow, yLoc + 1);
                            }
                        } 
                        board[numBelowStart] &= ~hidden;
                    }
                }
            }
            return 4;
        }
        return 5;
    }
    
}












