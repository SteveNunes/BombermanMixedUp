package frameset_tags;

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
	
}
