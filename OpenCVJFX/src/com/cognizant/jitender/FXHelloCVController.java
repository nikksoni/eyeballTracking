package src.com.cognizant.jitender;

import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import application.Main;
import application.ImageGaze;

/**
 * The controller for our application, where the application logic is
 * implemented. It handles the button for starting/stopping the camera and the
 * acquired video stream.
 *
 *
 */
public class FXHelloCVController {

	public static int flag = 0;
	// the FXML button
	@FXML
	private Button button;
	// the FXML button
	@FXML
	private Button setConfig;
	@FXML
	private Slider threshold;
	// the FXML image view
	@FXML
	private ImageView currentFrame;
	@FXML
	private ChoiceBox radius;
	ObservableList<Integer> radiusOptions = FXCollections.observableArrayList(8, 9, 10, 11, 12, 13);
	@FXML
	private void initialize() {
		radius.setItems(radiusOptions);
		radius.setValue(9);
	}
	// a timer for acquiring the video stream
	private ScheduledExecutorService timer;
	// the OpenCV object that realizes the video capture
	private VideoCapture capture = new VideoCapture();
	// a flag to change the button behavior
	private boolean cameraActive = false;

	/**
	 * The action triggered by pushing the button on the GUI
	 *
	 * @param event
	 *            the push button event
	 */

	/**
	 * Method to initialize the container
	 */

	@FXML
	protected void startCamera(ActionEvent event) {
		if (!this.cameraActive) {
			// start the video capture
			this.capture.open(0);
			// is the video stream available?
			if (this.capture.isOpened()) {

				this.cameraActive = true;

				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {

					@Override
					public void run() {
						if(1==flag){
							try {
								InputStream fis = new BufferedInputStream(new FileInputStream(Main.mainImgPath));
								BufferedImage bufferedImage = Main.rescale(ImageIO.read(fis));
								Image imageToShow = SwingFXUtils.toFXImage(bufferedImage, null);
								currentFrame.setImage(imageToShow);
								grabFrame(threshold.getValue());
							}
							catch (IOException e) {
								e.printStackTrace();
							}
						}
						else{
							Image imageToShow = grabFrame(threshold.getValue());
							currentFrame.setImage(imageToShow);
						}
					}
				};
				if(0==flag){
					this.button.setVisible(false);
					this.setConfig.setVisible(true);
				}
				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 10, TimeUnit.MILLISECONDS);

				// update the button content
				this.button.setText("Stop Camera");
			} else {
				// log the error
				System.err.println("Impossible to open the camera connection...");
			}
		} else {
			IrisDetectionConstantly.center1=null;
			IrisDetectionConstantly.center2=null;
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.button.setVisible(false);
			try {
				Point butt = new Point(button.getLayoutX() + button.getWidth() / 2,
						button.getLayoutY() + button.getHeight() / 2);

				IrisDetectionConstantly.saveData(butt);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// stop the timer
			try {
				this.timer.shutdown();
				this.timer.awaitTermination(500, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// log the exception
				System.err
						.println("Exception in stopping the frame capture, trying to release the camera now... "
								+ e);
			}

			// release the camera
			this.capture.release();
			// clean the frame
			// this.currentFrame.setImage(null);
		}
	}

	@FXML
	protected void setConfiguration(ActionEvent event) {
		if (this.cameraActive) {
			 {
			IrisDetectionConstantly.center1 = new Point(IrisDetectionConstantly.p1.x,
					IrisDetectionConstantly.p1.y);
			IrisDetectionConstantly.center2 = new Point(IrisDetectionConstantly.p2.x,
					IrisDetectionConstantly.p2.y);
			this.setConfig.setVisible(false);
			 }

		 	try {
				InputStream fis = new BufferedInputStream(new FileInputStream(Main.mainImgPath));
				BufferedImage bufferedImage = ImageIO.read(fis);
				Image imageToShow = SwingFXUtils.toFXImage(bufferedImage, null);

				currentFrame.setImage(imageToShow);
				this.button.setVisible(true);
				flag = 1;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Get a frame from the opened video stream (if any)
	 *
	 * @return the {@link Image} to show
	 */
	private Image grabFrame(double threshold) {
		// init everything
		// System.out.println( Toolkit.getDefaultToolkit().get +"kjhkj");
		// GraphicsDevice gd =
		// GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		Image imageToShow = null;
//		new Mat();
		Mat frame = new Mat();
		// System.out.println("th"+threshold);
		// check if the capture is open
		if (this.capture.isOpened()) {
			try {
				// read the current frame
				this.capture.read(frame);

				// if the frame is not empty, process it
				if (!frame.empty()) {
					imageToShow = mat2Image(pupilDetect(frame, threshold));
				}

			} catch (Exception e) {
				// log the error
				System.err.println("Exception during the image elaboration: " + e);
			}
		}

		return imageToShow;
	}

	/**
	 * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
	 *
	 * @param frame
	 *            the {@link Mat} representing the current frame
	 * @return the {@link Image} to show
	 */
	private Image mat2Image(Mat frame) {
		// create a temporary buffer
		MatOfByte buffer = new MatOfByte();
		// encode the frame in the buffer
		Imgcodecs.imencode(".png", frame, buffer);

		// build and return an Image created from the image encoded in the
		// buffer
		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}

	private Mat pupilDetect(Mat inputFrame, double threshold) {
		int pupilRadius = 11;

		// System.out.println("\nRunning DetectFaceDemo");

		// Create a face detector from the cascade file in the resources
		// directory.
		//Path currentRelativePath = Paths.get("");
		//String s = currentRelativePath.toAbsolutePath().toString();
		//System.out.println(s);
		CascadeClassifier faceDetector = new CascadeClassifier("haarcascade_eye.xml");
		// C:/Program%20Files/Java/jdk1.6.0_06/bin/file.txt
//		Mat image = Imgcodecs.imread("D:/eyeballProject/OpenCVJFX/resources/lena.png");
		Size size=new Size(880,720);
		Mat grayScaleImageMat = new Mat(size,0);
		Imgproc.cvtColor(inputFrame, grayScaleImageMat, Imgproc.COLOR_BGR2GRAY);
		// String filename = "faceDetection.png";
		// System.out.println(String.format("Writing %s", filename));
		// Imgcodecs.imwrite("greyscale.png", grey);

		// Imgproc.morphologyEx(grey, grey, 4,
		// Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
		// cv::morphologyEx(im_rgb,im_rgb,4,cv::getStructuringElement(cv::MORPH_RECT,cv::Size(size,size)));

//		Mat grayScaleImageMatBinaryInversion = new Mat();
//		Imgproc.threshold(grayScaleImageMat, grayScaleImageMatBinaryInversion, 0, 10,
//				Imgproc.THRESH_BINARY_INV);
		// Imgcodecs.imwrite("binaryInversion.png", grey);
		// System.out.println(String.format("Writing %s", filename));
//		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		// Imgproc.findContours(grayScaleImageMatBinaryInversion.clone(),
		// contours,new Mat(), 0, 1);
		// Imgproc.drawContours(grayScaleImageMatBinaryInversion, contours, -1,
		// new Scalar(255,255,255), -1);
		// Imgcodecs.imwrite("test.png", grey);
		// Imgproc.threshold(grey, grey, 80, 255, Imgproc.THRESH_BINARY_INV);
		// Imgcodecs.imwrite("inverted.png", grey);

		// std::vector<cv::Vec3f> circles;
	//	Mat circles = new Mat();
		// circles.get(1, 1);
		// Mat test;
		// Imgproc.HoughCircles(image, circles, method, dp, minDist, param1,
		// param2, minRadius, maxRadius);

		// Mat imgSource = Highgui.imread(filepath);
//		Imgproc.Canny(inputFrame, grayScaleImageMatBinaryInversion, 300, 600, 5, true);
//		Imgcodecs.imwrite("cannySample.png", inputFrame);
//		Imgproc.HoughCircles(grayScaleImageMatBinaryInversion, circles, Imgproc.HOUGH_GRADIENT, 1,
//				20, 3, 12, 6, 20);

		/*
		 * for (int i = 0; i < contours.size(); i++) { double area
		 * =Imgproc.contourArea(contours.get(i)); Rect
		 * rect=Imgproc.boundingRect(contours.get(i));
		 *
		 * int radius = rect.width/2; // Approximate radius
		 *
		 * // Look for round shaped blob if (area >= 30 && Math.abs(1 -
		 * ((double)rect.width / (double)rect.height))<=0.2 && Math.abs(1 -
		 * (area / (Math.PI * Math.pow(radius, 2))))<=0.2) {
		 * Imgproc.circle(image, new Point(rect.x + radius, rect.y + radius),
		 * radius, new Scalar(255,0,0),2); } } Imgcodecs.imwrite(filename,
		 * image);
		 */

		// Detect faces in the image.
		// MatOfRect is a special container class for Rect.
		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(grayScaleImageMat, faceDetections);

		// System.out.println(String.format("Detected %s faces",
		// faceDetections.toArray().length));

		// Draw a bounding box around each face.

		// for( int i = 0; i < circles.cols(); i++ )
		// {
		// for (Rect rect : faceDetections.toArray()) {
		// Imgproc.rectangle(grayScaleImageMat, new Point(rect.x, rect.y), new
		// Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255,
		// 0));
		// // double vCircle[]=circles.get(0,i);
		// // Point center=new Point(Math.round(vCircle[0]),
		// Math.round(vCircle[1]));
		//
		// grayScaleImageMat=
		// IrisDetectionConstantly.detectIris(rect,grayScaleImageMat,grayScaleImageMat,10,200);
		// //
		// if(center.x>(rect.x+0.4*rect.width)&&center.x<(rect.x+0.6*rect.width)&&center.y>(rect.y+0.4*rect.height)&&center.y<(rect.y+0.6*rect.height))
		// // {
		// //
		// // int radius = (int)Math.round(vCircle[2]);
		// // System.out.println("rad="+radius);
		// // // draw the circle center
		// // Imgproc.circle(grayScaleImageMat, new
		// Point(Math.round(rect.x+0.5*rect.width),
		// Math.round(rect.y+0.5*rect.height)), 3,new Scalar(0,255,0), -1, 8, 0
		// );
		// // // draw the circle outline
		// //// Imgproc.circle( image, center, radius, new Scalar(255,255,255),
		// 1, 10, 0 );
		// // Imgproc.circle( grayScaleImageMat, center, radius, new
		// Scalar(0,0,255), 1, 10, 0 );
		// // }
		// }
		// long startTime = System.nanoTime();
		// System.out.println(radius.getSelectionModel()+"fdhg");
		if (faceDetections.toArray().length > 1) {
			grayScaleImageMat = IrisDetectionConstantly.detectPoint(faceDetections.toArray()[0],
					faceDetections.toArray()[1], grayScaleImageMat, grayScaleImageMat,
					(int) radius.getValue(), (int) threshold);
		}
		// long endTime = System.nanoTime();
		// System.out.println("time="+ (endTime-startTime));
		// }
		return grayScaleImageMat;
	}

}
