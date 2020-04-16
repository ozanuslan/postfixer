import java.util.Random;

class Board {
    private String[][] BOARD;
    private String[] SYMBOLS = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "-", "*", "/" };
    private Queue INPUTQUEUE;
    private int inputQueueSize;
    private Random rnd;
    private int startingSymbolCount;
    private String[] DISPLAYQUEUE;

    public Board() {
        inputQueueSize = 10000;
        startingSymbolCount = 40;
        BOARD = new String[10][10];
        INPUTQUEUE = new Queue(inputQueueSize);
        DISPLAYQUEUE = new String[8];
        rnd = new Random();
        clearBoard();
        fillInputQueue();
        fillBoardRandomly(startingSymbolCount);
    }

    public String[][] getBoard() {
        return BOARD;
    }

    void displayInputQueue(){
        for(int i = 0; i < DISPLAYQUEUE.length; i++){
            System.out.print(DISPLAYQUEUE[i]);
        }
        System.out.println();
    }

    void updateInputQueueDisplay(){
        String temp;
        for(int i = 0; i < 8; i++){
            temp = (String)INPUTQUEUE.dequeue();
            DISPLAYQUEUE[i] = temp;
            INPUTQUEUE.enqueue(temp);
        }
    }

    public String removeSymbolFromBoard(int px, int py){
        String returnSymbol;
        returnSymbol = BOARD[py][px];
        BOARD[py][px] = ".";
        return returnSymbol;
    }

    public void pushFromQueueToBoard() {
        int neededSymbolCount = startingSymbolCount - countSymbolsOnBoard();
        fillBoardRandomly(neededSymbolCount); 
    }

    public int countSymbolsOnBoard() {
        int symbolCount = 0;
        for (String[] row : BOARD) {
            for (String col : row) {
                if (!col.contains(".")) {
                    symbolCount++;
                }
            }
        }
        return symbolCount;
    }

    public void fillInputQueue() {
        int rndNum;
        while (INPUTQUEUE.size() < 8) {
            rndNum = rnd.nextInt(SYMBOLS.length);
            INPUTQUEUE.enqueue(SYMBOLS[rndNum]);
        }
    }

    public void clearBoard() {
        for (int i = 0; i < BOARD.length; i++) {
            for (int j = 0; j < BOARD[i].length; j++) {
                BOARD[i][j] = ".";
            }
        }
    }

    public void fillBoardRandomly(int symbolCount) {
        int randomRowIndex;
        int randomColIndex;
        String randomSymbol;
        boolean cannotPlaceSymbol;

        for (int i = 0; i < symbolCount; i++) {
            fillInputQueue();
            randomSymbol = (String) INPUTQUEUE.dequeue();
            cannotPlaceSymbol = true;

            while (cannotPlaceSymbol) {
                randomRowIndex = rnd.nextInt(BOARD.length);
                randomColIndex = rnd.nextInt(BOARD[randomRowIndex].length);

                if (BOARD[randomRowIndex][randomColIndex].contains(".")) { // Tried using equals to see if there exists
                                                                           // a dot on the boards specified index but it
                                                                           // does not work
                    BOARD[randomRowIndex][randomColIndex] = randomSymbol;
                    cannotPlaceSymbol = false;
                }
            }
        }
        fillInputQueue();
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