package test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class Fractal extends JFrame {

	long lastFpsMeasure = 0;
	long fps = 0;
	long fpsCount = 0;

	List<Point2D> basePoints = new ArrayList<>();
	List<Point2D> points = new ArrayList<>();
	
	Random random = new Random();
	
	int sz = 0;
	double f = 0;
	double sc = 1;
	double sf = 1;
	double sff = 1.02;
	
	double sk;
	
	double turn = 0.0;
	double turnf = 0.00;
	
	final boolean diveIn;
	boolean initialized = false;
	
	Object repaintSync = new Object();
	
	SortedSet<Point> ss = new TreeSet<>(new Comparator<Point>() {
		@Override
		public int compare(Point p1, Point p2) {
			if (p1.getX() > p2.getX()) {
				return 1;
			} else
			if (p1.getX() < p2.getX()) {
				return -1;
			}
			if (p1.getY() > p2. getY()) {
				return 1;
			} else
			if (p1.getY() < p2.getY()) {
				return -1;
			}
			return 0;
		}
	});

	JPanel jp = new JPanel() {

		static final long serialVersionUID = 0;

		@Override
		public void paint(Graphics g) {
			
			if (!initialized) {
				return;
			}

			Graphics2D g2 = (Graphics2D) g;

			g2.setBackground(Color.BLACK);
			g2.setColor(Color.WHITE);
			g2.clearRect(0, 0, jp.getWidth(), jp.getHeight());
			g2.setStroke(new BasicStroke(1));
			
			g2.drawString("sz=" + sz + ", f=" + f + ", factor sc=" + sc, 100, 100);
			
	       	AffineTransform at = AffineTransform.getTranslateInstance(jp.getWidth()/2, jp.getHeight()/2);
        	at.rotate(turn);
        	at.translate(-jp.getWidth()/2, -jp.getHeight()/2);
        	g2.setTransform(at);

        	int size = points.size();
        	int jpw = jp.getWidth() / 2;
        	int jph = jp.getHeight() / 2;
			for (int i = 0; i < size; i++) {
				try {
					Point2D p = points.get(i);
					g2.drawLine(
							(int) Math.round(jpw + p.getX()),
							(int) Math.round(jph + p.getY()), 
							(int) Math.round(jpw + p.getX()),
							(int) Math.round(jph + p.getY())
							);
				} catch (Exception aioobe) {}
			}
			
			synchronized (repaintSync) {
				repaintSync.notifyAll();
			}
			/*
			for (Point2D p : basePoints) {
				g2.drawLine(
						(int) Math.round(jp.getWidth() / 2 + p.getX()),
						(int) Math.round(jp.getHeight() / 2 + p.getY()), 
						(int) Math.round(jp.getWidth() / 2 + p.getX()),
						(int) Math.round(jp.getHeight() / 2 + p.getY())
						);
			}
			*/
			
		}
	};
	
	private void initFandSC() {
		sz = basePoints.size();
		f = 1.0 + 4.0/(sz - 2);
		sk = sz/4.0;
		if (diveIn) {
			sc = 1;
			sf = sff;
		} else {
			sc = sk;
			sf = 1.0/sff;
			for (Point2D p : basePoints) {
				p.setLocation(p.getX() * sk, p.getY() * sk);
			}

		}
		initialized = true;
	}

	public Fractal(boolean diveIn) {
		
		this.diveIn = diveIn;

		basePoints.add(new Point2D.Double(-500, -300));
		basePoints.add(new Point2D.Double(-500, +500));
		basePoints.add(new Point2D.Double(+500, +500));
		basePoints.add(new Point2D.Double(+500, -300));
		basePoints.add(new Point2D.Double(0, -500));
		basePoints.add(new Point2D.Double(100, -500));
		basePoints.add(new Point2D.Double(200, -500));
		
		points.add(basePoints.get(0));

		setUndecorated(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Technology Radar");
		setResizable(true);
		setPreferredSize(new Dimension(getMaximumSize().width, getMaximumSize().height));

		Container conFrame = this.getContentPane();

		conFrame.add(jp);

		pack();
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);

	}
	
	Thread[] threads = new Thread[8];
	boolean doCalc = false;
	
	{
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread() {
				public void run() {
					while (true) {
						boolean dc;
						synchronized (threads) {
							dc = doCalc;
						}
						if (! dc) {
							try {
								sleep (1);
							} catch (InterruptedException e) {}
						} else {
							calculate();
							synchronized (this) {
								notifyAll();
							}
						}
					}
				}
			};
			threads[i].setDaemon(true);
			threads[i].start();
		}
	}

	public void runFractal() {
		Thread thread = new Thread() {
			public void run() {
				long t = 0;
				while (true) {
					try {
						if (System.currentTimeMillis() - lastFpsMeasure >= 1000) {
							fps = fpsCount;
							lastFpsMeasure = System.currentTimeMillis();
							fpsCount = 0;
						}
						long diff = 16 - (System.currentTimeMillis() - t);
						if (diff > 0) {
							Thread.sleep(Math.max(0, 16 - (System.currentTimeMillis() - t)));
						}
						int calcs = 0;
						t = System.currentTimeMillis();
						synchronized (threads) {
							doCalc = true;
						}
						while (System.currentTimeMillis() - t < 8 && points.size() < 15000) {
							try {
								sleep (1);
							} catch (InterruptedException e) {}
							//calcs++;
							//calculate();
						}
						synchronized (threads) {
							doCalc = false;
						}
						
						for (Thread thread : threads) {
							synchronized (thread) {
								thread.wait (1);
							}
						}
						
						//System.out.println("calcs:" + calcs + ", points=" + points.size() + ", timespend=" + (System.currentTimeMillis() - t));
						
						turn = (turn + turnf) % (Math.PI * 2);

						diveIn();
						repaint();
						synchronized (repaintSync) {
							try {
								repaintSync.wait();
							} catch (InterruptedException e) {}
						}
					} catch (Exception e) {
						e.printStackTrace(System.out);
					}
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}
	
	/*
	 * 3 : 2		= 1 + 1/(3-2)
	 * 4 : 1.5		= 1 + 1/(4-2)
	 * 5 : 1.25		= 1 + 1/(5-2)
	 */
	private void calculate() {
		int r;
		Point2D lp;
		synchronized (points) {
			lp = points.get(points.size() - 1);
		}
		Point2D bp;
		Point2D np = null;
		boolean add = false;
		while (! add) {
			r = random.nextInt(sz);
			bp = basePoints.get(r);
			np = new Point2D.Double(
					(bp.getX() - lp.getX()) / f + lp.getX(),
					(bp.getY() - lp.getY()) / f + lp.getY()
			);
			if (inViewRange(np)) {
				add = true;
			} else {
				lp = np;
			}
		}
		synchronized (points) {
			points.add(np);
		}
	}
	
	private void diveIn() {
		// dive in
		ss.clear();
		//synchronized (points) {
			for (int i = points.size() - 1; i >= 0; i--) {
				Point2D p = points.get(i);
				p.setLocation(p.getX() * sf, p.getY() * sf);
				if (diveIn) {
					if (!inViewRange(p)) {
						points.remove(i);
					}
				} else {
					Point pi = new Point((int) Math.round(p.getX()), (int) Math.round(p.getY()));
					if (ss.contains(pi)) {
						points.remove(i);
					} else {
						ss.add(pi);
					}
				}
			}
		//}
		for (Point2D p : basePoints) {
			p.setLocation(p.getX() * sf, p.getY() * sf);
		}
		sc *= sf;
		if (diveIn) {
			if (sc > sk) {
				for (Point2D p : basePoints) {
					p.setLocation(p.getX() / sk, p.getY() / sk);
				}
				sc /= sk;
			}
		} else {
			if (sc <= 1) {
				for (Point2D p : basePoints) {
					p.setLocation(p.getX() * sk, p.getY() * sk);
				}
				sc *= sk;
			}
		}
	}
	
	private boolean inViewRange(Point2D p) {
		return Math.abs(p.getX()) < jp.getWidth()/2 && Math.abs(p.getY()) < jp.getHeight()/2;
	}
	
	private void loadImage(String s) {
		try {
			Color[][] c = ImageUtil.loadPixelsFromImage(new File(s));
			basePoints.clear();
			points.clear();
			double found = 0;
			double total = 0;
			System.out.println("image " + s + ", size=[" + c.length + "][" + c[0].length + "]");
			double factor = Math.min(
					jp.getWidth() / (double) c.length,
					jp.getHeight() / (double) c[0].length
			);
			for (int i = 0; i < c.length; i++) {
				for (int o = 0; o < c[i].length; o++) {
					if (
							!c[i][o].equals(Color.WHITE) /*||
							Math.abs(i - c.length / 2.0) < 0.51 &&
							Math.abs(o - c[0].length / 2.0) < 0.51*/
					) {
						boolean skip = false;
						if (i > 0 && i < c.length - 1 && o > 0 && o < c[i].length - 1) {
							skip = 
									!c[i-1][o-1].equals(Color.WHITE) &&
									!c[i-1][o+0].equals(Color.WHITE) &&
									!c[i-1][o+1].equals(Color.WHITE) &&
									!c[i-0][o-1].equals(Color.WHITE) &&
									!c[i-0][o+1].equals(Color.WHITE) &&
									!c[i+1][o-1].equals(Color.WHITE) &&
									!c[i+1][o+0].equals(Color.WHITE) &&
									!c[i+1][o+1].equals(Color.WHITE);
						}
						if (!skip) {
							basePoints.add(new Point2D.Double(
									(i - c.length / 2) * factor, 
									(o - c[0].length / 2) * factor
							));
							found++;
						}
					}
					total++;
				}
				//System.out.println("basePoints:" + basePoints);
			}
			//points.add(basePoints.get(0));
			Point2D minDiffCenter = new Point2D.Double();
			double diff = Double.MAX_VALUE;
			//Point2D center = new Point2D.Double(0, 0);
			Point2D center = new Point2D.Double(1, 1);
			for (Point2D p : basePoints) {
				if (p.distance(center) < diff) {
					minDiffCenter.setLocation(p.getX(), p.getY());
					diff = p.distance(center);
				}
			}
			for (Point2D p : basePoints) {
				p.setLocation(p.getX() - minDiffCenter.getX(), p.getY() - minDiffCenter.getY());
			}
			points.add(new Point2D.Double(0,0));
			System.out.println("ratio = " + found + " / " + total);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Fractal f = new Fractal(true);
		f.setVisible(true);
		//f.loadImage("C:\\Users\\jg\\Downloads\\Novatec3.bmp");
		f.loadImage("C:\\Users\\jg\\Downloads\\Novatec2.bmp");
		//f.loadImage("C:\\Users\\jg\\Downloads\\Novatec.bmp");
		//f.loadImage("C:\\Users\\jg\\Downloads\\Smiley.bmp");
		//f.loadImage("C:\\Users\\jg\\Downloads\\triangle.bmp");
		//f.loadImage("C:\\Users\\jg\\Downloads\\heart.bmp");
		f.initFandSC();
		f.runFractal();
	}

}
