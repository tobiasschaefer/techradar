package radar.item;

import java.awt.geom.Point2D;

import radar.ring.Rings;

public class Util {
	
	public static double getAngle(Item p1, Item p2) {
		if (p1 == null || p2 == null) {
			return 0;
		}
		return getAngle(p1.getArcCenter(), p2.getArcCenter());
	}

	public static double getAngle(Point2D p1, Point2D p2)
	{       
	    double dx = p2.getX() - p1.getX();
	    double dy = p2.getY() - p1.getY();
	    double adx = Math.abs(dx);
	    double ady = Math.abs(dy);
	    
	    if (dy == 0 && dx == 0)
	    {
	        return 0;
	    }
	    else if (dy == 0 && dx > 0)
	    {
	        return 0;      
	    }
	    else if (dy == 0 && dx < 0)
	    {
	        return Math.PI;
	    }
	    else if (dy > 0 && dx == 0)
	    {
	        return Math.PI * 0.5;
	    }
	    else if (dy < 0 && dx == 0)
	    {
	        return Math.PI * 1.5;
	    }
	    
	    double rwinkel = Math.atan(ady /adx);  
	        
	    if (dx>0 && dy>0) 
	    {
	        //rwinkel = rwinkel;
	    }
	    else if (dx<0 && dy>0)  
	    {
	    	rwinkel = Math.PI - rwinkel;
	    }
	    else if (dx>0 && dy<0) 
	    {
	    	rwinkel = -rwinkel;
	    }
	    else if (dx<0 && dy<0)  
	    {
	    	rwinkel = Math.PI + rwinkel;
	    }
	    
	    return rwinkel;
	}

	public static Point2D moveBigBang2(
			double radius, double percentage, double dimm, 
			Point2D goal2D, Point2D center, Point2D original2D,
			double alphaControl, 
			Point2D target, boolean relative
		) {
		int maxZ = 1000;
		double z = Math.min(maxZ, 10000 / radius);
		Point2D yzPoint = new Point2D.Double((goal2D.getY() - center.getY()), z);
		double yzLength = yzPoint.distance(0, 0);
		double yzWinkel = Util.getAngle(new Point2D.Double(0, 0), yzPoint);
		
		yzWinkel -= (1 - percentage) * Math.PI / 2.5;
		yzPoint.setLocation(yzLength * Math.cos(yzWinkel), yzLength * Math.sin(yzWinkel));
		
		Point2D bigBangPoint = new Point2D.Double();
		bigBangPoint.setLocation(
				(1-percentage) * (goal2D.getX() - original2D.getX()), 
				(1-percentage) * (yzPoint.getX() - (original2D.getY() - center.getY()))
		);
		if (! relative) {
			target.setLocation(original2D.getX() + bigBangPoint.getX(), original2D.getY() + bigBangPoint.getY());
		} else {
			target.setLocation(bigBangPoint);
		}
				
		int maxDepth = 250;
		double alpha = 1;
		if (yzLength > maxDepth) {
			alpha = (Math.min(1, (Math.max(0, (maxDepth * 2 - yzLength)/maxDepth/2))));
		}
		//alpha = (int) Math.min(127, Math.round(alpha * bigBangShrink/1.8));
		alpha = Math.max(0, Math.min(1, (alpha * (alphaControl - radius/Rings.getInstance().getSize()))));
		alpha = (alpha * (1 - dimm));
		
		return new Point2D.Double(alpha, z);

	}

	public static double getWDiff(double w1, double w2) {
		double rdw = 0;
		w1 = w1 % (Math.PI * 2);
		w2 = w2 % (Math.PI * 2);
		double wdiff;
		if (w1 > w2) {
			wdiff = w1 - w2;
		} else {
			wdiff = w1 + (Math.PI * 2 - w2);
		}
		if (wdiff > 0 && wdiff <= Math.PI)
		{
			rdw = wdiff;
		}
		return rdw;
	}


}
