package frameset;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;
import frameset_tags.FrameTag;

public class Frame {

	private FrameSet sourceFrameSet;
	private List<Tags> frameSetTagsList;

	public Frame(Frame frame) {
		this(frame, frame.getSourceFrameSet());
	}

	public Frame(Frame frame, FrameSet sourceFrameSet) {
		this.sourceFrameSet = sourceFrameSet;
		frameSetTagsList = new ArrayList<>();
		int[] n = { 0 };
		frame.frameSetTagsList.forEach(tags -> {
			frameSetTagsList.add(tags = new Tags(tags));
			tags.setRootSprite(this.sourceFrameSet.getSprite(n[0]++));
		});
	}

	public Frame(FrameSet sourceFrameSet) {
		this.sourceFrameSet = sourceFrameSet;
		frameSetTagsList = new ArrayList<>();
		int n = sourceFrameSet.getTotalSprites(), n2;
		while ((n2 = getTotalTags()) < n)
			addFrameTagsList(new Tags(sourceFrameSet.getSprites().get(n2)));
	}

	public Entity getSourceEntity() {
		return sourceFrameSet.getSourceEntity();
	}

	public int getTotalTags() {
		return frameSetTagsList.size();
	}

	public void setMainFrameSet(FrameSet mainFrameSet) {
		this.sourceFrameSet = mainFrameSet;
	}

	public void run() {
		for (Tags tags : frameSetTagsList)
			tags.run();
	}

	public FrameSet getSourceFrameSet() {
		return sourceFrameSet;
	}

	public List<Tags> getFrameSetTagsList() {
		return frameSetTagsList;
	}

	public void addFrameTagsList(Tags tagsList) {
		frameSetTagsList.add(tagsList);
	}

	public void removeFrameTagsList(Tags tagsList) {
		frameSetTagsList.remove(tagsList);
	}

	public void removeFrameTagsList(int index) {
		frameSetTagsList.remove(index);
	}

	public void addFrameTagToSprite(int spriteIndex, FrameTag tag) {
		if (spriteIndex < 0 || spriteIndex >= sourceFrameSet.getTotalSprites())
			throw new RuntimeException(spriteIndex + " - Invalid Sprite Index (Min 0, Max " + (sourceFrameSet.getTotalSprites() - 1) + ")");
		while (getTotalTags() < sourceFrameSet.getTotalSprites())
			getFrameSetTagsList().add(new Tags(sourceFrameSet.getSprites().get(spriteIndex)));
		getFrameSetTagsList().get(spriteIndex).addTag(tag);
	}

	public void addFrameTagToSprite(Sprite sprite, FrameTag tag) {
		if (!sourceFrameSet.getSprites().contains(sprite))
			throw new RuntimeException("You can't add a tag because the informed sprite was not found");
		addFrameTagToSprite(sourceFrameSet.getSprites().indexOf(sprite), tag);
	}

	public void addFrameTagToFirstSprite(FrameTag tag) {
		if (!sourceFrameSet.getSprites().isEmpty())
			addFrameTagToSprite(sourceFrameSet.getSprites().get(0), tag);
	}

}
