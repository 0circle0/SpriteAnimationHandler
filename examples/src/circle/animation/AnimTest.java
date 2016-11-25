package circle.animation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import circle.animation.internal.Position;
import circle.helper.Helper;

public class AnimTest extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	private Graphics2D g2;
	private Thread th;

	public static boolean running = false;
	public int M_x, M_y;
	/*
	 * Angry Ghost Exorcism List
	 * */
//	private String[] test = { "deadghost", "tame", "hit", "kill", "bounce" };
	/*
	 * Falling Ball Animation List
	 * */
//	private String[] test= { "grassballexplode", "lavaballexplode", "steelballexplode", "stoneballexplode",
//			"sunballexplode", "waterballexplode", "starexplode", "oneupexplode", "downfloorexplode", "lavasplash",
//			"watersplash", "grasssplash", "sunsplash", "steelsplash", "stonesplash" };
	/*
	 * Presents Animation List
	 * */
	private String[] test = { "presentexplode", "goblinwalk", "presentwalk" };
	private int startTest = 0;

	private long lastTime = System.nanoTime();
	private double amountOfTicks = 60.0;
	private double ns = 1000000000 / amountOfTicks;
	private double delta = 0;

	public static JFrame frame;
	public final int AppletSizeX = 900;
	public final int AppletSizeY = 600;
	private final int FrameSizeX = AppletSizeX + 6;
	private final int FrameSizeY = AppletSizeY + 28;

	public AnimationGroup AH = new AnimationGroup();

	public static void main(String[] args) {
		new AnimTest();
	}

	public AnimTest() {
		init();
	}

	private void updateGame() {
		AH.update();
	}

	private void init() {
		this.setBackground(new Color(0, 0, 0));
		this.setSize(AppletSizeX, AppletSizeY);
		this.setLocation(0, 0);
		this.setLayout(null);
		frame = new JFrame();
		frame.setSize(FrameSizeX, FrameSizeY);
		frame.setLocation(0, 0);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().add(this);
		frame.getContentPane().setBackground(new Color(250, 250, 250));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quit();
			}
		});
		Helper helper = new Helper();
		AH = (AnimationGroup) helper.openObject("/Data/Sprites/Animations/Presents.frm");
		//test = AH.getNames();
		long start = System.currentTimeMillis();
		AH.init();
		long stop = System.currentTimeMillis();
		System.out.println(stop - start);
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (arg0.getButton() == 1) {
					getMouse();
					Position xy = new Position(M_x - 32, M_y - 32);
					AH.add(test[startTest], xy, false);
				}
				if (arg0.getButton() == 3) {
					startTest++;
					if (startTest >= test.length)
						startTest = 0;
				}
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				if (e.getModifiers() == 16) {
					getMouse();
					Position xy = new Position(M_x - 23, M_y - 23);
					AH.add(test[startTest], xy, false);
				}
			}
		});
		frame.setVisible(true);
		start();
	}

	public void getMouse() {
		try {
			M_x = MouseInfo.getPointerInfo().getLocation().x - this.getLocationOnScreen().x;
			M_y = MouseInfo.getPointerInfo().getLocation().y - this.getLocationOnScreen().y;
		} catch (Exception e) {
		}
	}

	private void start() {
		running = true;
		th = new Thread(this);
		th.start();
	}

	@Override
	public void run() {
		Thread thisThread = Thread.currentThread();
		while (running && th == thisThread) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				updateGame();
				delta--;
			}
			repaint();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		g2 = (Graphics2D) g;
		super.paintComponents(g2);
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		AH.draw(g2);
	}

	private void quit() {
		running = false;
		AnimTest.frame.dispose();
		System.exit(0);
	}
}
