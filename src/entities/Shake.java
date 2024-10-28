package entities;

import util.MyMath;

public class Shake {
	
	private Double startStrengthX;
	private Double startStrengthY;
	private Double strengthX;
	private Double strengthY;
	private Double incStrengthX;
	private Double incStrengthY;
	private Double finalStrengthX;
	private Double finalStrengthY;
	private int x, y;
	
	public Shake(Shake shake) {
		startStrengthX = shake.startStrengthX;
		startStrengthY = shake.startStrengthY;
		strengthX = shake.startStrengthX;
		strengthY = shake.startStrengthX;
		incStrengthX = shake.incStrengthX;
		incStrengthY = shake.incStrengthY;
		finalStrengthX = shake.finalStrengthX;
		finalStrengthY = shake.finalStrengthY;
		updateVals();
	}

	public Shake(Double incStrength, Double finalStrength)
		{ this(incStrength, incStrength, finalStrength, finalStrength);	}

	public Shake(Double startStrength, Double incStrength, Double finalStrength)
		{ this(startStrength, startStrength, incStrength, incStrength, finalStrength, finalStrength); }

	public Shake(Double incStrengthX, Double incStrengthY, Double finalStrengthX, Double finalStrengthY)
		{ this(null, null, incStrengthX, incStrengthY, finalStrengthX, finalStrengthY);	}
	
	public Shake(Double startStrengthX, Double startStrengthY, Double incStrengthX, Double incStrengthY, Double finalStrengthX, Double finalStrengthY) {
		this.startStrengthX = startStrengthX != null ? startStrengthX : incStrengthX > 0 ? 0 : finalStrengthX;
		this.startStrengthY = startStrengthY != null ? startStrengthY : incStrengthY > 0 ? 0 : finalStrengthY;
		this.strengthX = this.startStrengthX;
		this.strengthY = this.startStrengthX;
		this.incStrengthX = incStrengthX;
		this.incStrengthY = incStrengthY;
		this.finalStrengthX = finalStrengthX;
		this.finalStrengthY = finalStrengthY;
		System.out.println(this.startStrengthX + " " + this.startStrengthX + " " + this.incStrengthX + " " + this.incStrengthY + " " + this.finalStrengthX + " " + this.finalStrengthY);
		updateVals();
	}
	
	public void proccess() {
		if (isActive()) {
			if (strengthX != null) {
				strengthX += incStrengthX;
				if ((incStrengthX > 0 && strengthX >= finalStrengthX) || (incStrengthX < 0 && strengthX <= finalStrengthX))
					strengthX = null;
			}
			if (strengthY != null) {
				strengthY += incStrengthY;
				if ((incStrengthY > 0 && strengthY >= finalStrengthY) || (incStrengthY < 0 && strengthY <= finalStrengthY))
					strengthY = null;
			}
			updateVals();
		}
	}
	
	public boolean isActive()
		{ return strengthX != null || strengthY != null; }
	
	private void updateVals() {
		x = strengthX == null ? 0 : strengthX.intValue() - (int)MyMath.getRandom(0, strengthX.intValue() * 2);
		y = strengthY == null ? 0 : strengthY.intValue() - (int)MyMath.getRandom(0, strengthY.intValue() * 2);
	}
	
	public int getX()
		{ return x; }

	public int getY()
		{ return y; }

	public void reset() {
		strengthX = startStrengthX;
		strengthY = startStrengthY;
	}

	public void stop() {
		strengthX = null;
		strengthY = null;
	}

}
