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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class BufferByte implements Serializable {
	private static final long serialVersionUID = 4168708017642942202L;
	// byte array of BufferedImage
	public byte[] bufferByte;

	/**
	 * Initializes and converts BufferedImage to byte array
	 * 
	 * @param bufferedImage
	 *            BufferedImage to be converted to byte array
	 */
	public BufferByte(BufferedImage bufferedImage) {
		bufferByte = bufferedImageToByteArray(bufferedImage);
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
	 * Gets the BufferedImage from byte array. Used for initialization of
	 * FrameBuffer
	 * 
	 * @return The converted BufferImage
	 */
	public BufferedImage getImage() {
		return byteArrayToBufferedImage(bufferByte);
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
}
