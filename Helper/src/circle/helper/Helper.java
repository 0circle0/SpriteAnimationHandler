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
package circle.helper;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Helper {
	// Default toolkit used
	private Toolkit tk;
	// Makes sure images are fully loaded before being used
	private MediaTracker tracker;
	// Default location for loading images
	private String ImageLocation = "/Data/Sprites/";

	/**
	 * Creates a new Helper
	 */
	public Helper() {
		this(new JFrame());
	}

	/**
	 * Creates a new Helper
	 * 
	 * @param c
	 *            Component used for tracking image loading
	 */
	public Helper(Component c) {
		tk = Toolkit.getDefaultToolkit();
		tracker = new MediaTracker(c);
	}

	/**
	 * Creates a new Helper
	 * 
	 * @param c
	 *            Component used for tracking image loading
	 * @param defaultImageLocation
	 *            Location of loading images from file system
	 */
	public Helper(Component c, String defaultImageLocation) {
		this(c);
		this.ImageLocation = defaultImageLocation;
	}

	/**
	 * Sets the default location for loading images
	 * 
	 * @param imageLocation
	 *            Location to load images from
	 */
	public void setImageLocation(String imageLocation) {
		this.ImageLocation = imageLocation;
	}

	/**
	 * Loads an image from the file system using a mediatracker
	 * 
	 * @param filename
	 *            Name of the PNG image to load from the file system. Can be
	 *            loaded with or without .png extension
	 * @return Image loaded from the file system
	 */
	public Image loadImage(String filename) {
		Image i = tk.getImage(getURL(ImageLocation + filename + (filename.endsWith(".png") ? "" : ".png")));
		tracker.addImage(i, 0);

		try {
			tracker.waitForID(0);
		} catch (InterruptedException IE) {
			System.out.println("Error waitForID(0)");
		}
		tracker.removeImage(i);
		return i;
	}

	/**
	 * Gets the URL of the file being loaded
	 * 
	 * @param filename
	 *            Name of the file being loaded
	 * @return URL of the file being loaded
	 */
	public URL getURL(String filename) {
		URL url = null;
		try {
			url = ClassLoader.class.getResource(filename);
		} catch (Exception e) {
			System.out.println("Error getting URL");
		}
		return url;
	}

	/**
	 * Loads an image and converts it to BufferedImage
	 * 
	 * @param filename
	 *            Name of file to be loaded
	 * @return image loaded and converted to BufferedImage
	 */
	public BufferedImage loadBufferedImage(String filename) {
		Image i = loadImage(filename);
		return toBufferedImage(i);
	}

	/**
	 * Splits a animation sequence into individual frames from a Sprite Sheet or
	 * Strip Format
	 * 
	 * @param src
	 *            The source image containing all the frames to be split
	 * @param frameWidth
	 *            Width of each frame
	 * @param frameHeight
	 *            Height of each frame
	 * @return BufferedImage array containing every frame of the animation
	 */
	public BufferedImage[] SplitImage(BufferedImage src, int frameWidth, int frameHeight) {
		return SplitImage(src, new Dimension(frameWidth, frameHeight));
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

	/**
	 * Converts and Image to BufferedImage
	 * 
	 * @param src
	 *            Image to be converted
	 * @return Converted BufferedImage
	 */
	public BufferedImage toBufferedImage(Image src) {
		BufferedImage ret = new BufferedImage(src.getWidth(null), src.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = ret.createGraphics();
		g2.drawImage(src, 0, 0, null);
		g2.dispose();
		return ret;
	}

	/**
	 * Adds transparency to a BufferedImage
	 * 
	 * @param bi
	 *            Image to add transparency to
	 * @param opacity
	 *            Opacity percent to be added to the image (0.0 - 1.0)
	 * @return BufferedImage with transparency
	 */
	public BufferedImage addTransparency(Image bi, float opacity) {
		BufferedImage dest = new BufferedImage(bi.getWidth(null), bi.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = dest.createGraphics();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
		g2.drawImage(bi, 0, 0, null);
		g2.dispose();
		return dest;
	}

	/**
	 * Converts a byte[] array to a BufferedImage
	 * 
	 * @param imageData
	 *            byte[] array to be converted to BufferedImage
	 * @return BufferedImage from imageData
	 */
	public BufferedImage byteArrayToBufferedImage(byte[] imageData) {
		ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
		try {
			return ImageIO.read(bais);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Converts a BufferedImage to byte[] for Serialization
	 * 
	 * @param bufferedImage
	 *            BufferedImage to be converted
	 * @return A byte[] of bufferedImage for Serialization
	 */
	public byte[] bufferedImageToByteArray(BufferedImage bufferedImage) {
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
	 * Compresses and saves an Object to the file system
	 * 
	 * @param o
	 *            Object to be saved
	 * @param file
	 *            Location and name of the file being saved
	 * @param compressed
	 *            true if compressed
	 */
	public void saveObject(Object o, String file, boolean compressed) {
		try {
			FileOutputStream saveFile = new FileOutputStream(file);
			GZIPOutputStream gz = null;
			if (compressed)
				gz = new GZIPOutputStream(saveFile);
			ObjectOutputStream save = new ObjectOutputStream(compressed ? gz : saveFile);
			save.writeObject(o);
			if (compressed)
				gz.close();
			save.close();
		} catch (IOException e) {
		}
	}

	/**
	 * Saves an Object to the file system
	 * 
	 * @param o
	 *            Object to be saved
	 * @param file
	 *            Location and name of the file being saved
	 */
	public void saveObject(Object o, String file) {
		saveObject(o, file, false);
	}

	/**
	 * Compresses and saves a BufferedImage to the file system
	 * 
	 * @param i
	 *            Image to be saved. Can be instance of BufferedImage or Image
	 * @param filename
	 *            Location and name to save the image as
	 * @param compressed
	 *            true if compressed
	 */
	public void saveBufferedImageAs(Image i, String filename, boolean compressed) {
		BufferedImage bi = null;
		byte[] by;
		if (i instanceof Image)
			bi = toBufferedImage(i);
		else if (i instanceof BufferedImage)
			bi = (BufferedImage) i;
		by = bufferedImageToByteArray(bi);
		saveObject(by, filename, compressed);
	}

	/**
	 * Saves a BufferedImage to the file system
	 * 
	 * @param i
	 *            Image to be saved. Can be instance of BufferedImage or Image
	 * @param filename
	 *            Location and name to save the image as
	 */
	public void saveBufferedImageAs(Image i, String filename) {
		saveBufferedImageAs(i, filename, false);
	}

	/**
	 * Opens an Object as a BufferedImage that has been compressed
	 * 
	 * @param name
	 *            Location and name of the Object to load
	 * @param compressed
	 *            true if compressed
	 * @return Object converted to BufferedImage
	 */
	public BufferedImage openObjectAsBufferedImage(String name, boolean compressed) {
		byte[] an = (byte[]) openObject(name, compressed);
		return byteArrayToBufferedImage(an);
	}

	/**
	 * Opens an Object as a BufferedImage
	 * 
	 * @param name
	 *            Location and name of the Object to load
	 * @return Object converted to BufferedImage
	 */
	public BufferedImage openObjectAsBufferedImage(String name) {
		return openObjectAsBufferedImage(name, false);
	}

	/**
	 * Opens an Object from the file system that has been compressed
	 * 
	 * @param fileLocation
	 *            Name and location of the Object to open
	 * 
	 * @param compressed
	 *            true if Object is compressed
	 * @return Object loaded from the file system
	 */
	public Object openObject(String fileLocation, boolean compressed) {
		InputStream saveFile = null;
		Object an = null;
		try {
			saveFile = ClassLoader.class.getResourceAsStream(fileLocation);
		} catch (Exception e) {
			System.out.println("Could not get resource as stream " + fileLocation);
		}
		ObjectInputStream restore = null;
		GZIPInputStream gz = null;
		if (compressed) {
			try {
				gz = new GZIPInputStream(saveFile);
			} catch (IOException e1) {
				System.out.println("Could not uncompress");
			}
		}
		try {
			restore = new ObjectInputStream(compressed ? gz : saveFile);
		} catch (IOException e) {
			System.out.println("IOException restore");
		}

		try {
			an = restore.readObject();
		} catch (ClassNotFoundException e) {
			System.out.println("Object created in different package than class is loading from.");
		} catch (IOException e) {
			System.out.println("Can't Restore an = restore.readObject()");
		}

		try {
			restore.close();
		} catch (IOException e) {
			System.out.println("Cannot close restore. Maybe wasn't opened.");
		}
		return an;
	}

	/**
	 * Opens an Object from the file system
	 * 
	 * @param fileLocation
	 *            Name and location of the Object to open
	 * @return Object loaded from the file system
	 */
	public Object openObject(String fileLocation) {
		return openObject(fileLocation, false);
	}
}
