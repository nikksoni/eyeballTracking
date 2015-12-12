package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.opencv.core.Core;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics2D;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;


/**
 * The main class for a JavaFX application. It creates and handle the main
 * window with its resources (style, graphics, etc.).
 *
 *
 */
public class Main extends Application
{
	public static String mainImgPath;
    private static final int baseSize = 880;

	static{
		//System.out.println(System.getProperty("java.library.path"));
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		//System.loadLibrary("opencv_java300.dll");
	}

	public class ImageLoader extends JFrame{

	    private JPanel contentPane;
	    File targetFile;
	    BufferedImage targetImg;
	    public JPanel panel,panel_1,mainPanel;
	    private static final String basePath ="C:/Users/";

		public void loaderStart(final String[] args) {
	        EventQueue.invokeLater(new Runnable() {
	            public void run() {
	                try {
	                    ImageLoader frame = new ImageLoader(args);
	                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	                    frame.setVisible(true);
	                    frame.setResizable(false);
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	        });
	    }
		public ImageLoader(final String[] args) {
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        contentPane = new JPanel();
	        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
	        setContentPane(contentPane);
	        contentPane.setLayout(new BorderLayout(0, 0));

	        panel = new JPanel();
	        panel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
	        contentPane.add(panel, BorderLayout.WEST);

	        JButton btnBrowse = new JButton("Browse");
	        btnBrowse.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                browseButtonActionPerformed(e);
	            }
	        });

	        JLabel lblSelectTargetPicture = new JLabel("Select target picture..");

	        final JButton btnStart = new JButton("Start");
	        btnStart.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	startTracking(args,btnStart);
	            }
	        });

	        panel_1 = new JPanel();
	        panel_1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

	        GroupLayout gl_panel = new GroupLayout(panel);
	        gl_panel.setHorizontalGroup(
	            gl_panel.createParallelGroup(Alignment.LEADING)
	                .addGroup(gl_panel.createSequentialGroup()
	                    .addGap(6)
	                    .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
	                        .addGroup(gl_panel.createSequentialGroup()
	                            .addComponent(lblSelectTargetPicture)
	                            .addGap(6)
	                            .addComponent(btnBrowse))
	                        .addGroup(gl_panel.createSequentialGroup()
	                            .addGap(100)
	                            .addComponent(btnStart)
	                            .addGap(18))))
	                .addGroup(gl_panel.createSequentialGroup()
	                    .addGap(50))
	        );
	        gl_panel.setVerticalGroup(
	            gl_panel.createParallelGroup(Alignment.LEADING)
	                .addGroup(gl_panel.createSequentialGroup()
	                    .addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
	                        .addGroup(gl_panel.createSequentialGroup()
	                            .addGap(7)
	                            .addComponent(lblSelectTargetPicture))
	                        .addGroup(gl_panel.createSequentialGroup()
	                            .addGap(3)
	                            .addComponent(btnBrowse)))
	                    .addGap(22)
	                    .addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
	                        .addComponent(btnStart))
	                    .addGap(18)
	                    .addContainerGap())
	        );

	        panel.setLayout(gl_panel);
	    }
	    public void setTarget(File reference)
	    {
	        try {
	            targetFile = reference;
	            targetImg = rescale(ImageIO.read(reference));
	        } catch (IOException ex) {
	            Logger.getLogger(ImageLoader.class.getName()).log(Level.SEVERE, null, ex);
	        }

	        panel_1.removeAll();
	        panel_1.setLayout(new BorderLayout(0, 0));
	        panel_1.add(new JLabel(new ImageIcon(targetImg)));
	        setVisible(true);
	    }
	    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {
	        JFileChooser fc = new JFileChooser(basePath);
	        int res = fc.showOpenDialog(null);
	        // We have an image!
	        try {
	            if (res == JFileChooser.APPROVE_OPTION) {
	                File file = fc.getSelectedFile();
	                setTarget(file);
	                mainImgPath = fc.getSelectedFile().getAbsolutePath();
	            } // Oops!
	            else {
	                JOptionPane.showMessageDialog(null,
	                        "You must select one image to be the reference.", "Aborting...",
	                        JOptionPane.WARNING_MESSAGE);
	            }
	        } catch (Exception iOException) {
	        }

	    }
	}


    public static BufferedImage rescale(BufferedImage originalImage)
    {
        BufferedImage resizedImage = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, baseSize, baseSize, null);
        g.dispose();
        return resizedImage;
    }
	@Override
	public void start(Stage primaryStage)
	{
		try
		{
			// load the FXML resource
			FXMLLoader loader = new FXMLLoader(getClass().getResource("OpenCVJFX.fxml"));
			// store the root element so that the controllers can use it
			Pane rootElement = (Pane) loader.load();
			// create and style a scene
			Scene scene = new Scene(rootElement, 900, 720);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			// create the stage with the given title and the previously created
			// scene
			primaryStage.setTitle("iTracker");
			primaryStage.setScene(scene);
			// show the GUI
			primaryStage.show();

		}
		catch (Exception e)
		{
			System.out.println("Exception "+e);
			e.printStackTrace();
		}
	}

	/**
	 * For launching the application...
	 *
	 * @param args
	 *            optional params
	 */
	public static void main(String[] args)
	{// load the native OpenCV library
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Main ImgLdMain = new Main();
		ImageLoader ImgLd = ImgLdMain.new ImageLoader(args);
		ImgLd.loaderStart(args);
	}

	public static void startTracking(String[] args, JButton btn){

		launch(args);
	}
}