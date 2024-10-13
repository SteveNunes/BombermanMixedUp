package frameset_tags;

import java.awt.Rectangle;

import entities.TileCoord;
import frameset.Sprite;
import maps.MapSet;
import maps.Tile;

public class CopySprFromCopyLayer extends FrameTag {
	
	public int targetLayer;
	public Rectangle copyArea;
	public TileCoord targetCoord;
	int offsetX;
	int offsetY;
	
	// Valores tem que ser no formato coordenada de tile tanto para area e tamanho de origem quanto para area de destino
	public CopySprFromCopyLayer(int sx, int sy, int sw, int sh, int layer, int tx, int ty, int offsetX, int offsetY) {
		targetLayer = layer;
		copyArea = new Rectangle(sx, sy, sw, sh);
		targetCoord = new TileCoord(tx, ty);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + targetLayer + ";" + (int)copyArea.getX() + ";" + (int)copyArea.getY() + ";" + (int)copyArea.getWidth() + ";" + (int)copyArea.getHeight() + ";" + targetCoord.getX() + ";" + targetCoord.getY() + "}"; }

	public CopySprFromCopyLayer(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 7)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 3)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			int layer = Integer.parseInt(params[n]); n++;
			int sx = Integer.parseInt(params[n]); n++;
			int	sy = Integer.parseInt(params[n]); n++;
			int	sw = params.length < 4 ? 1 : Integer.parseInt(params[n]); n++;
			int	sh = params.length < 4 ? 1 : Integer.parseInt(params[n]); n++;
			int[] pos = params.length <= n ? null : FrameTag.getPosWithDeslocFromString(params[n]);
			int tx = pos == null ? -1 : pos[0];
			offsetX = pos == null ? 0 : pos[1];
			n++;
			pos = params.length <= n ? null : FrameTag.getPosWithDeslocFromString(params[n]);
			int ty = pos == null ? -1 : pos[0];
			offsetY = pos == null ? 0 : pos[1];
			targetLayer = layer;
			copyArea = new Rectangle(sx, sy, sw, sh);
			targetCoord = new TileCoord(tx, ty);
		}
		catch (Exception e)
			{ e.printStackTrace(); throw new RuntimeException(params[n] + " - Invalid parameter"); }
	}

	@Override
	public CopySprFromCopyLayer getNewInstanceOfThis()
		{ return new CopySprFromCopyLayer((int)copyArea.getX(), (int)copyArea.getY(), (int)copyArea.getWidth(), (int)copyArea.getHeight(), targetLayer, targetCoord.getX(), targetCoord.getY(), offsetX, offsetY); }
	
	@Override
	public void process(Sprite sprite) {
		int sx = (int)copyArea.getX(), sy = (int)copyArea.getY(),
				sw = (int)copyArea.getWidth(), sh = (int)copyArea.getHeight(),
				tx = targetCoord.getX(), ty = targetCoord.getY();
		if (tx == -1 && ty == -1) {
			TileCoord coord = sprite.getTileCoord();
			tx = coord.getX();
			ty = coord.getY();
		}
		for (int y = 0; y < sh; y++)
			for (int x = 0; x < sw; x++) {
				TileCoord sourceCoord = new TileCoord(sx + x, sy + y);
				TileCoord targetCoord = new TileCoord(tx + x + offsetX, ty + y + offsetY);
				MapSet.getLayer(targetLayer).removeAllTilesFromCoord(targetCoord);
				for (Tile tile : MapSet.getCopyLayer().getTilesFromCoord(sourceCoord))
					MapSet.getLayer(targetLayer).addTile(new Tile(tile), targetCoord);
				MapSet.getLayer(targetLayer).buildLayer();
			}
	}

}