package frameset_tags;

import application.Main;
import entities.Sprite;
import javafx.scene.media.MediaPlayer;
import tools.FrameSetEditor;
import tools.Sound;

public class PlaySound extends FrameTag {
	
	private String partialSoundPath;
	private float rate;
	private float balance;
	private float volume;
	
	public PlaySound(String partialSoundPath, float rate, float balance, float volume) {
		this.partialSoundPath = partialSoundPath;
		this.rate = rate;
		this.balance = balance;
		this.volume = volume;
	}
	
	public PlaySound(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 4)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 1)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			partialSoundPath = params[n];
			n++; rate = params.length < 2 || params[n].equals("-") ? 1 : Float.parseFloat(params[n]);
			n++; balance = params.length < 3 || params[n].equals("-") ? 1 : Float.parseFloat(params[n]);
			n++; volume = params.length < 4 || params[n].equals("-") ? 1 : Float.parseFloat(params[n]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[n] + " - Invalid parameter"); }
	}

	public String getPartialSoundPath()
		{ return partialSoundPath; }
	
	public float getRate()
		{ return rate; }

	public float getBalance()
		{ return balance; }

	public float getVolume()
		{ return volume; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + partialSoundPath + "}"; }

	@Override
	public PlaySound getNewInstanceOfThis()
		{ return new PlaySound(partialSoundPath, rate, balance, volume); }
	
	@Override
	public void process(Sprite sprite) {
		if (!Main.spriteEditor || !FrameSetEditor.isPaused) {
			MediaPlayer mp = Sound.playSound(partialSoundPath);
			if (getRate() != 1)
				mp.setRate(getRate());
			if (getBalance() != 1)
				mp.setBalance(getBalance());
			if (getVolume() != 1)
				mp.setVolume(getVolume());
		}
	}

}
