import java.util.Random;

class Board {
    private String[][] board;
    private String[] SYMBOLS = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "-", "*", "/" };
    private Queue queue;
    private Random rnd;
    private int startingSymbolCount;

    public Board() {
        board = new String[10][10];
        rnd = new Random();
        startingSymbolCount = 40;
        clearBoard();
        fillBoardRandomly();
    }

    public String[][] getBoard() {
        return board;
    }

    void clearBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = ".";
            }
        }
    }

    void fillBoardRandomly() {
        int randomSymbolIndex;
        int randomRowIndex;
        int randomColIndex;
        boolean cannotPlaceSymbol;

        for (int i = 0; i < startingSymbolCount; i++) {
            cannotPlaceSymbol = true;
            randomSymbolIndex = rnd.nextInt(13);

            while (cannotPlaceSymbol) {
                randomRowIndex = rnd.nextInt(board.length);
                randomColIndex = rnd.nextInt(board[randomRowIndex].length);

                if (!board[randomRowIndex][randomColIndex].equals("\\.")) {
                    board[randomRowIndex][randomColIndex] = SYMBOLS[randomSymbolIndex];
                    cannotPlaceSymbol = false;
                }
            }
        }
    }

    void displayBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                    System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }
}