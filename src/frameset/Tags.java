package frameset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import frameset_tags.DelayTags;
import frameset_tags.FrameTag;
import tools.FrameTagLoader;
import util.Timer;

public class Tags {

	private List<FrameTag> frameSetTags;
	private Sprite rootSprite;
	private boolean disabledTags;

	public Tags()
		{ this(null, null); }
	
	public Tags(Tags tags) {
		frameSetTags = new ArrayList<>();
		for (FrameTag tag : tags.frameSetTags)
			frameSetTags.add(tag.getNewInstanceOfThis());
		rootSprite = tags.rootSprite;
		disabledTags = tags.disabledTags;
	}
	
	public <T extends FrameTag> Tags(Sprite rootSprite, T tag) {
		this.rootSprite = rootSprite;
		frameSetTags = tag == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(tag));
		disabledTags = false;
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
	
	public <T extends FrameTag> void addFrameSetTag(T tag)
		{ frameSetTags.add(tag); }
	
	public <T extends FrameTag> void removeFrameSetTag(T tag)
		{ frameSetTags.remove(tag); }
	
	public int size()
		{ return frameSetTags.size(); }

	public <T extends FrameTag> int indexOf(T tag)
		{ return frameSetTags.indexOf(tag); }

	public FrameTag get(int index)
		{ return frameSetTags.get(index); }
	
	public int getTotalTags()
		{ return frameSetTags.size(); }
	
	public void disableTags()
		{ disabledTags = true; }

	public void enableTags()
		{ disabledTags = false; }

	public void run() {
		if (disabledTags || (rootSprite != null && rootSprite.getMainFrameSet().isStopped()))
			return;
		processTags(this);
	}
	
	public static void processTags(Tags tags) {
		if (tags != null && tags.getTotalTags() > 0 && tags.getFrameSetTags().get(0) instanceof DelayTags) {
			final Tags tags2 = new Tags(tags);
			DelayTags delay = (DelayTags)tags2.getFrameSetTags().get(0);  
			tags2.getFrameSetTags().remove(0);
			if (tags.getTotalTags() > 0)
				Timer.createTimer("delayedProcessTags " + tags2.hashCode(), delay.value, () -> processTags(tags2));
			return;
		}
		for (int n = 0; n <tags.getTotalTags(); n++) {
			FrameTag tag = tags.getFrameSetTags().get(n);
			if (tag.deleteMeAfterFirstRead)
				tags.getFrameSetTags().remove(n--);
			if (tag.getTriggerDelay() > 0)
				Timer.createTimer("runTag " + tag.hashCode(), tag.getTriggerDelay(), () -> tag.process(tags.rootSprite));
			else
				tag.process(tags.rootSprite);
		}
	}
	
	public static Tags loadTagsFromString(String tagsStr) {
		Tags tags = new Tags();
		String[] frames = tagsStr.split("\\|"); // Divisor de frames
		for (String s1 : frames) {
			String[] sprites = s1.split("\\,,"); // Divisor de sprites e suas FrameTags
			for (String s2 : sprites)
				FrameTagLoader.loadToTags(s2, tags);
		}
		return tags;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (FrameTag tag : frameSetTags) {
			if (!sb.isEmpty())
				sb.append(",");
			sb.append(tag.toString());
		}
		return sb.toString();
	}

}
