package com.cognizant.jitender;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class IrisDetectionConstantly {

	public static Mat detectIris(Rect rect, Mat input, Mat output,
			int irisRadius, int thresholdLevel) {
		Mat grey = new Mat();
		Imgproc.Canny(input, grey, 1500, 600, 5, true);
		int localMax = 0;
		int localRadiusX = 0;
		int localRadiusY = 0;
//		int conPosXPosY=0;
//		int conPosXNegY=0;
//		int conNegXPosY=0;
//		int conNegXNegY=0;
//		boolean boolPosXPosY=true;
//		boolean boolPosXNegY=true;
//		boolean boolNegXPosY=true;
//		boolean boolNegXNegY=true;
		
		for (int xAxis = (int) (rect.x + 0.4 * rect.width); xAxis <= rect.x
				+ 0.6 * rect.width; xAxis++) {
			for (int yAxis = (int) (rect.y + 0.4 * rect.height); yAxis <= rect.y
					+ 0.6 * rect.height; yAxis++) {
				int count = findMax(grey, xAxis, yAxis, irisRadius,
						thresholdLevel);
				if (count > localMax) {
					localMax = count;
					localRadiusX = xAxis;
					localRadiusY = yAxis;
				}

			}

		}
		Point center = new Point(localRadiusX, localRadiusY);
		Imgproc.circle(input, center, irisRadius, new Scalar(0, 0, 255), 1, 10,
				0);
		return input;

	}

	private static int findMax(Mat grey, int xAxis, int yAxis, int irisRadius,
			int thresholdLevel) {
		int count = 0;
		int radiusSq = irisRadius * irisRadius;
		for (int y = 1; y < irisRadius; y++) {
			int ySq = y * y;
			int x = (int) Math.sqrt(radiusSq - ySq);
			count += checkPoint(grey, xAxis + x, yAxis + y) ? 1 : 0;
			count += checkPoint(grey, xAxis - x, yAxis + y) ? 1 : 0;
			count += checkPoint(grey, xAxis + x, yAxis - y) ? 1 : 0;
			count += checkPoint(grey, xAxis - x, yAxis - y) ? 1 : 0;

		}
		count += checkPoint(grey, xAxis, yAxis + irisRadius) ? 1 : 0;
		count += checkPoint(grey, xAxis - irisRadius, yAxis) ? 1 : 0;
		count += checkPoint(grey, xAxis, yAxis - irisRadius) ? 1 : 0;
		count += checkPoint(grey, xAxis + irisRadius, yAxis) ? 1 : 0;
		return count;
	}

	public static boolean checkPoint(Mat grey, int row, int colm) {
		int pixelValue1 = (int) grey.get(colm, row)[0];
		int pixelValue2 = (int) grey.get(colm, row - 1)[0];
		int pixelValue3 = (int) grey.get(colm, row + 1)[0];
		int pixelValue4 = (int) grey.get(colm + 1, row)[0];
		int pixelValue5 = (int) grey.get(colm - 1, row)[0];
		return (pixelValue1 > 200 || pixelValue2 > 200 || pixelValue3 > 200
				|| pixelValue4 > 200 || pixelValue5 > 200) ? true : false;
	}
}