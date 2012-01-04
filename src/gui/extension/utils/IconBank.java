package extension.utils;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import extension.ExtensionApp;

public class IconBank {
	private static HashMap<URL,ImageIcon> icons = new HashMap<URL,ImageIcon>();
	public static ImageIcon getByUrl(URL url, int width, int height) {
		if( icons.containsKey(url) )
			return icons.get(url);
		else {
			ImageIcon icon = new ImageIcon(url);
			Image img = icon.getImage();  
			Image newimg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			ImageIcon newIcon = new ImageIcon(newimg);  
			icons.put(url, newIcon);
			return newIcon;
		}
	}
	public static ImageIcon getByUrl(String path, int width, int height)
	{
		URL imgURL = ExtensionApp.class.getResource(path);
		if (imgURL != null) {
			return getByUrl(imgURL, width, height);
		} else {
			System.err.println("Couldn't find file: " + path);
	        return null;
		}
	}
	
	private final static double DEGREE_90 = 90.0 * Math.PI / 180.0;
    public static ImageIcon createRotatedImage(Component c, Icon icon, double rotatedAngle) {
        // convert rotatedAngle to a value from 0 to 360
        double originalAngle = rotatedAngle % 360;
        if (rotatedAngle != 0 && originalAngle == 0) {
            originalAngle = 360.0;
        }

        // convert originalAngle to a value from 0 to 90
        double angle = originalAngle % 90;
        if (originalAngle != 0.0 && angle == 0.0) {
            angle = 90.0;
        }

        double radian = Math.toRadians(angle);

        int iw = icon.getIconWidth();
        int ih = icon.getIconHeight();
        int w;
        int h;

        if ((originalAngle >= 0 && originalAngle <= 90) || (originalAngle > 180 && originalAngle <= 270)) {
            w = (int) (iw * Math.sin(DEGREE_90 - radian) + ih * Math.sin(radian));
            h = (int) (iw * Math.sin(radian) + ih * Math.sin(DEGREE_90 - radian));
        }
        else {
            w = (int) (ih * Math.sin(DEGREE_90 - radian) + iw * Math.sin(radian));
            h = (int) (ih * Math.sin(radian) + iw * Math.sin(DEGREE_90 - radian));
        }
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        Graphics2D g2d = (Graphics2D) g.create();

        // calculate the center of the icon.
        int cx = iw / 2;
        int cy = ih / 2;

        // move the graphics center point to the center of the icon.
        g2d.translate(w/2, h/2);

        // rotate the graphcis about the center point of the icon
        g2d.rotate(Math.toRadians(originalAngle));

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        icon.paintIcon(c, g2d, -cx, -cy);

        g2d.dispose();
        return new ImageIcon(image);
    }	
    
	public static ImageIcon createMirroredImage(Component c, Icon icon) {		
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        int cx = w / 2;
        int cy = h / 2;
       
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        Graphics2D g2d = (Graphics2D) g.create();

		g2d.scale(-1,1);
		g2d.translate(-cx, cy);
		
        icon.paintIcon(c, g2d, -cx, -cy);

        g2d.dispose();
        return new ImageIcon(image);
	}
}