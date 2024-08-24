package entities;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import frameset_tags.FrameTag;
import tools.FrameTagLoader;
import tools.GameMisc;
import tools.Materials;

public class FrameSet {
	
	private Entity entity;
	private List<Sprite> sprites;
	private List<Frame> frames;
	private int totalFrames;
	private int framesPerTick;
	private int currentFrameIndex;
	private int ticks;
	private int totalSprites;
	private int maxY;
	private boolean changedIndex;
	private boolean stop;

	public FrameSet(FrameSet frameSet, Entity entity) {
		super();
		sprites = new ArrayList<>();
		for (Sprite sprite : frameSet.sprites)
			sprites.add(sprite = new Sprite(sprite, this));
		frames = new ArrayList<>();
		for (Frame frame : frameSet.frames)
			frames.add(frame = new Frame(frame, this));
		this.entity = entity;
		framesPerTick = frameSet.framesPerTick;
		maxY = frameSet.maxY;
		totalFrames = frameSet.totalFrames;
		totalSprites = frameSet.totalSprites;
		changedIndex = false;
		stop = false;
		currentFrameIndex = 0;
		ticks = 0;
	}

	public FrameSet(Entity entity, int framesPerTick, int x, int y) {
		super();
		this.entity = entity;
		this.framesPerTick = framesPerTick;
		entity.setPosition(x, y);
		frames = new ArrayList<>();
		sprites = new ArrayList<>();
		changedIndex = false;
		stop = false;
		currentFrameIndex = 0;
		ticks = 0;
		totalFrames = 0;
		totalSprites = 0;
		maxY = 0;
	}
	
	public FrameSet(Entity entity, int framesPerTick)
		{ this(entity, framesPerTick, 0, 0); }

	public FrameSet(Entity entity, int x, int y)
		{ this(entity, 1, x, y); }

	public FrameSet(Entity entity)
		{ this(entity, 1, 0, 0); }

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
	
	public void run()
		{ run(false); }
	
	public List<Sprite> getSprites()
		{ return sprites; }

	public void run(boolean isPaused) {
		if (!stop && totalFrames > 0 && currentFrameIndex < totalFrames) {
			if (ticks == 0)
				frames.get(currentFrameIndex).run();
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
			throw new RuntimeException("Sprite not found on the sprite list");
		return getFrameSetTagsFrom(frameIndex, getSprites().indexOf(sprite));
	}

	public Tags getFrameSetTagsFrom(Frame frame, int spriteIndex) {
		if (!getFrames().contains(frame))
			throw new RuntimeException("Frame not found on the frame list");
		int i = getFrames().indexOf(frame);
		return getFrameSetTagsFrom(i, spriteIndex);
	}

	public Tags getFrameSetTagsFrom(Frame frame, Sprite sprite) {
		if (!getSprites().contains(sprite))
			throw new RuntimeException("Sprite not found on the sprite list");
		if (!getFrames().contains(frame))
			throw new RuntimeException("Frame not found on the frame list");
		int spriteIndex = getSprites().indexOf(sprite);
		int frameIndex = getFrames().indexOf(frame);
		return getFrameSetTagsFrom(frameIndex, spriteIndex);
	}
	
	public Tags getFrameSetTagsFromFirstSprite(Frame frame)
		{ return getSprites().isEmpty() ? null : frame.getFrameSetTagsList().get(0); }

	public void addFrameAt(int index, Frame cloneFrame) {
		if (cloneFrame == null && totalFrames > 0 && index > 0)
			cloneFrame = new Frame(frames.get(index - 1), this);
		if(totalFrames == 0)
			frames.add(cloneFrame = (cloneFrame == null ? new Frame(this) : cloneFrame));
		else
			frames.add(index, cloneFrame = (cloneFrame == null ? new Frame(this) : cloneFrame));
		int n = totalSprites, n2;
		while ((n2 = cloneFrame.getTotalTags()) < n)
			cloneFrame.addFrameTagsList(new Tags(sprites.get(n2)));
		totalFrames++;
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
		{ addFrameAt(totalFrames == 0 ? 0 : totalFrames, cloneFrame); }
	
	public void addFrameAtEnd()
		{ addFrameAtEnd(null); }

	public Frame getFrame(int frameIndex)
		{ return frames.get(frameIndex); }
	
	public void setFrame(int index, Frame frame)
		{ frames.set(index, frame); }

	public void removeFrame(int frameIndex) {
		frames.remove(frameIndex);
		totalFrames--;
		if (currentFrameIndex >= totalFrames)
			currentFrameIndex = totalFrames == 0 ? 0 : totalFrames - 1;
		refreshFramesTags();
	}
	
	public void removeFrame(Frame frame) {
		if (frames.contains(frame))
			removeFrame(frames.indexOf(frame));
	}

	public int getFramesPerTick() 
		{ return framesPerTick; }

	public void setFramesPerTick(int framesPerTick) {
		this.framesPerTick = framesPerTick;
		ticks = 0;
	}

	public void incFramesPerTick(int value)
		{ framesPerTick += value; }

	public Frame getCurrentFrame()
		{ return currentFrameIndex >= totalFrames ? null : frames.get(currentFrameIndex); }

	public int getCurrentFrameIndex()
		{ return currentFrameIndex; }

	public int getTotalFrames()
		{ return totalFrames; }

	public void setCurrentFrameIndex(int index) {
		if (index < 0 || index >= totalFrames)
			throw new RuntimeException(index + " - Invalid Frame Index (Min: 0, Max: " + (totalFrames - 1) + ")");
		currentFrameIndex = index;
		ticks = 0;
		changedIndex = true;
	}
	
	public void decFrameIndex() {
		if (--currentFrameIndex < 0)
			currentFrameIndex = totalFrames - 1;
		setCurrentFrameIndex(currentFrameIndex);
		refreshFramesTags();
	}
	
	private void refreshFramesTags() {
		for (int i = 0; i < currentFrameIndex; i++)
			frames.get(i).run();
	}
	
	public void incFrameIndex() {
		if (++currentFrameIndex >= totalFrames)
			currentFrameIndex = 0;
		setCurrentFrameIndex(currentFrameIndex);
	}
	
	public void moveFrameToEnd(Frame currentFrame) {
		GameMisc.moveItemTo(frames, currentFrame, totalFrames);
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
		if (index < 0 || (totalSprites > 0 && index > totalSprites))
			throw new RuntimeException(index + " - Invalid Index (Min: 0, Max: " + (totalSprites - 1) + ")");
		sprite.setMainFrameSet(this);
		if (totalSprites == 0 || index == totalSprites) {
			sprites.add(sprite);
			for (Frame frame : frames)
				frame.getFrameSetTagsList().add(new Tags(sprite));
		}
		else {
			sprites.add(index, sprite);
			for (Frame frame : frames)
				frame.getFrameSetTagsList().add(index, new Tags(sprite));
		}
		totalSprites++;
	}

	public void addSpriteAtTop(Sprite sprite)
		{ addSpriteAt(0, sprite); }

	public void addSpriteAtEnd(Sprite sprite)
		{ addSpriteAt(totalSprites == 0 ? 0 : totalSprites, sprite); }

	public Sprite getSprite(int spriteIndex)
		{ return sprites.get(spriteIndex); }
	
	public void setSprite(int index, Sprite sprite)
		{ sprites.set(index, sprite); }

	public void removeSprite(int index) {
		if (index < 0 || index >= totalSprites)
			throw new RuntimeException(index + " - Invalid Index (Min: 0, Max: " + (totalSprites - 1) + ")");
		sprites.remove(index);
		for (Frame frame : frames)
			frame.getFrameSetTagsList().remove(index);
		totalSprites--;
	}
	
	public void removeSprite(Sprite sprite) {
		if (sprites.contains(sprite))
			removeSprite(sprites.indexOf(sprite));
	}
	
	private void moveSpriteTo(Sprite sprite, int index) {
		int i = sprites.indexOf(sprite);
		GameMisc.moveItemTo(sprites, sprite, index);
		for (Frame frame : frames)
			GameMisc.moveItemTo(frame.getFrameSetTagsList(), frame.getFrameSetTagsList().get(i), index);
	}
	
	public void moveSpriteToBack(Sprite sprite)
		{ moveSpriteTo(sprite, sprites.indexOf(sprite) + 1); }

	public void moveSpriteToFront(Sprite sprite)
		{ moveSpriteTo(sprite, sprites.indexOf(sprite) - 1); }

	public void moveSpriteToStart(Sprite sprite)
		{ moveSpriteTo(sprite, totalSprites - 1); }
	
	public void moveSpriteToEnd(Sprite sprite)
		{ moveSpriteTo(sprite, 0); }
	
	public int getTotalSprites()
		{ return totalSprites; }

	public boolean isEmptySprites()
		{ return totalSprites == 0; }

	public boolean isEmptyFrames()
		{ return totalFrames == 0; }

	public void loadFromString(String tags) {
		String[] frames = tags.split("\\|"); // Divisor de frames
		int totalSprites = -1;
		for (String s1 : frames) {
			Frame frame = new Frame(this);
			addFrameAtEnd(frame);
			String[] sprites = s1.split("\\,,"); // Divisor de sprites e suas FrameTags
			if (totalSprites == -1) {
				totalSprites = sprites.length;
				for (int n = 0; n < totalSprites; n++)
					addSpriteAtEnd(new Sprite(this, Materials.mainSprites, new Rectangle()));
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

}
