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
import org.opencv.videoio.Videoio;
import src.com.constants.Constants;
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
	ObservableList<Integer> radiusOptions = FXCollections.observableArrayList(7, 8, 9, 10, 11, 12, 13);
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
		Image imageToShow = null;
//		Mat frame = new Mat(Constants.SCREEN_DIMENSION,0);
		Mat frame = new Mat();
		// check if the capture is open
		if (this.capture.isOpened()) {
			try {
				// read the current frame
//			    this.capture.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, Constants.WIDTH_RESOLUTION);
//			    this.capture.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, Constants.HIEGHT_RESOLUTION);
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
        if (frame != null) {
            MatOfByte buffer = new MatOfByte();
            Imgcodecs.imencode(".png", frame, buffer);
            return new Image(new ByteArrayInputStream(buffer.toArray()));
        } else {
            System.out.println("Can't convert Mat to image due to NULL Mat found");
            return null;
        }
    }

    private Mat pupilDetect(Mat inputFrame, double threshold) {
        CascadeClassifier faceDetector = new CascadeClassifier("haarcascade_eye.xml");
        Size size = new Size(880, 720);
        Mat grayScaleImageMat = new Mat(size, 0);
        Imgproc.cvtColor(inputFrame, grayScaleImageMat, Imgproc.COLOR_BGR2GRAY);
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(grayScaleImageMat, faceDetections);
        if (faceDetections.toArray().length > 1) {
            grayScaleImageMat = IrisDetectionConstantly.detectPoint(faceDetections.toArray()[0],
                    faceDetections.toArray()[1], grayScaleImageMat, grayScaleImageMat, (int) radius.getValue(),
                    (int) threshold);
        }
        return grayScaleImageMat;
    }

}
