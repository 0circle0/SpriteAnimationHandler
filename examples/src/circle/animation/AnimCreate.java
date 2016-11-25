package circle.animation;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import circle.helper.Helper;

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

	/*
	 * ANGRY GHOST EXORCISM ANIMATIONS
	 * */
//	private String[] animNames = { "deadghost", "tame", "hit", "kill", "bounce" };
//	private int[][] xySize = { { 64, 64 }, { 64, 64 }, { 32, 32 }, { 64, 64 }, { 32, 32 } };
	/*
	 * FALLING BALL ANIMATIONS
	 * */
//	private String[] animNames = { "grassballexplode", "lavaballexplode", "steelballexplode", "stoneballexplode",
//			"sunballexplode", "waterballexplode", "starexplode", "oneupexplode", "downfloorexplode", "lavasplash",
//			"watersplash", "grasssplash", "sunsplash", "steelsplash", "stonesplash", "colorsplash" };
//	private int[][] xySize = { { 64, 64 }, { 64, 64 }, { 64, 64 }, { 64, 64 }, { 64, 64 }, { 64, 64 }, { 64, 64 },
//			{ 96, 96 }, { 96, 128 }, {96, 128}, {96, 128}, {96, 128}, {96, 128}, {96, 128}, {96, 128}, {96, 320} };
	/*
	 * PRESENTS ANIMATIONS
	 * */
	private String[] animNames = { "presentexplode", "goblinwalk", "presentwalk" };
	private int[][] xySize= { {64, 64}, {64, 64}, {64, 64} };

	private Animation[] anim = new Animation[animNames.length];

	public void doAnimations() {
		AnimationGroup ag = new AnimationGroup();
		for (int i = 0; i < animNames.length; i++) {
			BufferedImage bi = helper.loadBufferedImage(animNames[i]);
			anim[i] = new Animation(animNames[i], bi, xySize[i][0], xySize[i][1]);
			ag.injectNewUsable(animNames[i], anim[i]);
			System.out.println(anim[i].name);
		}
		/*
		 * Presents Animations
		 * */
		helper.saveObject(ag, "../GAME Presents/src/Data/Sprites/Animations/animation.frm", true);
		helper.saveObject(ag, "src/Data/Sprites/Animations/Presents.frm");
		/*
		 * Angry Ghost Exorcism Animations
		 * */
//		helper.saveObject(ag, "../GAME AngryGhostExorcism/src/Data/Sprites/Animations/animation.frm");
//		helper.saveObject(ag, "src/Data/Sprites/Animations/AngryGhosts.frm");
		/*
		 * Falling Ball Animations
		 * */
//		helper.saveObject(ag, "../GAME FallingBall/src/Data/Sprites/Animations/animation.frm", true);
//		helper.saveObject(ag, "src/Data/Sprites/Animations/FallingBall.frm");
	}
}
