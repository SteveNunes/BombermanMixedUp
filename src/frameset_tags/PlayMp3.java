package frameset_tags;

import application.Main;
import entities.Sprite;
import enums.GameMode;
import tools.FrameSetEditor;
import tools.Sound;

public class PlayMp3 extends FrameTag {
	
	private String partialSoundPath;
	private double rate;
	private double balance;
	private double volume;
	private boolean stopCurrent;
	
	public PlayMp3(String partialSoundPath, double rate, double balance, double volume, boolean stopCurrent) {
		this.partialSoundPath = partialSoundPath;
		this.rate = rate;
		this.balance = balance;
		this.volume = volume;
		this.stopCurrent = stopCurrent;
	}
	
	public PlayMp3(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 6)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 1)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			partialSoundPath = params[n];
			n++; rate = params.length <= n || params[n].equals("-") ? 1 : Double.parseDouble(params[n]);
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

	public double getBalance()
		{ return balance; }

	public double getVolume()
		{ return volume; }

	public boolean getStopCurrent()
		{ return stopCurrent; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + partialSoundPath + ";" + rate + ";" + balance + ";" + volume + ";" + stopCurrent + "}"; }

	@Override
	public PlayMp3 getNewInstanceOfThis()
		{ return new PlayMp3(partialSoundPath, rate, balance, volume, stopCurrent); }
	
	@Override
	public void process(Sprite sprite) {
		if (Main.mode == GameMode.GAME || !FrameSetEditor.isPaused) {
			Sound.playMp3(partialSoundPath, getRate(), getBalance(), getVolume(), getStopCurrent());
		}
	}

}
