package tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import application.Main;
import javafx.scene.text.Font;

public abstract class GameFonts {
	
	public static Font fontBomberMan20;
	public static Font fontBomberMan40;
	public static Font fontBomberMan60;
	
	public static void loadFonts() {
    InputStream fontStream;
		try {
			fontStream = new FileInputStream(new File("appdata/fonts/Bomberman.ttf"));
			fontBomberMan20 = Font.loadFont(fontStream, 20);
			fontStream = new FileInputStream(new File("appdata/fonts/Bomberman.ttf"));
			fontBomberMan40 = Font.loadFont(fontStream, 40);
			fontStream = new FileInputStream(new File("appdata/fonts/Bomberman.ttf"));
			fontBomberMan60 = Font.loadFont(fontStream, 60);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			Main.close();
		}
	}


}
