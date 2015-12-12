package application;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

public class ImageGaze extends JPanel {

	static BufferedImage image;
	static BufferedImage newImage;
	static JFrame frame;
	static JPanel panel;
	static JLabel picLabel;
	static int counter = 46;
	static int count = 0;
	static int imageHeight = 880;
	static int imageWidth = 880;

	public static BufferedImage process(BufferedImage old, int xCord, int yCord) {
		int w = old.getWidth();
		int h = old.getHeight();
//System.out.println(imageWidth+"----lll--"+imageHeight);
		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR_PRE);

		Graphics2D g2d = img.createGraphics();

		g2d.drawImage(old, 0, 0, imageWidth, imageHeight, null);
		g2d.setStroke(new BasicStroke(12));
		g2d.setColor(Color.BLUE);
		g2d.drawOval(xCord, yCord, 19, 10);
		g2d.dispose();
		return img;
	}

	public static void create(final double[][] arr) {

		try {
			//Path currentRelativePath = Paths.get("");
			//String s = currentRelativePath.toAbsolutePath().toString();
			InputStream fis = new BufferedInputStream(new FileInputStream(Main.mainImgPath));
			image = ImageIO.read(fis);
		} catch (IOException e) {
			e.printStackTrace();
		}

		panel = new JPanel();
		final JFrame f = new JFrame();
		Timer timer = new Timer((int) arr[count][2], new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				newImage = process(image, (int) arr[count][0], (int) arr[count][1]);

				picLabel = new JLabel(new ImageIcon(newImage));

				panel.removeAll();
				panel.add(picLabel);

				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().removeAll();
				f.getContentPane().add(new JScrollPane(panel));
				f.pack();
				f.setVisible(true);
				count++;

				if (count == counter) {
					((Timer) e.getSource()).stop();
				}
			}
		});

		timer.start();
	}

	public static void gazeImage(final double[][] array, int count) {
		counter = count;
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				create(array);
			}
		});
	}
}