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
    }

    void play() throws InterruptedException {
        int px = 5, py = 5;
        int timeDecreaseLimit = 1000;
        int timeDecreaseCounter = 0;
        while (true) {
            if(timeDecreaseCounter >= timeDecreaseLimit){
                timeDecreaseCounter = 0;
                TIME--;
            }
            cn.getTextWindow().setCursorPosition(0, 0);
            b.displayBoard();
            cn.getTextWindow().setCursorPosition(px, py);
            cn.getTextWindow().output(b.getBoard()[py][px], attr);
            cn.getTextWindow().setCursorPosition(12, 0);
            cn.getTextWindow().output(Integer.toString(TIME)+" ");
            if (keypr == 1) { // if keyboard button pressed
                if (rkey == KeyEvent.VK_LEFT)
                    px--;
                if (rkey == KeyEvent.VK_RIGHT)
                    px++;
                if (rkey == KeyEvent.VK_UP)
                    py--;
                if (rkey == KeyEvent.VK_DOWN)
                    py++;

                char rckey = (char) rkey;
                // left right up down

                if (rkey == KeyEvent.VK_SPACE) {
                    String str;
                    str = cn.readLine(); // keyboardlistener running and readline input by using enter
                    cn.getTextWindow().setCursorPosition(5, 20);
                    cn.getTextWindow().output(str);
                }

                keypr = 0; // last action
            }
            Thread.sleep(DELAY);
            timeDecreaseCounter+=DELAY;
        }
    }
}