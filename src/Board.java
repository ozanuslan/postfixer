import java.util.Random;

class Board {
    private String[][] BOARD;
    private String[] SYMBOLS = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "-", "*", "/" };
    private Queue INPUTQUEUE;
    private int inputQueueSize;
    private Random rnd;
    private int startingSymbolCount;

    public Board() {
        inputQueueSize = 200;
        startingSymbolCount = 40;
        BOARD = new String[10][10];
        INPUTQUEUE = new Queue(inputQueueSize);
        rnd = new Random();
        fillInputQueueRandomly();
        clearBoard();
        fillBoardRandomly(startingSymbolCount);
    }

    public String[][] getBoard() {
        return BOARD;
    }

    public Queue getInputQueue(){
        return INPUTQUEUE;
    }

    void fillInputQueueRandomly(){
        int rndNum;
        for(int i = 0; i < inputQueueSize; i++){
            rndNum = rnd.nextInt(SYMBOLS.length);
            INPUTQUEUE.enqueue(SYMBOLS[rndNum]);
        }
    }

    void clearBoard() {
        for (int i = 0; i < BOARD.length; i++) {
            for (int j = 0; j < BOARD[i].length; j++) {
                BOARD[i][j] = ".";
            }
        }
    }

    void fillBoardRandomly(int symbolCount) {
        int randomRowIndex;
        int randomColIndex;
        String randomSymbol;
        boolean cannotPlaceSymbol;

        for (int i = 0; i < symbolCount; i++) {
            randomSymbol = (String)INPUTQUEUE.dequeue();
            cannotPlaceSymbol = true;

            while (cannotPlaceSymbol) {
                randomRowIndex = rnd.nextInt(BOARD.length);
                randomColIndex = rnd.nextInt(BOARD[randomRowIndex].length);

                if (BOARD[randomRowIndex][randomColIndex].contains(".")) { //Tried using equals to see if there exists a dot on the boards specified index but it does not work
                    BOARD[randomRowIndex][randomColIndex] = randomSymbol;
                    cannotPlaceSymbol = false;
                }
            }
        }
    }

    void displayBoard() {
        for (int i = 0; i < BOARD.length; i++) {
            for (int j = 0; j < BOARD[i].length; j++) {
                    System.out.print(BOARD[i][j]);
            }
            System.out.println();
        }
    }
}