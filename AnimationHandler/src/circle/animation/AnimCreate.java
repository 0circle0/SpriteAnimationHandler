package circle.animation;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.circle.helper.Helper;

public class AnimCreate extends JPanel {
	private static final long serialVersionUID = 1L;
	public static AnimCreate fb = new AnimCreate();
	public Helper helper;

	public static void main(String[] args) {
		fb.init();
	}

	public void init() {
		helper = new Helper();
		doAnimations();
		System.out.println("End of Work.");
	}

	// deadghost is a Sprite Sheet format and the rest are in Strip format
	private String[] animNames = { "deadghost", "tame", "hit", "kill", "bounce" };
	private int[][] xySize = { { 64, 64 }, { 64, 64 }, { 32, 32 }, { 64, 64 }, { 32, 32 } };

	private Animation[] anim = new Animation[animNames.length];

	public void doAnimations() {
		AnimationGroup ag = new AnimationGroup();
		for (int i = 0; i < animNames.length; i++) {
			BufferedImage bi = helper.loadBufferedImage(animNames[i]);
			anim[i] = new Animation(animNames[i], bi, xySize[i][0], xySize[i][1]);
			// Example on making sure injection worked completely
			if (!ag.injectNewUsable(animNames[i], anim[i]))
				System.out.println("Failed to inject " + animNames[i] + " name already exists.");
			System.out.println(anim[i].name);
		}
		helper.saveObject(ag, "../FallingBall/src/Data/Sprites/Animations/animation.frm");
		helper.saveObject(ag, "src/Data/Sprites/Animations/animation.frm");
	}
}
