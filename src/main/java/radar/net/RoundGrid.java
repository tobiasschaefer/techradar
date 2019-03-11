package radar.net;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import radar.item.Util;
import radar.ring.Rings;

public class RoundGrid {
	
	private static final int mppc = 15;
	private static final int mppl = 6;
	private static int maxRad;
	private static final double[] radius = new double[mppl * 10];
	private static double minRadius;
	private static double maxr;
	
	private static Point2D[][] point;
	private static Color[][] colors;

	
	private static double versatzTotal;
	
	public static void init(double mv) {
		maxr = Rings.getInstance().getSize() / 1.5;
		minRadius = 0.1 * maxr / (double) mppl;
		radius[0] = maxr;
		maxRad = 1;
		while (radius[0] > minRadius) {
			for (int i = 0; i < maxRad; i++) {
				radius[i] *= mv;
			}
			if (maxr - radius[maxRad - 1] >= maxr/ (double) mppl) {
				radius[maxRad] = radius[maxRad - 1] + maxr/ (double) mppl;
				maxRad++;
			}
		}
		versatzTotal = 0;
	}

	public static void move(double mv, double versatz) {
		for (int i = 0; i< maxRad; i++) {
			radius[i] *= mv;
		}
		if (radius[0] < minRadius) {
			System.arraycopy(radius, 1, radius, 0, radius.length - 1);
			radius[maxRad - 1] = radius[maxRad - 2] + maxr / (double) mppl;
		}
		versatzTotal = versatz;
	}
	
	
	public static void drawGrid(Point2D itemsCenter, double percentage, Graphics2D g, double dimm, Color color) {
		
		if (point == null || maxRad > point.length) {
			point = new Point2D.Double[maxRad][mppc * 2];
		}
		if (colors == null || maxRad > colors.length) {
			colors = new Color[maxRad][mppc * 2];
		}

		Point2D c2 = new Point2D.Double();
		for (int i = 0; i < maxRad; i++) {
			double odiff = Math.PI / mppc;
			int oIdx = 0;
			for (double o = 0; o < Math.PI * 2; o += odiff) {
				
				c2.setLocation(
						itemsCenter.getX() + Math.cos(o) * radius[i],
						itemsCenter.getY() + Math.sin(o) * radius[i]
				);
				
				point[i][oIdx] = new Point2D.Double();
				
				//double radius = itemsCenter.distance(c2);
				double winkel = Util.getAngle(itemsCenter, c2);
				Point2D goal2D = new Point2D.Double();
				goal2D.setLocation(
						itemsCenter.getX() + Math.cos(winkel + versatzTotal * (1 - percentage)) * radius[i],
						itemsCenter.getY() + Math.sin(winkel + versatzTotal * (1 - percentage)) * radius[i]
				);
				
				Point2D p = Util.moveBigBang2(radius[i], percentage, dimm, goal2D, itemsCenter, c2, 0.65, point[i][oIdx], false);
				
				int alpha = (int) Math.round(127 * p.getX());
						
				colors[i][oIdx] = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
				oIdx++;
			}
		}
		g.setStroke(new BasicStroke(1));
		for (int i = 0; i < point.length; i++) {
			for (int o = 0; o < point[i].length; o++) {
				g.setColor(colors[i][o]);
				g.drawLine(
					(int) Math.round(point[i][o].getX()), (int) (point[i][o].getY()), 
					(int) Math.round(point[i][(o + 1) % point[i].length].getX()), (int) (point[i][(o + 1) % point[i].length].getY())
				);
				if (i < point.length - 1) {
					g.drawLine(
						(int) Math.round(point[i][o].getX()), (int) (point[i][o].getY()), 
						(int) Math.round(point[i + 1][o].getX()), (int) (point[i + 1][o].getY())
					);
				}
			}
		}

	}

}
