import enigma.core.Enigma;
import enigma.event.TextMouseEvent;
import enigma.event.TextMouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import enigma.console.TextAttributes;
import java.awt.Color;
import java.util.Scanner;

class Game {
    public enigma.console.Console cn = Enigma.getConsole("Post-Fixer", 85, 20, 24, 0);
    public TextAttributes attr;
    public TextAttributes redonblack;
    public TextAttributes greenonblack;
    public int OFFSET_X = 0;
    public int OFFSET_Y = 0;
    public Color c;
    public TextMouseListener tmlis;
    public KeyListener klis;
    public Scanner sc;

    // ------ Standard variables for mouse and keyboard ------
    public int mousepr; // mouse pressed?
    public int mousex, mousey; // mouse text coords.
    public int keypr; // key pressed?
    public int rkey; // key (for press/release)
    public int DELAY;
    int px = 5, py = 5;
    // ----------------------------------------------------

    private String MODE;
    private String FREE = "Free";
    private String TAKE = "Take";
    private String EVALUATION = "Evaluation";
    private int SCORE;
    private Board b;
    private Time TIME;
    private Queue EXPRESSIONQUEUE;
    private String[] EXPRESSIONQUEUEDISPLAY;
    private boolean evaluationComplete;
    private boolean isCorrectExpression;
    private boolean hasDivisionByZero;
    private int pointsWon;
    private Stack EVALUATIONSTACK;
    private String[] EVALUATIONSTACKDISPLAY;

    Game() throws Exception { // --- Contructor
        inputSetup();
        attr = new TextAttributes(c.BLACK, c.GREEN);
        redonblack = new TextAttributes(c.RED, c.BLACK);
        greenonblack = new TextAttributes(c.GREEN, c.BLACK);
        b = new Board();
        b.updateInputQueueDisplay();
        DELAY = 20;
        TIME = new Time(60, DELAY);
        MODE = FREE;
        SCORE = 0;
        EXPRESSIONQUEUE = new Queue(10000);
        EXPRESSIONQUEUEDISPLAY = new String[40];
        EVALUATIONSTACK = new Stack(40);
        EVALUATIONSTACKDISPLAY = new String[40];
        evaluationComplete = false;
        isCorrectExpression = false;
        hasDivisionByZero = false;
        emptyEvaluationStackDisplay();
    }

    void inputSetup() {
        sc = new Scanner(System.in);
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
        cn.getTextWindow().setCursorPosition(0, 13 + OFFSET_Y);
        TextAttributes textcolor;
        if (SCORE > 0) {
            textcolor = greenonblack;
        } else {
            textcolor = redonblack;
        }
        cn.getTextWindow().output(" _____                        _____                ", textcolor);
        System.out.println();
        cn.getTextWindow().output("|  __ \\                      |  _  |               ", textcolor);
        System.out.println();
        cn.getTextWindow().output("| |  \\/ __ _ _ __ ___   ___  | | | |_   _____ _ __ ", textcolor);
        System.out.println();
        cn.getTextWindow().output("| | __ / _` | '_ ` _ \\ / _ \\ | | | \\ \\ / / _ \\ '__|", textcolor);
        System.out.println();
        cn.getTextWindow().output("| |_\\ \\ (_| | | | | | |  __/ \\ \\_/ /\\ V /  __/ |   ", textcolor);
        System.out.println();
        cn.getTextWindow().output(" \\____/\\__,_|_| |_| |_|\\___|  \\___/  \\_/ \\___|_|   ", textcolor);
    }

    void displayExpressionQueue(int px, int py) {
        cn.getTextWindow().setCursorPosition(px + OFFSET_X, py + OFFSET_Y);
        System.out.print("Expression: ");
        for (int i = 0; i < EXPRESSIONQUEUE.size(); i++) {
            System.out.print(EXPRESSIONQUEUEDISPLAY[i] + " ");
        }
        System.out.println("                   ");
    }

    void emptyExpressionQueue() {
        while (!EXPRESSIONQUEUE.isEmpty()) {
            EXPRESSIONQUEUE.dequeue();
        }
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

    void emptyEvaluationStack() {
        while (!EVALUATIONSTACK.isEmpty()) {
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
            for (int i = stackSize - 1; i < EVALUATIONSTACKDISPLAY.length; i++) {
                EVALUATIONSTACKDISPLAY[i] = "         ";
            }
            for (int i = stackSize - 1; i < stackSize; i++) {
                element = (String) EVALUATIONSTACK.pop();
                EVALUATIONSTACKDISPLAY[i] = element;
                EVALUATIONSTACK.push(element);
            }
        }
    }

    void displayStackGraphic() {

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
        // Board display
        cn.getTextWindow().setCursorPosition(0, 0);
        b.displayBoard();

        // Cursor display
        cn.getTextWindow().setCursorPosition(px, py);
        cn.getTextWindow().output(b.getBoard()[py][px], attr);

        // Game parameter display
        cn.getTextWindow().setCursorPosition(12 + OFFSET_X, 0 + OFFSET_Y);
        cn.getTextWindow().output("Time:" + Integer.toString(TIME.getTime()) + " ");
        cn.getTextWindow().setCursorPosition(12 + OFFSET_X, 1 + OFFSET_Y);
        cn.getTextWindow().output("Score:" + Integer.toString(SCORE) + "       ");
        cn.getTextWindow().setCursorPosition(12 + OFFSET_X, 2 + OFFSET_Y);
        cn.getTextWindow().output("Mode:" + MODE + "                     ");

        // Input queue display
        displayInputQueue(12 + OFFSET_X, 5 + OFFSET_Y);

        // Expression queue display
        if (MODE.equalsIgnoreCase(TAKE) || MODE.equalsIgnoreCase(EVALUATION)) {
            displayExpressionQueue(11 + OFFSET_X, 10 + OFFSET_Y);
        } else {
            cn.getTextWindow().setCursorPosition(0 + OFFSET_X, 10 + OFFSET_Y);
            System.out.println("                                                                                     ");
        }

        // Stack display
        displayStackGraphic();

        // Expression state
        if (MODE.equalsIgnoreCase(EVALUATION)) {
            cn.getTextWindow().setCursorPosition(45 + OFFSET_X, 7 + OFFSET_Y);
            if (!isCorrectExpression && evaluationComplete && hasDivisionByZero) {
                cn.getTextWindow().output("Division by Zero! ");
                cn.getTextWindow().output(Integer.toString(-20), redonblack);
                cn.getTextWindow().output(" Points");
            } else if (!isCorrectExpression && evaluationComplete) {
                cn.getTextWindow().output("Invalid Expression! ");
                cn.getTextWindow().output(Integer.toString(-20), redonblack);
                cn.getTextWindow().output(" Points");
            } else if (isCorrectExpression && evaluationComplete) {
                cn.getTextWindow().output("Valid Expression! ");
                cn.getTextWindow().output("+" + Integer.toString(pointsWon), greenonblack);
                cn.getTextWindow().output(" Points");
            }
        } else {
            cn.getTextWindow().setCursorPosition(45 + OFFSET_X, 7 + OFFSET_Y);
            cn.getTextWindow().output("                              ");
        }

        // Real-time status of the expression display
        if (MODE.equalsIgnoreCase(TAKE) || MODE.equalsIgnoreCase(EVALUATION)) {
            cn.getTextWindow().setCursorPosition(45 + OFFSET_X, 5 + OFFSET_Y);
            if (!isCorrectExpression && hasDivisionByZero) {
                cn.getTextWindow().output("Expression Status: ");
                cn.getTextWindow().output("DIVISION BY ZERO!", redonblack);
            } else if (!isCorrectExpression) {
                cn.getTextWindow().output("Expression Status: ");
                cn.getTextWindow().output("INVALID          ", redonblack);
            } else if (isCorrectExpression) {
                cn.getTextWindow().output("Expression Status: ");
                cn.getTextWindow().output("VALID            ", greenonblack);
            }
        } else {
            cn.getTextWindow().setCursorPosition(45 + OFFSET_X, 5 + OFFSET_Y);
            cn.getTextWindow().output("                                    ");
        }
    }

    void takeKeyPress() {
        if (keypr == 1) { // if keyboard button pressed
            if ((rkey == KeyEvent.VK_LEFT || rkey == 97 || rkey == 65) && px > 0)
                if (MODE.equalsIgnoreCase(FREE)) {
                    px--;
                } else if (MODE.equalsIgnoreCase(TAKE)) {
                    move(-1, 0);
                }
            if ((rkey == KeyEvent.VK_RIGHT || rkey == 100 || rkey == 68) && px + 1 < b.getBoard()[py].length)
                if (MODE.equalsIgnoreCase(FREE)) {
                    px++;
                } else if (MODE.equalsIgnoreCase(TAKE)) {
                    move(1, 0);
                }
            if ((rkey == KeyEvent.VK_UP || rkey == 119 || rkey == 87) && py > 0)
                if (MODE.equalsIgnoreCase(FREE)) {
                    py--;
                } else if (MODE.equalsIgnoreCase(TAKE)) {
                    move(0, -1);
                }
            if ((rkey == KeyEvent.VK_DOWN || rkey == 115 || rkey == 83) && py + 1 < b.getBoard().length)
                if (MODE.equalsIgnoreCase(FREE)) {
                    py++;
                } else if (MODE.equalsIgnoreCase(TAKE)) {
                    move(0, 1);
                }
            if (rkey == 116 || rkey == 84) { // If the key pressed is T
                if (MODE.equalsIgnoreCase(FREE)) {
                    MODE = TAKE;
                }
            }
            if (rkey == 102 || rkey == 70) { // If the key pressed is F
                if (MODE.equalsIgnoreCase(TAKE)) {
                    MODE = EVALUATION;
                }
            }
            if (rkey == KeyEvent.VK_SPACE && MODE.equalsIgnoreCase(EVALUATION)) {
                // progress the stack evaluation
                progressExpressionEvaluation();
            }
            keypr = 0; // last action
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

    boolean hasNeighborNumber(int dirx, int diry) {
        if (py + diry >= 0 && py + diry < b.getBoard().length && px + dirx >= 0
                && px + dirx < b.getBoard()[py].length) {
            return tryParseInt(b.getBoard()[py + diry][px + dirx]);
        } else {
            return false;
        }
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
                    while (hasNeighborNumber(dirx, diry)) {
                        py += diry;
                        px += dirx;
                        combinedNumber += b.removeSymbolFromBoard(px, py);
                    }
                    EXPRESSIONQUEUE.enqueue(combinedNumber);
                    break;
                }
            } else {
                takeSymbol();
                break;
            }
        }
        updateExpressionQueueDisplay();

        if (isValidExpression()) {
            if (hasDivisionByZero()) {
                isCorrectExpression = false;
                hasDivisionByZero = true;
            } else {
                isCorrectExpression = true;
            }
        } else {
            isCorrectExpression = false;
        }
    }

    boolean isValidExpression() {
        int counter = 0;
        for (int i = 0; i < EXPRESSIONQUEUE.size(); i++) {
            if (tryParseInt(EXPRESSIONQUEUEDISPLAY[i])) { // If the elements is a number
                counter++;
            } else { // If element is an operator
                counter -= 2;
                if (counter < 0) {
                    return false;
                }
                counter++;
            }
        }
        if (counter != 1) {
            return false;
        }
        if (EXPRESSIONQUEUE.size() == 1) {
            return false;
        }

        return true;
    }

    boolean hasDivisionByZero() {
        int queueSize = EXPRESSIONQUEUE.size();
        Stack checkStack = new Stack(queueSize);
        Queue checkQueue = new Queue(queueSize);
        for (int i = 0; i < queueSize; i++) {
            Object obj = EXPRESSIONQUEUE.dequeue();
            checkQueue.enqueue(obj);
            EXPRESSIONQUEUE.enqueue(obj);
        }

        while (!checkQueue.isEmpty()) {
            String element;
            element = (String) checkQueue.dequeue();
            int firstNumber;
            int secondNumber;
            int result = 0;
            if (tryParseInt(element)) {
                checkStack.push(element);
            } else {
                firstNumber = Integer.parseInt((String) checkStack.pop());
                secondNumber = Integer.parseInt((String) checkStack.pop());
                if (element.contains("+")) {
                    result = firstNumber + secondNumber;
                } else if (element.contains("-")) {
                    result = secondNumber - firstNumber;
                } else if (element.contains("*")) {
                    result = firstNumber * secondNumber;
                } else if (element.contains("/")) {
                    if (firstNumber == 0) {
                        return true;
                    } else {
                        result = secondNumber / firstNumber;
                    }
                }
                checkStack.push(Integer.toString(result));
            }
        }
        return false;
    }

    void evaluateExpression() {
        if (isValidExpression() && !hasDivisionByZero()) {
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
            pointsWon = scoreFactor;
            SCORE += scoreFactor;

        } else if (isValidExpression() && hasDivisionByZero()) {
            hasDivisionByZero = true;
            SCORE -= 20;
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
        boolean hasDivisionByZero = false;
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
                if (firstNumber == 0) {
                    hasDivisionByZero = true;
                } else {
                    result = secondNumber / firstNumber;
                }
            }
            EVALUATIONSTACK.push(Integer.toString(result));
            if (EXPRESSIONQUEUE.isEmpty()) {
                evaluationComplete = true;
                isCorrectExpression = true;
            }
            if (hasDivisionByZero) {
                evaluationComplete = true;
                isCorrectExpression = false;
            }
        }
        updateExpressionQueueDisplay();
        updateEvaluationStackDisplay();
    }

    void evaluation() throws InterruptedException {
        evaluateExpression();
        while (!evaluationComplete) {
            displayGameScreen();
            takeKeyPress();
            Thread.sleep(DELAY);
        }
        displayGameScreen();
        Thread.sleep(2000);
        emptyExpressionQueue();
        updateExpressionQueueDisplay();
        emptyEvaluationStack();
        emptyEvaluationStackDisplay();
        isCorrectExpression = false;
        evaluationComplete = false;
        hasDivisionByZero = false;
        MODE = FREE;
        free();
    }

    void take() throws InterruptedException {
        takeSymbol();
        while (TIME.getTime() > 0) {
            displayGameScreen();
            takeKeyPress();
            Thread.sleep(DELAY);
            TIME.progressTime();
            if (MODE.equalsIgnoreCase(EVALUATION)) {
                b.pushFromQueueToBoard();
                b.updateInputQueueDisplay();
                evaluation();
            }
        }
        MODE = EVALUATION;
        b.pushFromQueueToBoard();
        b.updateInputQueueDisplay();
        evaluation();
    }

    void free() throws InterruptedException {
        while (TIME.getTime() > 0) {
            displayGameScreen();
            takeKeyPress();
            Thread.sleep(DELAY);
            if (MODE.equalsIgnoreCase(TAKE)) {
                take();
            }
        }
        displayGameScreen();
        showFinalScreen();
        sc.nextLine();
        System.exit(0);
    }

    void play() throws InterruptedException {
        free();
    }
}