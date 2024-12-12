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

public class Ride extends Entity {
	
	/* 
	 * OVO NAO ANDA DURANTE O PULO
	 * Arrumar os framesets da montaria para posicionar o personagem corretamente
	 * Habilitar os speciais da montaria
	 * Adicionar as outras montarias
	 * TESTAR morte pelo hurryup enquanto esta na montaria
	 * FAZER o hurry up matar mesmo na montaria
	 * Criar FrameSet da montaria morrendo sem ser por Fire
	 * Criar FrameSet da montaria em pose de vitoria
	 * Criar FrameSet da montaria em idle
	 */
	
	private RideType rideType;
	private BomberMan owner;
	private int palleteIndex;
	private boolean disabled;
	private Position riderDesloc;
	private int riderFrontValue;

	private static List<Ride> rideList = new ArrayList<>();

	public static List<Ride> getRides() {
		return rideList;
	}

	public static Ride addRide(TileCoord coord, RideType rideType, int palleteIndex) {
		return addRide(coord, rideType, palleteIndex, null);
	}

	public static Ride addRide(TileCoord coord, RideType rideType, int palleteIndex, BomberMan owner) {
		Ride ride = new Ride(coord, rideType, palleteIndex, owner);
		rideList.add(ride);
		return ride;
	}

	public static void removeRide(Ride ride) {
		rideList.remove(ride);		
	}

	public Ride(TileCoord coord, RideType rideType, int palleteIndex) {
		this(coord, rideType.getValue(), palleteIndex, null);
	}

	public Ride(TileCoord coord, RideType rideType, int palleteIndex, BomberMan owner) {
		this(coord, rideType.getValue(), palleteIndex, owner);
	}

	public Ride(TileCoord coord, int rideId, int palleteIndex) {
		this(coord, rideId, palleteIndex, null);
	}

	public Ride(TileCoord coord, int rideId, int palleteIndex, BomberMan owner) {
		this.setPosition(coord.getPosition());
		this.owner = owner;
		this.rideType = RideType.getRideTypeById(rideId);
		this.palleteIndex = palleteIndex;
		riderDesloc = new Position();
		disabled = false;
		String section = "" + rideType.getValue();
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
				if (!isDisabled() && bomber.isWaitingForRide() == this && getTileCoordFromCenter().equals(bomber.getTileCoordFromCenter())) {
					setOwner(bomber);
					bomber.jumpTo(bomber, getTileCoordFromCenter(), 4, 1.2, 40);
					break;
				}
		super.run(gc, isPaused);
		// FALTA: Adicionar triggers de tile ao pisar no tile de montaria
	}

	public RideType getRideType() {
		return rideType;
	}

	public BomberMan getOwner() {
		return owner;
	}

	public void setOwner(BomberMan owner) {
		this.owner = owner;
		disabled = owner == null;
	}

	public int getPalleteIndex() {
		return palleteIndex;
	}
	
	public boolean isDisabled() {
		return disabled;
	}

}
