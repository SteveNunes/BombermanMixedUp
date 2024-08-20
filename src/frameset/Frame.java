package frameset;

import java.util.ArrayList;
import java.util.List;

import frameset_tags.FrameTag;
import javafx.scene.canvas.GraphicsContext;
import tools.FrameTagProcessor;

public class Frame {
	
	private GraphicsContext gcTarget;
	private FrameSet mainFrameSet;
	private List<Tags> frameSetTagsList;
	private int totalTags;
	
	public Frame(Frame frame) {
		frameSetTagsList = new ArrayList<>();
		gcTarget = frame.gcTarget;
		mainFrameSet = frame.mainFrameSet;
		totalTags = frame.totalTags;
		int n = 0;
		for (Tags tags : frame.frameSetTagsList) {
			frameSetTagsList.add(tags = new Tags(tags));
			tags.setRootSprite(mainFrameSet.getSprite(n++));
		}
	}
	
	public Frame(FrameSet mainFrameSet) {
		this.mainFrameSet = mainFrameSet;
		this.gcTarget = mainFrameSet.getTargetGraphicsContext();
		frameSetTagsList = new ArrayList<>();
		totalTags = 0;
		int n = mainFrameSet.getTotalSprites(), n2;
		while ((n2 = getTotalTags()) < n)
			addFrameTagsList(new Tags(mainFrameSet.getSprites().get(n2)));
	}
	
	public int getTotalTags()
		{ return totalTags; }
	
	public void setMainFrameSet(FrameSet mainFrameSet)
		{ this.mainFrameSet = mainFrameSet; }
	
	public void run() {
		for (Tags tags : frameSetTagsList)
			FrameTagProcessor.process(tags);
	}
	
	public List<Tags> getFrameSetTagsList()
		{ return frameSetTagsList; }
	
	public void addFrameTagsList(Tags tagsList) {
		frameSetTagsList.add(tagsList);
		totalTags++;
	}
	
	public void removeFrameTagsList(Tags tagsList) {
		frameSetTagsList.remove(tagsList);
		totalTags--;
	}

	public void removeFrameTagsList(int index) {
		frameSetTagsList.remove(index);
		totalTags--;
	}

	public void addFrameTagToSprite(int spriteIndex, FrameTag tag) {
		if (spriteIndex < 0 || spriteIndex >= mainFrameSet.getTotalSprites())
			throw new RuntimeException(spriteIndex  + " - Invalid Sprite Index (Min 0, Max " + (mainFrameSet.getTotalSprites() - 1) + ")");
		while (getTotalTags() < mainFrameSet.getTotalSprites())
			getFrameSetTagsList().add(new Tags(mainFrameSet.getSprites().get(spriteIndex)));
		getFrameSetTagsList().get(spriteIndex).addFrameSetTag(tag);
	}
	
	public void addFrameTagToSprite(Sprite sprite, FrameTag tag) {
		if (!mainFrameSet.getSprites().contains(sprite))
			throw new RuntimeException("You can't add a tag because the informed sprite was not found");
		addFrameTagToSprite(mainFrameSet.getSprites().indexOf(sprite), tag);
	}	

	public void addFrameTagToFirstSprite(FrameTag tag) {
		if (!mainFrameSet.getSprites().isEmpty())
			addFrameTagToSprite(mainFrameSet.getSprites().get(0), tag);
	}
	
}
