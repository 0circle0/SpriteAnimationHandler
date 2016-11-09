/*
 * Copyright (c) 2016, Brian Jensen
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *     
 *     Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.circle.animation;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

/**
 * Animation contains the information for an animation. Animations created are
 * Serializable
 * 
 * @author Brian Jensen
 */
public class Animation implements Serializable, Cloneable {

	private static final long serialVersionUID = -138702520071857680L;
	// Contains entire image of all frames
	private transient BufferedImage bufferedImageAnimation;
	// Array containing each frame of the Animation
	private transient BufferedImage[] animation;
	// Defines if Animation loops or runs once
	public transient boolean persistent;
	// Contains the byte[] information of bufferedImageAnimation used in saving
	private byte[] byteAnimation;
	// Total number of frames for the Animation
	public int numOfFrames;
	// Width of each frame of the Animation
	public int frameWidth;
	// Height of each frame of the Animation
	public int frameHeight;
	// Name of the Animation
	public String name;
	// X, Y position of the Animation on the screen
	private int[] pos;
	// Current frame the Animation is on
	private int currentFrame;
	// Frames in each Row
	private int framesAcross;
	// Frames in each Column
	private int framesDown;
	// Format type of Animation (Sprite Sheet or Strip)
	private boolean spriteSheet = false;

	/**
	 * Creates a new Animation using the Strip animation format. This assumes
	 * that the Animation is all in a single row
	 * 
	 * @param name
	 *            Name of the Animation
	 * @param image
	 *            The BufferedImage that contains every frame of the animation
	 *            is strip format
	 * @param numOfFrames
	 *            Defines how many frames are in the Animation
	 * @param frameWidth
	 *            Defines the width of each frame of the Animation
	 */
	public Animation(String name, Image image, int numOfFrames, int frameWidth) {
		this(name, image, numOfFrames, numOfFrames, frameWidth, image.getHeight(null), false);
	}

	/**
	 * Creates a new Animation using the Sprite Sheet animation format. This
	 * assumes that the Animation is broken up into separate rows and columns.
	 * This can also be used for Strip Format Animations
	 * 
	 * @param name
	 *            Name of the Animation
	 * @param image
	 *            The BufferedImage that contains every frame of the animation
	 *            is strip format
	 * @param numOfFrames
	 *            Defines how many frames are in the Animation
	 * @param framesAcross
	 *            Number of frames in each Row
	 * @param frameWidth
	 *            Defines the width of each frame of the Animation
	 * @param frameHeight
	 *            Defines the height of each frame of the Animation
	 * @param spriteSheet
	 *            Defines if the Animation type is Sprite Sheet. Setting to
	 *            false will load as Strip Format
	 */
	public Animation(String name, Image image, int numOfFrames, int framesAcross, int frameWidth, int frameHeight,
			boolean spriteSheet) {
		this.name = name;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		this.numOfFrames = numOfFrames;
		this.framesAcross = framesAcross;
		this.framesDown = numOfFrames / framesAcross;
		this.spriteSheet = spriteSheet;
		setup(image);
	}

	/**
	 * Converts the Image to BufferedImage and to a byte array for Serialization
	 */
	private void setup(Image image) {
		this.bufferedImageAnimation = imageToBufferedImage(image);
		this.byteAnimation = bufferedImageToByteArray(this.bufferedImageAnimation);
	}

	/**
	 * Initialize Animation. This must be called before using animation.
	 */
	public void init() {
		this.bufferedImageAnimation = byteArrayToBufferedImage(this.byteAnimation);
		if (!spriteSheet)
			this.animation = SplitImage(this.bufferedImageAnimation, this.numOfFrames, this.frameWidth,
					this.frameHeight);
		else
			this.animation = SplitImage(this.bufferedImageAnimation, this.numOfFrames, this.framesAcross,
					this.framesDown, this.frameWidth, this.frameHeight);
	}

	/**
	 * Gets a frame from the animation
	 * 
	 * @param frame
	 *            The frame you would like to get.
	 * @return BufferedImage from Animation as specified by frame
	 */
	public BufferedImage getFrame(int frame) throws ArrayIndexOutOfBoundsException {
		return this.animation[frame];
	}

	/**
	 * Converts a BufferedImage to byte[] for Serialization
	 * 
	 * @param bufferedImage
	 *            BufferedImage to be converted
	 * @return A byte[] of bufferedImage for Serialization
	 */
	private byte[] bufferedImageToByteArray(BufferedImage bufferedImage) {
		byte[] ret = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, "png", baos);
			baos.flush();
			ret = baos.toByteArray();
			baos.close();
		} catch (IOException | NullPointerException e) {
			System.out.println("Cannot convert BufferedImage to byte[]");
		}
		return ret;
	}

	/**
	 * Converts a byte[] array to a BufferedImage
	 * 
	 * @param imageData
	 *            byte[] array to be converted to BufferedImage
	 * @return BufferedImage from imageData
	 */
	private BufferedImage byteArrayToBufferedImage(byte[] imageData) {
		ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
		try {
			return ImageIO.read(bais);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Splits a animation sequence into individual frames from a Strip Format
	 * 
	 * @param src
	 *            The source image containing all the frames to be split
	 * @param frames
	 *            The number of frames contained in the src
	 * @param frameW
	 *            The width of each frame
	 * @param frameH
	 *            The height of each frame
	 * @return BufferedImage array containing every frame of the animation
	 */
	private BufferedImage[] SplitImage(BufferedImage src, int frames, int frameW, int frameH) {
		return SplitImage(src, frames, frames, 1, frameW, frameH);
	}

	/**
	 * Splits a animation sequence into individual frames from a Sprite Sheet or
	 * Strip Format
	 * 
	 * @param src
	 *            The source image containing all the frames to be split
	 * @param frames
	 *            The number of frames contained in the src
	 * @param framesAcross
	 *            number of frames in each Row
	 * @param framesDown
	 *            number of frames in each Column
	 * @param frameW
	 *            The width of each frame
	 * @param frameH
	 *            The height of each frame
	 * @return BufferedImage array containing every frame of the animation
	 */
	private BufferedImage[] SplitImage(BufferedImage src, int frames, int framesAcross, int framesDown, int frameW,
			int frameH) {
		BufferedImage[] dest = new BufferedImage[frames + 1];
		Graphics2D g2 = null;
		for (int i = 0; i <= frames; i++)
			dest[i] = new BufferedImage(frameW, frameH, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0, i = 0; y > -(framesDown * frameH); y -= frameH) {
			for (int x = 0; x > -(framesAcross * frameW); x -= frameW, i++) {
				g2 = dest[i].createGraphics();
				g2.drawImage(src, x, y, null);
				g2.dispose();
			}
		}
		return dest;
	}

	/**
	 * Converts and Image to BufferedImage
	 * 
	 * @param image
	 *            Image to be converted to BufferedImage or if image is already
	 *            BufferedImage will return it immediately
	 * @return The converted image as BufferedImage
	 */
	private BufferedImage imageToBufferedImage(Image image) {
		if (image instanceof BufferedImage)
			return (BufferedImage) image;
		BufferedImage ret = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = ret.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return ret;
	}

	/**
	 * Set the x, y position of the Animation
	 * 
	 * @param pos
	 *            x, y position of the Animation on the screen
	 */
	public void setPos(int[] pos) {
		this.pos = pos;
	}

	/**
	 * Sets the x position of the Animation
	 * 
	 * @param x
	 *            x location to assign to the Animation
	 */
	public void setPosX(int x) {
		this.pos[0] = x;
	}

	/**
	 * Sets the y position of the Animation
	 * 
	 * @param y
	 *            y location to assign to the Animation
	 */
	public void setPosY(int y) {
		this.pos[1] = y;
	}

	/**
	 * Gets the position of the Animation on the screen
	 * 
	 * @return x, y position of the Animation on the screen
	 */
	public int[] getPos() {
		return this.pos;
	}

	/**
	 * Gets the X position of the Animation on the screen
	 * 
	 * @return y position
	 */
	public int getPosX() {
		return this.pos[0];
	}

	/**
	 * Gets the y position of the Animation on the screen
	 * 
	 * @return y position
	 */
	public int getPosY() {
		return this.pos[1];
	}

	/**
	 * Sets the frame the Animation is currently on
	 * 
	 * @param currentFrame
	 *            The frame to set the animation to
	 */
	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}

	/**
	 * Gets the frame of the Animation
	 * 
	 * @return The current frame the Animation is on
	 */
	public int getCurrentFrame() {
		return this.currentFrame;
	}

	/**
	 * Creates and returns a copy of this object.
	 * 
	 * @return A clone of this instance
	 */
	@Override
	protected Animation clone() {
		try {
			return (Animation) super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Problem Cloning Animation. Should never see this error");
		}
		return null;
	}
}
