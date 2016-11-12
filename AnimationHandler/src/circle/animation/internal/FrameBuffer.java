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
package circle.animation.internal;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.swing.ImageIcon;

/**
 * FrameBuffer contains the individual frames of a BufferedImage
 */
public class FrameBuffer implements Serializable {
	private static final long serialVersionUID = -8674065405735141416L;
	public transient BufferedImage[] frameBuffer;
	private ImageIcon imageIcon;
	private Dimension frameSize;

	/**
	 * Initialized the BufferedImage array with given parameters
	 * 
	 * @param src
	 *            Image to convert to individual frames
	 * @param frameSize
	 *            Width and Height of each frame
	 */
	public FrameBuffer(BufferedImage src, Dimension frameSize) {
		imageIcon = new ImageIcon(src);
		this.frameSize = frameSize;
		frameBuffer = SplitImage(src, this.frameSize);
	}

	/**
	 * Initializes this FrameBuffer. This must be called before using the
	 * FrameBuffer. init() is called when creating the object so it is not
	 * necessary to call after creating a new FrameBuffer
	 */
	public void init() {
		frameBuffer = SplitImage(buffer(this.imageIcon), this.frameSize);
	}

	/**
	 * Converts ImageIcon to BufferedImage
	 * 
	 * @param imageIcon
	 *            ImageIcon to be converted to BufferedImage
	 * @return The BufferedImage created in the converstion
	 */
	private BufferedImage buffer(ImageIcon imageIcon) {
		Image image = imageIcon.getImage();
		BufferedImage dest = new BufferedImage(image.getWidth(null), image.getHeight(null),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = dest.createGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return dest;
	}

	/**
	 * Splits a animation sequence into individual frames from a Sprite Sheet or
	 * Strip Format
	 * 
	 * @param src
	 *            The source image containing all the frames to be split
	 * @param frameSize
	 *            Dimension of the Width and Height of each frame
	 * @return BufferedImage array containing every frame of the animation
	 */
	private BufferedImage[] SplitImage(BufferedImage src, Dimension frameSize) {
		int framesDown = (int) (src.getHeight() / frameSize.getHeight());
		int framesAcross = (int) (src.getWidth() / frameSize.getWidth());
		int frames = framesDown * framesAcross;
		BufferedImage dest[] = new BufferedImage[frames];
		for (int y = 0, i = 0; y < src.getHeight(); y += frameSize.height)
			for (int x = 0; x < src.getWidth(); x += frameSize.width, i++)
				dest[i] = src.getSubimage(x, y, frameSize.width, frameSize.height);
		return dest;
	}

}
