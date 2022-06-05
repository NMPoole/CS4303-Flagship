import processing.core.PVector;

public class Map {


    // ########################################################################
    // Map Attributes:
    // ########################################################################

    private GameState gameState; // Reference to the game state.

    private float xROff, yROff; // Rendering offset - when panning less than 1 tile of space.

    private int xTOff, yTOff; // Terrain offset - when panning more than 1 tile of space.
    private int xTOffPrev, yTOffPrev; // Keeping track of change in terrain offset between updates.

    // How many tiles are loaded off the edges of the screen to allow seem-less panning of the map.
    private final int buffer = GameConfig.MAP_BUFFER;

    private final int mapGridWidth, mapGridHeight; // Width and height of the grid representing the map.
    private final GameConfig.Terrain[][] mapGrid; // 2D grid representing the map; thus, the map is tiled.


    // ########################################################################
    // Map Constructors:
    // ########################################################################

    public Map(GameState gameState) {

        this.gameState = gameState;

        // Grid width and height are number of tiles that can fit in the screen, plus the buffer tiles.
        mapGridWidth = (GameConfig.CANVAS_WIDTH / GameConfig.MAP_TILE_SIZE) + buffer;
        mapGridHeight = (GameConfig.CANVAS_HEIGHT / GameConfig.MAP_TILE_SIZE) + buffer;

        // Create the 2D map grid.
        mapGrid = new GameConfig.Terrain[mapGridWidth][mapGridHeight];
        this.generateMap();

        // Spawn in the game objects required at game start.
        this.mapStartSpawn();

    }


    // ########################################################################
    // Map Update Methods:
    // ########################################################################

    public void generateMap() {

        // Loop over all tiles in the map grid and assign terrain.
        for (int x = 0; x < mapGridWidth; x++) {
            for (int y = 0; y < mapGridHeight; y++) {
                mapGrid[x][y] = generateTerrain(x, y);
            }
        }

    }

    public GameConfig.Terrain generateTerrain(int x, int y) {

        // Create noise value.
        float noiseValue = Game.sketch.noise((xTOff + x) * GameConfig.MAP_SCALE, (yTOff + y) * GameConfig.MAP_SCALE);

        // Assign terrain based on noise value.
        if (noiseValue < 0.45) return GameConfig.Terrain.DEEP_WATER;
        else if (noiseValue < 0.6) return GameConfig.Terrain.SHALLOW_WATER;
        else if (noiseValue < 0.72) return GameConfig.Terrain.SAND;
        else return GameConfig.Terrain.GRASS;

    }

    public void update(float camXPan, float camYPan) {

        // Update the map based on changes to the game camera.
        xROff = camXPan % GameConfig.MAP_TILE_SIZE;
        yROff = camYPan % GameConfig.MAP_TILE_SIZE;

        xTOff = (int) camXPan / GameConfig.MAP_TILE_SIZE;
        yTOff = (int) camYPan / GameConfig.MAP_TILE_SIZE;

        // Need to regenerate the map if we have panned more than a tile's difference from previous.
        if ((xTOff - xTOffPrev) != 0 || (yTOff - yTOffPrev) != 0) {
            this.generateMap(); // Need to re-generate the map.
            this.mapUpdateSpawn(); // Need to add new enemies for updated parts of the map.
        }

        xTOffPrev = xTOff;
        yTOffPrev = yTOff;

    }


    // ########################################################################
    // Map Spawning Methods:
    // ########################################################################

    private void mapStartSpawn() {

        // This method will loop over tiles in the map and spawn game objects.
        for (int x = 0; x < mapGridWidth; x++) {
            for (int y = 0; y < mapGridHeight; y++) {
                this.spawnObject(x, y); // On-screen object spawning at the current tile.
            }
        }

    }

    private void mapUpdateSpawn() {

        // This method will loop over new rows of tiles created by panning the map and create spawns.

        // X direction camera panning spawn updates:
        // Left Pan - Loop over the left-most side of tiles and enact spawning.
        if ((xTOff - xTOffPrev) < 0) for (int y = 0; y < mapGridHeight; y++) spawnObject(0, y);
        // Right Pan - Loop over the right-most side of tiles and enact spawning.
        else if ((xTOff - xTOffPrev) > 0) for (int y = 0; y < mapGridHeight; y++) spawnObject(mapGridWidth - 1, y);

        // Y direction camera panning spawn updates:
        // Up Pan - Loop over the up-most side of tiles and enact spawning.
        else if ((yTOff - yTOffPrev) < 0) for (int x = 0; x < mapGridWidth; x++) spawnObject(x, 0);
        // Down Pan - Loop over the down-most side of tiles and enact spawning.
        else if ((yTOff - yTOffPrev) > 0) for (int x = 0; x < mapGridWidth; x++) spawnObject(x, mapGridHeight - 1);

    }

    private void spawnObject(int mapGridX, int mapGridY) {

        // Determine the position where the game object is to be spawned.
        PVector posToSpawn = this.getPosAtMapGridCoord(mapGridX, mapGridY); // Position to spawn the object.
        posToSpawn.x += (float) (GameConfig.MAP_TILE_SIZE / 2);
        posToSpawn.y += (float) (GameConfig.MAP_TILE_SIZE / 2);

        // Spawn game object according to terrain type at the spawn location.
        // NOTE: Not spawning enemies on the grass - there for diversity.
        GameConfig.Terrain terrain = mapGrid[mapGridX][mapGridY];
        switch (terrain) {
            case DEEP_WATER -> this.spawnObjectDeepWater(posToSpawn);
            case SHALLOW_WATER -> this.spawnObjectShallowWater(posToSpawn);
            case SAND -> this.spawnObjectSand(posToSpawn);
        }

    }

    private void spawnObjectDeepWater(PVector posToSpawn) {

        // Definitely spawning an object, just need to decide which.
        // Deep Water Objects: Enemy Ships,

        float randProb = Game.sketch.random(0, 1);
        if (randProb < GameConfig.SPAWN_PROB_SHARK) {
            gameState.addShark(posToSpawn); // Spawn enemy shark.
            return;
        }

        randProb = Game.sketch.random(0, 1);
        if (randProb < GameConfig.SPAWN_PROB_SHIP_ENEMY) {
            gameState.addEnemyShip(posToSpawn); // Spawn enemy ship.
            return;
        }

        randProb = Game.sketch.random(0, 1);
        if (randProb < GameConfig.SPAWN_PROB_SIREN) {
            gameState.addSiren(posToSpawn); // Spawn siren.
            return;
        }

    }

    private void spawnObjectShallowWater(PVector posToSpawn) {

        // Definitely spawning an object, just need to decide which.
        // Shallow Water Objects: Loot,

        float randProb = Game.sketch.random(0, 1);
        if (randProb < GameConfig.SPAWN_PROB_LOOT) {
            gameState.addLoot(posToSpawn); // Add loot to the game at the given location.
            return;
        }

        randProb = Game.sketch.random(0, 1);
        if (randProb < GameConfig.SPAWN_PROB_SIREN) {
            gameState.addSiren(posToSpawn); // Spawn siren.
            return;
        }

    }

    private void spawnObjectSand(PVector posToSpawn) {

        // Definitely spawning an object, just need to decide which.
        // Sand Objects: Forts

        float randProb = Game.sketch.random(0, 1);

        if (randProb < GameConfig.SPAWN_PROB_FORT) gameState.addFort(posToSpawn); // Spawn fort.

    }


    // ########################################################################
    // Map Render Methods:
    // ########################################################################

    public void display() {

        Game.sketch.noStroke(); // Edges of tiles are not visible.

        // Display all tiles within the grid as a map.
        for (int x = 0; x < mapGridWidth; x++) {
            for (int y = 0; y < mapGridHeight; y++) {

                // Get colour to show for the current tile in the map.
                int terrainColor = getTerrainColour(mapGrid[x][y]);
                Game.sketch.fill(terrainColor);

                // Show tile on the canvas.
                PVector mapGridCoords = getPosAtMapGridCoord(x, y);
                Game.sketch.rect(mapGridCoords.x, mapGridCoords.y, GameConfig.MAP_TILE_SIZE, GameConfig.MAP_TILE_SIZE);

            }
        }

    }

    public int getTerrainColour(GameConfig.Terrain terrain) {

        int terrainColor = -1;

        // Create a colour for the given terrain.
        switch (terrain) {
            case DEEP_WATER -> terrainColor = GameConfig.DEEP_WATER_COL;
            case SHALLOW_WATER -> terrainColor = GameConfig.SHALLOW_WATER_COL;
            case SAND -> terrainColor = GameConfig.SAND_COL;
            case GRASS -> terrainColor = GameConfig.GRASS_COL;
        }

        return terrainColor;

    }

    // ########################################################################
    // Map Collision Methods:
    // ########################################################################

    private boolean collidesWithTerrainType(boolean landCheck, int x, int y) {

        boolean collision;

        if (landCheck) { // Check collide with land.
            collision = isLandAtPos(x, y);
        } else { // Check collide with water.
            collision = isWaterAtPos(x, y);
        }

        return collision;

    }

    // W
    // W* ---
    // W
    public float collidesXLeft(ObjGameGeneric obj, boolean landCheck) {

        int charX = (int) obj.getPosX();
        int charY = (int) obj.getPosY();

        int[] gridCoords = this.getMapGridCoordAtPos(charX, charY);
        int charCol = gridCoords[0] + 1; // For reasons unknown, +1 is required here.
        int charRow = gridCoords[1];

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {

            int col = charCol - 1;
            int row = charRow + rowOffset;

            if (collidesWithTerrainType(landCheck, (int) obj.getPosX(), (int) obj.getPosY())) {

                PVector blockPos = this.getPosAtMapGridCoord(col, row);

                if (blockPos.x - charX > (float) (obj.getDiameter() / 2))
                    continue;
                if (charX - (blockPos.x + GameConfig.MAP_TILE_SIZE) > (float) (obj.getDiameter() / 2))
                    continue;
                if (blockPos.y - charY > (float) (obj.getDiameter() / 2))
                    continue;
                if (charY - (blockPos.y + GameConfig.MAP_TILE_SIZE) > (float) (obj.getDiameter() / 2))
                    continue;

                return (blockPos.x + GameConfig.MAP_TILE_SIZE + (float) (obj.getDiameter() / 2));

            }

        }

        return -1;

    }

    //   W
    //  *W ---
    //   W
    public float collidesXRight(ObjGameGeneric obj, boolean landCheck) {

        int charX = (int) obj.getPosX();
        int charY = (int) obj.getPosY();

        int[] gridCoords = this.getMapGridCoordAtPos(charX, charY);
        int charCol = gridCoords[0] - 1; // For reasons unknown, -1 is required here.
        int charRow = gridCoords[1];

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {

            int col = charCol + 1;
            int row = charRow + rowOffset;

            if (collidesWithTerrainType(landCheck, charX, charY)) {

                PVector blockPos = this.getPosAtMapGridCoord(col, row);

                if (blockPos.x - charX > (float) (obj.getDiameter() / 2))
                    continue;
                if (charX - (blockPos.x + GameConfig.MAP_TILE_SIZE) > (float) (obj.getDiameter() / 2))
                    continue;
                if (blockPos.y - charY > (float) (obj.getDiameter() / 2))
                    continue;
                if (charY - (blockPos.y + GameConfig.MAP_TILE_SIZE) > (float) (obj.getDiameter() / 2))
                    continue;

                return (blockPos.x - (float) (obj.getDiameter() / 2));

            }

        }

        return -1;

    }

    // WWW
    //  * ---
    public float collidesYUp(ObjGameGeneric obj, boolean landCheck) {

        int charX = (int) obj.getPosX();
        int charY = (int) obj.getPosY();

        int[] gridCoords = this.getMapGridCoordAtPos(charX, charY);
        int charCol = gridCoords[0];
        int charRow = gridCoords[1] + 1; // For reasons unknown, +1 is required here.

        for (int colOffset = -1; colOffset <= 1; colOffset++) {

            int col = charCol + colOffset;
            int row = charRow - 1;

            if (collidesWithTerrainType(landCheck, (int) obj.getPosX(), (int) obj.getPosY())) {

                PVector blockPos = this.getPosAtMapGridCoord(col, row);

                if (blockPos.x - charX > (float) (obj.getDiameter() / 2))
                    continue;
                if (charX - (blockPos.x + GameConfig.MAP_TILE_SIZE) > (float) (obj.getDiameter() / 2))
                    continue;
                if (blockPos.y - charY > ((float) obj.getDiameter() / 2))
                    continue;
                if (charY - (blockPos.y + GameConfig.MAP_TILE_SIZE) > (float) (obj.getDiameter() / 2))
                    continue;

                return (blockPos.y + GameConfig.MAP_TILE_SIZE + (float) (obj.getDiameter() / 2));

            }

        }

        return -1;

    }

    //  *
    // WWW ---
    public float collidesYDown(ObjGameGeneric obj, boolean landCheck) {

        int charX = (int) obj.getPosX();
        int charY = (int) obj.getPosY();

        int[] gridCoords = this.getMapGridCoordAtPos(charX, charY);
        int charCol = gridCoords[0];
        int charRow = gridCoords[1] - 1; // For reasons unknown, -1 is required here.

        for (int colOffset = -1; colOffset <= 1; colOffset++) {

            int col = charCol + colOffset;
            int row = charRow + 1;

            if (collidesWithTerrainType(landCheck, (int) obj.getPosX(), (int) obj.getPosY())) {

                PVector blockPos = this.getPosAtMapGridCoord(col, row);

                if (blockPos.x - charX > (float) (obj.getDiameter() / 2))
                    continue;
                if (charX - (blockPos.x + GameConfig.MAP_TILE_SIZE) > ((float) obj.getDiameter() / 2))
                    continue;
                if (blockPos.y - charY > (float) (obj.getDiameter() / 2))
                    continue;
                if (charY - (blockPos.y + GameConfig.MAP_TILE_SIZE) > (float) (obj.getDiameter() / 2))
                    continue;

                return (blockPos.y - (float) (obj.getDiameter() / 2));

            }

        }

        return -1;

    }


    // ########################################################################
    // Map Utility Methods:
    // ########################################################################

    public PVector getPlayerRandPos(GameConfig.Terrain terrainType) {

        // Given a terrain type, get the position of a random tile of that type in the map.
        // Naive implementation: pick a tile at random, test for the terrain, return pos if satisfied, otherwise generate again.

        boolean validPosFound = false;
        PVector randValidPos = null;

        while(!validPosFound) {

            int tileX = (int) Game.sketch.random(0, this.mapGridWidth);
            int tileY = (int) Game.sketch.random(0, this.mapGridHeight);

            if (mapGrid[tileX][tileY] == terrainType) {

                // Get map position of the tile. Actually return the centre of the tile to avoid tile boundary issues.
                randValidPos = this.getPosAtMapGridCoord(tileX, tileY);
                randValidPos.x = randValidPos.x + (float) (GameConfig.MAP_TILE_SIZE / 2);
                randValidPos.y = randValidPos.y + (float) (GameConfig.MAP_TILE_SIZE / 2);

                // Game-Play Optimisation: SPawn on screen and at least don't respawn the player by the final boss.
                // NOTE: May still spawn by other enemies, so further optimisation could be implemented.
                if (randValidPos.x > 0 && randValidPos.x < GameConfig.CANVAS_WIDTH &&
                    randValidPos.y > 0 && randValidPos.y < GameConfig.CANVAS_HEIGHT &&
                    !gameState.getFlagship().isTargetInRange(randValidPos)) {

                    validPosFound = true;

                }

            }

        }

        return randValidPos;

    }

    public PVector getPosAtMapGridCoord(int col, int row) {

        float x = (float) (col - buffer / 2) * GameConfig.MAP_TILE_SIZE - xROff;
        float y = (float) (row - buffer / 2) * GameConfig.MAP_TILE_SIZE - yROff;

        return new PVector(x, y);

    }

    public int[] getMapGridCoordAtPos(int x, int y) {

        int[] coords = new int[2];

        coords[0] = (int) ((x + xROff) / GameConfig.MAP_TILE_SIZE) + (buffer / 2);
        coords[1] = (int) ((y + yROff) / GameConfig.MAP_TILE_SIZE) + (buffer / 2);

        return coords; // Coords of x value in 0, coord of y value in 1.

    }

    public GameConfig.Terrain getTerrainAtPos(int x, int y) {

        // Convert position into tile co-ordinate and return terrain at the given tile co-ordinate.
        int[] coords = getMapGridCoordAtPos(x, y);

        // Guard Condition: Make sure given map grid co-ordinates are within the map or we will get an error.
        if (coords[0] < 0 || coords[0] >= gameState.getMap().getMapGridWidth() ||
            coords[1] < 0 || coords[1] >= gameState.getMap().getMapGridHeight()) return null;

        GameConfig.Terrain terrainAtPos = mapGrid[coords[0]][coords[1]];
        return terrainAtPos;

    }

    public boolean isLandAtPos(int x, int y) {

        GameConfig.Terrain terrainAtPos = this.getTerrainAtPos(x, y);
        return (terrainAtPos == GameConfig.Terrain.SAND || terrainAtPos == GameConfig.Terrain.GRASS);

    }

    public boolean isWaterAtPos(int x, int y) {

        GameConfig.Terrain terrainAtPos = this.getTerrainAtPos(x, y);
        return (terrainAtPos == GameConfig.Terrain.DEEP_WATER || terrainAtPos == GameConfig.Terrain.SHALLOW_WATER);

    }

    public int getMapGridHeight() {
        return mapGridHeight;
    }

    public int getMapGridWidth() {
        return mapGridWidth;
    }


}
