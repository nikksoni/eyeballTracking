package com.cognizant.jitender;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class IrisDetectionConstantly {
	static Point center1=null;
	static Point center2=null;
	static Point p1=null;
	static Point p2=null;

	/*public static Mat detectIris(Rect rect, Mat input, Mat output,
			int irisRadius, int thresholdLevel) {
		Mat grey = new Mat();
		System.out.println("threshold= "+thresholdLevel);
		Imgproc.Canny(input, grey, thresholdLevel, 600, 5, true);
		int localMax = 0;
		int localRadiusX = 0;
		int localRadiusY = 0;
		byte[] pixelD = { 0, 0, 0 };
		for (int xalign = 150; xalign < 180; xalign++) {

			input.put(xalign, 280, pixelD);
		}
		for (int yalign = 265; yalign < 295; yalign++) {

			input.put(165, yalign, pixelD);
		}

		for (int xAxis = (int) (rect.x + 0.3 * rect.width); xAxis <= rect.x
				+ 0.7 * rect.width; xAxis++) {
			for (int yAxis = (int) (rect.y + 0.3 * rect.height); yAxis <= rect.y
					+ 0.7 * rect.height; yAxis++) {
				int count = findMax(grey, xAxis, yAxis, irisRadius,
						thresholdLevel);
				if (count > localMax) {
					localMax = count;
					localRadiusX = xAxis;
					localRadiusY = yAxis;
				}
			}
		}
		if (localRadiusX != 0 && localRadiusY != 0 && x == 0 && y == 0) {
			x = localRadiusX;
			y = localRadiusY;
		}
		int delx = x - localRadiusX;
		int dely = localRadiusY - y;
		if (delx > -4 && delx < 4) {
			delx = 0;
		}
		if (dely > -1 && dely < 1) {
			dely = 0;
		}
		if (delx < 35 && dely < 35 && delx > -35 && dely > -35) {
			int dx = (int) ((delx * 26.2) / 1.2);
			int dy = (int) ((dely * 26.2) / 1.2);
			System.out.println("delx= " + dx + " dy= " + dy);
			Point center2 = new Point(x + dx, y + dy);
			Imgproc.circle(input, center2, 1, new Scalar(0, 0, 255), 3, 10, 0);
		}
		Point center = new Point(localRadiusX, localRadiusY);
		Imgproc.circle(input, center, irisRadius, new Scalar(0, 0, 255), 1, 10,
				0);
		return input;
	}
	*/
	public static Mat detectPoint(Rect rect1, Rect rect2, Mat input, Mat output, int irisRadius,
			int thresholdLevel) {
		Imgproc.rectangle(input, new Point(rect1.x, rect1.y), new Point(rect1.x + rect1.width,
				rect1.y + rect1.height), new Scalar(0, 255, 0));
		Imgproc.rectangle(input, new Point(rect2.x, rect2.y), new Point(rect2.x + rect2.width,
				rect2.y + rect2.height), new Scalar(0, 255, 0));
		Mat grey = new Mat();
		Imgproc.Canny(input, grey, thresholdLevel, 600, 5, true);
		int localMax1 = 0;
		int localRadiusX1 = 0;
		int localRadiusY1 = 0;
		int localMax2 = 0;
		int localRadiusX2 = 0;
		int localRadiusY2 = 0;
		for (int xAxis = (int) (rect1.x + 0.4 * rect1.width); xAxis <= rect1.x + 0.6 * rect1.width; xAxis++) {
			for (int yAxis = (int) (rect1.y + 0.4 * rect1.height); yAxis <= rect1.y + 0.6
					* rect1.height; yAxis++) {
				int count = findMax(grey, xAxis, yAxis, irisRadius, thresholdLevel);
				if (count > localMax1) {
					localMax1 = count;
					localRadiusX1 = xAxis;
					localRadiusY1 = yAxis;
				}
			}
		}
		for (int xAxis = (int) (rect2.x + 0.4 * rect2.width); xAxis <= rect2.x + 0.6 * rect2.width; xAxis++) {
			for (int yAxis = (int) (rect2.y + 0.4 * rect2.height); yAxis <= rect2.y + 0.6
					* rect2.height; yAxis++) {
				int count = findMax(grey, xAxis, yAxis, irisRadius, thresholdLevel);
				if (count > localMax2) {
					localMax2 = count;
					localRadiusX2 = xAxis;
					localRadiusY2 = yAxis;
				}
			}
		}
		int delx = 0, dely = 0, del2x = 0, del2y = 0;
		double dis = 46.2;
		if (center1 != null) {
			delx = (int) center1.x - localRadiusX1;
			dely = localRadiusY1 - (int) center1.y;
		}
		if (center2 != null) {
			del2x = (int) center2.x - localRadiusX2;
			del2y = localRadiusY2 - (int) center2.y;
		}
		int errorFactor = 2;
		if ((delx - del2x > errorFactor || delx - del2x < -errorFactor)
				|| (dely - del2y > errorFactor || dely - del2y < -errorFactor)) {
			delx = 0;
			dely = 0;
			del2x = 0;
			del2y = 0;
		}
		int dx = (int) ((delx * dis) / 1.2);
		int dy = (int) ((dely * dis) / 1.2);
		Point pt1 = new Point(input.cols() / 2 + dx, input.rows() / 2 + dy);
//		 Imgproc.circle(input, pt1, 1, new Scalar(255, 255, 255), 3, 10, 0);
		p1 = new Point(localRadiusX1, localRadiusY1);
		Imgproc.circle(input, p1, irisRadius, new Scalar(0, 0, 255), 1, 10, 0);
		dx = (int) ((del2x * dis) / 1.2);
		dy = (int) ((del2y * dis) / 1.2);
		Point pt2 = new Point(input.cols() / 2 + dx, input.rows() / 2 + dy);
//		 Imgproc.circle(input, pt2, 1, new Scalar(255, 255, 255), 3, 10, 0);
		p2 = new Point(localRadiusX2, localRadiusY2);
		Imgproc.circle(input, p2, irisRadius, new Scalar(0, 0, 255), 1, 10, 0);
		input = drawPoint(pt1, pt2, input);
		return input;
//		 return grey;
	}	
	
	public static Mat drawPoint(Point point1, Point point2, Mat input) {
		Point defaultCenter = new Point(input.cols()/2,input.rows()/2);
		int margin = 60;
		if (point1.x - point2.x < margin && point1.y - point2.y < margin
				&& point1.x - point2.x > -margin && point1.y - point2.y > -margin) {
			Point newPoint = new Point((point1.x + point2.x) / 2, (point1.y + point2.y) / 2);
			Imgproc.circle(input, newPoint, 1, new Scalar(0, 0, 255), 3, 10, 0);
		} else {
			Imgproc.circle(input, defaultCenter, 1, new Scalar(0, 0, 255), 3, 10, 0);
		}
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
		// int pixelValue2 = (int) grey.get(colm, row - 1)[0];
		// int pixelValue3 = (int) grey.get(colm, row + 1)[0];
		// int pixelValue4 = (int) grey.get(colm + 1, row)[0];
		// int pixelValue5 = (int) grey.get(colm - 1, row)[0];
		// return (pixelValue1 > 200 || pixelValue2 > 200 || pixelValue3 > 200
		// || pixelValue4 > 200 || pixelValue5 > 200) ? true : false;
		return (pixelValue1 > 200) ? true : false;
	}
}