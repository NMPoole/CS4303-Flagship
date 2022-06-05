import processing.core.PVector;

import static processing.core.PConstants.*;

public class CharacterSiren extends Character {


    // ########################################################################
    // Siren Attributes:
    // ########################################################################

    private float damage;


    // ########################################################################
    // Siren Constructors:
    // ########################################################################

    CharacterSiren(float startX, float startY, float maxSpeed, int diam, int color, int health, boolean willDeSpawn,
                   float damage, GameState gameState) {

        // Initialise with Character class constructor.
        super(GameConfig.CharType.WATER, startX, startY, maxSpeed, diam, color, health, willDeSpawn, gameState);

        // Siren Attribute Initialisation:
        this.damage = damage;
        this.setAwareRadius((int) (this.getAwareRadius() * GameConfig.SIREN_AWARE_RAD_MULT));
        this.setOri(Game.sketch.random(0, 2 * PI));

    }


    // ########################################################################
    // Siren Update Methods:
    // ########################################################################

    public boolean update() {

        // Decision Tree:
        //      If aware of the player, then pull the player in with random probability by halting them and enacting force.
        //      Otherwise, do nothing.

        CharacterShipPlayer player = (CharacterShipPlayer) super.awareOfPlayer();
        if (player != null) { // The siren is aware of the player.

            PVector playerToSirenVector = this.getPos().copy().sub(player.getPos());

            this.setOri(playerToSirenVector.copy().mult(-1).heading()); // Update orientation to show aware of the player.

            float randProb = Game.sketch.random(0, 1);
            if (randProb < GameConfig.SIREN_PROB_HALT_PLYR) {

                player.halt(); // Halt the player - removes all velocity and removes point to seek.

                PVector pullForce = playerToSirenVector.normalize().mult(GameConfig.SIREN_FORCE_MULT);
                player.applyForce(pullForce); // Apply pulling force.

            }

            Game.gameSound.soundSirenSong(true);

        }

        boolean remove = super.update(); // Update this enemy siren's movement as an instance of a Character.
        boolean removeHit = this.collisionResolution(); // Apply collision resolution for this shark.

        return (remove || removeHit); // Whether this object should be removed (i.e., de-spawning or death).

    }

    private boolean collisionResolution() {

        // Only worry about collisions with the player, so collision resolution is very simple.
        if (this.collide(gameState.getPlayer())) {
            gameState.getPlayer().subHealth(this.damage);
            return true;
        }

        return false;

    }


    // ########################################################################
    // Siren Render/Draw Methods:
    // ########################################################################

    public void display() {

        this.displaySirenGeom(); // Display the siren.

        super.display(); // Display attributes all characters should display (e.g., health).

    }

    private  void displaySirenGeom() {

        // Calculate and apply rotation of model to point at aim.

        Game.sketch.translate(this.getPosX(), this.getPosY()); // Translate co-ordinate origin to position.
        Game.sketch.rotate(this.getOri() + HALF_PI); // Rotate model to point in aiming direction.

        Game.sketch.fill(this.getCol());
        Game.sketch.stroke(0);

        // Draw model.
        Game.sketch.triangle((float) -this.getDiameter() / 2, (float) this.getDiameter() / 2,
                (float) this.getDiameter() / 2, (float) this.getDiameter() / 2,
                0, -(2 * (float) (this.getDiameter() / 2)));

        // Reset the origin.
        Game.sketch.rotate(-(this.getOri() + HALF_PI));
        Game.sketch.translate(-this.getPosX(), -this.getPosY());

    }


}
