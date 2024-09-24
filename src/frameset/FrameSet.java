package frameset;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import entities.Entity;
import frameset_tags.FrameTag;
import frameset_tags.Goto;
import frameset_tags.RepeatLastFrame;
import objmoveutils.JumpMove;
import objmoveutils.Position;
import tools.FrameTagLoader;
import tools.GameMisc;
import tools.Materials;

public class FrameSet extends Position {
	
	private Entity entity;
	private List<Sprite> sprites;
	private List<Frame> frames;
	private int framesPerTick;
	private int currentFrameIndex;
	private int ticks;
	private int maxY;
	private boolean changedIndex;
	private boolean stop;
	private JumpMove jumpMove;

	public FrameSet(FrameSet frameSet, Entity entity) {
		super(frameSet);
		jumpMove = null;
		sprites = new ArrayList<>();
		frames = new ArrayList<>();
		frameSet.sprites.forEach(sprite -> sprites.add(sprite = new Sprite(sprite, this)));
		frameSet.frames.forEach(frame -> frames.add(frame = new Frame(frame, this)));
		this.entity = entity;
		framesPerTick = frameSet.framesPerTick;
		maxY = frameSet.maxY;
		changedIndex = false;
		stop = false;
		currentFrameIndex = 0;
		ticks = 0;
	}

	public FrameSet(Entity entity, int framesPerTick, int x, int y) {
		super(x, y);
		this.entity = entity;
		this.framesPerTick = framesPerTick;
		frames = new ArrayList<>();
		sprites = new ArrayList<>();
		changedIndex = false;
		stop = false;
		currentFrameIndex = 0;
		ticks = 0;
		maxY = 0;
		jumpMove = null;
	}
	
	public JumpMove getJumpMove()
		{ return jumpMove; }
	
	public void setJumpMove(double jumpStrenght, double strenghtMultipiler, int speedInFrames)
		{ jumpMove = new JumpMove(getPosition(), new Position(), jumpStrenght, strenghtMultipiler, speedInFrames); }
	
	public FrameSet(Entity entity, int framesPerTick)
		{ this(entity, framesPerTick, 0, 0); }

	public FrameSet(Entity entity, int x, int y)
		{ this(entity, 1, x, y); }

	public FrameSet(Entity entity)
		{ this(entity, 1, 0, 0); }
	
	public double getAbsoluteX() {
		if (entity != null)
			return entity.getX() + getX();
		return getX();
	}

	public double getAbsoluteY() {
		if (entity != null)
			return entity.getY() + getY();
		return getY();
	}

	public void setAbsoluteX(int x) {
		if (entity != null)
			setX(x - (int)entity.getX());
		else
			setX(x);
	}
	
	public void setAbsoluteY(int y) {
		if (entity != null)
			setY(y - (int)entity.getY());
		else
			setY(y);
	}
	
	public Entity getEntity()
		{ return entity; }
	
	public void setEntity(Entity entity)
		{ this.entity = entity; }
	
	public void stop()
		{ stop = true; }
	
	public void resume()
		{ stop = false; }
	
	public boolean isStopped()
		{ return stop; }

	public int getMaxY()
		{ return maxY; }
	
	public List<Sprite> getSprites()
		{ return sprites; }

	public void run(boolean isPaused) {
		if (!stop && getTotalFrames() > 0 && currentFrameIndex >= 0 && currentFrameIndex < getTotalFrames()) {
			if (jumpMove != null) {
				jumpMove.move();
				if (jumpMove.jumpReachedFloorAgain())
					jumpMove = null;
			}
			if (ticks == 0) {
				if (isPaused)
					ticks = 1;
				frames.get(currentFrameIndex).run();
			}
			if (changedIndex) {
				changedIndex = false;
				run(isPaused);
				return;
			}
			for (Sprite sprite : sprites) {
				if (isStopped())
					return;
				sprite.draw();
				maxY = sprite.getMaxOutputSpriteY();
			}
			if (!isPaused && ++ticks >= framesPerTick) {
				ticks = 0;
				currentFrameIndex++;
			}
		}
	}
	
	public boolean isRunning()
		{ return getCurrentFrame() != null; }
	
	public List<Frame> getFrames()
		{ return frames; }
	
	public void addFrameTagToSpriteFromString(Sprite sprite, String tags)
		{ FrameTagLoader.loadToTags(tags, getFrameSetTagsFrom(sprite)); }
	
	public Tags getFrameSetTagsFrom(int frameIndex, int spriteIndex)
		{ return frames.get(frameIndex).getFrameSetTagsList().get(spriteIndex); }

	public Tags getFrameSetTagsFrom(int spriteIndex)
		{ return getFrameSetTagsFrom(currentFrameIndex, spriteIndex); }

	public Tags getFrameSetTagsFrom(Sprite sprite)
		{ return getFrameSetTagsFrom(currentFrameIndex, sprite); }

	public Tags getFrameSetTagsFrom(int frameIndex, Sprite sprite) {
		if (!getSprites().contains(sprite))
			GameMisc.throwRuntimeException("Sprite not found on the sprite list");
		return getFrameSetTagsFrom(frameIndex, getSprites().indexOf(sprite));
	}

	public Tags getFrameSetTagsFrom(Frame frame, int spriteIndex) {
		if (!getFrames().contains(frame))
			GameMisc.throwRuntimeException("Frame not found on the frame list");
		int i = getFrames().indexOf(frame);
		return getFrameSetTagsFrom(i, spriteIndex);
	}

	public Tags getFrameSetTagsFrom(Frame frame, Sprite sprite) {
		if (!getSprites().contains(sprite))
			GameMisc.throwRuntimeException("Sprite not found on the sprite list");
		if (!getFrames().contains(frame))
			GameMisc.throwRuntimeException("Frame not found on the frame list");
		int spriteIndex = getSprites().indexOf(sprite);
		int frameIndex = getFrames().indexOf(frame);
		return getFrameSetTagsFrom(frameIndex, spriteIndex);
	}
	
	public Tags getFrameSetTagsFromFirstSprite()
		{ return getSprites().isEmpty() ? null : getCurrentFrame().getFrameSetTagsList().get(0); }

	public Tags getFrameSetTagsFromFirstSprite(Frame frame)
		{ return getSprites().isEmpty() ? null : frame.getFrameSetTagsList().get(0); }

	public void addFrameAt(int index, Frame cloneFrame) {
		if (cloneFrame == null && getTotalFrames() > 0 && index > 0)
			cloneFrame = new Frame(frames.get(index - 1), this);
		if(getTotalFrames() == 0)
			frames.add(cloneFrame = (cloneFrame == null ? new Frame(this) : cloneFrame));
		else
			frames.add(index, cloneFrame = (cloneFrame == null ? new Frame(this) : cloneFrame));
		int n = getTotalSprites(), n2;
		while ((n2 = cloneFrame.getTotalTags()) < n)
			cloneFrame.addFrameTagsList(new Tags(sprites.get(n2)));
	}

	public void addFrameAt(int index)
		{ addFrameAt(index, null); }
	
	public void addFrameAtStart(Frame cloneFrame)
		{ addFrameAt(0, cloneFrame); }
		
	public void addFrameAtStart()
		{ addFrameAtStart(null); }

	public void addFrameAfterCurrentIndex(Frame cloneFrame)
		{ addFrameAt(currentFrameIndex, cloneFrame); }

	public void addFrameAfterCurrentIndex()
		{ addFrameAfterCurrentIndex(null); }

	public void addFrameAtEnd(Frame cloneFrame)
		{ addFrameAt(getTotalFrames() == 0 ? 0 : getTotalFrames(), cloneFrame); }
	
	public void addFrameAtEnd()
		{ addFrameAtEnd(null); }

	public Frame getFrame(int frameIndex)
		{ return frames.get(frameIndex); }
	
	public void setFrame(int index, Frame frame)
		{ frames.set(index, frame); }

	public void removeFrame(int frameIndex) {
		frames.remove(frameIndex);
		if (currentFrameIndex >= getTotalFrames())
			currentFrameIndex = getTotalFrames() == 0 ? 0 : getTotalFrames() - 1;
		refreshFramesTags();
	}
	
	public void removeFrame(Frame frame) {
		if (frames.contains(frame))
			removeFrame(frames.indexOf(frame));
	}

	public int getFramesPerTick() 
		{ return framesPerTick; }

	public void setFramesPerTick(int framesPerTick)
		{ this.framesPerTick = framesPerTick; }

	public void incFramesPerTick(int value)
		{ framesPerTick += value; }

	public Frame getCurrentFrame()
		{ return currentFrameIndex < 0 || currentFrameIndex >= getTotalFrames() ? null : frames.get(currentFrameIndex); }

	public int getCurrentFrameIndex()
		{ return currentFrameIndex; }

	public int getCurrentTicks()
		{ return ticks; }

	public void setTicks(int ticks)
		{ this.ticks = ticks; }

	public int getTotalFrames()
		{ return frames.size(); }

	public void setCurrentFrameIndex(int index) {
		currentFrameIndex = index;
		ticks = 0;
		changedIndex = true;
	}
	
	public void incFrameIndex() {
		if (++currentFrameIndex < getTotalFrames())
			setCurrentFrameIndex(currentFrameIndex);
	}
	
	public void decFrameIndex() {
		if (--currentFrameIndex >= 0)
			setCurrentFrameIndex(currentFrameIndex);
	}
	
	public void refreshFramesTags() {
		if (currentFrameIndex >= 0 && currentFrameIndex < getTotalFrames())
			for (int i = 0; i < currentFrameIndex; i++)
				frames.get(i).run();
	}
	
	public void moveFrameToEnd(Frame currentFrame) {
		GameMisc.moveItemTo(frames, currentFrame, getTotalFrames());
		refreshFramesTags();
	}

	public void moveFrameToStart(Frame currentFrame) { 
		GameMisc.moveItemTo(frames, currentFrame, 0);
		refreshFramesTags();
	}

	public void moveFrameToFront(Frame currentFrame) {
		GameMisc.moveItemTo(frames, currentFrame, currentFrameIndex + 1);
		refreshFramesTags();
	}

	public void moveFrameToBack(Frame currentFrame) {
		GameMisc.moveItemTo(frames, currentFrame, currentFrameIndex - 1);
		refreshFramesTags();
	}

	public void addSpriteAt(int index, Sprite sprite) {
		if (index < 0 || (getTotalSprites() > 0 && index > getTotalSprites()))
			GameMisc.throwRuntimeException(index + " - Invalid Index (Min: 0, Max: " + (getTotalSprites() - 1) + ")");
		sprite.setMainFrameSet(this);
		if (getTotalSprites() == 0 || index == getTotalSprites()) {
			sprites.add(sprite);
			frames.forEach(frame -> frame.getFrameSetTagsList().add(new Tags(sprite)));
		}
		else {
			sprites.add(index, sprite);
			frames.forEach(frame -> frame.getFrameSetTagsList().add(index, new Tags(sprite)));
		}
	}

	public void addSpriteAtTop(Sprite sprite)
		{ addSpriteAt(0, sprite); }

	public void addSpriteAtEnd(Sprite sprite)
		{ addSpriteAt(getTotalSprites() == 0 ? 0 : getTotalSprites(), sprite); }

	public Sprite getSprite(int spriteIndex)
		{ return sprites.get(spriteIndex); }
	
	public void setSprite(int index, Sprite sprite)
		{ sprites.set(index, sprite); }

	public void removeSprite(int index) {
		if (index < 0 || index >= getTotalSprites())
			GameMisc.throwRuntimeException(index + " - Invalid Index (Min: 0, Max: " + (getTotalSprites() - 1) + ")");
		sprites.remove(index);
		frames.forEach(frame -> frame.getFrameSetTagsList().remove(index));
	}
	
	public void removeSprite(Sprite sprite) {
		if (sprites.contains(sprite))
			removeSprite(sprites.indexOf(sprite));
	}
	
	private void moveSpriteTo(Sprite sprite, int index) {
		int i = sprites.indexOf(sprite);
		GameMisc.moveItemTo(sprites, sprite, index);
		frames.forEach(frame -> GameMisc.moveItemTo(frame.getFrameSetTagsList(), frame.getFrameSetTagsList().get(i), index));
	}
	
	public void moveSpriteToBack(Sprite sprite)
		{ moveSpriteTo(sprite, sprites.indexOf(sprite) + 1); }

	public void moveSpriteToFront(Sprite sprite)
		{ moveSpriteTo(sprite, sprites.indexOf(sprite) - 1); }

	public void moveSpriteToStart(Sprite sprite)
		{ moveSpriteTo(sprite, getTotalSprites() - 1); }
	
	public void moveSpriteToEnd(Sprite sprite)
		{ moveSpriteTo(sprite, 0); }
	
	public int getTotalSprites()
		{ return sprites.size(); }

	public boolean isEmptySprites()
		{ return sprites.isEmpty(); }

	public boolean isEmptyFrames()
		{ return frames.isEmpty(); }

	public void loadFromString(String tags) {
		if (tags == null)
			GameMisc.throwRuntimeException("Unable to load FrameSet from String because its null");
		sprites.clear();
		frames.clear();
		currentFrameIndex = 0;
		ticks = 0;
		maxY = 0;
		changedIndex = false;
		stop = false;
		String[] frames = tags.split("\\|"); // Divisor de frames
		boolean first = true;
		for (String s1 : frames) {
			Frame frame = new Frame(this);
			addFrameAtEnd(frame);
			String[] sprites = s1.split("\\,,"); // Divisor de sprites e suas FrameTags
			if (first) {
				for (int n = 0; n < sprites.length; n++)
					addSpriteAtEnd(new Sprite(this, Materials.mainSprites, new Rectangle()));
				first = false;
			}
			int n = 0;
			for (String s2 : sprites)
				FrameTagLoader.loadToTags(s2, frame.getFrameSetTagsList().get(n++));
		}
	}

	public String getStringFromFrameSetTags() {
		String fSet = "";
		for (int frameIndex = 0; frameIndex < getTotalFrames(); frameIndex++) {
			if (frameIndex > 0)
				fSet += "|";
			for (int spriteIndex = 0; spriteIndex < getTotalSprites(); spriteIndex++) {
				boolean added = false;
				for (int tagIndex = 0; tagIndex < getFrameSetTagsFrom(frameIndex, spriteIndex).size(); tagIndex++) {
					FrameTag tag = getFrameSetTagsFrom(frameIndex, spriteIndex).get(tagIndex);
					fSet += (spriteIndex > 0 && !added ? ",," : "") + (tagIndex > 0 ? "," : "") + tag;
					added= true;
				}
			}
		}
		return fSet;
	}

	public void resetTags() {
		frames.forEach(frame -> {
			for (Tags tags : frame.getFrameSetTagsList())
				for (FrameTag tag : tags.getFrameSetTags()) {
					if (tag instanceof Goto)
						((Goto)tag).resetCycles();
					else if (tag instanceof RepeatLastFrame)
						((RepeatLastFrame)tag).resetCycles();
				}
		});
	}

}
