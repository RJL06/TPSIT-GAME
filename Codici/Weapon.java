import javax.swing.*;
import java.awt.*;

public class Weapon implements Runnable {
    private static final int WEAPON_WIDTH = 30;
    private static final int WEAPON_HEIGHT = 30;
    private final Image weaponImage;
    private int x;
    private int y;
    private boolean collected;
    private boolean running;
	private Buffer buf;
    public Weapon(int x, int y, String imagePath,Buffer buf) {
        this.x = x;
        this.y = y;
        this.collected = false;
        this.running = true;
        this.weaponImage = new ImageIcon(imagePath).getImage()
            .getScaledInstance(WEAPON_WIDTH, WEAPON_HEIGHT, Image.SCALE_SMOOTH);
        this.buf=buf;
        // Avvia il thread per l'arma
        Thread thread = new Thread(this);
        thread.start();
    }

		@Override
	public void run() {
		while (running) {
			try {
				moveWeapon();
				if (buf != null) { // Controllo per evitare NullPointerException
					buf.scrivi("");
				}
				Thread.sleep(90); //velocità di caduta
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


    private void moveWeapon() {
        // Esempio di movimento: sposta l’arma su e giù
        if (!collected) {
            y += 1; 
			// Modifica l'asse `y` per creare un movimento verticale
            if (y > 600) {  // Limite inferiore dello schermo (da adattare alle dimensioni del pannello)
                y = 0;      // Riporta l'arma all'inizio per un ciclo continuo
            }
        }
    }

    public void draw(Graphics g) {
        if (!collected) {
            g.drawImage(weaponImage, x, y, null);
        }
    }

    public boolean checkCollision(Rectangle playerBounds) {
        if (!collected && playerBounds.intersects(new Rectangle(x, y, WEAPON_WIDTH, WEAPON_HEIGHT))) {
            collected = true;
            stopThread();  // Ferma il thread una volta raccolto
            return true;
        }
        return false;
    }

    public boolean isCollected() {
        return collected;
    }

    public void stopThread() {
        running = false;
    }
}

