package frameset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import frameset_tags.DelayTags;
import frameset_tags.FrameTag;
import frameset_tags.IncEntityPos;
import tools.FrameTagLoader;
import util.TimerFX;

public class Tags {

	private List<FrameTag> frameSetTags;
	private Sprite rootSprite;

	public Tags()
		{ this(null, null); }
	
	public Tags(Tags tags) {
		frameSetTags = new ArrayList<>();
		for (FrameTag tag : tags.frameSetTags) {
			FrameTag tag2;
			frameSetTags.add(tag2 = tag.getNewInstanceOfThis());
			tag2.setTriggerDelay(tag.getTriggerDelay());
			tag2.deleteMeAfterFirstRead = tag.deleteMeAfterFirstRead;
		}
		rootSprite = tags.rootSprite;
	}
	
	public <T extends FrameTag> Tags(Sprite rootSprite, T tag) {
		this.rootSprite = rootSprite;
		frameSetTags = tag == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(tag));
	}

	public Tags(Sprite rootSprite)
		{ this(rootSprite, null); }
	
	public Tags(FrameTag tag) {
		// TODO Auto-generated constructor stub
	}

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
	
	public void run() {
		if ((rootSprite != null && rootSprite.getSourceFrameSet().isStopped()))
			return;
		if (getTotalTags() > 0 && getFrameSetTags().get(0) instanceof DelayTags) {
			final Tags tags2 = new Tags(this);
			DelayTags delay = (DelayTags)tags2.getFrameSetTags().get(0);  
			tags2.getFrameSetTags().remove(0);
			if (getTotalTags() > 0)
				TimerFX.createTimer("delayedProcessTags " + tags2.hashCode(), delay.value, () -> tags2.run());
			return;
		}
		for (int n = 0; n < getTotalTags(); n++) {
			FrameTag tag = getFrameSetTags().get(n);
			if (tag.deleteMeAfterFirstRead)
				getFrameSetTags().remove(n--);
			if (tag.getTriggerDelay() > 0) {
				final FrameTag tag2 = tag.getNewInstanceOfThis();
				int delay = tag.getTriggerDelay();
				tag2.setTriggerDelay(0);
				TimerFX.createTimer("delayedRunTag " + tag2.hashCode(), delay, () -> tag2.process(rootSprite));
			}
			else
				tag.process(rootSprite);
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
