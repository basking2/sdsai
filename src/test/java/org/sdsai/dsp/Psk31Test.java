package org.sdsai.dsp;

import org.testng.annotations.Test;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;

public class Psk31Test {

    @Test
    public void play1010() throws LineUnavailableException {
        Psk31 p = new Psk31(1000, 22000);

        SourceDataLine sdl = AudioSystem.getSourceDataLine(p.getAudioFormat());

        byte[] one = p.getOne();
        byte[] zero = p.getZero();

        long t = System.currentTimeMillis();

        sdl.open(p.getAudioFormat());
        sdl.start();

        while (System.currentTimeMillis() - t < 2000) {
            sdl.write(one, 0, one.length);
            sdl.write(zero, 0, zero.length);
        }

        sdl.stop();
        sdl.close();
    }
}