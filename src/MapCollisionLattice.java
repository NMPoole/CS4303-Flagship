
import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PApplet.ceil;

public class MapCollisionLattice {


    // ########################################################################
    // Collision Lattice Attributes:
    // ########################################################################

    private final Map map; // Reference to the game map.

    private final int latticeWidth; // Width of the bin lattice.
    private final int latticeHeight; // Height of the bin lattice

    private ArrayList<ObjGameGeneric>[][] lattice; // Lattice with each cell corresponding to list of objects in said bin.


    // ########################################################################
    // Collision Lattice Constructors:
    // ########################################################################

    public MapCollisionLattice(Map map) {

        this.map = map; // Reference to the game map.

        this.latticeWidth = ceil((float) this.map.getMapGridWidth() / GameConfig.LATTICE_RES);
        this.latticeHeight = ceil((float) this.map.getMapGridHeight() / GameConfig.LATTICE_RES);

        this.reset(); // Initialise the lattice array.

    }


    // ########################################################################
    // Collision Lattice Update Methods:
    // ########################################################################

    public void reset() {
        this.lattice = new ArrayList[latticeHeight][latticeWidth];
    }

    public void register(ObjGameGeneric obj) {

        // Get co-ordinate of current obj in the lattice given its position.
        int[] latticeGridCoord = this.getLatticeCoordAtPos((int) obj.getPosX(), (int) obj.getPosY());
        if (latticeGridCoord == null) return; // Cannot register if not at location valid for lattice.

        // Check if a list of objects has been created for the bin in the lattice - if not, create one.
        if (lattice[latticeGridCoord[1]][latticeGridCoord[0]] == null) {
            lattice[latticeGridCoord[1]][latticeGridCoord[0]] = new ArrayList<>();
        }

        // Add the object to the bin in the lattice.
        lattice[latticeGridCoord[1]][latticeGridCoord[0]].add(obj);

    }

    public ArrayList<ObjGameGeneric> nearbyObjects(ObjGameGeneric obj) {

        int[] latticeGridCoord = this.getLatticeCoordAtPos((int) obj.getPosX(), (int) obj.getPosY());
        if (latticeGridCoord == null) return new ArrayList<>(); // Empty array list if not in lattice - should not handle collisions.

        ArrayList<ObjGameGeneric> nearbyLatticeObjects = new ArrayList<>();

        // Add all objects in the current lattice cell and all immediate neighbours.
        for (int i = latticeGridCoord[1] - 1; i <= latticeGridCoord[1] + 1; i++) {

            if (i < 0 || i >= this.latticeHeight) continue; // Edge of lattice condition - do not go out-of-bounds.

            for (int j = latticeGridCoord[0] - 1; j <= latticeGridCoord[0] + 1; j++) {

                if (j < 0 || j >= this.latticeWidth) continue; // Edge of lattice condition - do not go out-of-bounds.

                if (lattice[i][j] == null) continue; // If no objects in cell, then skip this lattice cell.

                // Add all objects in this current lattice cell to the set of nearby objects to return.
                nearbyLatticeObjects.addAll(lattice[i][j]);

            }

        }

        // Returned list of nearby game objects to an object should not contain itself.
        // Actually, this is true for collision detection but not true for, say, flocking, so don't assume here.
        //nearbyLatticeObjects.remove(obj);

        return nearbyLatticeObjects;

    }


    // ########################################################################
    // Collision Lattice Render/Draw Methods:
    // ########################################################################

    public void display() {

        Game.sketch.stroke(255, 255, 255, 50);

        // Display the bin lattice spatial sub-division.
        for (int col = 0; col < this.latticeWidth; col++) {
            for (int row = 0; row < this.latticeHeight; row++) {

                // Highlight lattice cell if it contains game objects.
                if (this.lattice[row][col] != null) Game.sketch.fill(255, 255, 255, 50);
                else Game.sketch.noFill();

                // Calculate on screen co-ordinates of this lattice.
                PVector cellMapGridCoord = this.map.getPosAtMapGridCoord(col * GameConfig.LATTICE_RES, row * GameConfig.LATTICE_RES);
                int rectSize = GameConfig.LATTICE_RES * GameConfig.MAP_TILE_SIZE;

                // Show lattice cell on the canvas.
                Game.sketch.rect(cellMapGridCoord.x, cellMapGridCoord.y, rectSize, rectSize);

            }
        }

    }


    // ########################################################################
    // Collision Lattice Utility Methods:
    // ########################################################################

    private int[] getLatticeCoordAtPos(float x, float y) {

        int[] latticeCoord = new int[2];

        // Convert object position to a map grid co-ordinate (i.e., which tile is the object in).
        int[] mapGridCoord = this.map.getMapGridCoordAtPos((int) x, (int) y);

        if (mapGridCoord[0] <= 0 || mapGridCoord[0] >= this.map.getMapGridWidth() ||
                mapGridCoord[1] <= 0 || mapGridCoord[1] >= this.map.getMapGridHeight()) {
            // Objects out the map do not need to have collisions enforced, their interactions should occur in map.
            return null;
        }

        // Convert tile of object to a given bin within the lattice.
        latticeCoord[0] = mapGridCoord[0] / GameConfig.LATTICE_RES;
        latticeCoord[1] = mapGridCoord[1] / GameConfig.LATTICE_RES;

        return latticeCoord;

    }


}
