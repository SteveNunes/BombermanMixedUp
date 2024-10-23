package frameset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import frameset_tags.DelayTags;
import frameset_tags.FrameTag;
import tools.FrameTagLoader;
import util.TimerFX;

public class Tags {

	private List<FrameTag> tags;
	private Sprite rootSprite;

	public Tags()
		{ this(null, null); }
	
	public Tags(Tags tags) {
		this.tags = new ArrayList<>();
		for (FrameTag tag : tags.tags) {
			FrameTag tag2;
			this.tags.add(tag2 = tag.getNewInstanceOfThis());
			tag2.setTriggerDelay(tag.getTriggerDelay());
			tag2.deleteMeAfterFirstRead = tag.deleteMeAfterFirstRead;
		}
		rootSprite = tags.rootSprite;
	}
	
	public <T extends FrameTag> Tags(Sprite rootSprite, T tag) {
		this.rootSprite = rootSprite;
		tags = tag == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(tag));
	}

	public Tags(Sprite rootSprite)
		{ this(rootSprite, null); }
	
	public Sprite getRootSprite()
		{ return rootSprite; }
	
	public void setRootSprite(Sprite sprite)
		{ rootSprite = sprite; }

	public void clearTags()
		{ tags.clear(); }
	
	public List<FrameTag> getTags()
		{ return tags; }
	
	public <T extends FrameTag> void addTag(T tag)
		{ tags.add(tag); }
	
	public void loadFromString(String stringTags) {
		tags.clear();
		loadTagsFromString(stringTags);
	}
	
	public void addTagsFromString(String stringTags) {
		Tags tempTags = Tags.loadTagsFromString(stringTags);
		for (FrameTag t : tempTags.getTags())
			tags.add(t);
	}
	
	public <T extends FrameTag> void removeTag(T tag)
		{ tags.remove(tag); }
	
	public void removeTagFromIndex(int i) {
		if (i >= tags.size())
			throw new RuntimeException(i + " - Indice inválido (Max " + (tags.size() - 1));
		tags.remove(i);
	}
	
	public void removeTag(String tagName) {
		FrameTag tag = FrameTag.getFrameTagClassFromString(tags, tagName);
		if (tag == null)
			throw new RuntimeException(tagName + " - Tag não encontrada");
		tags.remove(tag);
	}
	
	public int size()
		{ return tags.size(); }

	public <T extends FrameTag> int indexOf(T tag)
		{ return tags.indexOf(tag); }

	public FrameTag get(int index)
		{ return tags.get(index); }
	
	public int getTotalTags()
		{ return tags.size(); }
	
	public void run() {
		if ((rootSprite != null && rootSprite.getSourceFrameSet().isStopped()))
			return;
		if (getTotalTags() > 0 && getTags().get(0) instanceof DelayTags) {
			final Tags tags2 = new Tags(this);
			DelayTags delay = (DelayTags)tags2.getTags().get(0);  
			tags2.getTags().remove(0);
			if (getTotalTags() > 0)
				TimerFX.createTimer("delayedProcessTags " + tags2.hashCode(), delay.value, () -> tags2.run());
			return;
		}
		for (int n = 0; n < getTotalTags(); n++) {
			FrameTag tag = getTags().get(n);
			if (tag.deleteMeAfterFirstRead)
				getTags().remove(n--);
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
		for (FrameTag tag : tags) {
			if (!sb.isEmpty())
				sb.append(",");
			sb.append(tag.toString());
		}
		return sb.toString();
	}

}
