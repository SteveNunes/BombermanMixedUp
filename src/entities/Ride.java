package entities;

import java.util.ArrayList;
import java.util.List;

import enums.Direction;
import enums.RideType;
import frameset.Tags;
import javafx.scene.canvas.GraphicsContext;
import objmoveutils.Position;
import objmoveutils.TileCoord;
import tools.IniFiles;
import tools.Sound;

public class Ride extends Entity {
	
	/* 
	 * Adicionar as outras montarias
	 * 
	 * Quando aperta o botao de chutar e ja segura pro lado oposto, ele chuta denovo pro lado oposto, apos terminar o chute atual
	 * Nao da para chutar a bomba quando ela ta colada de uma pareed (ela pula e cai no mesmo tile)
	 * Ovo nao eh destruido quando bomba cai em cima
	 * Criar FrameSet da montaria em pose de vitoria
	 * Criar FrameSet da montaria em pose de derrota
	 * Criar FrameSet da montaria em pose de on-edge
	 */
	
	private RideType rideType;
	private BomberMan owner;
	private int palleteIndex;
	private boolean isDead;
	private Position riderDesloc;
	private int riderFrontValue;
	private String ridingSound;

	private static List<Ride> rideList = new ArrayList<>();

	public static List<Ride> getRides() {
		return rideList;
	}

	public static Ride addRide(TileCoord coord, RideType rideType, int palleteIndex) {
		Ride ride = new Ride(coord, rideType, palleteIndex);
		rideList.add(ride);
		return ride;
	}

	public static void removeRide(Ride ride) {
		rideList.remove(ride);		
	}

	public Ride(TileCoord coord, RideType rideType, int palleteIndex) {
		this(coord, rideType.getValue(), palleteIndex);
	}

	public Ride(TileCoord coord, int rideId, int palleteIndex) {
		this.setPosition(coord.getPosition());
		this.rideType = RideType.getRideTypeById(rideId);
		this.palleteIndex = palleteIndex;
		owner = null;
		riderDesloc = new Position();
		isDead = false;
		String section = "" + rideType.getValue();
		ridingSound = IniFiles.rides.read(section, "RiringSound");
		if (IniFiles.rides.read(section, "DefaultTags") != null)
			setDefaultTags(Tags.loadTagsFromString(IniFiles.rides.read(section, "DefaultTags")));
		for (String item : IniFiles.rides.getItemList(section))
			if (item.length() > 9 && item.substring(0, 9).equals("FrameSet."))
				addNewFrameSetFromIniFile(this, item.substring(9), "Rides", section, item);
		setDirection(Direction.DOWN);
		setFrameSet("Stand");
	}
	
	public int getRiderFrontValue() {
		return riderFrontValue;
	}

	public void setRiderFrontValue(int value) {
		riderFrontValue = value;
	}

	public Position getRiderDesloc() {
		return riderDesloc;
	}

	public void setRiderDesloc(int x, int y) {
		riderDesloc.setPosition(x, y);
	}

	public void setRiderDesloc(Position position) {
		riderDesloc.setPosition(position);
	}

	public static void drawRides() {
		for (Ride ride : new ArrayList<>(rideList))
		ride.run();
	}
	
	@Override
	public void run() {
		run(null, false);
	}

	@Override
	public void run(boolean isPaused) {
		run(null, isPaused);
	}

	@Override
	public void run(GraphicsContext gc) {
		run(gc, false);
	}

	@Override
	public void run(GraphicsContext gc, boolean isPaused) {
		if (getOwner() != null) {
			if (getOwner().getRide() == this) { // Isso impede de atualizar a posicao da montaria enquanto o personagem ainda esta pulando para pegar ela
				setPosition(getOwner().getPosition());
				if (!getOwner().isBlockedMovement())
					forceDirection(getOwner().getDirection());
			}
		}
		else
			for (BomberMan bomber : BomberMan.getBomberManList())
				if (!isDead() && bomber.isWaitingForRide() == this && getTileCoordFromCenter().equals(bomber.getTileCoordFromCenter())) {
					setOwner(bomber);
					bomber.jumpTo(bomber, getTileCoordFromCenter(), 4, 1.2, 40);
					if (ridingSound != null)
						Sound.playWav(ridingSound);
					break;
				}
		super.run(gc, isPaused);
		if (!getCurrentFrameSet().isRunning())
			getOwner().changeToStandFrameSet();
	}

	public RideType getRideType() {
		return rideType;
	}

	public BomberMan getOwner() {
		return owner;
	}

	public void setOwner(BomberMan owner) {
		this.owner = owner;
		isDead = owner == null;
	}

	public int getPalleteIndex() {
		return palleteIndex;
	}
	
	public boolean isDead() {
		return isDead;
	}

}
