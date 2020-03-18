import enigma.core.Enigma;
import enigma.event.TextMouseEvent;
import enigma.event.TextMouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import enigma.console.TextAttributes;
import java.awt.Color;

class Game {
    public enigma.console.Console cn = Enigma.getConsole("Post-Fixer", 80, 20, 24, 0);
    public TextAttributes attr;
    public int OFFSET_X = 0;
    public int OFFSET_Y = 0;
    public Color c;
    public TextMouseListener tmlis;
    public KeyListener klis;
    // ------ Standard variables for mouse and keyboard ------
    public int mousepr; // mouse pressed?
    public int mousex, mousey; // mouse text coords.
    public int keypr; // key pressed?
    public int rkey; // key (for press/release)
    public int DELAY;
    int px = 5, py = 5;
    // ----------------------------------------------------

    private int TIME;
    private String MODE;
    private int SCORE;
    private Board b;
    private int timeDecreaseLimit = 1000; // A second in miliseconds
    private int timeDelayCounter = 0; // A counter for counting miliseconds
    private Queue takeQueue;
    private String[] DISPLAYTAKEQUEUE;
    private boolean evaluationComplete;

    Game() throws Exception { // --- Contructor
        inputSetup();
        attr = new TextAttributes(c.BLACK, c.GREEN);
        b = new Board();
        b.updateDisplayQueue();
        DELAY = 20;
        TIME = 60;
        MODE = "Free";
        SCORE = 0;
        takeQueue = new Queue(10000);
        DISPLAYTAKEQUEUE = new String[40];
    }

    void inputSetup() {
        // ------ Standard code for mouse and keyboard ------ Do not change
        tmlis = new TextMouseListener() {
            public void mouseClicked(TextMouseEvent arg0) {
            }

            public void mousePressed(TextMouseEvent arg0) {
                if (mousepr == 0) {
                    mousepr = 1;
                    mousex = arg0.getX();
                    mousey = arg0.getY();
                }
            }

            public void mouseReleased(TextMouseEvent arg0) {
            }
        };
        cn.getTextWindow().addTextMouseListener(tmlis);

        klis = new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                if (keypr == 0) {
                    keypr = 1;
                    rkey = e.getKeyCode();
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        };
        cn.getTextWindow().addKeyListener(klis);
        // ----------------------------------------------------
    }

    void displayInputQueue(int px, int py) {
        cn.getTextWindow().setCursorPosition(px, py - 1);
        cn.getTextWindow().output("<<<<<<<<", attr);
        cn.getTextWindow().setCursorPosition(px, py + 1);
        cn.getTextWindow().output("<<<<<<<<", attr);
        cn.getTextWindow().setCursorPosition(px, py);
        b.displayInputQueue();
    }

    void showFinalScreen() {
        cn.getTextWindow().setCursorPosition(0, 18 + OFFSET_Y);
        System.out.println(" _____                        _____                ");
        System.out.println("|  __ \\                      |  _  |               ");
        System.out.println("| |  \\/ __ _ _ __ ___   ___  | | | |_   _____ _ __ ");
        System.out.println("| | __ / _` | '_ ` _ \\ / _ \\ | | | \\ \\ / / _ \\ '__|");
        System.out.println("| |_\\ \\ (_| | | | | | |  __/ \\ \\_/ /\\ V /  __/ |   ");
        System.out.println(" \\____/\\__,_|_| |_| |_|\\___|  \\___/  \\_/ \\___|_|   ");
    }

    void displayTakeQueue(int px, int py) {
        cn.getTextWindow().setCursorPosition(px + OFFSET_X, py + OFFSET_Y);
        System.out.print("Take Queue: ");
        for (int i = 0; i < takeQueue.size(); i++) {
            System.out.print(DISPLAYTAKEQUEUE[i] + " ");
        }
        System.out.println();
    }

    void updateDisplayQueue() {
        String temp;
        int size = takeQueue.size();
        for (int i = 0; i < size; i++) {
            temp = (String) takeQueue.dequeue();
            DISPLAYTAKEQUEUE[i] = temp;
            takeQueue.enqueue(temp);
        }
    }

    void displayGameScreen() {
        cn.getTextWindow().setCursorPosition(0, 0);
        b.displayBoard();
        cn.getTextWindow().setCursorPosition(px + OFFSET_X, py + OFFSET_Y);
        cn.getTextWindow().output(b.getBoard()[py][px], attr);
        cn.getTextWindow().setCursorPosition(12 + OFFSET_X, 0 + OFFSET_Y);
        cn.getTextWindow().output("Time:" + Integer.toString(TIME) + " ");
        cn.getTextWindow().setCursorPosition(12 + OFFSET_X, 1 + OFFSET_Y);
        cn.getTextWindow().output("Score:" + Integer.toString(SCORE) + "   ");
        cn.getTextWindow().setCursorPosition(12 + OFFSET_X, 2 + OFFSET_Y);
        cn.getTextWindow().output("Mode:" + MODE + "                     ");
        displayInputQueue(12 + OFFSET_X, 5 + OFFSET_Y);
        if (MODE.equalsIgnoreCase("Take")) {
            displayTakeQueue(12 + OFFSET_X, 9 + OFFSET_Y);
        } else {
            cn.getTextWindow().setCursorPosition(12 + OFFSET_X, 9 + OFFSET_Y);
            System.out.println("                                                                                                   ");
        }
    }

    void takeKeyPress() {
        if (keypr == 1) { // if keyboard button pressed
            if ((rkey == KeyEvent.VK_LEFT || rkey == 97 || rkey == 65) && px > 0)
                if (MODE.equalsIgnoreCase("Free")) {
                    px--;
                } else if (MODE.equalsIgnoreCase("Take")) {
                    move(-1, 0);
                }
            if ((rkey == KeyEvent.VK_RIGHT || rkey == 100 || rkey == 68) && px + 1 < b.getBoard()[py].length)
                if (MODE.equalsIgnoreCase("Free")) {
                    px++;
                } else if (MODE.equalsIgnoreCase("Take")) {
                    move(1, 0);
                }
            if ((rkey == KeyEvent.VK_UP || rkey == 119 || rkey == 87) && py > 0)
                if (MODE.equalsIgnoreCase("Free")) {
                    py--;
                } else if (MODE.equalsIgnoreCase("Take")) {
                    move(0, -1);
                }
            if ((rkey == KeyEvent.VK_DOWN || rkey == 115 || rkey == 83) && py + 1 < b.getBoard().length)
                if (MODE.equalsIgnoreCase("Free")) {
                    py++;
                } else if (MODE.equalsIgnoreCase("Take")) {
                    move(0, 1);
                }
            if (rkey == 116 || rkey == 84) { // If the key pressed is T
                if (MODE.equalsIgnoreCase("Free")) {
                    MODE = "Take";
                }
                // DELETE THIS CONDITION LATER !!!!!!!!!!!
                if (MODE.equalsIgnoreCase("Take")) {
                    takeSymbol();
                }
            }
            if (rkey == 102 || rkey == 70) { // If the key pressed is F
                if (MODE.equalsIgnoreCase("Take")) {
                    MODE = "Evaluation";
                }
            }
            if (rkey == KeyEvent.VK_SPACE && MODE.equalsIgnoreCase("Evaluation")) {
                // progress the stack evaluation
                evaluationComplete = true; // PROP DELETE LATER !!!!!!!!
            }
            keypr = 0; // last action
        }
    }

    void progressTime() {
        if (timeDelayCounter >= timeDecreaseLimit) {
            timeDelayCounter = 0;
            TIME--;
        }
        if (MODE.equalsIgnoreCase("Take")) {
            timeDelayCounter += DELAY;
        }
    }

    void takeSymbol() {
        if (!b.getBoard()[py][px].contains(".")) {
            takeQueue.enqueue(b.removeSymbolFromBoard(px, py));
            updateDisplayQueue();
        }
    }

    boolean tryParseInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    boolean containsOperator() {
        return b.getBoard()[py][px].contains("+") || b.getBoard()[py][px].contains("-")
                || b.getBoard()[py][px].contains("*") || b.getBoard()[py][px].contains("/");
    }

    boolean hasOperatorAhead(int dirx, int diry) {
        boolean hasSymAhead = false;
        int tempx = px;
        int tempy = py;
        tempx += dirx;
        tempy += diry;
        while (tempy >= 0 && tempy < b.getBoard().length && tempx >= 0 && tempx < b.getBoard()[tempy].length) { // Coordinates
                                                                                                              // in the
                                                                                                              // board
                                                                                                              // limits

            if (b.getBoard()[tempy][tempx].contains("+") || b.getBoard()[tempy][tempx].contains("-")
            || b.getBoard()[tempy][tempx].contains("*") || b.getBoard()[tempy][tempx].contains("/")) {
                hasSymAhead = true;
                break;
            }
            tempx += dirx;
            tempy += diry;
        }
        return hasSymAhead;
    }

    boolean hasNumberAhead(int dirx, int diry) {
        boolean hasNumAhead = false;
        int tempx = px;
        int tempy = py;
        tempx += dirx;
        tempy += diry;
        while (tempy >= 0 && tempy < b.getBoard().length && tempx >= 0 && tempx < b.getBoard()[tempy].length) { // Coordinates
                                                                                                              // in the
                                                                                                              // board
                                                                                                              // limits

            if (tryParseInt(b.getBoard()[tempy][tempx])) {
                hasNumAhead = true;
                break;
            }
            tempx += dirx;
            tempy += diry;
        }
        return hasNumAhead;
    }

    void move(int dirx, int diry) {
        String combinedNumber;
        while (py >= 0 && py < b.getBoard().length && px >= 0 && px < b.getBoard()[py].length) {
            combinedNumber = "";
            if (hasNumberAhead(dirx, diry) || hasOperatorAhead(dirx, diry) || tryParseInt(b.getBoard()[py][px]) || containsOperator()) {
                if (containsOperator()) {
                    takeSymbol();
                    break;
                }
                if (b.getBoard()[py][px].contains(".")) {
                    px += dirx;
                    py += diry;
                    continue;
                }
                if (tryParseInt(b.getBoard()[py][px])) {
                    combinedNumber += b.removeSymbolFromBoard(px, py);
                    while (hasNumberAhead(dirx, diry)) {
                        py += diry;
                        px += dirx;
                        if (tryParseInt(b.getBoard()[py][px])) {
                            combinedNumber += b.removeSymbolFromBoard(px, py);
                        } else {
                            break;
                        }
                    }
                    takeQueue.enqueue(combinedNumber);
                }
                if(!hasNumberAhead(dirx, diry)){
                    break;
                }
                
            } else {
                takeSymbol();
                break;
            }
        }
        updateDisplayQueue();
    }

    void evaluation() throws InterruptedException {
        evaluationComplete = false;
        while (!evaluationComplete) {
            displayGameScreen();
            takeKeyPress();
            Thread.sleep(DELAY);
        }
        MODE = "Free";
        free();
    }

    void take() throws InterruptedException {
        takeSymbol();
        while (TIME > 0) {
            progressTime();
            displayGameScreen();
            takeKeyPress();
            Thread.sleep(DELAY);
            if (MODE.equalsIgnoreCase("Evaluation")) {
                b.pushFromQueueToBoard();
                b.updateDisplayQueue();
                evaluation();
            }
        }
        showFinalScreen();
    }

    void free() throws InterruptedException {
        while (TIME > 0) {
            displayGameScreen();
            takeKeyPress();
            Thread.sleep(DELAY);
            if (MODE.equalsIgnoreCase("Take")) {
                take();
            }
        }
        showFinalScreen();
    }

    void play() throws InterruptedException {
        free();
    }
}