package com.github.basking2.sdsai.math;

import org.junit.Assert;
import org.junit.Test;

public class MultilinearInterpolatorTest {

	@Test
	public void testLinear() {
		MultilinearInterpolator i = 
				MultilinearInterpolator.build(new Double[]{0D}, new Double[]{1D}, d->d[0]);


		Assert.assertEquals(i.interpolate(new Double[]{0.5D}), 0.5D, 0.0001D);
	}

	@Test
	public void testBilinear() {
		MultilinearInterpolator i = 
				MultilinearInterpolator.build(new Double[]{0D, 0D}, new Double[]{1D, 1D}, d-> d[0] + d[1]);
		
		Assert.assertEquals(i.interpolate(new Double[]{0.5D, 0.5D}), 1D, 0.0001D);
		Assert.assertEquals(i.interpolate(new Double[]{1D, 0D}), 1D, 0.0001D);
		Assert.assertEquals(i.interpolate(new Double[]{0D, 1D}), 1D, 0.0001D);
		Assert.assertEquals(i.interpolate(new Double[]{1D, 1D}), 2D, 0.0001D);
		
	}
}
