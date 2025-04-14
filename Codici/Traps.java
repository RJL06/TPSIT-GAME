import javax.swing.*;
import java.awt.*;

public class Traps {
    private static final int TRAP_WIDTH = 40;
    private static final int TRAP_HEIGHT = 40;

    private Image trapImage;
    private int x;
    private int y;
    private boolean isVisible;  // La trappola è invisibile inizialmente
    private boolean soundPlayed; // Flag per evitare ripetizioni di suono

    public Traps(int x, int y, String imagePath) {
        this.x = x;
        this.y = y;
        this.isVisible = false;
        this.soundPlayed = false;
        this.trapImage = new ImageIcon(imagePath).getImage()
                           .getScaledInstance(TRAP_WIDTH, TRAP_HEIGHT, Image.SCALE_SMOOTH);
    }

    public void drawTrap(Graphics g) {
        // Disegna la trappola solo se è visibile
        if (isVisible && trapImage != null) {
            g.drawImage(trapImage, x, y, null);
        }
    }

    public boolean checkCollision(Rectangle playerBounds) {
        // Se il bounding box del personaggio interseca la trappola, rende la trappola visibile
        if (!isVisible && playerBounds.intersects(getBounds())) {
            isVisible = true;
        }
        return isVisible;
    }

    // Getter per soundPlayed flag
    public boolean isSoundPlayed() {
        return soundPlayed;
    }

    // Setter per soundPlayed flag
    public void setSoundPlayed(boolean soundPlayed) {
        this.soundPlayed = soundPlayed;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, TRAP_WIDTH, TRAP_HEIGHT);
    }
}
