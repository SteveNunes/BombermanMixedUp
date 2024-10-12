package frameset_tags;

import java.awt.Rectangle;

import application.Main;
import entities.TileCoord;
import frameset.Sprite;
import maps.MapSet;
import maps.Tile;

public class CopySprFromCopyLayer extends FrameTag {
	
	public Rectangle copyArea;
	public TileCoord destination;
	
	// Valores tem que ser no formato coordenada de tile tanto para area e tamanho de origem quanto para area de destino
	public CopySprFromCopyLayer(int sx, int sy, int sw, int sh, int tx, int ty) {
		copyArea = new Rectangle(sx, sy, sw, sh);
		destination = new TileCoord(tx, ty);
	}

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + (int)copyArea.getX() + ";" + (int)copyArea.getY() + ";" + (int)copyArea.getWidth() + ";" + (int)copyArea.getHeight() + ";" + destination.getX() + ";" + destination.getY() + "}"; }

	public CopySprFromCopyLayer(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 6)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 2)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			int sx = Integer.parseInt(params[n++]), sy = Integer.parseInt(params[n++]),
					sw = params.length < 5 ? 1 : Integer.parseInt(params[n++]),
					sh = params.length < 5 ? 1 : Integer.parseInt(params[n++]),
					tx = params.length < 5 || params[n++].equals("-") ? -1 : Integer.parseInt(params[n]),
					ty = params.length < 5 || params[n++].equals("-") ? -1 : Integer.parseInt(params[n]);
			copyArea = new Rectangle(sx, sy, sw, sh);
			destination = new TileCoord(tx, ty);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public CopySprFromCopyLayer getNewInstanceOfThis()
		{ return new CopySprFromCopyLayer((int)copyArea.getX(), (int)copyArea.getY(), (int)copyArea.getWidth(), (int)copyArea.getHeight(), destination.getX(), destination.getY()); }
	
	@Override
	public void process(Sprite sprite) {
		int sx = (int)copyArea.getX(), sy = (int)copyArea.getY(),
				sw = (int)copyArea.getWidth(), sh = (int)copyArea.getHeight(),
				tx = destination.getX(), ty = destination.getY();
		if (tx == -1 && ty == -1) {
			TileCoord coord = sprite.getTileCoord();
			tx = coord.getX();
			ty = coord.getY();
		}
		for (int y = 0; y < sh; y++)
			for (int x = 0; x < sw; x++) {
				TileCoord sourceCoord = new TileCoord(sx + x, sy + y);
				TileCoord targetCoord = new TileCoord(tx + x, ty + y);
				MapSet.getLayer(26).removeAllTilesFromCoord(targetCoord);
				for (Tile tile :MapSet.getLayer(27).getTilesFromCoord(sourceCoord)) {
					Tile tile2 = new Tile(tile);
					tile2.outX = targetCoord.getX() * Main.TILE_SIZE;
					tile2.outY = targetCoord.getY() * Main.TILE_SIZE;
					MapSet.getLayer(26).addTile(tile2);
				}
			}
	}

}












