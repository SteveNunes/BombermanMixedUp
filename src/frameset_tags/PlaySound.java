package frameset_tags;

import application.Main;
import entities.Sprite;
import tools.FrameSetEditor;
import tools.Sound;

public class PlaySound extends FrameTag {
	
	private String partialSoundPath;
	private String tags;
	
	public PlaySound(String tags) {
		this.tags = tags;
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ partialSoundPath = params[0]; }
		catch (Exception e)
			{ partialSoundPath = tags; }
	}

	public String getPartialSoundPath()
		{ return partialSoundPath; }
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + partialSoundPath + "}"; }

	@Override
	public PlaySound getNewInstanceOfThis()
		{ return new PlaySound(tags); }
	
	@Override
	public void process(Sprite sprite) {
		if (!Main.spriteEditor || !FrameSetEditor.isPaused)
			Sound.playSound(partialSoundPath);
	}

	@Override
	public void reset() {
	}

}
