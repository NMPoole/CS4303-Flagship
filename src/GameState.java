import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Iterator;

import static processing.core.PConstants.*;

public class GameState {


    // ########################################################################
    // Game State Attributes:
    // ########################################################################

    // Camera:
    private float camXPan; // Camera panning along x-axis.
    private float camYPan; // Camera panning along y-axis.
    private float camXPanDelta = 0; // Change in camera pan along x-axis this draw cycle.
    private float camYPanDelta = 0; // Change in camera pan along y-axis this draw cycle.

    // Weather Conditions:
    private float windDirAngle; // Direction of the wind as an angle between 0 and 2PI.
    private float windStrength; // Strength of the wind, with 0 being the weakest, and 1 being the strongest.

    // Game Objects:
    private Map map; // The map of the game: procedurally generated as 2D Perlin noise, with thresholds for terrain.
    private MapCollisionLattice collisionLattice; // Bin lattice used for handling game object collisions.

    private PVector aim; // Aim of the player - position of the mouse.

    private ArrayList<ObjCannonBall> cannonBalls; // Active cannonballs in the game.
    private ArrayList<ObjLoot> loot; // Active loot in the game.
    private ArrayList<CharacterShipEnemy> enemyShips; // Active enemy ships.
    private ArrayList<CharacterFort> forts; // Active forts.
    private ArrayList<CharacterShark> sharks; // Active sharks.
    private ArrayList<CharacterSiren> sirens; // Active sirens.

    private CharacterShipPlayer player; // The player ship of the game.
    private CharacterShipBoss flagship; // The final boss of the game.
    private CharacterFortBoss fortBoss; // Mini boss encounter of the mega-fort.


    // ########################################################################
    // Game State Constructors:
    // ########################################################################

    public GameState() {

        // Game State Initialisation: initialise all game objects.

        this.initWind(); // Initialise the weather conditions.
        this.initAim(); // Initialise aim - player reticule visible at mouse location.

        this.initCannonBalls(); // Initialise the list of active cannonballs.
        this.initLoot(); // Initialise the list of active loot.

        this.initPlayer(); // Initialise the player.
        this.initFlagship(); // Initialise the final boss.
        this.initFortBoss(); // Initialise the fort mini-boss.
        this.initEnemyShips(); // Initialise enemy ship objects.
        this.initForts(); // Initialise enemy forts.
        this.initSharks(); // Initialise enemy sharks.
        this.initSirens(); // Initialise enemy sirens.

        this.initMap(); // Initialise the map.
        this.initCollisionLattice();

    }


    // ########################################################################
    // Game State Overall Update/Display:
    // ########################################################################

    public void update() {

        this.updateCollisionLattice(); // Update the collision lattice.

        // Updating Game Objects:

        this.updateMap(); // Update the map.
        this.updateWind(); // Update the weather.
        this.updateAim(); // Update the aim.

        this.updateCannonBalls(); // Update cannonballs.
        this.updateLoot(); // Update loot.

        this.updatePlayer(); // Update player.
        this.updateFlagship(); // Update final boss.
        this.updateFortBoss(); // Update the fort mini-boss.
        this.updateEnemyShips(); // Update enemy ships.
        this.updateSharks(); // Update enemy sharks.
        this.updateSirens(); // Update siren enemies.
        this.updateForts(); // Update enemy forts.


        // Updating Game Camera:

        this.handleCameraPanning(); // Handle panning of the map given the player's position and movement direction.
        this.accountForPan(); // Apply camera panning to all objects.

    }

    public void display() {

        // Displaying Game Objects:

        this.displayMap(); // Display the map.

        this.displayLoot(); // Display loot.
        this.displayCannonBalls(); // Display cannonballs.

        this.displayForts(); // Display forts.
        this.displaySirens(); // Display sirens.
        this.displaySharks(); // Display sharks.
        this.displayEnemyShips(); // Display enemy ships.
        this.displayFortBoss(); // Display the fort mini-boss.
        this.displayFlagship(); // Display flagship.
        this.displayPlayer(); // Display player.

        this.displayWind(); // Display the weather.
        this.displayAim(); // Display aiming reticule.

        this.displayHUD(); // Display additional info on-screen the player needs.

        if (Game.showMechanics) {
            this.collisionLattice.display();
            this.displayGameObjectStructs();
        }

    }

    private void displayHUD() {

        Game.sketch.fill(GameConfig.SHOP_BACK_COL);
        Game.sketch.stroke(0);
        Game.sketch.strokeWeight(3);

        Game.sketch.rect((float) (GameConfig.CANVAS_WIDTH / 2) - 120, 0, 240, 50);

        Game.sketch.textAlign(CENTER);
        Game.sketch.textSize(GameConfig.TITLE_TEXT_SIZE);

        Game.sketch.stroke(255);
        Game.sketch.fill(255);
        Game.sketch.text("TOTAL DEATHS: " + this.player.getTotalDeaths(), (float) GameConfig.CANVAS_WIDTH / 2, 20);

        Game.sketch.stroke(GameConfig.GOLD_COL);
        Game.sketch.fill(GameConfig.GOLD_COL);
        Game.sketch.text("GOLD: " + this.player.getGold(), (float) GameConfig.CANVAS_WIDTH / 2, 45);

        Game.sketch.strokeWeight(1); // Reset stroke-weight.

    }

    private void displayGameObjectStructs() {

        Game.sketch.textAlign(LEFT);
        Game.sketch.stroke(GameConfig.TITLE_TEXT_COLOR);
        Game.sketch.fill(GameConfig.TITLE_TEXT_COLOR);
        Game.sketch.textSize((float) GameConfig.TITLE_TEXT_SIZE / 1.5f);

        Game.sketch.text("NUM LOOT: " + this.loot.size(), (float) GameConfig.CANVAS_WIDTH / 20, (float) (GameConfig.CANVAS_HEIGHT / 20));
        Game.sketch.text("NUM ENEMY SHIPS: " + this.enemyShips.size(), (float) GameConfig.CANVAS_WIDTH / 20, (float) (GameConfig.CANVAS_HEIGHT / 20) + 15);
        Game.sketch.text("NUM ENEMY FORTS: " + this.forts.size(), (float) GameConfig.CANVAS_WIDTH / 20, (float) (GameConfig.CANVAS_HEIGHT / 20) + 30);
        Game.sketch.text("NUM CANNONBALLS: " + this.cannonBalls.size(), (float) GameConfig.CANVAS_WIDTH / 20, (float) (GameConfig.CANVAS_HEIGHT / 20) + 45);
        Game.sketch.text("NUM SHARKS: " + this.sharks.size(), (float) GameConfig.CANVAS_WIDTH / 20, (float) (GameConfig.CANVAS_HEIGHT / 20) + 60);
        Game.sketch.text("NUM SIRENS: " + this.sirens.size(), (float) GameConfig.CANVAS_WIDTH / 20, (float) (GameConfig.CANVAS_HEIGHT / 20) + 75);

    }


    // ########################################################################
    // Game State - Game Status:
    // ########################################################################

    public boolean isGameEnd() {
        return !this.flagship.isAlive(); // Game is over when the final boss has been defeated!
    }


    // ########################################################################
    // Game State - Camera:
    // ########################################################################

    public void panLeft(float xToAdd) {
        this.camXPan -= xToAdd;
        this.camXPanDelta = -xToAdd;
    }

    public void panRight(float xToAdd) {
        this.camXPan += xToAdd;
        this.camXPanDelta = xToAdd;
    }

    public void panUp(float yToAdd) {
        this.camYPan -= yToAdd;
        this.camYPanDelta = -yToAdd;
    }

    public void panDown(float yToAdd) {
        this.camYPan += yToAdd;
        this.camYPanDelta = yToAdd;
    }

    public void accountForPan() {

        // Update the positions of all game objects to account for pan.
        // This is naive, but permissible given that panning is expected to happen somewhat rarely.
        if (camXPanDelta != 0 || camYPanDelta != 0) {

            for (ObjGameGeneric currObject : this.cannonBalls) currObject.updatePan(this.camXPanDelta, this.camYPanDelta);
            for (ObjGameGeneric currObject : this.loot) currObject.updatePan(this.camXPanDelta, this.camYPanDelta);

            this.player.updatePan(this.camXPanDelta, this.camYPanDelta);
            this.player.panPointToSeek(this.camXPanDelta, this.camYPanDelta);

            this.flagship.updatePan(this.camXPanDelta, this.camYPanDelta);

            this.fortBoss.updatePan(this.camXPanDelta, this.camYPanDelta);
            for (ObjGameGeneric currObject : this.fortBoss.getCornerForts()) currObject.updatePan(this.camXPanDelta, this.camYPanDelta);

            for (ObjGameGeneric currObject : this.enemyShips) currObject.updatePan(this.camXPanDelta, this.camYPanDelta);
            for (ObjGameGeneric currObject : this.forts) currObject.updatePan(this.camXPanDelta, this.camYPanDelta);
            for (ObjGameGeneric currObject : this.sharks) currObject.updatePan(this.camXPanDelta, this.camYPanDelta);
            for (ObjGameGeneric currObject : this.sirens) currObject.updatePan(this.camXPanDelta, this.camYPanDelta);

            this.camXPanDelta = 0;
            this.camYPanDelta = 0;
        }

    }

    private void handleCameraPanning() {

        int[] aimMapTile = this.map.getMapGridCoordAtPos((int) this.aim.x, (int) this.aim.y);
        int aimMapCol = aimMapTile[0];
        int aimMapRow = aimMapTile[1];

        if (aimMapCol <= GameConfig.MAP_CAM_BORDER) {
            this.panLeft(GameConfig.PAN_SPEED);
        } else if (aimMapCol >= (map.getMapGridWidth() - GameConfig.MAP_CAM_BORDER)) {
            this.panRight(GameConfig.PAN_SPEED);
        }

        if (aimMapRow <= GameConfig.MAP_CAM_BORDER) {
            this.panUp(GameConfig.PAN_SPEED);
        } else if (aimMapRow >= (map.getMapGridHeight() - GameConfig.MAP_CAM_BORDER)) {
            this.panDown(GameConfig.PAN_SPEED);
        }

    }

    public float getCamXPan() {
        return camXPan;
    }

    public float getCamYPan() {
        return camYPan;
    }


    // ########################################################################
    // Game State - Weather:
    // ########################################################################

    private void initWind() {

        // Select random angle between 0 and (2 * PI) as the direction of the wind.
        this.windDirAngle = Game.sketch.random(-PI, PI);
        // Wind strength represented as a value between 0 and 1; 0 is no wind, 1 is the strongest wind.
        this.windStrength = Game.sketch.random(0, 1);

    }

    private void updateWind() {

        // Change the direction of the wind if probability of change has succeeded.
        if (Game.sketch.random(0, 1) <= GameConfig.WIND_CHANGE_PROB) {
            this.initWind();
            Game.gameSound.soundWindGust(true); // Play wind gust sound when wind is updated.
        }

    }

    private void displayWind() {

        // Wind is displayed by an arrow: direction of arrow indicates wind direction, arrow width represents wind strength.

        int windArrowX = 50;
        int windArrowY = 50;
        int windArrowLength = 40;
        float windArrowStroke = PApplet.map(this.windStrength, 0, 1, 1, 8);

        Game.sketch.pushMatrix();

        Game.sketch.fill(GameConfig.SHOP_BACK_COL);
        Game.sketch.stroke(0);
        Game.sketch.strokeWeight(3);

        Game.sketch.rect(windArrowX - (windArrowLength * 1.1f), windArrowY - (windArrowLength * 1.1f), (windArrowLength * 1.1f) * 2, (windArrowLength * 1.1f) * 2);

        // Wind Arrow Appearance.
        Game.sketch.strokeWeight(windArrowStroke); // Thickness of arrow illustrates wind strength.
        Game.sketch.stroke(GameConfig.WIND_IND_COL);

        // Wind Arrow Lines.
        Game.sketch.translate(windArrowX, windArrowY);
        Game.sketch.rotate(windDirAngle);

        Game.sketch.line(0, 0, windArrowLength, 0);
        Game.sketch.line(windArrowLength, 0, windArrowLength - 10, -10);
        Game.sketch.line(windArrowLength, 0, windArrowLength - 10, 10);

        Game.sketch.popMatrix();

        Game.sketch.strokeWeight(1);

    }

    public float getWindDirAngle() {
        return windDirAngle;
    }

    public float getWindStrength() {
        return windStrength;
    }


    // ########################################################################
    // Game State - Map:
    // ########################################################################

    private void initMap() {
        this.map = new Map(this); // Create new map, which generate a map grid with proc. generated terrain.
    }

    private void updateMap() {
        this.map.update(this.camXPan, this.camYPan); // Update the map, which requires re-generation depending on cam.
    }

    private void displayMap() {
        this.map.display(); // Display the map.
    }

    public Map getMap() {
        return this.map;
    }


    // ########################################################################
    // Game State - Collision Lattice:
    // ########################################################################

    private void initCollisionLattice() {
        this.collisionLattice = new MapCollisionLattice(this.map);
    }

    private void updateCollisionLattice() {

        // Reset the collision lattice:
        this.collisionLattice.reset();

        // Register all game objects in the collision lattice:

        for (ObjCannonBall currCannonBall : this.cannonBalls) this.collisionLattice.register(currCannonBall);
        for (ObjLoot currLoot : this.loot) this.collisionLattice.register(currLoot);

        this.collisionLattice.register(this.player);
        this.collisionLattice.register(this.flagship);

        this.collisionLattice.register(this.fortBoss);
        for (CharacterFort currFort : this.fortBoss.getCornerForts()) this.collisionLattice.register(currFort);

        for (CharacterShipEnemy currEnemyShip : this.enemyShips) this.collisionLattice.register(currEnemyShip);
        for (CharacterFort currFort : this.forts) this.collisionLattice.register(currFort);
        for (CharacterShark currShark : this.sharks) this.collisionLattice.register(currShark);
        for (CharacterSiren currSiren : this.sirens) this.collisionLattice.register(currSiren);

    }

    public MapCollisionLattice getCollisionLattice() {
        return collisionLattice;
    }


    // ########################################################################
    // Game State - Aim:
    // ########################################################################

    private void initAim() {
        this.aim = new PVector(0, 0);
    }

    public void updateAim() {
        this.aim = new PVector(Game.sketch.mouseX, Game.sketch.mouseY);
    }

    private void displayAim() {

        int aimColor = GameConfig.AIM_DEF_COL;
        if (Game.sailAlterEngaged) aimColor = GameConfig.SHIP_MAST_COL;
        if (Game.cannonFireEngaged) aimColor = GameConfig.CANNON_COL;

        // Aiming reticule displayed as simple circle.
        Game.sketch.fill(aimColor);
        Game.sketch.stroke(aimColor);
        Game.sketch.ellipse(this.aim.x, this.aim.y, GameConfig.AIM_SIZE, GameConfig.AIM_SIZE);

        if (Game.showMechanics) this.displayInfoAtAim(); // Showing game mechanics.

    }

    public void displayInfoAtAim() {

        // Display the terrain under the mouse cursor.
        int[] mapGridPos = this.map.getMapGridCoordAtPos((int) this.aim.x, (int) this.aim.y);
        Game.sketch.fill(GameConfig.TITLE_TEXT_COLOR);
        Game.sketch.textSize((float) GameConfig.TITLE_TEXT_SIZE / 2);

        Game.sketch.text("PosX: " + this.aim.x + ", PosY: " + this.aim.y, this.aim.x, this.aim.y - 30);
        Game.sketch.text("MapGridCol: " + mapGridPos[0] + ", MapGridRow: " + mapGridPos[1], this.aim.x, this.aim.y - 20);

        GameConfig.Terrain terrainAtMouse = this.map.getTerrainAtPos((int) this.aim.x, (int) this.aim.y);
        Game.sketch.text(String.valueOf(terrainAtMouse), this.aim.x, this.aim.y - 10);

    }

    public PVector getAim() {
        return aim;
    }


    // ########################################################################
    // Game State - Cannonballs:
    // ########################################################################

    private void initCannonBalls() {
        this.cannonBalls = new ArrayList<>();
    }

    private void updateCannonBalls() {

        Iterator<ObjCannonBall> cannonBallsIter = this.cannonBalls.iterator();

        while(cannonBallsIter.hasNext()) {

            ObjCannonBall currCannonBall = cannonBallsIter.next();
            boolean remove = currCannonBall.update();
            if (remove) cannonBallsIter.remove();

        }

    }

    private void displayCannonBalls() {
        for (ObjCannonBall currCannonBall : this.cannonBalls) currCannonBall.display();
    }

    public void addCannonBall(ObjCannonBall cannonBall) {
        this.cannonBalls.add(cannonBall);
    }

    public ArrayList<ObjCannonBall> getCannonBalls() {
        return this.cannonBalls;
    }


    // ########################################################################
    // Game State - Loot:
    // ########################################################################

    private void initLoot() {
        this.loot = new ArrayList<>();
    }

    private void updateLoot() {

        Iterator<ObjLoot> lootIter = this.loot.iterator();

        while(lootIter.hasNext()) {

            ObjLoot currLoot = lootIter.next();
            boolean remove = currLoot.update();
            if (remove) lootIter.remove();

        }

    }

    private void displayLoot() {
        for (ObjLoot currLoot : this.loot) currLoot.display();
    }

    public void addLoot(PVector pos) {

        // Determine amount of gold to give when this loot is collected.
        int goldAward = (int) Game.sketch.random(GameConfig.LOOT_GOLD_AWARD_MIN, GameConfig.LOOT_GOLD_AWARD_MAX);

        this.loot.add(
                new ObjLoot(
                        pos.x,
                        pos.y,
                        0,
                        GameConfig.LOOT_RADIUS,
                        GameConfig.LOOT_COL,
                        goldAward,
                        true,
                        this));

    }

    public ArrayList<ObjLoot> getLoot() {
        return loot;
    }


    // ########################################################################
    // Game State - Player:
    // ########################################################################

    private void initPlayer() {

        // Factory Design Pattern would be ideal...
        this.player = new CharacterShipPlayer(
                        GameConfig.PLYR_MAP_START.x,
                        GameConfig.PLYR_MAP_START.y,
                        GameConfig.PLYR_MAX_SPEED,
                        GameConfig.PLYR_RADIUS,
                        GameConfig.PLYR_COL,
                        GameConfig.PLYR_HEALTH,
                        GameConfig.PLYR_DEF_NUM_VOLLEYS,
                        GameConfig.PLYR_DEF_NUM_BALLS_PER_VOLLEY,
                        GameConfig.PLYR_DEF_CANNON_RANGE,
                        GameConfig.PLYR_DEF_COOL_DOWN,
                        GameConfig.PLYR_DEF_BALL_DMG,
                        this);

    }

    private void updatePlayer() {

        this.player.update(); // Update the player.

        if (!this.player.isAlive()) {
            Game.gameSound.soundCharDeath(true);
            this.respawnPlayer(); // Need to respawn the player if they are dead.
        }

    }

    private void respawnPlayer() {

        // Re-spawning: Replenish player health, enact penalty for dying (i.e., lost gold), change to new location.

        this.player.replenishHealth(); // Restore player to their full health.
        this.player.subGold((int) (this.player.getGold() * GameConfig.PLYR_DEATH_GOLD_PEN)); // Enact death gold penalty.
        PVector newSpawnPos = this.map.getPlayerRandPos(GameConfig.Terrain.DEEP_WATER); // Change player location on death as a re-spawn.
        this.player.setPos(newSpawnPos);

        this.player.halt(); // Halt the player.

        this.player.incrementTotalDeaths(); // Add a death that the player has received.

    }

    private void displayPlayer() {
        this.player.display(); // Display the player.
    }

    public CharacterShipPlayer getPlayer() {
        return this.player;
    }


    // ########################################################################
    // Game State - Flagship:
    // ########################################################################

    private void initFlagship() {

        this.flagship = new CharacterShipBoss(
                GameConfig.BOSS_MAP_START.x,
                GameConfig.BOSS_MAP_START.y,
                GameConfig.BOSS_MAX_SPEED,
                GameConfig.BOSS_RADIUS,
                GameConfig.BOSS_COL,
                GameConfig.BOSS_HEALTH,
                GameConfig.BOSS_DEF_NUM_VOLLEYS,
                GameConfig.BOSS_DEF_NUM_BALLS_PER_VOLLEY,
                GameConfig.BOSS_DEF_CANNON_RANGE,
                GameConfig.BOSS_DEF_COOL_DOWN,
                GameConfig.BOSS_DEF_BALL_DMG,
                this);

    }

    private void updateFlagship() {
        this.flagship.update();
    }

    private void displayFlagship() {
        this.flagship.display();
    }

    public CharacterShipBoss getFlagship() {
        return flagship;
    }


    // ########################################################################
    // Game State - Enemy Ships:
    // ########################################################################

    private void initEnemyShips() {
        this.enemyShips = new ArrayList<>();
    }

    private void updateEnemyShips() {

        Iterator<CharacterShipEnemy> enemyShipsIter = this.enemyShips.iterator();

        while(enemyShipsIter.hasNext()) {

            CharacterShipEnemy currEnemyShip = enemyShipsIter.next();
            boolean remove = currEnemyShip.update();
            if (remove) {
                if (!currEnemyShip.isAlive()) Game.gameSound.soundCharDeath(true); // Death sound effect.
                enemyShipsIter.remove();
            }

        }

    }

    private void displayEnemyShips() {
        for (CharacterShipEnemy currEnemyShip : this.enemyShips) currEnemyShip.display();
    }

    public void addEnemyShip(PVector pos) {

        this.enemyShips.add(
                new CharacterShipEnemy(
                        pos.x,
                        pos.y,
                        (this.player.getMaxSpeed() * GameConfig.ENEMY_SHIP_SCALE_MULT),
                        GameConfig.ENEMY_SHIP_RADIUS,
                        GameConfig.ENEMY_SHIP_COL,
                        (int) (this.player.getBaseHealth() * GameConfig.ENEMY_SHIP_SCALE_MULT),
                        true,
                        GameConfig.ENEMY_SHIP_DEF_NUM_VOLLEYS,
                        this.player.getNumBallsPerVolley(),
                        this.player.getCannonRange(),
                        (int) (this.player.getCannonCoolDown() / GameConfig.ENEMY_SHIP_SCALE_MULT),
                        this.player.getDamagePerBall(),
                        this));

    }

    public ArrayList<CharacterShipEnemy> getEnemyShips() {
        return enemyShips;
    }


    // ########################################################################
    // Game State - Enemy Forts:
    // ########################################################################

    private void initForts() {
        this.forts = new ArrayList<>();
    }

    private void updateForts() {

        Iterator<CharacterFort> fortsIter = this.forts.iterator();

        while(fortsIter.hasNext()) {

            CharacterFort currFort = fortsIter.next();
            boolean remove = currFort.update();
            if (remove) {
                if (!currFort.isAlive()) Game.gameSound.soundCharDeath(true);
                fortsIter.remove();
            }

        }

    }

    private void displayForts() {
        for (CharacterFort currFort : this.forts) currFort.display();
    }

    public void addFort(PVector pos) {

        int numCannons = (int) Game.sketch.random(GameConfig.FORT_CANNONS_MIN, GameConfig.FORT_CANNONS_MAX);

        this.forts.add(
                new CharacterFort(
                        pos.x,
                        pos.y,
                        0,
                        GameConfig.FORT_RADIUS,
                        GameConfig.FORT_INNER_COL,
                        (int) (this.player.getBaseHealth() * GameConfig.FORT_SCALE_MULT),
                        true,
                        numCannons,
                        GameConfig.FORT_DEF_NUM_VOLLEYS,
                        this.player.getNumBallsPerVolley(),
                        (int) (this.player.getCannonRange() / GameConfig.FORT_SCALE_MULT),
                        (int) (this.player.getCannonCoolDown() / GameConfig.FORT_SCALE_MULT),
                        this.player.getDamagePerBall(),
                        this));

    }

    public ArrayList<CharacterFort> getForts() {
        return forts;
    }


    // ########################################################################
    // Game State - Enemy Sharks:
    // ########################################################################

    private void initSharks() {
        this.sharks = new ArrayList<>();
    }

    private void updateSharks() {

        Iterator<CharacterShark> sharksIter = this.sharks.iterator();

        while(sharksIter.hasNext()) {

            CharacterShark currShark = sharksIter.next();
            boolean remove = currShark.update();
            if (remove) {
                if (!currShark.isAlive()) Game.gameSound.soundCharDeath(true);
                sharksIter.remove();
            }

        }

    }

    private void displaySharks() {
        for (CharacterShark currShark : this.sharks) currShark.display();
    }

    public void addShark(PVector pos) {

        this.sharks.add(
                new CharacterShark(
                        pos.x,
                        pos.y,
                        GameConfig.SHARK_MAX_SPEED,
                        GameConfig.SHARK_RADIUS,
                        GameConfig.SHARK_COL,
                        (int) (this.player.getBaseHealth() * GameConfig.SHARK_SCALE_MULT),
                        true,
                        (this.player.getDamagePerBall() * GameConfig.SHARK_SCALE_MULT * GameConfig.SHARK_DMG_SCALE),
                        this));

    }

    public ArrayList<CharacterShark> getSharks() {
        return sharks;
    }


    // ########################################################################
    // Game State - Enemy Sirens:
    // ########################################################################

    private void initSirens() {
        this.sirens = new ArrayList<>();
    }

    private void updateSirens() {

        Iterator<CharacterSiren> sirensIter = this.sirens.iterator();

        while(sirensIter.hasNext()) {

            CharacterSiren currSiren = sirensIter.next();
            boolean remove = currSiren.update();
            if (remove) {
                if (!currSiren.isAlive()) Game.gameSound.soundCharDeath(true);
                sirensIter.remove();
            }

        }

    }

    private void displaySirens() {
        for (CharacterSiren currSiren : this.sirens) currSiren.display();
    }

    public void addSiren(PVector pos) {

        this.sirens.add(
                new CharacterSiren(
                        pos.x,
                        pos.y,
                        0,
                        GameConfig.SIREN_RADIUS,
                        GameConfig.SIREN_COL,
                        (int) (this.player.getBaseHealth() * GameConfig.SIREN_SCALE_MULT),
                        true,
                        this.player.getDamagePerBall(),
                        this));

    }

    public ArrayList<CharacterSiren> getSirens() {
        return this.sirens;
    }


    // ########################################################################
    // Game State - Fort Mini-Boss:
    // ########################################################################

    private void initFortBoss() {

        PVector fortBossStart = GameConfig.FORT_BOSS_START;
        this.fortBoss = new CharacterFortBoss(
                fortBossStart.x,
                fortBossStart.y,
                GameConfig.FORT_BOSS_RADIUS,
                GameConfig.FORT_BOSS_INNER_COL,
                GameConfig.FORT_BOSS_HEALTH,
                false,
                GameConfig.FORT_BOSS_NUM_CANNONS,
                GameConfig.FORT_BOSS_DEF_NUM_VOLLEYS,
                GameConfig.FORT_BOSS_DEF_NUM_BALLS_PER_VOLLEY,
                GameConfig.FORT_BOSS_DEF_CANNON_RANGE,
                GameConfig.FORT_BOSS_DEF_COOL_DOWN,
                GameConfig.FORT_BOSS_DEF_BALL_DMG,
                this);

    }

    private void updateFortBoss() {

        boolean remove = this.fortBoss.update();

        if (remove) {
            if (!this.fortBoss.isAlive()) Game.gameSound.soundCharDeath(true);
            this.fortBoss = null;
        }

    }

    private void displayFortBoss() {
        if (this.fortBoss != null) this.fortBoss.display();
    }

    public CharacterFortBoss getFortBoss() {
        return fortBoss;
    }


}
