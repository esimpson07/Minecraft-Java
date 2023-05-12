import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JTextField;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class DDDTutorial 
{    
    static Dimension ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
    static JTextField TF;
    
    public static void main(String[] args)
    {
        initMusic();
        JFrame F = new JFrame();
        Screen ScreenObject = new Screen();
        F.add(ScreenObject);
        F.setUndecorated(true);
        F.setSize(ScreenSize);
        F.setVisible(true);
        F.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    static Clip themeMusic;
    static Clip bellSound;
    
    public static void initMusic() 
    {
        Integer songNumber = (int)(Math.random() * 7) + 1;
        String songPath = new String("Resources/Soundtrack/" + songNumber.toString() + ".wav");
        themeMusic = loadClip(songPath);
        //if(themeMusic != null)
        //    themeMusic.loop(2);
    }
    
    public static Clip loadClip(String fnm)
    {
        AudioInputStream audioInputStream; 
        Clip clip = null;
        try
        {
            audioInputStream =  
                AudioSystem.getAudioInputStream(new File(fnm).getAbsoluteFile());
            // create clip reference 
            clip = AudioSystem.getClip(); 
            clip.open(audioInputStream);
        }
        catch(Exception e)
        {
            System.out.println("Error building audio clip from file="+fnm);
        }
        return clip;
     }
}
