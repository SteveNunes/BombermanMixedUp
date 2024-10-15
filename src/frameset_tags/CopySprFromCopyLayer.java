package frameset_tags;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import entities.TileCoord;
import frameset.Sprite;
import maps.MapSet;
import maps.Tile;

public class CopySprFromCopyLayer extends FrameTag {
	
	public int targetLayer;
	public Rectangle copyArea;
	public List<TileCoord2> targetCoords;
	
	// Valores tem que ser no formato coordenada de tile tanto para area e tamanho de origem quanto para area de destino
	public CopySprFromCopyLayer(int layer, Rectangle copyArea, List<TileCoord2> targetCoords) {
		targetLayer = layer;
		this.copyArea = new Rectangle(copyArea);
		this.targetCoords = new ArrayList<>(targetCoords);
	}

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + targetLayer + ";" + (int)copyArea.getX() + ";" + (int)copyArea.getY() + ";" + (int)copyArea.getWidth() + ";" + (int)copyArea.getHeight() + ";" + FrameTag.tileCoord2ListToString(targetCoords) + "}"; }

	public CopySprFromCopyLayer(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 6)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 3)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			targetLayer = Integer.parseInt(params[n]);
			copyArea = new Rectangle(Integer.parseInt(params[++n]), Integer.parseInt(params[++n]),
					params.length < 4 ? 1 : Integer.parseInt(params[++n]),
					params.length < 4 ? 1 : Integer.parseInt(params[++n]));
			targetCoords = FrameTag.stringToTileCoord2List(++n >= params.length ? null : params[n]);
		}
		catch (Exception e)
			{ e.printStackTrace(); throw new RuntimeException(params[n] + " - Invalid parameter"); }
	}

	@Override
	public CopySprFromCopyLayer getNewInstanceOfThis()
		{ return new CopySprFromCopyLayer(targetLayer, copyArea, targetCoords); }
	
	@Override
	public void process(Sprite sprite) {
		FrameTag.processTile(sprite, targetCoords, coord -> {
			for (int y = 0; y < (int)copyArea.getHeight(); y++)
				for (int x = 0; x < (int)copyArea.getWidth(); x++) {
					TileCoord sourceCoord = new TileCoord((int)copyArea.getX() + x, (int)copyArea.getY() + y);
					TileCoord targetCoord = new TileCoord(coord.getX() + x, coord.getY() + y);
					MapSet.getLayer(targetLayer).removeAllTilesFromCoord(targetCoord);
					for (Tile tile : MapSet.getCopyLayer().getTilesFromCoord(sourceCoord))
						MapSet.getLayer(targetLayer).addTile(new Tile(tile), targetCoord);
					MapSet.getLayer(targetLayer).buildLayer();
				}
		});
	}

}