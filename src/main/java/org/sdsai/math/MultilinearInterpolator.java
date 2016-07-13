package org.sdsai.math;

import java.util.function.Function;
import java.lang.Math;

/**
 * A generalization of interpolation.
 *
 * The startPoints and stopPoints arrays must be the same length. Each index
 * in the arrays denotes the starting and stopping value in a single dimension.
 * For example, startPoints(0) could be x_1 in which case stopPoints(0)
 * would be x_2.
 *
 * A single startPoint and stopPoint value pair is simply linear interpolation.
 * Two values is bilinear, and the dimensionality increases.
 *
 * The results array is complex because it must hold every combination of
 * dimension values. That is, 2^dimension^ values.
 *
 */
public class MultilinearInterpolator {
	
	private final Double[] startPoints;
	private final Double[] stopPoints;
	private final Double[] results;
	private final Double term_denominator;

	/**
	 * @param startPoints The x_1 etc points.
	 * @param stopPoints The x_2 etc points.
	 * @param results The result of all combinations of start-stop data points.
     */
	public MultilinearInterpolator(
		final Double[] startPoints,
		final Double[] stopPoints,
		final Double[] results
	)
	{
		assert(startPoints.length == startPoints.length);
		assert(Math.pow(2, startPoints.length) == results.length);
	  
		this.startPoints = startPoints;
		this.stopPoints = stopPoints;
		this.results = results;
		
		Double v = 1D;
		for (int i = 0; i < startPoints.length; ++i) {
			v *= stopPoints[i] - startPoints[i];
		}
		
		this.term_denominator = v;
	}
	
	public static MultilinearInterpolator build(final Double[] startPoints, final Double[] stopPoints, Function<Double[], Double> op) {
		Double[] results = new Double[(int)Math.pow(2D, startPoints.length)];
		
		Double[] args = new Double[startPoints.length];
		
		for (int i = 0; i < results.length; ++i) {
			for (int j = 0; j < startPoints.length; ++j) {
				args[j] = ((i&(1<<j)) == 0)? startPoints[j] : stopPoints[j];
			}

			results[i] = op.apply(args);
		}

		return new MultilinearInterpolator(startPoints, stopPoints, results);
	}
	
	public Double interpolate(Double[] points) {
		Double v = 0D;
		
		for (int r = 0; r < results.length; ++r) {
			v += results[r] * term_diff(r, points);
		}
		
		return v / term_denominator;
	}
	
	private Double term_diff(int r, Double[] point) {
		Double v = 1D;
		
		for (int i = 0; i < startPoints.length; ++i) {
			final Double v1 = ((r & (1 << i)) == 0)? (stopPoints[i] - point[i]) : (point[i] - startPoints[i]);
			
			v *= v1;
		}
		
		return v;
	}
}
