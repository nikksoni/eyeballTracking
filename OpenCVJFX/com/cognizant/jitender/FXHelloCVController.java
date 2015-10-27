package com.cognizant.jitender;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * The controller for our application, where the application logic is
 * implemented. It handles the button for starting/stopping the camera and the
 * acquired video stream.
 * 
 * 
 */
public class FXHelloCVController
{
	// the FXML button
	@FXML
	private Button button;
	// the FXML image view
	@FXML
	private ImageView currentFrame;
	
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
	protected void startCamera(ActionEvent event)
	{	
		if (!this.cameraActive)
		{
			// start the video capture
			this.capture.open(0);
			
			// is the video stream available?
			if (this.capture.isOpened())
			{
				
				this.cameraActive = true;
				
				// grab a frame every 33 ms (30 frames/sec)
				Runnable frameGrabber = new Runnable() {
					

					@Override
					public void run()
					{
						Image imageToShow = grabFrame();
						
						currentFrame.setImage(imageToShow);
					}
				};

				this.timer = Executors.newSingleThreadScheduledExecutor();
				this.timer.scheduleAtFixedRate(frameGrabber, 0, 330, TimeUnit.MILLISECONDS);
				
				// update the button content
				this.button.setText("Stop Camera");
			}
			else
			{
				// log the error
				System.err.println("Impossible to open the camera connection...");
			}
		}
		else
		{
			// the camera is not active at this point
			this.cameraActive = false;
			// update again the button content
			this.button.setText("Start Camera");
			
			// stop the timer
			try
			{
				this.timer.shutdown();
				this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e)
			{
				// log the exception
				System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
			}
			
			// release the camera
			this.capture.release();
			// clean the frame
			this.currentFrame.setImage(null);
		}
	}
	
	/**
	 * Get a frame from the opened video stream (if any)
	 * 
	 * @return the {@link Image} to show
	 */
	private Image grabFrame()
	{
		// init everything
		Image imageToShow = null;
		Mat frame = new Mat();
		
		// check if the capture is open
		if (this.capture.isOpened())
		{				
			try
			{
				// read the current frame
				this.capture.read(frame);
				
				// if the frame is not empty, process it
				if (!frame.empty())
				{				
					

					// convert the image to gray scale
					//Imgproc.cvtColor(frame, frame, Imgproc.COLOR_BGR2GRAY);
					// convert the Mat object (OpenCV) to Image (JavaFX)
					imageToShow = mat2Image(pupilDetect(frame));
					
				}
				
			}
			catch (Exception e)
			{
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
	private Image mat2Image(Mat frame)
	{
		System.out.println("mat = "  );
		
		// create a temporary buffer
		MatOfByte buffer = new MatOfByte();
		// encode the frame in the buffer
		Imgcodecs.imencode(".png", frame, buffer);
		
		// build and return an Image created from the image encoded in the
		// buffer
		return new Image(new ByteArrayInputStream(buffer.toArray()));
	}
	
	private Mat pupilDetect(Mat inputFrame)
	{
		
		
//		 System.out.println("\nRunning DetectFaceDemo");

		    // Create a face detector from the cascade file in the resources
		    // directory.
		    CascadeClassifier faceDetector = new CascadeClassifier("D:/eyeballProject/OpenCVJFX/resources/haarcascade_eye.xml");
//		    C:/Program%20Files/Java/jdk1.6.0_06/bin/file.txt
//		    Mat image = Imgcodecs.imread("D:/eyeballProject/OpenCVJFX/resources/lena.png");

		    Mat grayScaleImageMat=new Mat();
		  Imgproc.cvtColor(inputFrame, grayScaleImageMat, Imgproc.COLOR_BGR2GRAY);
		   // String filename = "faceDetection.png";
		  //  System.out.println(String.format("Writing %s", filename));
		   // Imgcodecs.imwrite("greyscale.png", grey);
		    
//		    Imgproc.morphologyEx(grey, grey, 4, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
//		    cv::morphologyEx(im_rgb,im_rgb,4,cv::getStructuringElement(cv::MORPH_RECT,cv::Size(size,size)));
		    
		  Mat grayScaleImageMatBinaryInversion=new Mat();
		    Imgproc.threshold(grayScaleImageMat, grayScaleImageMatBinaryInversion, 80, 255, Imgproc.THRESH_BINARY_INV);
//		    Imgcodecs.imwrite("binaryInversion.png", grey);
//		    System.out.println(String.format("Writing %s", filename));
		    List<MatOfPoint> contours= new ArrayList<MatOfPoint>();
		    Imgproc.findContours(grayScaleImageMatBinaryInversion.clone(), contours,new Mat(), 0, 1);
		    Imgproc.drawContours(grayScaleImageMatBinaryInversion, contours, -1, new Scalar(255,255,255), -1);
//		    Imgcodecs.imwrite("test.png", grey);
		  //  Imgproc.threshold(grey, grey, 80, 255, Imgproc.THRESH_BINARY_INV);
//		    Imgcodecs.imwrite("inverted.png", grey);
		    
//		    std::vector<cv::Vec3f> circles;
		    Mat circles=new Mat();
//		    Mat test;
//		    Imgproc.HoughCircles(image, circles, method, dp, minDist, param1, param2, minRadius, maxRadius);
		    Imgproc.HoughCircles(grayScaleImageMatBinaryInversion, circles, Imgproc.HOUGH_GRADIENT, 1, 20,3,12,6,20);
		    
		    

		    
		   /* for (int i = 0; i < contours.size(); i++)
		    {
		        double area =Imgproc.contourArea(contours.get(i));
		        Rect rect=Imgproc.boundingRect(contours.get(i));
		        
		        int radius = rect.width/2;                     // Approximate radius

		        // Look for round shaped blob
		        if (area >= 30 && Math.abs(1 - ((double)rect.width / (double)rect.height))<=0.2 && Math.abs(1 - (area / (Math.PI * Math.pow(radius, 2))))<=0.2)
		        {
		        	Imgproc.circle(image, new Point(rect.x + radius, rect.y + radius), radius, new Scalar(255,0,0),2);
		        }
		    }
		    Imgcodecs.imwrite(filename, image);*/
		 

		    
		    // Detect faces in the image.
		    // MatOfRect is a special container class for Rect.
		    MatOfRect faceDetections = new MatOfRect();
		    faceDetector.detectMultiScale(grayScaleImageMat, faceDetections);

		    System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

		    // Draw a bounding box around each face.
		   

		    for( int i = 0; i < circles.cols(); i++ ) 
		    {
		    	 for (Rect rect : faceDetections.toArray()) {
		    	    	Imgproc.rectangle(grayScaleImageMat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
		    	    	double vCircle[]=circles.get(0,i); 
		    	    	Point center=new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
		    	
		    	if(center.x>rect.x&&center.x<(rect.x+rect.width)&&center.y>rect.y&&center.y<(rect.y+rect.height))
		    	{
		    		
		    		  int radius = (int)Math.round(vCircle[2]);
		    	        // draw the circle center
		    	       // Imgproc.circle(image, center, 3,new Scalar(0,255,0), -1, 8, 0 );
		    	        // draw the circle outline
//		    	        Imgproc.circle( image, center, radius, new Scalar(255,255,255), 1, 10, 0 );
		    	        Imgproc.circle( grayScaleImageMat, center, radius, new Scalar(0,0,255), 1, 10, 0 );
		    	}
		      }
		    	
		    }
//		    Imgcodecs.imwrite("bw.png", grey);
//		    Imgcodecs.imwrite("color.png", image);
		    
		    
		    
		    
		    // Save the visualized detection.
//		    System.out.println(String.format("Writing %s", filename));
//		    Imgcodecs.imwrite(filename, grey);
		    return grayScaleImageMat;
	}
	
}
