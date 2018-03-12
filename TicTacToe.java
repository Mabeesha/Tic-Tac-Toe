package ticktacttoe;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
//import javax.swing.JPanel;

public class TicTacToe extends JFrame implements ActionListener 
{
    private JFrame gameOverFrame; //game over frame
    private JButton[][] button; //array of buttons
    private final JPanel panel1; //main game
    private JPanel panel2; //game over panel
    private int[][] bState = new int[3][3]; //keeps the state of the button
    private int currentPlayer = 1;//current player identifier.4-player 2(because of some calculation issue)
    private int moveNo =0;//keeps the number of butons pressed
    
    private final int BUFFER_SIZE = 128000;
    private File soundFile;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;    
    
    public TicTacToe()
    {   
        //setting up the main frame 
        setTitle("TickTactToe");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600,800);
        setLocationRelativeTo(null);
        
        //setting the panel and adding it to the frame
        panel1 = new JPanel(); panel1.setLayout(new GridLayout(3,3)); panel1.setPreferredSize(new Dimension(600,600));
        
        //setting the buttons
        button = new JButton[3][3];
        for(int i=0;i<3;i++)
        {
            for(int j=0;j<3;j++)
            {
                button[i][j] = new JButton("");
                button[i][j].addActionListener(this);
                button[i][j].setFont(new Font("serif",Font.BOLD,100));
                button[i][j].setForeground(new Color(30,144,255));
                panel1.add(button[i][j]);
            }
        }
        
        add(panel1); //addig the panel to the plane
        pack();
        setVisible(true);
        playSound("POL-smiley-island-short.wav");
    }
    
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        //moveNo++; // keeping the move count to check whether the all the buttons have pressed.
        int playedBy = currentPlayer;
        for(int i=0;i<3;i++) // setting the button state and button text after pressing 
        {
            for(int j=0;j<3;j++)
            {
                if(e.getSource()==button[i][j] && bState[i][j]==0)
                {
                    moveNo++;
                    bState[i][j] = currentPlayer;
                    button[i][j].setText(""+(int)Math.sqrt(currentPlayer));
                    flipPlayer();
                }
            }
        }
        
        boolean won = false;
        if(playedBy==1)// checks whether someone has won
        {
            if(bState[0][0]+bState[0][1]+bState[0][2]==3 || bState[1][0]+bState[1][1]+bState[1][2]==3 || bState[2][0]+bState[2][1]+bState[2][2]==3 || bState[0][0]+bState[1][0]+bState[2][0]==3 || bState[0][1]+bState[1][1]+bState[2][1]==3 || bState[0][2]+bState[1][2]+bState[2][2]==3 || bState[0][0]+bState[1][1]+bState[2][2]==3 || bState[0][2]+bState[1][1]+bState[2][0]==3)
            {
                gameOver("Player 1 wins!");
                won = true;
            }
        }
        else if(playedBy==4)
        {
            if(bState[0][0]+bState[0][1]+bState[0][2]==12 || bState[1][0]+bState[1][1]+bState[1][2]==12 || bState[2][0]+bState[2][1]+bState[2][2]==12 || bState[0][0]+bState[1][0]+bState[2][0]==12 || bState[0][1]+bState[1][1]+bState[2][1]==12 || bState[0][2]+bState[1][2]+bState[2][2]==12 || bState[0][0]+bState[1][1]+bState[2][2]==12 || bState[0][2]+bState[1][1]+bState[2][0]==12)
            {
                gameOver("Player 2 wins!");
                won = true;
            }               
        }
        
        if(moveNo==9 && !won)// checks whether its a draw
        {
            gameOver("Draw!!");
        }
        
        //flipPlayer();
    }
    
    public void flipPlayer() // used change the state of the current player.
    {
        if(currentPlayer==1){currentPlayer=4;}
        else{currentPlayer=1;}
    } 
    
    public void gameOver(String s) // this runs when the game is over
    {
        //initializing the game over frame
        gameOverFrame = new JFrame("Game Over!");
        gameOverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameOverFrame.setSize(600,150);
        gameOverFrame.setLocationRelativeTo(this);        
        
        panel2 = new JPanel();
        panel2.setSize(new Dimension(600,150));
        JLabel l = new JLabel(s);
        l.setFont(new Font("Serif",Font.PLAIN,50));
        l.setHorizontalAlignment(SwingConstants.CENTER);
        panel2.add(l);
        gameOverFrame.add(panel2);
        gameOverFrame.setVisible(true);
    }
    
    public void playSound(String filename){

        String strFilename = filename;

        try {
            soundFile = new File(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine.start();

        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }

        sourceLine.drain();
        sourceLine.close();
    }

    
    
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException 
    {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        TicTacToe t = new TicTacToe();
        
    }
}
