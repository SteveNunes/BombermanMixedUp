package frameset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import frameset_tags.FrameTag;

public class Tags {

	private List<FrameTag> frameSetTags;
	private Sprite rootSprite;
	private int totalTags;

	public Tags(Tags tags) {
		rootSprite = tags.rootSprite;
		totalTags = tags.totalTags;
		frameSetTags = new ArrayList<>(tags.frameSetTags);
	}
	
	public <T extends FrameTag> Tags(Sprite rootSprite, T tag) {
		this.rootSprite = rootSprite;
		totalTags = 0;
		frameSetTags = tag == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(tag));
	}

	public Tags(Sprite rootSprite)
		{ this(rootSprite, null); }
	
	public Sprite getRootSprite()
		{ return rootSprite; }
	
	public void setRootSprite(Sprite sprite)
		{ rootSprite = sprite; }

	public void clearFrameSetTags()
		{ frameSetTags.clear(); }
	
	public List<FrameTag> getFrameSetTags()
		{ return frameSetTags; }
	
	public <T extends FrameTag> void addFrameSetTag(T tag) {
		frameSetTags.add(tag);
		totalTags++;
	}
	
	public <T extends FrameTag> void removeFrameSetTag(T tag) {
		frameSetTags.remove(tag);
		totalTags--;
	}
	
	public int size()
		{ return frameSetTags.size(); }

	public <T extends FrameTag> int indexOf(T tag)
		{ return frameSetTags.indexOf(tag); }

	public FrameTag get(int index)
		{ return frameSetTags.get(index); }

}
