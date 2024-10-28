package enums;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TileProp {
	
	NOTHING(0), // Tiles que não tem função alguma
	GROUND(1), // Chão normal, qualquer coisa passa por cima
	GROUND_HOLE(2), // Buraco (Só pode atravessar voando ou pulando, explosão passa por cima)
	MAX_SCREEN_TILE_LIMITER(3), // Tile que define o limite máximo de tiles do mapa (objetos que passarem X ou Y desse tile são teleportados para o lado oposto da tela. Também serve para limitar o scroll da tela em mapas que tem conteudos fora da tela, como plataformas flutuantes
	GROUND_NO_PLAYER(5), // Chão normal, que player não passa
	GROUND_NO_MOB(6), // Chão normal, que mob não passa
	GROUND_NO_BOMB(7), // Chão normal, que não pode por bomba
	GROUND_NO_FIRE(8), // Chão normal, que não passa explosão
	GROUND_NO_ITEM(9), // Chão normal, que não para item que estiver kikando e cair nesse tile
	GROUND_NO_BLOCK(10), // Chão normal, que não para bloco que estiver kikando e cair nesse tile
	JUMP_OVER(11), // O personagem pula por cima desse bloco ao andar na direção dele
	WALL(12), // Parede (Não dá para passar nem pulando)
	HIGH_WALL(13), // Parede (Não dá para passar nem pulando)
	PLAYER_INITIAL_POSITION(14), // Define o tile onde os players vão iniciar nos mapas
	MOB_INITIAL_POSITION(15), // Define o tile onde os mobs vão iniciar nos mapas
	BRICK_RANDOM_SPAWNER(16), // Tile onde pode ser gerado um tijolo aleatoriamente
	FIXED_BRICK(17), // Tile onde deverá haver um tijolo obrigatoriamente
	FIXED_ITEM(18), // Tile onde deverá haver um item (pego em ordem da lista de itens definida no arquivo do mapa em SETUP FixedItemList
	HOLE(19), // Buraco (Só pode atravessar voando ou pulando, explosão passa por cima)
	DEEP_HOLE(20), // Buraco profundo (Se a bomba cair nesse tile, causa o efeito da bomba caindo diminuindo)
	WATER(21), // Água (Igual GROUND, mas gera efeito visual de água cobrindo as pernas)
	DEEP_WATER(22), // Água profunda (Se o personagem cair nesse tile, causa o efeito dele se afogando)
	SLIPPY(23), // Personagem escorrega ao andar em cima
	SPEED_MIN(27), // Deixa a velocidade de movimento no minimo
	SPEED_HALF(28), // Deixa a velocidade de movimento pela metade
	SPEED_NORMAL(29), // Normaliza a velocidade de movimento, caso tenha sido alterada
	SPEED_HIGH(30), // Deixa a velocidade de movimento no dobro
	SPEED_MAX(31), // Deixa a velocidade de movimento no maximo
	SLIMY(32), // Pegajoso (Igual SPEED_MIN, além de não permitir chutar nem agarrar bomba depositada nesse tile)
	PUSH_PLAYER_TO_LEFT(33), // Empurra o personagem para a esquerda
	PUSH_PLAYER_TO_UP(34), // Empurra o personagem para cima
	PUSH_PLAYER_TO_RIGHT(35), // Empurra o personagem para a direita
	PUSH_PLAYER_TO_DOWN(36), // Empurra o personagem para baixo
	TREADMILL_TO_LEFT(37), // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para a esquerda  
	TREADMILL_TO_UP(38), // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para cima  
	TREADMILL_TO_RIGHT(39), // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para a direita  
	TREADMILL_TO_DOWN(40), // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para baixo  
	REDIRECT_BOMB_TO_LEFT(41), // Faz a bomba chutada que passar por esse tile, ir para a esquerda
	REDIRECT_BOMB_TO_UP(42), // Faz a bomba chutada que passar por esse tile, ir para cima
	REDIRECT_BOMB_TO_RIGHT(43), // Faz a bomba chutada que passar por esse tile, ir para a direita
	REDIRECT_BOMB_TO_DOWN(44), // Faz a bomba chutada que passar por esse tile, ir para baixo
	TELEPORT_FROM_FLOATING_PLATFORM(45), // Tile especial que deve ficar ao redor de plataformas flutuantes, que teleporta a bomba ou o player para o tile correspondente como se ele tivesse pulado para fora da plataforma
	MAP_EDGE(46), // Borda do mapa que dá para cair (Personagem fica se equilibrando)
	STAGE_CLEAR(47), // Tile de Stage Clear
	MOVING_BLOCK(48), // Tile do tipo parede, que se move 1 bloco quando recebe uma explosão
	RAIL_UL(52), // Trilho de curva de cima para a direita do BomberKart
	RAIL_UR(53), // Trilho de curva BomberKart
	RAIL_DL(54), // Trilho de curva BomberKart
	RAIL_DR(55), // Trilho de curva BomberKart
	RAIL_H(56), // Trilho horizontal de BomberKart
	RAIL_V(57), // Trilho vertiacl de BomberKart
	RAIL_JUMP(58), // Trilho que faz o BomberKart iniciar/finalizar um salto
	RAIL_START(59), // Trilho de partida do BomberKart
	RAIL_END(60), // Trilho de chegada do BomberKart
	TRIGGER_WHEN_STEP_OUT(64), // Combinado com um TRIGGER_BY_* só dispara o evento quando após pisar no tile, o objeto sair do tile
	TRIGGER_BY_PLAYER(65), // As Tags de tile só são disparadas se o jogador (COM ou SEM montaria) pisar no tile
	TRIGGER_BY_UNRIDE_PLAYER(66), // As Tags de tile só são disparadas se o jogador (SEM montaria) pisar no tile
	TRIGGER_BY_RIDE(67), // As Tags de tile só são disparadas se o jogador (COM montaria) pisar no tile
	TRIGGER_BY_BOMB(68), // As Tags de tile só são disparadas se uma bomba for depositada/chutada no tile
	TRIGGER_BY_STOPPED_BOMB(69), // As Tags de tile só são disparadas se uma bomba for depositada no tile
	TRIGGER_BY_EXPLOSION(70), // As Tags de tile só são disparadas se uma explosão acertar o tile
	TRIGGER_BY_MOB(71), // As Tags de tile só são disparadas se um mob passar pelo tile
	TRIGGER_BY_ITEM(72), // As Tags de tile só são disparadas se um item cair no tile
	TRIGGER_BY_BLOCK(73), // As Tags de tile só são disparadas se um bloco cair no tile
	NO_TRIGGER_WHILE_HAVE_PLAYER(74), // As Tags de tile só são disparadas se não houver player no bloco atual
	NO_TRIGGER_WHILE_HAVE_MOB(75), // As Tags de tile só são disparadas se não houver mob no bloco atual
	NO_TRIGGER_WHILE_HAVE_BRICK(76), // As Tags de tile só são disparadas se não houver tijolo no bloco atual
	NO_TRIGGER_WHILE_HAVE_ITEM(77), // As Tags de tile só são disparadas se não houver item no bloco atual
	NO_TRIGGER_WHILE_HAVE_BOMB(78), // As Tags de tile só são disparadas se não houver bomba no bloco atual
	DAMAGE_PLAYER(79),
	DAMAGE_ENEMY(80),
	DAMAGE_BOMB(81),
	DAMAGE_BRICK(82),
	DAMAGE_ITEM(83);
	// NOTA: VAGOS: 18, 24, 25, 26, 49 50 51 61 62 63
	
	@SuppressWarnings("serial")
	private static Map<Elevation, List<TileProp>> cantCross = new HashMap<>() {{
		put(Elevation.ON_GROUND, Arrays.asList(
				JUMP_OVER,
				WALL,
				HIGH_WALL,
				HOLE,
				DEEP_HOLE,
				WATER,
				DEEP_WATER,
				MAP_EDGE,
				MOVING_BLOCK));
		put(Elevation.FLYING, Arrays.asList(
				JUMP_OVER,
				WALL,
				HIGH_WALL,
				MOVING_BLOCK));
		put(Elevation.HIGH_FLYING, Arrays.asList(HIGH_WALL));
	}};
	
	private int value;
	
	@SuppressWarnings("serial")
	private static Map<Integer, TileProp> propFromValueList = new HashMap<>() {{
		put(0, NOTHING);
		put(1, GROUND);
		put(2, GROUND_HOLE);
		put(3, MAX_SCREEN_TILE_LIMITER);
		put(5, GROUND_NO_PLAYER);
		put(6, GROUND_NO_MOB);
		put(7, GROUND_NO_BOMB);
		put(8, GROUND_NO_FIRE);
		put(9, GROUND_NO_ITEM);
		put(10, GROUND_NO_BLOCK);
		put(11, JUMP_OVER);
		put(12, WALL);
		put(13, HIGH_WALL);
		put(14, PLAYER_INITIAL_POSITION);
		put(15, MOB_INITIAL_POSITION);
		put(16, BRICK_RANDOM_SPAWNER);
		put(17, FIXED_BRICK);
		put(18, FIXED_ITEM);
		put(19, HOLE);
		put(20, DEEP_HOLE);
		put(21, WATER);
		put(22, DEEP_WATER);
		put(23, SLIPPY);
		put(27, SPEED_MIN);
		put(28, SPEED_HALF);
		put(29, SPEED_NORMAL);
		put(30, SPEED_HIGH);
		put(31, SPEED_MAX);
		put(32, SLIMY);
		put(33, PUSH_PLAYER_TO_LEFT);
		put(34, PUSH_PLAYER_TO_UP);
		put(35, PUSH_PLAYER_TO_RIGHT);
		put(36, PUSH_PLAYER_TO_DOWN);
		put(37, TREADMILL_TO_LEFT);
		put(38, TREADMILL_TO_UP);
		put(39, TREADMILL_TO_RIGHT);
		put(40, TREADMILL_TO_DOWN);
		put(41, REDIRECT_BOMB_TO_LEFT);
		put(42, REDIRECT_BOMB_TO_UP);
		put(43, REDIRECT_BOMB_TO_RIGHT);
		put(44, REDIRECT_BOMB_TO_DOWN);
		put(45, TELEPORT_FROM_FLOATING_PLATFORM);
		put(46, MAP_EDGE);
		put(47, STAGE_CLEAR);
		put(48, MOVING_BLOCK);
		put(52, RAIL_UL);
		put(53, RAIL_UR);
		put(54, RAIL_DL);
		put(55, RAIL_DR);
		put(56, RAIL_H);
		put(57, RAIL_V);
		put(58, RAIL_JUMP);
		put(59, RAIL_START);
		put(60, RAIL_END);
		put(64, TRIGGER_WHEN_STEP_OUT);
		put(65, TRIGGER_BY_PLAYER);
		put(66, TRIGGER_BY_UNRIDE_PLAYER);
		put(67, TRIGGER_BY_RIDE);
		put(68, TRIGGER_BY_BOMB);
		put(69, TRIGGER_BY_STOPPED_BOMB);
		put(70, TRIGGER_BY_EXPLOSION);
		put(71, TRIGGER_BY_MOB);
		put(72, TRIGGER_BY_ITEM);
		put(73, TRIGGER_BY_BLOCK);
		put(74, NO_TRIGGER_WHILE_HAVE_PLAYER);
		put(75, NO_TRIGGER_WHILE_HAVE_MOB);
		put(76, NO_TRIGGER_WHILE_HAVE_BRICK);
		put(77, NO_TRIGGER_WHILE_HAVE_ITEM);
		put(78, NO_TRIGGER_WHILE_HAVE_BOMB);
		put(79, DAMAGE_PLAYER);
		put(80, DAMAGE_ENEMY);
		put(81, DAMAGE_BOMB);
		put(82, DAMAGE_BRICK);
		put(83, DAMAGE_ITEM);
	}};
	
	public static List<TileProp> getCantCrossList(Elevation elevation)
		{ return cantCross.get(elevation); }
	
	private TileProp(int value)
		{ this.value = value; }
	
	public int getValue()
		{ return value; }
	
	public static TileProp getPropFromValue(int value)
		{ return propFromValueList.containsKey(value) ? propFromValueList.get(value) : null; }

	public static Collection<TileProp> getList()
		{ return propFromValueList.values(); }

}
