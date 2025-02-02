package frameset_tags;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import frameset.Sprite;
import frameset.Tags;
import maps.MapSet;
import maps.Tile;
import objmoveutils.TileCoord;

public class CopySprFromCopyLayer extends FrameTag {

	public int targetLayer;
	public Rectangle copyArea;
	public List<TileCoord2> targetCoords;

	// Valores tem que ser no formato coordenada de tile tanto para area e tamanho
	// de origem quanto para area de destino
	public CopySprFromCopyLayer(int layer, Rectangle copyArea, List<TileCoord2> targetCoords) {
		targetLayer = layer;
		this.copyArea = new Rectangle(copyArea);
		this.targetCoords = new ArrayList<>(targetCoords);
	}

	public CopySprFromCopyLayer(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags);
		if (params.length > 6)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 3)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			targetLayer = Integer.parseInt(params[n]);
			copyArea = new Rectangle(Integer.parseInt(params[++n]), Integer.parseInt(params[++n]), params.length < 4 ? 1 : Integer.parseInt(params[++n]), params.length < 4 ? 1 : Integer.parseInt(params[++n]));
			targetCoords = stringToTileCoord2List(++n >= params.length ? null : params[n]);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public CopySprFromCopyLayer getNewInstanceOfThis() {
		return new CopySprFromCopyLayer(targetLayer, copyArea, targetCoords);
	}

	@Override
	public void process(Sprite sprite) {
		processTile(sprite.getTileCoordFromCenter(), targetCoords, coord -> {
			for (int y = 0; y < (int) copyArea.getHeight(); y++)
				for (int x = 0; x < (int) copyArea.getWidth(); x++) {
					TileCoord sourceCoord = new TileCoord((int) copyArea.getX() + x, (int) copyArea.getY() + y);
					TileCoord targetCoord = new TileCoord(coord.getX() + x, coord.getY() + y);
					if (MapSet.getLayer(targetLayer).haveTilesOnCoord(targetCoord))
						MapSet.getLayer(targetLayer).removeAllTilesFromCoord(targetCoord);
					for (Tile tile : MapSet.getCopyLayer().getTilesFromCoord(sourceCoord))
						MapSet.getLayer(targetLayer).addTile(new Tile(tile, MapSet.getLayer(targetLayer)), targetCoord);
					MapSet.getLayer(targetLayer).buildLayer();
					if (MapSet.getCopyLayer().tileHaveTags(sourceCoord)) {
						Tags tags = MapSet.getCopyLayer().getTileTags(sourceCoord);
						MapSet.getLayer(targetLayer).setTileTags(targetCoord, new Tags(tags));
					}
					else
						MapSet.getLayer(targetLayer).clearTileTags(targetCoord);
					MapSet.getLayer(targetLayer).setTileProps(targetCoord, MapSet.getCopyLayer().getTileProps(sourceCoord));
				}
		});
	}

}