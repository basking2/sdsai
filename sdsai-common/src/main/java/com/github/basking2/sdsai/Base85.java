/**
 * Copyright (c) 2017-2021 Sam Baskinger
 */

package com.github.basking2.sdsai;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Base85 codec.
 */
public class Base85 {

	Charset US_ASCII = Charset.forName("US-ASCII");

	public static final char[] Z85_ALPHABET = new String(
			"!#$%()*+,-.0123456789;=?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[]^_abcdefghijklmnopqrstuvwxyz{|}~"
	).toCharArray();


	/**
	 * Set true if the Z85 alphabet should be used.
	 *
	 * Z85 encoding omits a few characters, such as quotes, to make use in computer programs easier.
	 */
	private boolean z85;

	public Base85() {
		this(false);
	}

	public Base85(final boolean z85) {
		this.z85 = z85;
	}

	public boolean isZ85() {
		return z85;
	}

	private int toInt(final char c) {
		if (z85) {
			final int i = Arrays.binarySearch(Z85_ALPHABET, c);
			if (i >= 0) {
				return i;
			}
			return -1;
		}

		return c - 33;
	}

	private char toChar(final int i) {
		if (i < 0) {
			return Character.MIN_VALUE;
		}

		if (z85) {
			if (i >= Z85_ALPHABET.length) {
				return Character.MIN_VALUE;
			}

			return Z85_ALPHABET[i];
		}
		else {
			return (char)(i+33);
		}
	}

	/**
	 * Encode 4 bytes of the {@code in} array into 5 bytes into the {@code out} array.
	 *
	 * @param in The source data.
	 * @param inOffset An offset into the source data.
	 * @param out The destination data.
	 * @param outOffset The offset into the destination data.
	 * @throws IllegalArgumentException If there are not 4 bytes to read or 5 spaces to write results.
	 */
	public void encode(final byte[] in, final int inOffset, final byte[] out, final int outOffset) throws IllegalArgumentException {
		if (4 + inOffset > in.length) {
			throw new IllegalArgumentException("Input buffer needs at least 4 bytes to read.");
		}
		if (5 + outOffset > out.length) {
			throw new IllegalArgumentException("Output buffer needs at least 5 bytes to write.");
		}

		long l = 0;
		l = (l << 8) | (0xff & in[inOffset + 0]);
		l = (l << 8) | (0xff & in[inOffset + 1]);
		l = (l << 8) | (0xff & in[inOffset + 2]);
		l = (l << 8) | (0xff & in[inOffset + 3]);

		for (int i = 4; i >=0; i--) {
			final int r = (int)(l % 85);
			//final long r = l - l / 85 * 85;

			l = l / 85;

			out[outOffset + i] = (byte)toChar(r);
		}
	}

	/**
	 * Decode 5 bytes of the {@code in} array into 4 bytes into the {@code out} array.
	 *
	 * @param in The source data.
	 * @param inOffset An offset into the source data.
	 * @param out The destination data.
	 * @param outOffset The offset into the destination data.
	 * @throws IllegalArgumentException If there are not 5 bytes to read or 4 spaces to write results.
	 */
	public void decode(final byte[] in, final int inOffset, final byte[] out, final int outOffset) throws IllegalArgumentException {
		if (5 + inOffset > in.length) {
			throw new IllegalArgumentException("Input buffer needs at least 5 bytes to read.");
		}
		if (4 + outOffset > out.length) {
			throw new IllegalArgumentException("Output buffer needs at least 4 bytes to write.");
		}

		long l = 0;
		for (int i = 0 ; i < 5; i++) {
			l = (l * 85) + toInt((char)in[inOffset + i]);
		}

		out[outOffset + 0] = (byte)(0xff&(((l & 0x00000000ff000000) >>> 24)));
		out[outOffset + 1] = (byte)(0xff&(((l & 0x0000000000ff0000) >>> 16)));
		out[outOffset + 2] = (byte)(0xff&(((l & 0x000000000000ff00) >>> 8)));
		out[outOffset + 3] = (byte)(0xff&(((l & 0x00000000000000ff) >>> 0)));
	}


	public byte[] encode(final byte[] in) {
		final byte[] out = new byte[5];
		encode(in, 0, out, 0);
		return out;
	}

	public byte[] decode(final byte[] in) {
		final byte[] out = new byte[4];
		decode(in, 0, out, 0);
		return out;
	}

	public String encodeString(final byte[] in) {
		final byte[] out = new byte[(in.length + 3) / 4 * 5];
		int i = 0;
		int j = 0;
		for (; i + 4 < in.length; i += 4, j += 5) {
			encode(in, i, out, j);
		}

		// Do we need to take some padding action?
		if (i < in.length) {
			int padded = 0;
			byte[] paddedIn = new byte[4];
			int i2 = 0;
			for (; i2+i < in.length; i2++) {
				paddedIn[i2] = in[i2+i];
			}
			for (; i2 < paddedIn.length; i2++) {
				paddedIn[i2] = 0;
				padded++;
			}

			encode(paddedIn, 0, out, j);
			return new String(out, 0, out.length - padded, US_ASCII);
		}
		else {
			return new String(out, US_ASCII);
		}
	}

	public byte[] decodeString(final String inString) {
		final byte[] in = inString.getBytes(US_ASCII);
		final byte[] out = new byte[(in.length +4) / 5 * 4];
		int i = 0;
		int j = 0;
		for (; i + 5 < in.length; i += 5, j += 4) {
			decode(in, i, out, j);
		}

		// We need some special padding logic.
		if (i < in.length) {
			int padded = 0;
			byte[] paddedIn = new byte[5];
			int i2 = 0;
			for (; i2 + i < in.length; i2++) {
				paddedIn[i2] = in[i2+i];
			}
			for (; i2 < paddedIn.length; i2++) {
				paddedIn[i2] = 'u';
				padded++;
			}

			decode(paddedIn, 0, out, j);
			return Arrays.copyOf(out, j + 4 - padded);
		}
		else {
			return out;
		}
	}
}

