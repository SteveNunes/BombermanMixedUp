package enums;

public enum TileProp {
	
	UNKNOWN, // Desconhecido
	GROUND, // Chão normal, qualquer coisa passa por cima
	GROUND_HOLE, // Buraco (Só pode atravessar voando ou pulando, explosão passa por cima)
	FRAGILE_GROUND_LV1, // Chão intacto que raxa ao passar por cima
	FRAGILE_GROUND_LV2, // Chão raxado que quebra e vira buraco ao passar por cima
	GROUND_NO_BOMB, // Chão normal, que não pode por bomba
	GROUND_NO_FIRE, // Chão normal, que não passa explosão
	GROUND_NO_PLAYER, // Chão normal, que player não passa
	GROUND_NO_MOB, // Chão normal, que mob não passa
	WALL, // Parede (Só pode atravessar pulando)
	HIGH_WALL, // Parede (Não dá para passar nem pulando)
	PLAYER_INITIAL_POSITION,
	MOB_INITIAL_POSITION,
	BRICK_RANDOM_SPAWNER, // Tile onde pode ser gerado um tijolo aleatoriamente
	FIXED_BRICK, // Tile onde deverá haver um tijolo obrigatoriamente
	MOVING_BLOCK_HOLE, // Buraco onde encaixa o bloco que move com explosão
	DEEP_HOLE, // Buraco (Só pode atravessar voando ou pulando, explosão passa por cima)
	WATER, // Água (Igual GROUND, mas gera efeito visual de água cobrindo as pernas)
	SLIPPY, // Personagem escorrega ao andar em cima
	DAMAGE_PLAYER, // Causa dano no jogador que passar por cima
	DAMAGE_MOB, // Causa dano no mob que passar por cima
	DAMAGE_BOMB, // Explode bombas que forem colocadas em cima
	QUICKSAND, // Areia movediça (Deixa o movimento mais lento)
	SLIMY, // Pegajoso (Deixa o movimento mais lento, não dá para chutar nem agarrar bomba depositada nesse tile)
	PUSH_PLAYER_TO_LEFT, // Empurra o personagem para a esquerda
	PUSH_PLAYER_TO_UP, // Empurra o personagem para cima
	PUSH_PLAYER_TO_RIGHT, // Empurra o personagem para a direita
	PUSH_PLAYER_TO_DOWN, // Empurra o personagem para baixo
	TREADMILL_TO_LEFT, // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para a esquerda  
	TREADMILL_TO_UP, // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para cima  
	TREADMILL_TO_RIGHT, // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para a direita  
	TREADMILL_TO_DOWN, // Esteira (Vai empurrando objetos em cima dela (incluindo bombas e itens) para baixo  
	REDIRECT_BOMB_TO_LEFT, // Faz a bomba chutada que passar por esse tile, ir para a esquerda
	REDIRECT_BOMB_TO_UP, // Faz a bomba chutada que passar por esse tile, ir para cima
	REDIRECT_BOMB_TO_RIGHT, // Faz a bomba chutada que passar por esse tile, ir para a direita
	REDIRECT_BOMB_TO_DOWN, // Faz a bomba chutada que passar por esse tile, ir para baixo
	RAIL_UL, // Trilho de BomberKart
	RAIL_U, // Trilho de BomberKart
	RAIL_UR, // Trilho de BomberKart
	RAIL_L, // Trilho de BomberKart
	RAIL_R, // Trilho de BomberKart
	RAIL_DL, // Trilho de BomberKart
	RAIL_D, // Trilho de BomberKart
	RAIL_DR, // Trilho de BomberKart
	RAIL_JUMP, // Trilho de BomberKart
	RAIL_START, // Trilho de BomberKart
	RAIL_END; // Trilho de BomberKart
	
}
