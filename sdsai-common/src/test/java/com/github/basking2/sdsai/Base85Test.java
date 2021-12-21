/**
 * Copyright (c) 2017-2021 Sam Baskinger
 */

package com.github.basking2.sdsai;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 */
public class Base85Test {
	@Test
	public void testEncode() {
		final byte[] input = new byte[]{'a', 'b', 'c', 'd'};
		final byte[] result = new Base85().encode(input);
		final byte[] output = new Base85().decode(result);

		assertArrayEquals(input, output);
	}

	@Test
	public void testWikipediaExample() {
		final String s = "9jqo^BlbD-BleB1DJ+*+F(f,q/0JhKF<GL>Cj@.4Gp$d7F!,L7@<6@)/0JDEF<G%<+EV:2F!,O<DJ+*.@<*K0@<6L(Df-\\0Ec5e;DffZ(EZee.Bl.9pF\"AGXBPCsi+DGm>@3BB/F*&OCAfu2/AKY" + "i(DIb:@FD,*)+C]U=@3BN#EcYf8ATD3s@q?d$AftVqCh[NqF<G:8+EV:.+Cf>-FD5W8ARlolDIal(DId<j@<?3r@:F%a+D58'ATD4$Bl@l3De:,-DJs`8ARoFb/0JMK@qB4^F!,R<AKZ&-DfTqBG%G>uD.RTpAKYo'+CT/5+Cei#DII?(E,9)oF*2M7/c";
		final String s2 = "Man is distinguished, not only by his reason, but by this singular passion from other animals, which is a lust of the mind, that by a perseverance of delight in the continued and indefatigable generation of knowledge, exceeds the short vehemence of any carnal pleasure.";
		final Base85 base85 = new Base85();

		final String s2_actual = new String(base85.decodeString(s));

		final String s_actual = base85.encodeString(s2.getBytes());

		assertEquals(s, s_actual);
		assertEquals(s2, s2_actual);
	}

	@Test
	public void encodeTest() {
		final String input = "wow, great test.";
		final String expected = "GAh[V+D5_+@<<W6ATMrG";

		final String actual = new Base85().encodeString(input.getBytes());

		assertEquals(expected, actual);
	}

	@Test
	public void decodeTest() {
		final String input = "GAh[V+D5_+@<<W6ATMrG";
		final String expected = "wow, great test.";

		final String actual = new String(new Base85().decodeString(input));

		assertEquals(expected, actual);
	}

	@Test
	public void encodeTest2() {
		final String input = "This is a test.";
		final String expected = "<+oue+DGm>@3BZ'F*'#";

		final String actual = new Base85().encodeString(input.getBytes());

		assertEquals(expected, actual);
	}

	@Test
	public void decodeTest2() {
		final String input = "<+oue+DGm>@3BZ'F*'#";
		final String expected = "This is a test.";

		final String actual = new String(new Base85().decodeString(input));

		assertEquals(expected, actual);
	}
}
