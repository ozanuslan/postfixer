import enigma.core.Enigma;
import enigma.event.TextMouseEvent;
import enigma.event.TextMouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import enigma.console.TextAttributes;
import java.awt.Color;

class Game {
    public enigma.console.Console cn = Enigma.getConsole("Post-Fixer", 85, 20, 24, 0);
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
    private Queue EXPRESSIONQUEUE;
    private String[] EXPRESSIONQUEUEDISPLAY;
    private boolean evaluationComplete;
    private Stack EVALUATIONSTACK;
    private String[] EVALUATIONSTACKDISPLAY;

    Game() throws Exception { // --- Contructor
        inputSetup();
        attr = new TextAttributes(c.BLACK, c.GREEN);
        b = new Board();
        b.updateInputQueueDisplay();
        DELAY = 20;
        TIME = 60;
        MODE = "Free";
        SCORE = 0;
        EXPRESSIONQUEUE = new Queue(10000);
        EXPRESSIONQUEUEDISPLAY = new String[40];
        EVALUATIONSTACK = new Stack(40);
        EVALUATIONSTACKDISPLAY = new String[40];
        emptyEvaluationStackDisplay();
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

    void displayExpressionQueue(int px, int py) {
        cn.getTextWindow().setCursorPosition(px + OFFSET_X, py + OFFSET_Y);
        System.out.print("Expression: ");
        for (int i = 0; i < EXPRESSIONQUEUE.size(); i++) {
            System.out.print(EXPRESSIONQUEUEDISPLAY[i] + " ");
        }
        System.out.println("                   ");
    }

    void updateExpressionQueueDisplay() {
        String temp;
        int size = EXPRESSIONQUEUE.size();
        for (int i = 0; i < EXPRESSIONQUEUEDISPLAY.length; i++) {
            EXPRESSIONQUEUEDISPLAY[i] = " ";
        }
        for (int i = 0; i < size; i++) {
            temp = (String) EXPRESSIONQUEUE.dequeue();
            EXPRESSIONQUEUEDISPLAY[i] = temp;
            EXPRESSIONQUEUE.enqueue(temp);
        }
    }

    void emptyExpressionQueue(){
        while(!EXPRESSIONQUEUE.isEmpty()){
            EXPRESSIONQUEUE.dequeue();
        }
    }

    void emptyEvaluationStack(){
        while(!EVALUATIONSTACK.isEmpty()){
            EVALUATIONSTACK.pop();
        }
    }

    void emptyEvaluationStackDisplay() {
        for (int i = 0; i < EVALUATIONSTACKDISPLAY.length; i++) {
            EVALUATIONSTACKDISPLAY[i] = "         ";
        }
    }

    void updateEvaluationStackDisplay() {
        String element;
        int stackSize = EVALUATIONSTACK.size();

        if (stackSize >= 1) {
            for (int i = stackSize-1; i < EVALUATIONSTACKDISPLAY.length; i++) {
                EVALUATIONSTACKDISPLAY[i] = "         ";
            }
            for (int i = stackSize - 1; i < stackSize; i++) {
                element = (String) EVALUATIONSTACK.pop();
                EVALUATIONSTACKDISPLAY[i] = element;
                EVALUATIONSTACK.push(element);
            }
        }
    }

    void displayStack() {

        int lastIndex = 9;
        for (int i = 0; i < 8; i++) {
            cn.getTextWindow().setCursorPosition(29 + OFFSET_X, 1 + OFFSET_Y + i);
            System.out.println("|         |");
            lastIndex = i;
        }
        cn.getTextWindow().setCursorPosition(29 + OFFSET_X, 1 + OFFSET_Y + lastIndex);
        System.out.println("+---------+");

        for (int i = 0; i < EVALUATIONSTACKDISPLAY.length; i++) {
            lastIndex--;
            cn.getTextWindow().setCursorPosition(30 + OFFSET_X, 1 + OFFSET_Y + lastIndex);
            System.out.println(EVALUATIONSTACKDISPLAY[i]);
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
        if (MODE.equalsIgnoreCase("Take") || MODE.equalsIgnoreCase("Evaluation")) {
            displayExpressionQueue(0 + OFFSET_X, 10 + OFFSET_Y);
        } else {
            cn.getTextWindow().setCursorPosition(0 + OFFSET_X, 10 + OFFSET_Y);
            System.out.println(
                    "                                                                                                   ");
        }
        displayStack();
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
            }
            if (rkey == 102 || rkey == 70) { // If the key pressed is F
                if (MODE.equalsIgnoreCase("Take")) {
                    MODE = "Evaluation";
                }
            }
            if (rkey == KeyEvent.VK_SPACE && MODE.equalsIgnoreCase("Evaluation")) {
                // progress the stack evaluation
                progressExpressionEvaluation();
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
            EXPRESSIONQUEUE.enqueue(b.removeSymbolFromBoard(px, py));
            updateExpressionQueueDisplay();
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
                                                                                                                // in
                                                                                                                // the
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
                                                                                                                // in
                                                                                                                // the
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
            if (hasNumberAhead(dirx, diry) || hasOperatorAhead(dirx, diry) || tryParseInt(b.getBoard()[py][px])
                    || containsOperator()) {
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
                    EXPRESSIONQUEUE.enqueue(combinedNumber);
                }
                if (!hasNumberAhead(dirx, diry)) {
                    break;
                }

            } else {
                takeSymbol();
                break;
            }
        }
        updateExpressionQueueDisplay();
    }

    boolean isValidExpression() {
        boolean isValid = true;
        int counter = 0;
        for (int i = 0; i < EXPRESSIONQUEUE.size(); i++) {
            if (tryParseInt(EXPRESSIONQUEUEDISPLAY[i])) { // If the elements is a number
                counter++;
            } else { // If element is an operator
                counter -= 2;
                if (counter < 0) {
                    isValid = false;
                    break;
                }
                counter++;
            }
        }
        if (counter != 1) {
            isValid = false;
        }
        if (EXPRESSIONQUEUE.size() == 1) {
            isValid = false;
        }
        return isValid;
    }

    void evaluateExpression() {
        if (isValidExpression()) {
            int scoreFactor = 0;
            boolean lastElementType = true; // True for numbers False for operators
            for (int i = 0; i < EXPRESSIONQUEUE.size(); i++) {
                if (tryParseInt(EXPRESSIONQUEUEDISPLAY[i])) { // If number
                    if (EXPRESSIONQUEUEDISPLAY[i].length() > 1) {
                        scoreFactor += EXPRESSIONQUEUEDISPLAY[i].length() * 2;
                    } else {
                        if (lastElementType) {
                            scoreFactor += 1;
                        } else {
                            scoreFactor += 2;
                        }
                    }
                    lastElementType = true;
                } else { // If operator
                    if (!lastElementType) {
                        scoreFactor += 1;
                    } else {
                        scoreFactor += 2;
                    }
                    lastElementType = false;
                }
            }
            scoreFactor *= scoreFactor;
            SCORE += scoreFactor;
        } else {
            SCORE -= 20;
            evaluationComplete = true;
        }
    }

    void progressExpressionEvaluation() {
        String element;
        element = (String) EXPRESSIONQUEUE.dequeue();
        int firstNumber;
        int secondNumber;
        int result = 0;
        if (tryParseInt(element)) {
            EVALUATIONSTACK.push(element);
        } else {
            firstNumber = Integer.parseInt((String) EVALUATIONSTACK.pop());
            secondNumber = Integer.parseInt((String) EVALUATIONSTACK.pop());
            if (element.contains("+")) {
                result = firstNumber + secondNumber;
            } else if (element.contains("-")) {
                result = secondNumber - firstNumber;
            } else if (element.contains("*")) {
                result = firstNumber * secondNumber;
            } else if (element.contains("/")) {
                if(firstNumber == 0){
                    result = 0;
                } else{
                    result = secondNumber / firstNumber;
                }
            }
            EVALUATIONSTACK.push(Integer.toString(result));
            if (EXPRESSIONQUEUE.isEmpty()) {
                evaluationComplete = true;
            }
        }
        updateExpressionQueueDisplay();
        updateEvaluationStackDisplay();
    }

    void evaluation() throws InterruptedException {
        evaluationComplete = false;
        evaluateExpression();
        while (!evaluationComplete) {
            displayGameScreen();
            takeKeyPress();
            Thread.sleep(DELAY);
        }
        displayGameScreen();
        Thread.sleep(1000);
        emptyExpressionQueue();
        updateExpressionQueueDisplay();
        emptyEvaluationStack();
        emptyEvaluationStackDisplay();
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
                b.updateInputQueueDisplay();
                evaluation();
            }
        }
        evaluation();
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