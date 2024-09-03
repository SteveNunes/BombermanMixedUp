package frameset_tags;

import application.Main;
import entities.Sprite;
import enums.GameMode;
import tools.FrameSetEditor;
import tools.Sound;

public class PlayWav extends FrameTag {
	
	private String partialSoundPath;
	private double rate;
	private double pan;
	private double balance;
	private double volume;
	private boolean stopCurrent;
	
	public PlayWav(String partialSoundPath, double rate, double pan, double balance, double volume, boolean stopCurrent) {
		this.partialSoundPath = partialSoundPath;
		this.rate = rate;
		this.pan = pan;
		this.balance = balance;
		this.volume = volume;
		this.stopCurrent = stopCurrent;
	}
	
	public PlayWav(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 6)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 1)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			partialSoundPath = params[n];
			n++; rate = params.length <= n || params[n].equals("-") ? 1 : Double.parseDouble(params[n]);
			n++; pan = params.length <= n || params[n].equals("-") ? 1 : Double.parseDouble(params[n]);
			n++; balance = params.length <= n || params[n].equals("-") ? 1 : Double.parseDouble(params[n]);
			n++; volume = params.length <= n || params[n].equals("-") ? 1 : Double.parseDouble(params[n]);
			n++; stopCurrent = params.length <= n || params[n].equals("-") ? false : Boolean.parseBoolean(params[n]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[n] + " - Invalid parameter"); }
	}

	public String getPartialSoundPath()
		{ return partialSoundPath; }
	
	public double getRate()
		{ return rate; }

	public double getPan()
		{ return pan; }

	public double getBalance()
		{ return balance; }

	public double getVolume()
		{ return volume; }

	public boolean getStopCurrent()
		{ return stopCurrent; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + partialSoundPath + ";" + rate + ";" + pan + ";" + balance + ";" + volume + ";" + stopCurrent + "}"; }

	@Override
	public PlayWav getNewInstanceOfThis()
		{ return new PlayWav(partialSoundPath, rate, pan, balance, volume, stopCurrent); }
	
	@Override
	public void process(Sprite sprite) {
		if (Main.mode == GameMode.GAME || !FrameSetEditor.isPaused) {
			Sound.playWav(partialSoundPath, getRate(), getPan(), getBalance(), getVolume(), getStopCurrent());
		}
	}

}
