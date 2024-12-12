package frameset;

import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import application.Main;
import entities.Entity;
import frameset_tags.FrameTag;
import frameset_tags.Goto;
import frameset_tags.RepeatLastFrame;
import javafx.scene.canvas.GraphicsContext;
import objmoveutils.Position;
import tools.FrameTagLoader;
import tools.Tools;
import util.IniFile;

public class FrameSet extends Position {

	private static Map<String, FrameSet> preLoadedFrameSets = new HashMap<>();
	
	private Entity sourceEntity;
	private List<Sprite> sprites;
	private List<Frame> frames;
	private int framesPerTick;
	private int currentFrameIndex;
	private int ticks;
	private boolean changedIndex;
	private boolean stop;
	private int frontValue;

	public FrameSet(FrameSet frameSet) {
		this(frameSet, new Entity());
	}

	public FrameSet(FrameSet frameSet, Entity entity) {
		this(frameSet, new Position(), entity);
	}

	public FrameSet(FrameSet frameSet, Position position) {
		this(frameSet, position, new Entity());
	}

	public FrameSet(FrameSet frameSet, Position position, Entity entity) {
		this(entity, position);
		frameSet.sprites.forEach(sprite -> sprites.add(new Sprite(sprite, this)));
		frameSet.frames.forEach(frame -> frames.add(new Frame(frame, this)));
	}

	public FrameSet(Entity entity, Position position) {
		super(position);
		sprites = new ArrayList<>();
		frames = new ArrayList<>();
		sourceEntity = entity;
		framesPerTick = 1;
		changedIndex = false;
		stop = false;
		currentFrameIndex = 0;
		ticks = 0;
	}

	public FrameSet(Position position) {
		this(new Entity(), position);
	}

	public FrameSet(Entity entity) {
		this(entity, new Position());
	}

	public FrameSet() {
		this(new Entity(), new Position());
	}

	public void setFrontValue(int value) {
		frontValue = value;
	}
	
	public int getFrontValue() {
		return frontValue;
	}
	
	public double getAbsoluteX() {
		if (sourceEntity != null)
			return sourceEntity.getX() + getX();
		return getX();
	}

	public double getAbsoluteY() {
		if (sourceEntity != null)
			return sourceEntity.getY() + getY();
		return getY();
	}

	public Position getAbsolutePosition() {
		return new Position(getAbsoluteX(), getAbsoluteY());
	}

	public void setAbsoluteX(int x) {
		if (sourceEntity != null)
			setX(x - (int) sourceEntity.getX());
		else
			setX(x);
	}

	public void setAbsoluteY(int y) {
		if (sourceEntity != null)
			setY(y - (int) sourceEntity.getY());
		else
			setY(y);
	}

	public void setAbsolutePosition(int x, int y) {
		setAbsoluteX(x);
		setAbsoluteX(y);
	}

	public Entity getSourceEntity() {
		return sourceEntity;
	}

	public void setEntity(Entity entity) {
		this.sourceEntity = entity;
	}

	public void stop() {
		stop = true;
	}

	public void resume() {
		stop = false;
	}

	public boolean isStopped() {
		return stop;
	}

	public List<Sprite> getSprites() {
		return sprites;
	}

	public void run() {
		run(null);
	}

	public void run(GraphicsContext gc) {
		if (!stop && getTotalFrames() > 0 && currentFrameIndex >= 0 && currentFrameIndex < getTotalFrames()) {
			if (getSourceEntity().getShake() != null) {
				getSourceEntity().getShake().proccess();
				if (!getSourceEntity().getShake().isActive())
					getSourceEntity().unsetShake();
			}
			if (ticks == 0) {
				if (Main.isFreeze())
					ticks = 1;
				if (currentFrameIndex == 0 && getSourceEntity().getDefaultTags() != null) {
					getSourceEntity().getDefaultTags().setRootSprite(sprites.get(0));
					getSourceEntity().getDefaultTags().run();
				}
				frames.get(currentFrameIndex).run();
			}
			if (changedIndex) {
				changedIndex = false;
				run(gc);
				return;
			}
			if (getSourceEntity().isVisible())
				for (Sprite sprite : sprites) {
					if (isStopped())
						return;
					sprite.draw(gc);
				}
			if (!Main.isFreeze() && ++ticks >= framesPerTick) {
				ticks = 0;
				currentFrameIndex++;
			}
		}
	}

	public boolean isRunning() {
		return getCurrentFrame() != null;
	}

	public List<Frame> getFrames() {
		return frames;
	}

	public void addFrameTagToSpriteFromString(Sprite sprite, String tags) {
		FrameTagLoader.loadToTags(tags, getFrameSetTagsFrom(sprite));
	}

	public Tags getFrameSetTagsFrom(int frameIndex, int spriteIndex) {
		return frames.get(frameIndex).getFrameSetTagsList().get(spriteIndex);
	}

	public Tags getFrameSetTagsFrom(int spriteIndex) {
		return getFrameSetTagsFrom(currentFrameIndex, spriteIndex);
	}

	public Tags getFrameSetTagsFrom(Sprite sprite) {
		return getFrameSetTagsFrom(currentFrameIndex, sprite);
	}

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

	public Tags getFrameSetTagsFromFirstSprite() {
		return getSprites().isEmpty() ? null : getCurrentFrame().getFrameSetTagsList().get(0);
	}

	public Tags getFrameSetTagsFromFirstSprite(Frame frame) {
		return getSprites().isEmpty() ? null : frame.getFrameSetTagsList().get(0);
	}

	public void addFrameAt(int index, Frame cloneFrame) {
		if (cloneFrame == null && getTotalFrames() > 0 && index > 0)
			cloneFrame = new Frame(frames.get(index - 1), this);
		if (getTotalFrames() == 0)
			frames.add(cloneFrame = (cloneFrame == null ? new Frame(this) : cloneFrame));
		else
			frames.add(index, cloneFrame = (cloneFrame == null ? new Frame(this) : cloneFrame));
		int n = getTotalSprites(), n2;
		while ((n2 = cloneFrame.getTotalTags()) < n)
			cloneFrame.addFrameTagsList(new Tags(sprites.get(n2)));
	}

	public void addFrameAt(int index) {
		addFrameAt(index, null);
	}

	public void addFrameAtStart(Frame cloneFrame) {
		addFrameAt(0, cloneFrame);
	}

	public void addFrameAtStart() {
		addFrameAtStart(null);
	}

	public void addFrameAfterCurrentIndex(Frame cloneFrame) {
		addFrameAt(currentFrameIndex, cloneFrame);
	}

	public void addFrameAfterCurrentIndex() {
		addFrameAfterCurrentIndex(null);
	}

	public void addFrameAtEnd(Frame cloneFrame) {
		addFrameAt(getTotalFrames() == 0 ? 0 : getTotalFrames(), cloneFrame);
	}

	public void addFrameAtEnd() {
		addFrameAtEnd(null);
	}

	public Frame getFrame(int frameIndex) {
		return frames.get(frameIndex);
	}

	public void setFrame(int index, Frame frame) {
		frames.set(index, frame);
	}

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

	public int getFramesPerTick() {
		return framesPerTick;
	}

	public void setFramesPerTick(int framesPerTick) {
		this.framesPerTick = framesPerTick;
	}

	public void incFramesPerTick(int value) {
		framesPerTick += value;
	}

	public Frame getCurrentFrame() {
		return currentFrameIndex < 0 || currentFrameIndex >= getTotalFrames() ? null : frames.get(currentFrameIndex);
	}

	public int getCurrentFrameIndex() {
		return currentFrameIndex;
	}

	public int getCurrentTicks() {
		return ticks;
	}

	public void setTicks(int ticks) {
		this.ticks = ticks;
	}

	public int getTotalFrames() {
		return frames.size();
	}

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
		Tools.moveItemTo(frames, currentFrame, getTotalFrames());
		refreshFramesTags();
	}

	public void moveFrameToStart(Frame currentFrame) {
		Tools.moveItemTo(frames, currentFrame, 0);
		refreshFramesTags();
	}

	public void moveFrameToFront(Frame currentFrame) {
		Tools.moveItemTo(frames, currentFrame, currentFrameIndex + 1);
		refreshFramesTags();
	}

	public void moveFrameToBack(Frame currentFrame) {
		Tools.moveItemTo(frames, currentFrame, currentFrameIndex - 1);
		refreshFramesTags();
	}

	public void addSpriteAt(int index, Sprite sprite) {
		if (index < 0 || (getTotalSprites() > 0 && index > getTotalSprites()))
			throw new RuntimeException(index + " - Invalid Index (Min: 0, Max: " + (getTotalSprites() - 1) + ")");
		sprite.setSourceFrameSet(this);
		if (getTotalSprites() == 0 || index == getTotalSprites()) {
			sprites.add(sprite);
			frames.forEach(frame -> frame.getFrameSetTagsList().add(new Tags(sprite)));
		}
		else {
			sprites.add(index, sprite);
			frames.forEach(frame -> frame.getFrameSetTagsList().add(index, new Tags(sprite)));
		}
		sprite.frontValue = index;
	}

	public void addSpriteAtTop(Sprite sprite) {
		addSpriteAt(0, sprite);
	}

	public void addSpriteAtEnd(Sprite sprite) {
		addSpriteAt(getTotalSprites() == 0 ? 0 : getTotalSprites(), sprite);
	}

	public Sprite getSprite(int spriteIndex) {
		return sprites.get(spriteIndex);
	}

	public void setSprite(int index, Sprite sprite) {
		sprites.set(index, sprite);
		sprite.frontValue = index;
	}

	public void removeSprite(int index) {
		if (index < 0 || index >= getTotalSprites())
			throw new RuntimeException(index + " - Invalid Index (Min: 0, Max: " + (getTotalSprites() - 1) + ")");
		sprites.remove(index);
		frames.forEach(frame -> frame.getFrameSetTagsList().remove(index));
	}

	public void removeSprite(Sprite sprite) {
		if (sprites.contains(sprite))
			removeSprite(sprites.indexOf(sprite));
	}

	private void moveSpriteTo(Sprite sprite, int index) {
		int i = sprites.indexOf(sprite);
		Tools.moveItemTo(sprites, sprite, index);
		frames.forEach(frame -> Tools.moveItemTo(frame.getFrameSetTagsList(), frame.getFrameSetTagsList().get(i), index));
		sprite.frontValue = index;
	}

	public void moveSpriteToBack(Sprite sprite) {
		moveSpriteTo(sprite, sprites.indexOf(sprite) + 1);
	}

	public void moveSpriteToFront(Sprite sprite) {
		moveSpriteTo(sprite, sprites.indexOf(sprite) - 1);
	}

	public void moveSpriteToStart(Sprite sprite) {
		moveSpriteTo(sprite, getTotalSprites() - 1);
	}

	public void moveSpriteToEnd(Sprite sprite) {
		moveSpriteTo(sprite, 0);
	}

	public int getTotalSprites() {
		return sprites.size();
	}

	public boolean isEmptySprites() {
		return sprites.isEmpty();
	}

	public boolean isEmptyFrames() {
		return frames.isEmpty();
	}

	public void loadFromIni(Entity entity, String file, String section, String item) {
		if (file == null)
			throw new RuntimeException("Unable to load FrameSet from String because 'file' null");
		if (!file.contains("/") && !file.contains("\\"))
			file = "./appdata/configs/" + file + ".ini";
		file.replace("\\", "/");
		String shortName = file.substring(file.lastIndexOf("/appdata/") + 9) + "¡" + section + "¡" + item;
		if (preLoadedFrameSets.containsKey(shortName)) {
			FrameSet frameSet = preLoadedFrameSets.get(shortName);
			sprites.clear();
			frames.clear();
			currentFrameIndex = 0;
			ticks = 0;
			changedIndex = false;
			stop = false;
			framesPerTick = 1;
			setEntity(entity);
			frameSet.setEntity(entity);
			frameSet.sprites.forEach(sprite -> sprites.add(new Sprite(sprite, this)));
			frameSet.frames.forEach(frame -> frames.add(new Frame(frame, this)));
			return;
		}
		if (!new File(file).exists())
			throw new RuntimeException("Unable find file \"" + file + "\"");
		IniFile ini = IniFile.getNewIniFileInstance(file);
		if (section == null)
			throw new RuntimeException("Unable to load FrameSet from String because 'section' null");
		if (!ini.sectionExists(section))
			throw new RuntimeException("Invalid section (" + section + ") on file \"" + file + "\"");
		if (item == null)
			throw new RuntimeException("Unable to load FrameSet from String because 'item' null");
		if (!ini.itemExists(section, item))
			throw new RuntimeException("Invalid item (" + item + ") in section \"" + section + "\" on file \"" + file + "\"");
		loadFromString(entity, ini.read(section, item));
		setEntity(entity);
		preLoadedFrameSets.put(shortName, new FrameSet(this, getSourceEntity()));
	}
	
	public void loadFromString(Entity entity, String stringTileTags) {
		sprites.clear();
		frames.clear();
		currentFrameIndex = 0;
		ticks = 0;
		changedIndex = false;
		stop = false;
		framesPerTick = 1;
		setEntity(entity);
		String[] frames = stringTileTags.split("\\|"); // Divisor de frames
		boolean first = true;
		for (String s1 : frames) {
			Frame frame = new Frame(this);
			addFrameAtEnd(frame);
			String[] sprites = s1.split("\\,,"); // Divisor de sprites e suas FrameTags
			if (first) {
				for (int n = 0; n < sprites.length; n++)
					addSpriteAtEnd(new Sprite(this, "mainSprites", new Rectangle()));
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
					added = true;
				}
			}
		}
		return fSet;
	}
	
	public void iterateFrameTags(Consumer<FrameTag> consumer) {
		for (Frame frame : getFrames())
			for (Tags tags : frame.getFrameSetTagsList())
				for (FrameTag tag : tags.getTags())
					consumer.accept(tag);
	}

	public void TEMPresetTags() { // TEMP: AParentemente nao esta em uso entao apagar se confirmado
		frames.forEach(frame -> {
			for (Tags tags : frame.getFrameSetTagsList())
				for (FrameTag tag : tags.getTags()) {
					if (tag instanceof Goto)
						((Goto) tag).resetCycles();
					else if (tag instanceof RepeatLastFrame)
						((RepeatLastFrame) tag).resetCycles();
				}
		});
	}

}
