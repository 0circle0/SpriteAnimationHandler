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
import java.awt.image.BufferedImage;

/**
 * FrameBuffer contains the individual frames of a BufferedImage
 */
public class FrameBuffer {
	public transient BufferedImage[] frameBuffer;

	/**
	 * Initialized the BufferedImage array with given parameters
	 * 
	 * @param src
	 *            Image to convert to individual frames
	 * @param frames
	 *            Number of frames inside the image
	 * @param framesAcross
	 *            Number of frames in each Row
	 * @param framesDown
	 *            Number of frames in each Column
	 * @param frameSize
	 *            Width and Height of each frame
	 */
	public FrameBuffer(BufferedImage src, int frames, int framesAcross, int framesDown, Dimension frameSize) {
		frameBuffer = SplitImage(src, frames, framesAcross, framesDown, frameSize);
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
	private BufferedImage[] SplitImage(BufferedImage src, int frames, int framesAcross, int framesDown,
			Dimension frameSize) {
		BufferedImage[] dest = new BufferedImage[frames + 1];
		for (int y = 0, i = 0; y < framesDown * frameSize.height; y += frameSize.height) {
			for (int x = 0; x < framesAcross * frameSize.width; x += frameSize.width, i++) {
				dest[i] = new BufferedImage(frameSize.width, frameSize.height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = dest[i].createGraphics();
				g2.drawImage(src, -x, -y, null);
				g2.dispose();
			}
		}
		return dest;
	}
}
