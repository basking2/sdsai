package org.sdsai.dsp;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Clip;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineUnavailableException;

/**
 * Generate or decode a PSK31 signal.
 */
public class Psk31
{
    public static final double REVERSALS_PER_SECOND = 31.25;

    /**
     * The target frequency to generate or decode a PSK31 signal on.
     */
    private double hz;

    private int sampleRate;

    /**
     * Send a PSK digital 1. This is a
     * 1/31.25 second clip at the given sample rate of a cos
     * wave with 0 degrees shift.
     */
    private byte[] one;

    /**
     * Send a PSK digital 0. This is a 1/31.25 second clip at the
     * given sample rate of a cos wave with 180 degrees of shift.
     */
    private byte[] zero;

    public Psk31(final double hz, final int sampleRate) {
        this.hz = hz;
        this.sampleRate = sampleRate;
        this.one = generateTone(hz, sampleRate, 0, 1.0 / 31.25);
        this.zero = generateTone(hz, sampleRate, Math.PI, 1.0 / 31.25);
    }

    public byte[] getOne() {
        return one;
    }

    public byte[] getZero() {
        return zero;
    }

    /**
     * Generate a Cos wave with a sin filter around it.
     */
    private byte[] generateTone(
        final double hz,
        final int sampleRate,
        final double shift,
        final double duration)
    {
        byte[] buffer = new byte[(int)(sampleRate * duration + 1D)];
        double radians = hz * 2 * Math.PI / (double)sampleRate;

        /* Generate the shifted cos wave. */
        for (int i = 0; i < buffer.length; ++i) {
            double amplitude = Math.cos(radians * i + shift);
            buffer[i] = (byte)((double)Byte.MAX_VALUE * amplitude);
        }

        /* Sin filter the wave. */
        for (int i = 0; i < buffer.length; ++i) {
            buffer[i] = (byte)((double)buffer[i] * Math.sin(i * Math.PI/buffer.length));
        }

        return buffer;
    }

    AudioFormat getAudioFormat() {
        return new AudioFormat(sampleRate, 8, 1, true, true);
    }
}