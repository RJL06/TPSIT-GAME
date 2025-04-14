import javax.swing.JFrame;

public class GameTest {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Character Animation"); // Crea un frame con titolo
        Buffer buffer = new Buffer();
        CharacterAnimationPanel panel = new CharacterAnimationPanel(buffer); // Istanzia il pannello    
        frame.add(panel); // Aggiunge il pannello al frame
        frame.setSize(620, 650);
         frame.setResizable(false);// Imposta la dimensione del frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Chiude l'applicazione quando si chiude il frame
        frame.setVisible(true); // Rende visibile il frame
        panel.requestFocusInWindow(); // Richiede il focus sulla finestra per ricevere input dalla tastiera

    }
}
