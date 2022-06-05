import processing.core.PVector;

import static processing.core.PApplet.*;

public class GameConfig {


    // ################################################################################################################
    // GAME CONFIGURATION:
    // ################################################################################################################

    // CANVAS:

    final static int FPS = 60; // Frame rate to use.

    final static int CANVAS_WIDTH = Game.sketch.displayWidth; // Width of canvas; screen width.
    final static int CANVAS_HEIGHT = Game.sketch.displayHeight - 50; // Height of canvas; screen height.

    final static int TITLE_TEXT_SIZE = 24; // Size of on-screen title text.
    final static int TITLE_TEXT_COLOR = Game.sketch.color(0); // Colour of on-screen titles; white.

    // SHOP:

    enum SHOP_OPTION {INCR_HP, INCR_VOLLEYS, INCR_RANGE, INCR_SPR_SHOT, INCR_BALL_DMG} // Available upgrades in the shop.

    // Each upgrade option has descriptive text, an amount determining the increase of the upgrade, and a gold cost.
    final static String INCR_HP_TXT = "ADDITIONAL HEALTH";
    final static int INCR_HP_AMOUNT = 10;
    final static int INCR_HP_COST = 15;

    final static String INCR_VOLLEYS_TXT = "ADDITIONAL VOLLEYS";
    final static int INCR_VOLLEYS_AMOUNT = 4;
    final static int INCR_VOLLEYS_COST = 10;

    final static String INCR_RANGE_TXT = "ADDITIONAL RANGE";
    final static int INCR_RANGE_AMOUNT = 1;
    final static int INCR_RANGE_COST = 30;

    final static String INCR_SPR_SHOT_TXT = "ADDITIONAL CANNONBALLS PER VOLLEY";
    final static int INCR_SPR_SHOT_AMOUNT = 2;
    final static int INCR_SPR_SHOT_COST = 30;

    final static String INCR_BALL_DMG_TXT = "ADDITIONAL DAMAGE PER CANNONBALL";
    final static int INCR_BALL_DMG_AMOUNT = 10;
    final static int INCR_BALL_DMG_COST = 30;

    final static int GOLD_COL = Game.sketch.color(221, 212, 23, 255); // Colour of gold in the game.
    final static int SHOP_BACK_COL = Game.sketch.color(94, 42, 14, 255); // Shop background colour.

    // PHYSICS:

    final static float SPRING_COEFF = 0.0001f; // Defines springiness when objects bounce off each other.

    // COLLISION LATTICE:

    final static int LATTICE_RES = 4; // Number of map tiles considered as a bin in the bin-lattice spatial sub-division.

    // MAP:

    final static int MAP_TILE_SIZE = 16; // Number of pixels comprising a tile in the map (i.e., map resolution).
    final static float MAP_SCALE = 0.1f; // Scale (i.e., frequency) of Perlin noise used to generate 2D map.
    final static int MAP_SEED = 42; // Seed for Perlin noise generation.
    final static int RAND_SEED = 101010; // Seed for random generation.

    final static int PAN_SPEED = 2; // Speed of panning.

    final static int DESPAWN_TIMER = 10 * FPS; // Objects that can de-spawn will do so after this limit is exceeded.

    enum Terrain {DEEP_WATER, SHALLOW_WATER, SAND, GRASS} // Possible terrains assigned to the map.

    // Colour of the different terrains in the map.
    final static int DEEP_WATER_COL = Game.sketch.color(35, 137, 218, 255);
    final static int SHALLOW_WATER_COL = Game.sketch.color(28, 163, 236, 255);
    final static int SAND_COL = Game.sketch.color(194, 178, 128, 255);
    final static int GRASS_COL = Game.sketch.color(79, 121, 66, 255);

    final static int MAP_BUFFER = 10; // Size of the map buffer in tiles (i.e., number of off-screen tiles rendered).
    final static int MAP_CAM_BORDER = (MAP_BUFFER / 2) + 2; // Number of tiles in map used as a border for panning.

    // SPAWNING:

    final static float SPAWN_PROB_SHARK = 0.002f; // Probability that a spawned object is a shark.
    final static float SPAWN_PROB_SHIP_ENEMY = 0.0005f; // Probability that a spawned object is an enemy ship.
    final static float SPAWN_PROB_SIREN = 0.0005f; // Probability that a spawned object is a siren.
    final static float SPAWN_PROB_FORT = 0.004f; // Probability that a spawned objects is an enemy fort.
    final static float SPAWN_PROB_LOOT = 0.003f; // Probability that a spawned object is a loot item.

    // WEATHER CONDITIONS:

    final static float WIND_CHANGE_PROB = 0.001f; // Probability that the wind will change direction.
    final static int WIND_IND_COL = Game.sketch.color(209, 241, 249, 255); // Wind indicator colour.
    final static float WIND_SCALE_MULT = 0.001f; // Used to scale the affect of wind on maritime game objects.

    final static float WIND_DRAG_COEFF = -0.01f; // Coefficient of drag used to slow projectile game objects.
    final static float WATER_DRAG_COEFF = -0.01f; // Coefficient of drag used to slow ships in the game.

    // AIM:

    final static int AIM_DEF_COL = Game.sketch.color(255, 255, 255); // Colour of reticule.
    final static int AIM_SIZE = 10; // Size of aiming reticule.

    // CANNONBALLS:

    final static float CANNONBALL_DEF_SPEED = 1f; // Used for maximum speed of cannonballs in the game.
    final static float CANNONBALL_MIN_SPEED = CANNONBALL_DEF_SPEED / 10; // Minimum speed required for cannonballs before `sinking'.
    final static float CANNONBALL_SIZE_MULT = 1f; // Multiplier for scaling cannonball size with ship size.

    final static int CANNONBALL_COL = Game.sketch.color(56, 56, 56, 255); // Cannon ball colour.

    // CANNONS:

    final static float CANNON_BALL_SPREAD = PI / 32; // Spread of arc between cannonballs in a volley.

    final static float CANNON_SHIP_SCALE_MULT = 0.3f; // Multiplier for size of cannon as proportion of ship size.
    final static float CANNON_LENGTH_MULT = 3; // Multiplier used for determining length of cannon display.

    final static int CANNON_RANGE_RADIUS_MULT = 80; // Used to convert cannon range into approximate radius of effect.

    final static int CANNON_COL = Game.sketch.color(56, 56, 56, 255); // Colour of cannons.
    final static int CANNON_COOL_DOWN_COL = Game.sketch.color(56, 56, 56, 170); // Colour of cannons cooling down.
    final static int CANNON_EMPTY_COL = Game.sketch.color(56, 56, 56, 170); // Colour of empty cannons.

    // LOOT:

    final static int LOOT_GOLD_AWARD_MIN = 1; // Minimum amount of gold awarded by loot when collected.
    final static int LOOT_GOLD_AWARD_MAX = 5; // Maximum amount of gold awarded by loot when collected.

    final static int LOOT_RADIUS = 12; // Loot size.
    final static int LOOT_COL = Game.sketch.color(255, 215, 0, 255); // Loot colour.

    // CHARACTER:

    enum CharType {WATER, LAND, MARITIME} // Possible character types; either on land, in the water, or on the water.

    enum SEEK_TYPE {NORM, ARRIVE, LUNGE} // Type of seeking, can arrive, lunge, or neither.

    final static int CHAR_ARRIVE_MULT = 10; // Multiplier by character size to determine distance to start slowing when seeking.
    final static int CHAR_LUNGE_MULT = 4; // Multiplier of character max speed to allow temporary speed boost for lunging.
    final static int CHAR_PRED_MULT = 10; // // Multiplier determining the size of predictions when pursuing, etc.

    final static int CHAR_AWARE_MULT = 7; // Multiplier by character size to determine size of awareness radius.

    final static float CHAR_AVOID_TER_RAY_ANG = PI / 8; // Angle of separation between cast rays when avoiding terrain.
    final static float CHAR_AVOID_TER_TURN_ANG = PI / 32; // Amount of turning a character can enact to avoid the terrain.
    final static float CHAR_AVOID_TER_AWARE_MULT = 0.75f; // Proportion of awareness radius that can be used for land avoidance.
    final static int CHAR_AVOID_TER_NUM_SAMPLES = 15; // Number of samples along terrain avoidance ray to detect terrain to avoid.

    final static float CHAR_FLOCK_MIN_MULT = 3; // Multiplier by character size determining min flock distance (for separation).
    final static float CHAR_FLOCK_MAX_MULT = 15; // Multiplier by character size determining max flock distance (for align & cohesion).
    final static float CHAR_FLOCK_SEP_WEIGHT = 1; // Weight of separation when flocking.
    final static float CHAR_FLOCK_ALI_WEIGHT = 1; // Weight of alignment when flocking.
    final static float CHAR_FLOCK_COH_WEIGHT = 1; // Weight of cohesion whe flocking.

    final static int CHAR_HEALTH_COL_BACK = Game.sketch.color(0, 0, 0, 127); // Health bar background.
    final static int CHAR_HEALTH_COL_FRONT = Game.sketch.color(255, 0, 0, 255); // Health bar foreground.
    final static int CHAR_HEALTH_PER_PIX = 2; // Amount of health represented by one pixel of width on the health bar.

    // SHIP:

    final static float SHIP_MOV_FORCE_MULT = 0.01f; // Used to limit force applied to the ships when seeking a point.

    final static float SHIP_SAIL_SIZE_MULT = 0.25f; // Multiplier used for determining size of sail display.
    final static float SHIP_SAIL_TURN_SPEED = (2 * PI) / (8 * FPS); // Roughly how many seconds to turn sail a full circle.

    final static int SHIP_MAST_COL = Game.sketch.color(139, 69, 19, 140); // Mast colour for ships.
    final static int SHIP_SAIL_COL = Game.sketch.color(255, 255, 255, 140); // Sail colour for ships.

    // PLAYER:

    final static PVector PLYR_MAP_START = new PVector(100, 200); // Position where player begins the game.

    final static float PLYR_MAX_SPEED = 0.6f; // Maximum speed the player moves with per frame.

    final static int PLYR_HEALTH = 50; // Player base health.

    final static int PLYR_START_GOLD = 0; // Player starting gold.
    final static float PLYR_DEATH_GOLD_PEN = 0.1f; // Multiplier determining how much of gold is lost when player dies.

    final static int PLYR_DEF_NUM_VOLLEYS = 5; // Number of volleys player is granted at the start of the game.
    final static int PLYR_DEF_NUM_BALLS_PER_VOLLEY = 1; // Player's number of cannonballs shot in a volley at game start.
    final static float PLYR_DEF_CANNON_RANGE = 1f; // Range of player's cannons at game start.
    final static int PLYR_DEF_BALL_DMG = 10; // Default damage assigned to each cannonball fired by the player.
    final static int PLYR_DEF_COOL_DOWN = 2 * FPS; // Cool down of player's cannons at game start.

    final static int PLYR_RADIUS = 24; // Size of player in pixels as radius.
    final static int PLYR_COL = Game.sketch.color(160, 82, 45, 255); // Colour of player.
    final static int PLYR_ICON_COL = Game.sketch.color(0, 255, 0, 180); // Colour of player.

    // FLAGSHIP (FINAL BOSS):

    final static PVector BOSS_MAP_START = new PVector(1300, 600); // Position where the boss begins in the game.

    final static float BOSS_MAX_SPEED = 0.1f; // Maximum speed the boss moves with per frame.

    final static int BOSS_HEALTH = 500; // Boss base health.

    final static int BOSS_DEF_NUM_VOLLEYS = -1; // Number of volleys boss is granted at the start of the game; infinite!
    final static int BOSS_DEF_NUM_BALLS_PER_VOLLEY = 9; // Boss's number of cannonballs shot in a volley at game start.
    final static float BOSS_DEF_CANNON_RANGE = 4f; // Range of boss's cannons at game start.
    final static int BOSS_DEF_BALL_DMG = 50; // Default damage assigned to each cannonball fired by the boss.
    final static int BOSS_DEF_COOL_DOWN = 6 * FPS; // Cool down of boss's cannons at game start.

    final static int BOSS_RADIUS = 48; // Size of boss in pixels as radius.
    final static int BOSS_COL = Game.sketch.color(160, 82, 45, 255); // Colour of boss.

    // FORT (MINI BOSS):

    final static PVector FORT_BOSS_START = new PVector(-75, -75);

    final static int FORT_BOSS_HEALTH = 200; // Fort Boss base health.

    final static int FORT_BOSS_DEF_NUM_VOLLEYS = -1; // Number of volleys boss is granted at the start of the game; infinite!
    final static int FORT_BOSS_DEF_NUM_BALLS_PER_VOLLEY = 1; // Boss's number of cannonballs shot in a volley at game start.
    final static float FORT_BOSS_DEF_CANNON_RANGE = 3f; // Range of boss's cannons at game start.
    final static int FORT_BOSS_DEF_BALL_DMG = 20; // Default damage assigned to each cannonball fired by the boss.
    final static int FORT_BOSS_DEF_COOL_DOWN = 6 * FPS; // Cool down of boss's cannons at game start.
    final static int FORT_BOSS_NUM_CANNONS = 4; // Number of cannons assigned to each member of the fort mini-boss.

    final static int FORT_BOSS_RADIUS = 128; // Size of boss in pixels as radius.
    final static int FORT_BOSS_INNER_COL = SAND_COL; // Colour of boss.

    // ENEMY SHIP:

    final static int ENEMY_SHIP_DEF_NUM_VOLLEYS = -1; // Number of volleys granted at the start of the game; infinite!
    final static float ENEMY_SHIP_SCALE_MULT = 0.6f; // Scaling multiplier for enemy ships as the player gets stronger.

    final static float SHIP_WANDER_RAND_FACT = PI; // Maximum variation in orientation when wandering for enemy ships.

    final static int ENEMY_SHIP_RADIUS = 24; // Size of enemy ship in pixels as radius.
    final static int ENEMY_SHIP_COL = Game.sketch.color(160, 82, 45, 255); // Colour of enemy ship.

    // FORT:

    final static int FORT_CANNONS_MIN = 1; // Minimum amount of cannons in a fort.
    final static int FORT_CANNONS_MAX = 4; // Maximum amount of cannons in a fort.

    final static int FORT_DEF_NUM_VOLLEYS = -1; // Number of volleys granted at the start of the game; infinite!
    final static float FORT_SCALE_MULT = 0.5f; // Scaling multiplier for forts as the player gets stronger.

    final static float CANNON_FORT_SCALE_MULT = 0.2f; // Multiplier for size of cannon as proportion of fort size.

    final static int FORT_RADIUS = 36; // Size of fort in pixels as radius.
    final static int FORT_INNER_COL = SAND_COL; // Colour of the fort interior.
    final static int FORT_WALL_COL = Game.sketch.color(105,105,105, 255); // Fort wall colour.
    final static int FORT_WALL_WEIGHT = 5; // Weight of stroke to represent the wall.

    // SHARKS:

    final static float SHARK_MAX_SPEED = 0.5f; // Maximum speed the shark moves with per frame.
    final static float SHARK_FORCE_MULT = 0.01f; // Multiplier to scale the sharks applied forces applied as otherwise to quick.
    final static float SHARK_SCALE_MULT = 0.4f; // Scaling multiplier for sharks as the player gets stronger.
    final static float SHARK_WANDER_RAND_FACT = PI / 4; // Maximum variation in orientation when wandering for sharks.
    final static float SHARK_DMG_SCALE = 0.01f; // Shark damage scale as sharks deal constant damage.

    final static int SHARK_RADIUS = 12; // Size of shark in pixels as radius.
    final static int SHARK_COL = Game.sketch.color( 31, 36, 42, 180); // Colour of the shark.

    // SIRENS:

    final static float SIREN_PROB_HALT_PLYR = 0.015f; // Probability that a player gets halted by a siren and pulled in.

    final static float SIREN_AWARE_RAD_MULT = 2; // Multiplier to increase size of siren awareness radius compared to default.

    final static float SIREN_SCALE_MULT = 0.4f; // Scaling multiplier for sirens as the player gets stronger.
    final static float SIREN_FORCE_MULT = 0.2f; // Multiplier to scale the sirens applied forces.

    final static int SIREN_RADIUS = 12; // Size of siren in pixels as radius.
    final static int SIREN_COL = Game.sketch.color( 72, 191, 145, 180); // Colour of the siren.

    // SOUNDS:

    final static String SOUND_LOC = "../data/sounds/";

    final static String GAME_MUSIC_LOC = SOUND_LOC + "game_music.mp3";
    final static String GAME_OVER_LOC = SOUND_LOC + "game_over.mp3";
    final static String SHIP_AMBIENT_LOC = SOUND_LOC + "ship_ambient.mp3";

    final static String CANNON_FIRE_LOC = SOUND_LOC + "cannon_fire.mp3";
    final static String MONEY_COLLECT_LOC = SOUND_LOC + "money_collect.mp3";
    final static String IMPACT_THUD_LOC = SOUND_LOC + "impact_thud.mp3";
    final static String CHAR_DEATH_LOC = SOUND_LOC + "disabled.mp3";
    final static String SHARK_ATTACK_LOC = SOUND_LOC + "shark_attack.mp3";
    final static String SIREN_SONG_LOC = SOUND_LOC + "siren_song.mp3";
    final static String WIND_GUST_LOC = SOUND_LOC + "wind_gust.mp3";


}
