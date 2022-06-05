import java.util.ArrayList;

public class ObjLoot extends ObjGameGeneric {


    // ########################################################################
    // Loot Attributes:
    // ########################################################################

    private int goldAward;


    // ########################################################################
    // Loot Constructors:
    // ########################################################################

    public ObjLoot(float posXInit, float posYInit, float maxSpeed, int diameter, int colour, int goldAward,
                         boolean willDeSpawn, GameState gamestate) {

        // Game Object Constructor; cannons will move with a ship or are stationary as part of a fort.
        super(posXInit, posYInit, maxSpeed, diameter, colour, willDeSpawn, gamestate);

        // Loot Attribute Instantiation:
        this.goldAward = goldAward;

    }


    // ########################################################################
    // Loot Update Methods:
    // ########################################################################

    public boolean update() {

        boolean removeCollected = this.collisionResolution(); // Loot hit detection.
        boolean removeDeSpawn = super.update(); // Update this loot as an instance of ObjGameGeneric.

        return (removeCollected || removeDeSpawn);

    }

    private boolean collisionResolution() {

        // Loot can only be collected by the player, so collision resolution is very simple.
        if (this.collide(gameState.getPlayer())) {

            gameState.getPlayer().addGold(this.goldAward);
            // To game balance, also have loot crates add volleys as well!
            gameState.getPlayer().getLeftCannon().addNumVolleys(this.goldAward);
            gameState.getPlayer().getRightCannon().addNumVolleys(this.goldAward);

            Game.gameSound.soundMoneyCollect(true); // Play sound of the gold being collected.

            return true;
        }

        return false;

    }

    // ########################################################################
    // Loot Render/Display Methods:
    // ########################################################################

    public void display() {

        // Display loot as rectangle as though they are crates.
        Game.sketch.fill(this.getCol());
        Game.sketch.stroke(0); // Outline looks better and more identifiable.

        Game.sketch.rect(this.getPosX() - (float) (this.getDiameter() / 2),
                         this.getPosY() - (float)(this.getDiameter() / 2),
                            this.getDiameter(), this.getDiameter());

    }


}
