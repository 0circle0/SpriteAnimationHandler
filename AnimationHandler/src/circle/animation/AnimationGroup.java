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
package circle.animation;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import circle.animation.internal.Position;

/**
 * AnimationGroup contains an array of Animation Objects. Using a UUID to track
 * each animation. AnimationsGroups created are Serializable.
 * 
 * @author Brian Jensen
 */
public class AnimationGroup implements Serializable {

	private static final long serialVersionUID = 8747907961688422176L;
	// Each Animation Object accessed by its name
	private HashMap<String, Animation> usableAnimationGroup;
	// Holds the Animations being used and drawn to the screen
	private transient ConcurrentHashMap<UUID, Animation> displayGroup;
	private UUID ID;

	/**
	 * Creates a new AnimationGroup
	 */
	public AnimationGroup() {
		this.usableAnimationGroup = new HashMap<String, Animation>();
		setup();
	}

	/**
	 * Initialized the AnimationGroup. This must be called before using
	 * AnimationGroup
	 */
	public void init() {
		for (String s : this.usableAnimationGroup.keySet())
			this.usableAnimationGroup.get(s).init();
		setup();
	}

	/**
	 * Creates all the Arrays used by AnimationGroup
	 */
	private void setup() {
		this.displayGroup = new ConcurrentHashMap<>();
		ID = UUID.randomUUID();
	}

	/**
	 * Adds a new Animation into the usable Animations available to draw to
	 * screen. If the name exists already in the usable Animations it will fail
	 * to add
	 * 
	 * @param name
	 *            Name of the Animation to add
	 * @param animation
	 *            Animation to add
	 * @return Will return false if the name already exists and will not
	 *         overwrite with the new data. If the name does not exist the new
	 *         Animation will be added
	 * @see AnimationGroup#overwriteUsable(String, Animation)
	 */
	public boolean injectNewUsable(String name, Animation animation) {
		if (usableAnimationGroup.containsKey(name)) {
			/*
			 * Name already in use. We don't want to overwrite an Animation that
			 * already exists.
			 */
			System.out.println(name + " being added to AnimationGroup is already in use.");
			return false;
		}
		usableAnimationGroup.put(name, animation);
		return true;
	}

	/**
	 * Overwrites or adds an animation even if the name being added already
	 * exists. This is used for replacing an already added Animation. It is
	 * recommended, unless your intention is to overwrite Animations is to use
	 * inject(String, Animation)
	 * 
	 * @param name
	 *            Name of the Animation to add
	 * @param animation
	 *            Animation to add
	 * @see AnimationGroup#injectNewUsable(String, Animation)
	 */
	public void overwriteUsable(String name, Animation animation) {
		usableAnimationGroup.put(name, animation);
	}

	/**
	 * Adds a new Animation you would like to display on screen
	 * 
	 * @param name
	 *            Name of the Animation you would like to use
	 * @param pos
	 *            x, y position of the Animation
	 * @return UUID generated for accessing elements of the displaying Animation
	 */
	public UUID add(String name, Position pos) {
		return add(name, pos, null, 0);
	}

	/**
	 * Adds a new Animation you would like to display on screen with a rotation
	 * angle and a rotation amount with the option to LOOP
	 * 
	 * @param name
	 *            Name of the Animation you would like to use
	 * @param pos
	 *            x, y position of the Animation
	 * @param angleDeg
	 *            Angle of rotation -180 - 180
	 * @param rotationAmount
	 *            amount to rotate each frame -180 - 180
	 * @param persistent
	 *            true to loop false to play once
	 * @return UUID generated for accessing elements of the displaying Animation
	 */
	public UUID add(String name, Position pos, Double angleDeg, double rotationAmount, boolean persistent) {
		ID = UUID.randomUUID();
		Animation anim = this.usableAnimationGroup.get(name).clone();
		anim.setPos(pos);
		anim.setCurrentFrame(0);
		if (angleDeg != null) {
			anim.rotation = angleDeg;
			anim.rotate = true;
			anim.rotationAmount = rotationAmount;
		}
		this.displayGroup.put(ID, anim);
		makePersistent(ID, persistent);
		return ID;
	}

	/**
	 * Adds a new Animation you would like to display on screen with a rotation
	 * angle and a rotation amount.
	 * 
	 * @param name
	 *            Name of the Animation you would like to use
	 * @param pos
	 *            x, y position of the Animation
	 * @param angleDeg
	 *            Angle of rotation -180 - 180
	 * @param rotationAmount
	 *            amount to rotate each frame -180 - 180
	 * @return UUID generated for accessing elements of the displaying Animation
	 */
	public UUID add(String name, Position pos, Double angleDeg, double rotationAmount) {
		return add(name, pos, angleDeg, rotationAmount, false);
	}

	/**
	 * Adds a new Animation you would like to display on screen with a rotation
	 * angle with no rotation.
	 * 
	 * @param name
	 *            Name of the Animation you would like to use
	 * @param pos
	 *            x, y position of the Animation
	 * @param angleDeg
	 *            Angle of rotation -180 - 180
	 * @return UUID generated for accessing elements of the displaying Animation
	 */
	public UUID add(String name, Position pos, double angleDeg) {
		return add(name, pos, angleDeg, 0, false);
	}

	/**
	 * Adds a new Animation you would like to display on screen
	 * 
	 * @param name
	 *            Name of the ANimation you would like to use
	 * @param x
	 *            The x position of the Animation
	 * @param y
	 *            The y position of the Animation
	 * @return UUID generated for accessing elements of the displaying Animation
	 */
	public UUID add(String name, int x, int y) {
		return add(name, new Position(x, y));
	}

	/**
	 * Adds a new Animation you would like to display on screen
	 * 
	 * @param name
	 *            Name of the Animation you would like to use
	 * @param pos
	 *            X, Y location of the Animation
	 * @param persistent
	 *            True if looping, false if play once
	 * @return UUID generated for accessing elements of the displaying Animation
	 */
	public UUID add(String name, Position pos, boolean persistent) {
		UUID ID = add(name, pos);
		makePersistent(ID, persistent);
		return ID;
	}

	/**
	 * Adds a new Animation you would like to display on screen
	 * 
	 * @param name
	 *            Name of the ANimation you would like to use
	 * @param x
	 *            The x position of the Animation
	 * @param y
	 *            The y position of the Animation
	 * @param persistent
	 *            True if looping, false if play once
	 * @return UUID generated for accessing elements of the displaying Animation
	 */
	public UUID add(String name, int x, int y, boolean persistent) {
		return add(name, new Position(x, y), persistent);
	}

	/**
	 * Changes an Animation being used to either play once or loop
	 */
	private void makePersistent(UUID ID, boolean persistent) {
		this.displayGroup.get(ID).loop = persistent;
	}

	/**
	 * Removes an Animation being drawn to the screen
	 * 
	 * @param hashID
	 *            UUID of the animation to be removed
	 */
	public void remove(UUID hashID) {
		synchronized (this) {
			this.displayGroup.remove(hashID);
		}
	}

	/**
	 * Get the position of an Animation using UUID of the Animation being drawn
	 * to the screen
	 * 
	 * @param hashID
	 *            UUID of the Animation being drawn
	 * @return the X, Y array for the position of the Animation
	 */
	public Position getPosition(UUID hashID) {
		return this.displayGroup.get(hashID).getPos();
	}

	/**
	 * Sets the position of an Animation using UUID of the Animation being drawn
	 * to the screen
	 * 
	 * @param hashID
	 *            UUID of the Animation being drawn
	 * @param newPos
	 *            X, Y of the Animation
	 */
	public void setPosition(UUID hashID, Position newPos) {
		this.displayGroup.get(hashID).setPos(newPos);
	}

	/**
	 * Sets the position of an Animation using UUID of the Animation being drawn
	 * to the screen
	 * 
	 * @param hashID
	 *            UUID of the Animation being drawn
	 * @param x
	 *            X position of the Animation
	 * @param y
	 *            Y position of the Animation
	 */
	public void setPosition(UUID hashID, int x, int y) {
		setPosition(hashID, new Position(x, y));
	}

	/**
	 * Updates each frame of the Animation increasing the frame by one and
	 * removing Animations that are over
	 * 
	 * @return An Array of UUID of all the elements that were removed
	 */
	public ArrayList<UUID> update() {
		ArrayList<UUID> ret = new ArrayList<UUID>();
		Iterator<UUID> i = this.displayGroup.keySet().iterator();
		while (i.hasNext()) {
			UUID ID = i.next();
			this.displayGroup.get(ID).rotation += this.displayGroup.get(ID).rotationAmount;
			int frame = this.displayGroup.get(ID).getCurrentFrame() + 1;
			if (frame >= this.displayGroup.get(ID).numOfFrames) {
				if (this.displayGroup.get(ID).loop) {
					this.displayGroup.get(ID).setCurrentFrame(0);
				} else {
					remove(ID);
					ret.add(ID);
				}
			} else
				this.displayGroup.get(ID).setCurrentFrame(frame);
		}
		return ret;
	}

	/**
	 * Draws all Animations being used to the screen
	 * 
	 * @param g2
	 *            Graphics2D being used to draw Images to the screen
	 */
	public void draw(Graphics2D g2) {
		synchronized (this) {
			for (UUID ID : this.displayGroup.keySet()) {
				if (this.displayGroup.get(ID).rotate) {
					AffineTransform at = new AffineTransform();
					BufferedImage temp = this.displayGroup.get(ID)
							.getFrameImage(this.displayGroup.get(ID).getCurrentFrame());
					int x = this.displayGroup.get(ID).getPosX();
					int y = this.displayGroup.get(ID).getPosY();
					at.translate(x + temp.getWidth() / 2, y + temp.getHeight() / 2);
					if (this.displayGroup.get(ID).rotate)
						at.rotate(Math.toRadians(this.displayGroup.get(ID).rotation));
					at.translate(-temp.getWidth() / 2, -temp.getHeight() / 2);
					g2.drawImage(temp, at, null);
				} else {
					g2.drawImage(this.displayGroup.get(ID).getFrameImage(this.displayGroup.get(ID).getCurrentFrame()),
							this.displayGroup.get(ID).getPosX(), this.displayGroup.get(ID).getPosY(), null);
				}
			}
		}
	}

	/**
	 * @return returns amount of Animations in displayGroup
	 */
	public int size() {
		return this.displayGroup.size();
	}

	/**
	 * Gets all the names of the animations available
	 * 
	 * @return list of names in a String array
	 */
	public String[] getNames() {
		String[] ret = new String[this.usableAnimationGroup.keySet().size()];
		int i = 0;
		for (String s : this.usableAnimationGroup.keySet()) {
			ret[i] = s;
			i++;
		}
		return ret;
	}
}
