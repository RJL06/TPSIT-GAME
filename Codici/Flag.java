import javax.swing.*;
import java.awt.*;

public class Flag {
    private static final int FLAG_WIDTH = 30;
    private static final int FLAG_HEIGHT = 30;
    private final Image flagImage;
    private int x, y;
    private boolean collected;

    public Flag(int x, int y, String imagePath) {
        this.x = x;
        this.y = y;
        this.collected = false;
        this.flagImage = new ImageIcon(imagePath).getImage()
            .getScaledInstance(FLAG_WIDTH, FLAG_HEIGHT, Image.SCALE_SMOOTH);
    }

    public void draw(Graphics g) {
        if (!collected) {
            g.drawImage(flagImage, x, y, null);
        }
    }

    public boolean checkCollision(Rectangle playerBounds, int weaponsCollected, int totalWeapons) {
        if (!collected && weaponsCollected == totalWeapons && playerBounds.intersects(new Rectangle(x, y, FLAG_WIDTH, FLAG_HEIGHT))) {
            collected = true;
            return true;
        }
        return false;
    }

    public boolean isCollected() {
        return collected;
    }
}
