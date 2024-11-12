package frameset_tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import frameset.Sprite;
import objmoveutils.TileCoord;

public abstract class FrameTag {

	// Mudar esse valor para 'true' no construtor da classe que herda FrameTag, se
	// for uma Tag que só precisa ser lida uma unica vez.
	public boolean deleteMeAfterFirstRead = false;
	int triggerDelay = 0;
	Map<String, Double> vars = new HashMap<>();

	public abstract FrameTag getNewInstanceOfThis();

	public abstract void process(Sprite sprite);

	public int getTriggerDelay() {
		return triggerDelay;
	}

	public void setTriggerDelay(int delay) {
		triggerDelay = delay;
	}

	static <T> String[] validateStringTags(T clazz, String tags) {
		return validateStringTags(clazz, tags, -1);
	}

	/*
	 * PRE_CARREGAR OS FRAMESETS
	 * ITEM NAO TA ALTERANDO IMAGEM
	 * 
	 */
	
	static <T> String[] validateStringTags(T clazz, String tags, int totalParams) {
		String thisClass = getClassName(clazz);
		if (tags.length() < thisClass.length() + 2 || tags.charAt(0) != '{' || tags.charAt(tags.length() - 1) != '}')
			throw new RuntimeException(tags + " - Invalid tags");
		tags = tags.substring(1, tags.length() - 1);
		String[] split = tags.split(";");
		if (!split[0].equals(thisClass))
			throw new RuntimeException(tags + " - Invalid tags");
		String[] attribs = new String[split.length - 1];
		if (totalParams != -1) {
			if (attribs.length > totalParams)
				throw new RuntimeException(tags + " - Too much parameters");
			if (attribs.length < totalParams)
				throw new RuntimeException(tags + " - Too few parameters");
		}
		for (int n = 1; n < split.length; n++)
			attribs[n - 1] = split[n];
		return attribs;
	}

	static <T> String getClassName(T clazz) {
		return clazz.getClass().getSimpleName();
	}

	public static FrameTag getFrameTagClassFromString(List<FrameTag> list, String frameTagClassName) {
		for (FrameTag obj : list)
			if (obj.getClass().getSimpleName().equals(frameTagClassName))
				return obj;
		return null;
	}

	// Formato da string: X:Y onde se X for --X ou ++X, o valor X,Y de TileCoord2 é
	// atualizado para a coordenada do tile que disparou o evento, e offsetX,offsetY
	// é definido com os valores informados na String
	static TileCoord2 stringToTileCoord2(String str) {
		String[] split = str.split(":");
		TileCoord2 coord = new TileCoord2();
		coord.setOriginalTag(str);
		for (int n = 0; n < 2; n++) {
			String s = split[n];
			if (s.length() > 2 && (s.subSequence(0, 2).equals("--") || s.subSequence(0, 2).equals("++"))) {
				if (n == 0) {
					coord.setX(-1);
					coord.setOffsetX(Integer.parseInt(s.substring(1)));
				}
				else {
					coord.setY(-1);
					coord.setOffsetY(Integer.parseInt(s.substring(1)));
				}
			}
			else {
				try {
					if (n == 0)
						coord.setX(s.equals("-") ? -1 : Integer.parseInt(s));
					else
						coord.setY(s.equals("-") ? -1 : Integer.parseInt(s));
				}
				catch (Exception e) {
					return null;
				}
			}
		}
		return coord;
	}

	static String tileCoord2ListToString(List<TileCoord2> coords) {
		StringBuilder sb = new StringBuilder();
		for (TileCoord2 coord : coords) {
			if (!sb.isEmpty())
				sb.append("!");
			sb.append(coord.getOriginalTag());
		}
		return sb.toString();
	}

	// Formato da String: X1:Y1!X2:Y2!X3:Y3...
	static List<TileCoord2> stringToTileCoord2List(String coords) {
		if (coords == null) {
			TileCoord2 coord = new TileCoord2(-1, -1, 0, 0);
			coord.setOriginalTag("");
			return new ArrayList<>(Arrays.asList(coord));
		}
		List<TileCoord2> coordList = new ArrayList<>();
		String[] split = coords.split("!");
		for (String s : split)
			coordList.add(stringToTileCoord2(s));
		return coordList;
	}

	static void processTile(TileCoord whoActivatedCoord, List<TileCoord2> coords, Consumer<TileCoord> consumer) {
		for (TileCoord2 coord2 : coords)
			processTile(whoActivatedCoord, coord2, consumer);
	}

	static void processTile(TileCoord whoActivatedCoord, TileCoord2 coord, Consumer<TileCoord> consumer) {
		int tx = coord.getX(), ty = coord.getY();
		if (tx == -1 || ty == -1) {
			tx = tx == -1 ? whoActivatedCoord.getX() : tx;
			ty = ty == -1 ? whoActivatedCoord.getY() : ty;
		}
		consumer.accept(new TileCoord(tx + coord.getOffsetX(), ty + coord.getOffsetY()));
	}

}

class TileCoord2 extends TileCoord {

	private int offsetX;
	private int offsetY;
	private String originalTag;

	public TileCoord2() {
		this(0, 0, 0, 0);
	}

	public TileCoord2(TileCoord2 tileCoord2) {
		this(tileCoord2.getX(), tileCoord2.getY(), tileCoord2.offsetX, tileCoord2.offsetY);
	}

	public TileCoord2(int x, int y, int offsetX, int offsetY) {
		super(x, y);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	public TileCoord2 getNewInstance() {
		TileCoord2 coord = new TileCoord2(getX(), getY(), offsetX, offsetY);
		coord.setOriginalTag(getOriginalTag());
		return coord;
	}

	public String getOriginalTag() {
		return originalTag;
	}

	public void setOriginalTag(String originalTag) {
		this.originalTag = originalTag;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	public void setOffset(int offsetX, int offsetY) {
		setOffsetX(offsetX);
		setOffsetY(offsetY);
	}

	public void setOffset(TileCoord2 tileCoord2) {
		setOffset(tileCoord2.getOffsetX(), tileCoord2.getOffsetY());
	}

	public TileCoord getTileCoord() {
		return (TileCoord) this;
	}

	@Override
	public String toString() {
		return "TileCoord2 [" + getX() + "," + getY() + "] [" + offsetX + "," + offsetY + "]";
	}

}