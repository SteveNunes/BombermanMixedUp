package frameset;

import java.util.ArrayList;
import java.util.List;

import frameset_tags.FrameTag;

public class Frame {
	
	private FrameSet mainFrameSet;
	private List<Tags> frameSetTagsList;

	public Frame(Frame frame)
		{ this(frame, frame.getMainFrameSet()); }

	public Frame(Frame frame, FrameSet mainFrameSet) {
		this.mainFrameSet = mainFrameSet;
		frameSetTagsList = new ArrayList<>();
		int[] n = {0};
		frame.frameSetTagsList.forEach(tags -> {
			frameSetTagsList.add(tags = new Tags(tags));
			tags.setRootSprite(this.mainFrameSet.getSprite(n[0]++));
		});
	}
	
	public Frame(FrameSet mainFrameSet) {
		this.mainFrameSet = mainFrameSet;
		frameSetTagsList = new ArrayList<>();
		int n = mainFrameSet.getTotalSprites(), n2;
		while ((n2 = getTotalTags()) < n)
			addFrameTagsList(new Tags(mainFrameSet.getSprites().get(n2)));
	}
	
	public int getTotalTags()
		{ return frameSetTagsList.size(); }
	
	public void setMainFrameSet(FrameSet mainFrameSet)
		{ this.mainFrameSet = mainFrameSet; }
	
	public void run() {
		for (Tags tags : frameSetTagsList)
			tags.run();
	}
	
	public FrameSet getMainFrameSet()
		{ return mainFrameSet; }
	
	public List<Tags> getFrameSetTagsList()
		{ return frameSetTagsList; }
	
	public void addFrameTagsList(Tags tagsList)
		{ frameSetTagsList.add(tagsList); }
	
	public void removeFrameTagsList(Tags tagsList)
		{ frameSetTagsList.remove(tagsList); }

	public void removeFrameTagsList(int index)
		{ frameSetTagsList.remove(index); }

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
