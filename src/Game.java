import enigma.core.Enigma;
import enigma.event.TextMouseEvent;
import enigma.event.TextMouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import enigma.console.TextAttributes;
import java.awt.Color;

class Game {
    public enigma.console.Console cn = Enigma.getConsole("Post-Fixer", 60, 30, 18, 3);
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
    int TIME;
    String MODE;
    int SCORE;
    Board b;

    Game() throws Exception { // --- Contructor
        attr = new TextAttributes(c.BLACK, c.GREEN);
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

        b = new Board();
        DELAY = 20;
        TIME = 60;
        MODE = "Free";
        SCORE = 0;
    }

    void showFinalScreen(){
        cn.getTextWindow().setCursorPosition(0,18 + OFFSET_Y);
        System.out.println(" _____                        _____                ");
        System.out.println("|  __ \\                      |  _  |               ");
        System.out.println("| |  \\/ __ _ _ __ ___   ___  | | | |_   _____ _ __ ");
        System.out.println("| | __ / _` | '_ ` _ \\ / _ \\ | | | \\ \\ / / _ \\ '__|");
        System.out.println("| |_\\ \\ (_| | | | | | |  __/ \\ \\_/ /\\ V /  __/ |   ");
        System.out.println(" \\____/\\__,_|_| |_| |_|\\___|  \\___/  \\_/ \\___|_|   ");
        System.out.println();
        System.out.println("Final Score: "+SCORE);
    }

    void play() throws InterruptedException {
        int timeDecreaseLimit = 1000;
        int timeDecreaseCounter = 0;
        while (TIME > 0) {
            if (timeDecreaseCounter >= timeDecreaseLimit) {
                timeDecreaseCounter = 0;
                TIME--;
            }

            cn.getTextWindow().setCursorPosition(0, 0);
            b.displayBoard();
            cn.getTextWindow().setCursorPosition(px + OFFSET_X, py + OFFSET_Y);
            cn.getTextWindow().output(b.getBoard()[py][px], attr);
            cn.getTextWindow().setCursorPosition(12 + OFFSET_X, 0 + OFFSET_Y);
            cn.getTextWindow().output(Integer.toString(TIME) + " ");
            cn.getTextWindow().setCursorPosition(12 + OFFSET_X, 3 + OFFSET_Y);
            cn.getTextWindow().output(MODE + "   ");

            if (keypr == 1) { // if keyboard button pressed
                if (rkey == KeyEvent.VK_LEFT && px > 0)
                    px--;
                if (rkey == KeyEvent.VK_RIGHT && px + 1 < b.getBoard()[py].length)
                    px++;
                if (rkey == KeyEvent.VK_UP && py > 0)
                    py--;
                if (rkey == KeyEvent.VK_DOWN && py + 1 < b.getBoard().length)
                    py++;
                if (rkey == 116 || rkey == 84) { // If the key pressed is T
                    MODE = "Take";
                }
                if (rkey == 102 || rkey == 70) { // If the key pressed is F
                    MODE = "Free";
                }
                if (rkey == KeyEvent.VK_SPACE && MODE.equalsIgnoreCase("Evaluation")) {
                    // progress the stack evaluation
                }
                keypr = 0; // last action
            }

            Thread.sleep(DELAY);
            if (MODE.equalsIgnoreCase("Take")) {
                timeDecreaseCounter += DELAY;
            }
        }
        showFinalScreen();
    }
}