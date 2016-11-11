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

import com.circle.helper.Helper;

import circle.animation.internal.Position;

public class AnimTest extends JPanel implements Runnable {

	private static final long serialVersionUID = 1L;
	private Graphics2D g2;
	private Thread th;

	public static boolean running = false;
	public int M_x, M_y;
	private String test = "deadghost";

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
		AH = (AnimationGroup) helper.openObject("/Data/Sprites/Animations/animation.frm");
		AH.init();
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent arg0) {
				getMouse();
				Position xy = new Position(M_x - 32, M_y - 32);
				AH.add(test, xy);
			}
		});
		this.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				getMouse();
				AH.add(test, M_x - 32, M_y - 32);
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
		g2.setColor(new Color(0, 0, 0));
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2.setColor(Color.black);
		AH.draw(g2);
	}

	private void quit() {
		running = false;
		AnimTest.frame.dispose();
		System.exit(0);
	}
}
