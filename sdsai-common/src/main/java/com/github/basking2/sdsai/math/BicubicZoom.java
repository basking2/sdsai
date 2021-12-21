/**
 * Copyright (c) 2016-2021 Sam Baskinger
 */

package com.github.basking2.sdsai.math;

import org.apache.commons.math3.analysis.interpolation.BicubicInterpolatingFunction;
import org.apache.commons.math3.analysis.interpolation.BicubicInterpolator;

/**
 */
public class BicubicZoom {
	final private BicubicInterpolator bicubicInterpolator = new BicubicInterpolator();

	/**
	 * Zoom region
	 *
	 * @param dataIn The source data.
	 * @param xIn Starting x in the source data.
	 * @param yIn Starting y in the source data.
	 * @param widthIn How wide the region to interpolate is.
	 * @param heightIn How high the region to interpolate is.
	 * @param strideIn How wide the source data is. That is, we may zoom a 10x10 region into a 30x30 image.
	 *
	 * @param dataOut The destination array.
	 * @param xOut Starting x in the destination data.
	 * @param yOut Starting y in the destination data.
	 * @param widthOut How wide the region to write into is.
	 * @param heightOut How high the region to write into is.
	 * @param strideOut How wide the destination data is.
	 */
	public void zoom
	(
		final double[] dataIn,
		final int xIn,
		final int yIn,
		final int widthIn,
		final int heightIn,
		final int strideIn,
	
		final double[] dataOut,
		final int xOut,
		final int yOut,
		final int widthOut,
		final int heightOut,
		final int strideOut
	) {
		// out * scale = out
		final double scale = (double)widthOut / (double)widthIn;

		// Build the function.
		final BicubicInterpolatingFunction bicubicInterpolatingFunction = buildBicubicInterpolatingFunction(
				dataIn,
				xIn,
				yIn,
				widthIn,
				heightIn,
				strideIn,

				xOut,
				yOut,
				widthOut,
				heightOut,
				strideOut,

				scale
				);

		// Interpolate the inside.
		fillWithBicubicInterpolatingFunction(
				bicubicInterpolatingFunction,

				dataOut,
				xOut,
				yOut,
				widthOut,
				heightOut,
				strideOut
				);
	}

	private BicubicInterpolatingFunction buildBicubicInterpolatingFunction
	(
		final double[] data,
		final int xIn,
		final int yIn,
		final int widthIn,
		final int heightIn,
		final int strideIn,

		final int xOut,
		final int yOut,
		final int widthOut,
		final int heightOut,
		final int strideOut,

		final double scale
	) {
		// I and j are indexes along the width and height.
		// They are converted to x and y values.
		final double[] xPoints = new double[widthIn+2];
		final double[] yPoints = new double[heightIn+2];
		final int offset = xIn + yIn * strideIn;
		final double[][] values = new double[widthIn + 2][heightIn + 2];

		for (int i = 0; i < widthIn; ++i) {
			xPoints[i+1] = i * scale;

			final double[] array = values[i+1];
			array[0] = data[offset + i + 0 * strideIn];
			array[array.length-1] = data[offset + i + (heightIn-1) * strideIn];

			for (int j = 0; j < heightIn; ++j) {

				// FIXME - this only needs to be done once but is done `i` times.
				yPoints[j+1] = j * scale;

				array[j+1] = data[offset + i + j * strideIn];
			}    	
		}

		final double[] leftCol = values[0];
		leftCol[0] = data[offset];
		leftCol[heightIn+1] = data[offset + (heightIn-1) * strideIn];

		final double[] rightCol = values[widthIn+1];
		rightCol[0] = data[offset + widthIn - 1];
		rightCol[heightIn+1] = data[offset + widthIn - 1 + (heightIn-1) * strideIn];

		for (int i = 0; i < heightIn; ++i) {
			leftCol[i+1]  = data[offset + (i * strideIn)];
			rightCol[i+1] = data[offset + widthIn - 1 + (i * strideIn)];
		}

		xPoints[0] = -1 * scale;
		xPoints[widthIn+1] = (widthIn+1)*scale;

        yPoints[0] = -1 * scale;
		yPoints[heightIn+1] = (heightIn+1)*scale;

		return bicubicInterpolator.interpolate(xPoints, yPoints, values);
	}

	private void fillWithBicubicInterpolatingFunction(
		final BicubicInterpolatingFunction bicubicInterpolatingFunction,

		final double[] dataOut,
		final int xOut,
		final int yOut,
		final int widthOut,
		final int heightOut,
		final int strideOut
	) {
		final int offsetOut = xOut + yOut * strideOut;

		for (int i = 0; i < widthOut; ++i) {
			for (int j = 0; j < heightOut; ++j) {
				dataOut[offsetOut + i + j * strideOut] = bicubicInterpolatingFunction.value(i, j);
			}
		}
	}
}