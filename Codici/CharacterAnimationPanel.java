import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class CharacterAnimationPanel extends JPanel implements KeyListener,Runnable {
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 650;
    private static final int CHARACTER_WIDTH = 30;
    private static final int CHARACTER_HEIGHT = 30;
    private static final int NORMAL_SPEED = 4;
    private double cumulativeSlowFactor = 1.0;
    
    private static final int GAME_TIME = 20; // 30 secondi di tempo

    // Variabili di stato del gioco
    private int x = 40, y = 0;
    private int currentSpeed = NORMAL_SPEED;
    private int timeRemaining = GAME_TIME;
    private int weaponsCollected = 0;
    private boolean gameOver = false;
    private boolean rightPressed = false, leftPressed = false, upPressed = false, downPressed = false;
    private String direction = "DOWN";

    // Oggetti di gioco
    private LabirintioPanel labirinto;
    private List<Traps> trapsList;
    private List<Weapon> weapons;
    private Flag flag;
    private Timer gameTimer;
    private Timer countdownTimer;

    // Immagini
    private BufferedImage backgroundImage;
    private BufferedImage rightImage, leftImage, upImage, downImage;
	private Buffer buf;
	private boolean showInstructions = true;
	private boolean trapSoundPlayed = false;
	
    public CharacterAnimationPanel(Buffer buf) {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        labirinto = new LabirintioPanel();
        trapsList = new ArrayList<>();
        weapons = new ArrayList<>();
        flag = new Flag(530,560,"../Image/Flag.png");
        loadImages();
        setupKeyListener();
        initializeGame();
        this.rightImage = loadAndResizeImage("../Image/lateral_right.png",35,35);
        this.leftImage = loadAndResizeImage("../Image/lateral_left.png",35,35);
        this.upImage = loadAndResizeImage("../Image/back_view.png",35,35);
        this.downImage = loadAndResizeImage("../Image/frontal_view.png",35,35);
        this.buf = buf;
        
    }
    
	public void run() {
		System.out.println(this.getName() + " " + buf.leggi());	// stampo a schermo il nome del thread consumatore (this) e il 
																// contenuto del buffer (che ha il nome del produttore che vi ha scritto)
	}

    private void loadImages() {
        try {
            backgroundImage = ImageIO.read(new File("../Image/LawnBackground.jpg"));
            rightImage = ImageIO.read(new File("../Image/lateral_right.png"));
            leftImage = ImageIO.read(new File("../Image/lateral_left.png"));
            upImage = ImageIO.read(new File("../Image/back_view.png"));
            downImage = ImageIO.read(new File("../Image/frontal_view.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //ridimensionamento delle immagini
    private BufferedImage loadAndResizeImage(String imagePath, int width, int height) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage bufferedResizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = bufferedResizedImage.createGraphics();
            g2d.drawImage(resizedImage, 0, 0, null);
            g2d.dispose();

            return bufferedResizedImage;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setupKeyListener() {
        setFocusable(true);
        addKeyListener(this);
    }

    private void initializeGame() {
        // Crea le trappole
       
		Traps nT1 = new Traps(520, 475, "../Image/Trap1.png");
        Traps nT2 = new Traps(200, 360, "../Image/Trap2.png");
        //Traps nT3 = new Traps(480, 440, "../Image/Trap3.png");
        
        // Crea le armi in posizioni diverse
		trapsList.add(nT1);
		trapsList.add(nT2);
		//trapsList.add(nT3);
		
		
        Weapon n1 = new Weapon(450, 160, "../Image/Arma1.png",this.buf);
        Weapon n2 = new Weapon(200, 160, "../Image/Arma2.png",this.buf);
        Weapon n3 = new Weapon(500, 250, "../Image/Arma3.png",this.buf);
        
        weapons.add(n1);
        weapons.add(n2);
        weapons.add(n3);

        // Timer per l'aggiornamento del gioco
        gameTimer = new Timer(16, e -> {
            if (!gameOver) {
                updateCharacterPosition();
                repaint();
            }
        });
        gameTimer.start();
		
		 new Timer(3000, e -> {
        showInstructions = false;
        repaint();
		}).start();
    
        // Timer per il countdown
        countdownTimer = new Timer(1000, e -> {
            if (!gameOver) {
                timeRemaining--;
                if (timeRemaining <= 0 || weaponsCollected == weapons.size() && flag.checkCollision(new Rectangle(x, y, CHARACTER_WIDTH, CHARACTER_HEIGHT), weaponsCollected, weapons.size())) {
                    gameOver = true;
                    ((Timer)e.getSource()).stop();
                    gameTimer.stop();
                }
                repaint();
            }
        });
        countdownTimer.start();
    }

    private void updateCharacterPosition() {
    int newX = x;
    int newY = y;

    // Aggiorna la posizione basata sulla velocità corrente
    if (rightPressed) {
        newX += currentSpeed;
        direction = "RIGHT";
    }
    if (leftPressed) {
        newX -= currentSpeed;
        direction = "LEFT";
    }
    if (upPressed) {
        newY -= currentSpeed;
        direction = "UP";
    }
    if (downPressed) {
        newY += currentSpeed;
        direction = "DOWN";
    }

    // Verifica collisioni e limiti del pannello
    Rectangle newBounds = new Rectangle(newX, newY, CHARACTER_WIDTH, CHARACTER_HEIGHT);
    if (!labirinto.checkCollision(newBounds) &&
            newX >= 0 && newX <= PANEL_WIDTH - CHARACTER_WIDTH &&
            newY >= 0 && newY <= PANEL_HEIGHT - CHARACTER_HEIGHT) {
        x = newX;
        y = newY;
    }

    // Verifica collisione con le trappole e applica rallentamento
    currentSpeed = NORMAL_SPEED;
    for (Traps trap : trapsList) {
        if (trap.checkCollision(new Rectangle(x, y, CHARACTER_WIDTH, CHARACTER_HEIGHT))) {
            cumulativeSlowFactor += 0.5; // Incrementa il rallentamento cumulativo
            currentSpeed = (int) (NORMAL_SPEED / cumulativeSlowFactor);
            if(currentSpeed <=1)
            {
				currentSpeed = 2;
			}

            // Riproduce il suono solo se non è ancora stato riprodotto per questa trappola
            if (!trap.isSoundPlayed()) {
                playSoundEffectTraps("../Sounds/trap.wav");
                trap.setSoundPlayed(true); // Imposta suono riprodotto per questa trappola
            }
            break;
        }
    }

    // Verifica raccolta armi
    for (Weapon weapon : weapons) {
        if (!weapon.isCollected() && weapon.checkCollision(new Rectangle(x, y, CHARACTER_WIDTH, CHARACTER_HEIGHT))) {
            weaponsCollected++;
            playSoundEffect("../Sounds/weapon.wav"); // Percorso del file audio
        }
    }
}

    @Override
    
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Disegna lo sfondo
    if (backgroundImage != null) {
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    // Disegna il labirinto
    labirinto.drawMaze(g);

    // Disegna le armi
    for (Weapon weapon : weapons) {
        weapon.draw(g);
    }

    // Disegna il personaggio
    Image currentImage = switch (direction) {
        case "RIGHT" -> rightImage;
        case "LEFT" -> leftImage;
        case "UP" -> upImage;
        default -> downImage;
    };
    g.drawImage(currentImage, x, y, this);

    // Disegna le trappole
    for (Traps trap : trapsList) {
        trap.drawTrap(g);
    }

    // Disegna la Flag
    flag.draw(g);

    // Disegna il timer e il conteggio delle armi
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 20));
    g.drawString("Time: " + timeRemaining + "s", 10, PANEL_HEIGHT - 20);
    g.drawString("Weapons: " + weaponsCollected + "/" + weapons.size(), PANEL_WIDTH - 150, PANEL_HEIGHT - 20);

    // Disegna le istruzioni se showInstructions è true
    if (showInstructions) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        
        String instructions = "Raccogli le 3 armi e prendi la";
        String instructions2 = "bandiera per uscire dal labirinto";

        FontMetrics fm = g.getFontMetrics();
        int messageWidth = fm.stringWidth(instructions + instructions2);
        g.drawString(instructions, (PANEL_WIDTH - messageWidth) / 2+120, PANEL_HEIGHT / 2-30);
        g.drawString(instructions2, (PANEL_WIDTH - messageWidth) / 2+105, PANEL_HEIGHT / 2+10);
    }

    // Mostra messaggio di vittoria se il gioco è finito e la Flag è stata raccolta
    if (gameOver) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String message = flag.isCollected() ? "You win! Flag Collected!" : "Game Over!";

        if (message.equals("You win! Flag Collected!")) {
            playSoundEffect("../Sounds/flag.wav");
        }

        FontMetrics fm = g.getFontMetrics();
        int messageWidth = fm.stringWidth(message);
        g.drawString(message, (PANEL_WIDTH - messageWidth) / 2-100, PANEL_HEIGHT / 2);
    }
    if (!gameOver) {
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 20));

    // Aggiornamento per essere visibile
    g.drawString("Time: " + timeRemaining + "s", 10, PANEL_HEIGHT - 50);  // più in alto di 20
    
}
}


	private void playSoundEffect(String soundFilePath) {
		try {
			File soundFile = new File(soundFilePath);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
			Clip clip = AudioSystem.getClip();
			clip.open(audioStream);
			clip.start(); // Avvia la riproduzione dell'effetto sonoro
			Timer timer = new Timer(500, e -> clip.stop()); // 1000 millisecondi = 1 secondo
			timer.setRepeats(false); // Esegui solo una volta
			timer.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
	
	private void playSoundEffectTraps(String soundFilePath) {
    try {
        File soundFile = new File(soundFilePath);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);
        
        // Avvia la riproduzione del suono per una sola volta
        clip.start();

        // Listener per chiudere il clip al termine della riproduzione
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                clip.close(); // Chiudi il clip
            }
        });

    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
        e.printStackTrace();
    }
}

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_RIGHT) rightPressed = true;
        if (key == KeyEvent.VK_LEFT) leftPressed = true;
        if (key == KeyEvent.VK_UP) upPressed = true;
        if (key == KeyEvent.VK_DOWN) downPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_UP) upPressed = false;
        if (key == KeyEvent.VK_DOWN) downPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
