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
    public String YELLOW_BG = "\033[43m";
    // ------ Standard variables for mouse and keyboard ------
    public int mousepr; // mouse pressed?
    public int mousex, mousey; // mouse text coords.
    public int keypr; // key pressed?
    public int rkey; // key (for press/release)
    public int DELAY;
    // ----------------------------------------------------

    int TIME;
    String MODE;
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
    }

    void play() throws InterruptedException {
        int px = 5, py = 5;
        int timeDecreaseLimit = 1000;
        int timeDecreaseCounter = 0;
        while (true) {
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
                if (rkey == KeyEvent.VK_LEFT)
                    px--;
                if (rkey == KeyEvent.VK_RIGHT)
                    px++;
                if (rkey == KeyEvent.VK_UP)
                    py--;
                if (rkey == KeyEvent.VK_DOWN)
                    py++;
                if (rkey == 116 || rkey == 84) { // If the key pressed is T
                    MODE = "Take";
                }
                if (rkey == 102 || rkey == 70) { // If the key pressed is F
                    MODE = "Free";
                }
                if(rkey == KeyEvent.VK_SPACE && MODE.equalsIgnoreCase("Evaluation")){
                    // progress the stack evaluation
                }
                keypr = 0; // last action
            }
            Thread.sleep(DELAY);
            if (MODE.equals("Take")) {
                timeDecreaseCounter += DELAY;
            }
        }
    }
}