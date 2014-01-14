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

    static class Alphabet {
        /*
        NUL 1010101011  DLE 1011110111
SOH 1011011011  DCI 1011110101
STX 1011101101  DC2 1110101101
ETX 1101110111  DC3 1110101111
EOT 1011101011  DC4 1101011011
ENQ 1101011111  NAK 1101101011
ACK 1011101111  SYN 1101101101
BEL 1011111101  ETB 1101010111
BS 1011111111   CAN 1101111011
HT 11101111     EM 1101111101
LF 11101    SUB 1110110111
VT 1101101111   ESC 1101010101
FF 1011011101   FS 1101011101
CR 11111    GS 1110111011
SO 1101110101   RS 1011111011
SI 1110101011   US 1101111111
SP 1    C 10101101
! 111111111     D 10110101
" 101011111     E 1110111
# 111110101     F 11011011
$ 111011011     G 11111101
% 1011010101    H 101010101
& 1010111011    I 1111111
101111111   J 111111101
( 11111011  K 101111101
) 11110111  L 11010111
* 101101111     M 10111011
+ 111011111     N 11011101
, 1110101   O 10101011
- 110101    P 11010101
. 1010111   Q 111011101
/ 110101111     R 10101111
0 10110111  S 1101111
1 10111101  T 1101101
2 11101101  U 101010111
3 11111111  V 110110101
    W 101011101
4 101110111     X 101011101
5 101011011     Y 101110101
6 101101011     Z 101111011
7 110101101     [ 1010101101
8 110101011     \ 111110111
9 110110111     ] 111101111
: 11110101  ^ 111111011
; 110111101     _ 1010111111
< 111101101     . 101101101
= 1010101   / 1011011111
> 111010111     a 1011
? 1010101111    b 1011111
@ 1010111101    c 101111
A 1111101   d 101101
B 11101011  e 11
f 111101    s 10111
g 1011011   t 101
h 101011    u 110111
i 1101  v 1111011
j 111101011     w 1101011
k 10111111  x 11011111
l 11011     y 1011101
m 111011    z 111010101
n 1111  { 1010110111
o 111   | 110111011
p 1111111   } 1010110101
q 110111111     ~ 1011010111
r 10101     DEL 1110110101
*/
    }
}