import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D; // Aggiungi l'import per Graphics2D
import java.awt.Color;
import java.util.Random;

public class LabirintioPanel {
    private List<Rectangle> walls;
    private BufferedImage wallTexture;
    private TexturePaint texture;
    
    private static final int WALL_SIZE = 40; // Dimensione dei muri del labirinto

    public LabirintioPanel() {
        walls = new ArrayList<>();
        loadWallTexture();
        createMaze();
    }

    private void loadWallTexture() {
        try {
            // Carica l'immagine di texture dei muri
            
            wallTexture = ImageIO.read(getClass().getResourceAsStream("/wall_texture.png"));

            Rectangle2D rect = new Rectangle2D.Double(0, 0, wallTexture.getWidth(), wallTexture.getHeight());
            texture = new TexturePaint(wallTexture,rect);
            System.out.println("TEXTURE TROVATA");
        } catch (IOException e) {
            System.err.println("Errore nel caricamento della texture del muro: " + e.getMessage());
        }
    }

    private void createMaze() {
    // Creazione dei muri del labirinto complesso
    // Bordi esterni del labirinto
    for (int i = 0; i < 15; i++) { // muro superiore e inferiore
        addWall(i * WALL_SIZE, 0, WALL_SIZE, WALL_SIZE); // Muro superiore
        addWall(i * WALL_SIZE, 14 * WALL_SIZE, WALL_SIZE, WALL_SIZE); // Muro inferiore
    }
    for (int i = 0; i < 15; i++) { // muro sinistro e destro
        addWall(0, i * WALL_SIZE, WALL_SIZE, WALL_SIZE); // Muro sinistro
        addWall(14 * WALL_SIZE, i * WALL_SIZE, WALL_SIZE, WALL_SIZE); // Muro destro
    }

    // Aggiungere l'entrata (lasciare uno spazio aperto in alto a sinistra)
    walls.removeIf(wall -> wall.x == WALL_SIZE && wall.y == 0);

    // Aggiungere l'uscita (lasciare uno spazio aperto in basso a destra)
    walls.removeIf(wall -> wall.x == 13 * WALL_SIZE && wall.y == 14 * WALL_SIZE);

    // Aggiungere muri interni per il labirinto
    // Questo è un esempio di disposizione dei muri. Puoi modificare queste coordinate per creare un labirinto più complesso.

    // Primo blocco di muri
    addWall(2 * WALL_SIZE, WALL_SIZE, WALL_SIZE, 5 * WALL_SIZE);
    addWall(3 * WALL_SIZE, 5 * WALL_SIZE, 4 * WALL_SIZE, WALL_SIZE);
    addWall(6 * WALL_SIZE, 2 * WALL_SIZE, WALL_SIZE, 3 * WALL_SIZE);

    // Secondo blocco di muri
    addWall(8 * WALL_SIZE, 3 * WALL_SIZE, WALL_SIZE, 7 * WALL_SIZE);
    addWall(9 * WALL_SIZE, 3 * WALL_SIZE, 4 * WALL_SIZE, WALL_SIZE);
    addWall(12 * WALL_SIZE, 4 * WALL_SIZE, WALL_SIZE, 4 * WALL_SIZE);

    // Terzo blocco di muri
    addWall(4 * WALL_SIZE, 8 * WALL_SIZE, 5 * WALL_SIZE, WALL_SIZE);
    addWall(2 * WALL_SIZE, 10 * WALL_SIZE, WALL_SIZE, 3 * WALL_SIZE);
    addWall(5 * WALL_SIZE, 10 * WALL_SIZE, WALL_SIZE, 2 * WALL_SIZE);

    // Quarto blocco di muri
    addWall(10 * WALL_SIZE, 10 * WALL_SIZE, 3 * WALL_SIZE, WALL_SIZE);
    addWall(7 * WALL_SIZE, 12 * WALL_SIZE, WALL_SIZE, 2 * WALL_SIZE);
}

    private void addWall(int x, int y, int width, int height) {
        walls.add(new Rectangle(x, y, width, height));
    }

    public void drawMaze(Graphics g) {
        Graphics2D g2d = (Graphics2D) g; // Converti Graphics in Graphics2D
    if (texture != null) {
        g2d.setPaint(texture); // Imposta la texture come paint
        for (Rectangle wall : walls) {
            g2d.fill(wall); // Riempi ogni muro con la texture
        }
    } else {
        // Se la texture non è caricata, usa un colore solido per i muri
        g2d.setColor(new Color(64, 64, 64)); // Colore grigio scuro
        for (Rectangle wall : walls) {
            g2d.fill(wall); // Riempi ogni muro con il colore
        }
    }
    }

    public boolean checkCollision(Rectangle player) {
        for (Rectangle wall : walls) {
            if (player.intersects(wall)) {
                return true; // C'è una collisione
            }
        }
        return false; // Nessuna collisione
    }

    public boolean canMove(int newX, int newY, int width, int height) {
        Rectangle newPosition = new Rectangle(newX, newY, width, height);
        return !checkCollision(newPosition);
    }
}
