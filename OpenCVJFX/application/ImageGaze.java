package application;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
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
	static int counter = 0;
	static int count = 0;
	static int imageHeight = 480;
	static int imageWidth = 640;

	public static BufferedImage process(BufferedImage old, double[][] arrFin, int count ) {
//		int w = old.getWidth();
//		int h = old.getHeight();
//System.out.println(xCord+"----IMAGE--"+yCord);
		float alpha = 0.0f;

		BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = img.createGraphics();
		
		g2d.drawImage(old, 0, 0, imageWidth, imageHeight, null);
		
		for(int stepWiseloc=0; stepWiseloc<count; stepWiseloc++)
		{	
			alpha =  (((float)(stepWiseloc+1))/((float) count));
			//Sanity check
			if(stepWiseloc > 0){
				for(int itr = 0; itr < stepWiseloc; itr++)
				{
					if((int) arrFin[itr][0] == (int) arrFin[stepWiseloc][0]){
						if((int) arrFin[itr][1] == (int) arrFin[stepWiseloc][1]){
							arrFin[stepWiseloc][0] = arrFin[stepWiseloc-1][0];
							arrFin[stepWiseloc][1] = arrFin[stepWiseloc-1][1];
						}
					}
				}
			}
			if(count - stepWiseloc > 10)
				alpha = 0.0f;
			System.out.println("count = "+count+",stepwise = "+stepWiseloc+",x = "+arrFin[stepWiseloc][0]+" y = "+arrFin[stepWiseloc][1]);
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			int xCordCurr = (int) arrFin[stepWiseloc][0];
			int yCordCurr = (int) arrFin[stepWiseloc][1];
			if(stepWiseloc != 0){
				int xCordPrev = (int) arrFin[stepWiseloc-1][0];
				int yCordPrev = (int) arrFin[stepWiseloc-1][1];
				float alphaPrev =  (((float)(stepWiseloc))/((float)(count)));
				g2d.setStroke(new BasicStroke(4));
				g2d.setColor(Color.BLACK);
				g2d.drawLine(xCordPrev, yCordPrev, xCordCurr, yCordCurr);
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaPrev));
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setStroke(new BasicStroke(14));
				//RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				//g2d.setRenderingHints(hints);
				g2d.setColor(Color.PINK);
				g2d.drawOval(xCordPrev, yCordPrev, 14, 14);
				
			}
			g2d.setStroke(new BasicStroke(14));
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(Color.PINK);
			g2d.drawOval(xCordCurr, yCordCurr, 14, 14);
		}
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
				newImage = process(image, arr, count);

				picLabel = new JLabel(new ImageIcon(newImage),JLabel.CENTER);			
				picLabel.setMaximumSize(new Dimension(880, 880));
				picLabel.setMinimumSize(new Dimension(880, 880));
				picLabel.setSize(new Dimension(880, 880));
				picLabel.setPreferredSize(new Dimension(880, 880));
				panel.removeAll();
				panel.add(picLabel);
				panel.setMaximumSize(new Dimension(880, 880));
				panel.setMinimumSize(new Dimension(880, 880));
				panel.setSize(new Dimension(880, 880));
				panel.setPreferredSize(new Dimension(880, 880));
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.getContentPane().removeAll();
				f.getContentPane().add(new JScrollPane(panel));
				f.setPreferredSize(new Dimension(880, 880));
				f.setResizable(false);
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
		System.out.println("Fx="+array[0][0]+"Fy="+array[0][1]);
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				create(array);
			}
		});
	}
}