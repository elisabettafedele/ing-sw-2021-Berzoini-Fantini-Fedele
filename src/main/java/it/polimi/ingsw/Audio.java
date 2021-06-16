package it.polimi.ingsw;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.*;


public class Audio {
    private AudioInputStream audioInputStream;
    private Clip clip;
    private long clipTimePosition;

    public Audio(String path) {
        try {
            this.audioInputStream = AudioSystem.getAudioInputStream(new File(path).getAbsoluteFile());
            this.clip = AudioSystem.getClip();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    public Audio() {
        try {
            this.audioInputStream = AudioSystem.getAudioInputStream(new File("C:\\Users\\raffo\\Downloads\\medieval-music.wav").getAbsoluteFile());
            this.clip = AudioSystem.getClip();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    public void playSound() {
        try {
            clip.open(audioInputStream);
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch(Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }

    public void stopSound(){
        this.clipTimePosition = clip.getMicrosecondPosition();
        this.clip.stop();
    }

    public void resumeSound(){
        this.clip.setMicrosecondPosition(clipTimePosition);
        this.clip.start();
        this.clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

}

