package frameset_tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import entities.TileCoord;
import frameset.Sprite;

public abstract class FrameTag {
	
	// Mudar esse valor para 'true' no construtor da classe que herda FrameTag, se for uma Tag que só precisa ser lida uma unica vez.
	public boolean deleteMeAfterFirstRead = false;
	int triggerDelay = 0;
	Map<String, Double> vars = new HashMap<>();
	
	public abstract FrameTag getNewInstanceOfThis();

	public abstract void process(Sprite sprite);
	
	public int getTriggerDelay()
		{ return triggerDelay; }
	
	public void setTriggerDelay(int delay)
		{ triggerDelay = delay; }
	
	static <T> String[] validateStringTags(T clazz, String tags)
		{ return validateStringTags(clazz, tags, -1); }
	
	static <T> String[] validateStringTags(T clazz, String tags, int totalParams) {
		String thisClass = getClassName(clazz);
		if (tags.length() < thisClass.length() + 2 ||
				tags.charAt(0) != '{' || tags.charAt(tags.length() - 1) != '}')
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
	
	static <T> String getClassName(T clazz)
		{ return clazz.getClass().toString().replace("class frameset_tags.", ""); }
	
	// Formato da string: X:Y onde se X for --X ou ++X, o valor X,Y de TileCoord2 é atualizado para a coordenada do tile que disparou o evento, e offsetX,offsetY é definido com os valores informados na String
	static TileCoord2 getCoord2FromString(String str) {
		String[] split = str.split(":");
		TileCoord2 coord = new TileCoord2();
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
				catch (Exception e)
					{ return null; }
			}
		}
		return coord;
	}

	static String tileCoord2ListToString(List<TileCoord2> coords) {
		StringBuilder sb = new StringBuilder();
		for (TileCoord2 pos : coords) {
			if (!sb.isEmpty())
				sb.append("!");
			sb.append(pos.getX());
			sb.append(":");
			sb.append(pos.getY());
			sb.append(":");
			sb.append(pos.getOffsetX());
			sb.append(":");
			sb.append(pos.getOffsetY());
		}
		return sb.toString();
	}

	// Formato da String: X1:Y1!X2:Y2!X3:Y3...
	static List<TileCoord2> stringToTileCoord2List(String coords) {
		if (coords == null)
			return new ArrayList<>(Arrays.asList(new TileCoord2(-1, -1, 0, 0)));
		List<TileCoord2> coordList = new ArrayList<>();
		String[] split = coords.split("!");
		for (String s : split)
			coordList.add(getCoord2FromString(s));
		return coordList;
	}
	
	static void processTile(Sprite sprite, List<TileCoord2> tileCoords, Consumer<TileCoord> consumer) {
		for (TileCoord2 coord : tileCoords) {
			int tx = coord.getX(), ty = coord.getY();
			if (tx == -1 || ty == -1) {
				if (sprite == null)
					throw new RuntimeException("sprite is null (This FrameTag becames from StageTag instead of a tile tag, so you must provide 'width;height;targetX;targetY' params)");
				TileCoord coord2 = sprite.getTileCoord();
				tx = tx == -1 ? coord2.getX() : tx;
				ty = ty == -1 ? coord2.getY() : ty;
			}
			consumer.accept(new TileCoord(tx + coord.getOffsetX(), ty + coord.getOffsetY()));
		}
	}
	
}