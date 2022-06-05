import processing.core.PVector;
import processing.core.PConstants;

import java.util.HashMap;

public class Shop {


    // ########################################################################
    // Shop Attributes:
    // ########################################################################

    private final GameState gameState; // Reference to the game state for use.

    private final HashMap<GameConfig.SHOP_OPTION, PVector[]> shopOptionCoords; // Co-ordinates of the shop options for upgrades.


    // ########################################################################
    // Shop Constructors:
    // ########################################################################

    public Shop(GameState gameState) {
        this.gameState = gameState;
        this.shopOptionCoords = new HashMap<>(); // Co-ordinates of the shop options when the game is paused.
    }


    // ########################################################################
    // Shop Update Methods:
    // ########################################################################

    public void purchase(PVector mouseSelection) {

        // Determine if on eof the shop option buttons has been selected.
        shopOptionCoords.forEach((optionType, optionBoxBounds) -> {

            PVector topLeft = optionBoxBounds[0];
            PVector bottomRight = optionBoxBounds[1];

            // Is the mouse selection for this option.
            if (mouseSelection.x >= topLeft.x && mouseSelection.x <= bottomRight.x &&
                    mouseSelection.y >= topLeft.y && mouseSelection.y <= bottomRight.y) {

                int playerGold = gameState.getPlayer().getGold();

                // Enact purchase behaviour depending on the option corresponding to this selected box.
                switch(optionType) {
                    case INCR_HP -> this.purchaseHealth(playerGold);
                    case INCR_VOLLEYS -> this.purchaseVolleys(playerGold);
                    case INCR_RANGE -> this.purchaseRange(playerGold);
                    case INCR_SPR_SHOT -> this.purchaseSpreadShot(playerGold);
                    case INCR_BALL_DMG -> this.purchaseCBallDamage(playerGold);
                }

            }

        });

    }

    private void purchaseHealth(int playerGold) {

        int cost = GameConfig.INCR_HP_COST;
        int amount = GameConfig.INCR_HP_AMOUNT;

        if (playerGold < cost) return; // Guard clause; player must have sufficient gold to purchase.

        // Player has enough gold: increase player HP by amount and decrease gold by cost.
        gameState.getPlayer().upgradeHealth(amount);
        gameState.getPlayer().subGold(cost);

    }

    private void purchaseVolleys(int playerGold) {

        int cost = GameConfig.INCR_VOLLEYS_COST;
        int amount = GameConfig.INCR_VOLLEYS_AMOUNT;

        if (playerGold < cost) return; // Guard clause; player must have sufficient gold to purchase.

        // Player has enough gold; add volleys spread across each cannon and decrease gold by cost.
        int volleysPerCannon = amount / 2; // Evenly assign purchased volleys amongst cannons.
        gameState.getPlayer().getLeftCannon().addNumVolleys(volleysPerCannon);
        gameState.getPlayer().getRightCannon().addNumVolleys(volleysPerCannon);
        gameState.getPlayer().subGold(cost);

    }

    private void purchaseRange(int playerGold) {

        int cost = GameConfig.INCR_RANGE_COST;
        int amount = GameConfig.INCR_RANGE_AMOUNT;

        if (playerGold < cost) return; // Guard clause; player must have sufficient gold to purchase.

        // Player has enough gold; increase range for each of the player's cannons and reduce player gold by cost.
        gameState.getPlayer().getLeftCannon().addCannonRange(amount);
        gameState.getPlayer().getRightCannon().addCannonRange(amount);
        gameState.getPlayer().subGold(cost);

    }

    private void purchaseSpreadShot(int playerGold) {

        int cost = GameConfig.INCR_SPR_SHOT_COST;
        int amount = GameConfig.INCR_SPR_SHOT_AMOUNT;

        if (playerGold < cost) return; // Guard clause; player must have sufficient gold to purchase.

        // Player has enough gold; add amount of cannonballs per volley to each cannon and decrease gold by cost.
        gameState.getPlayer().getLeftCannon().addNumBallsPerVolley(amount);
        gameState.getPlayer().getRightCannon().addNumBallsPerVolley(amount);
        gameState.getPlayer().subGold(cost);

    }

    private  void purchaseCBallDamage(int playerGold) {

        int cost = GameConfig.INCR_BALL_DMG_COST;
        int amount = GameConfig.INCR_BALL_DMG_AMOUNT;

        if (playerGold < cost) return; // Guard clause; player must have sufficient gold to purchase.

        // Player has enough gold; increase players' damage per ball in each cannon and decrease their gold by cost.
        gameState.getPlayer().getLeftCannon().addDamagePerBall(amount);
        gameState.getPlayer().getRightCannon().addDamagePerBall(amount);
        gameState.getPlayer().subGold(cost);

    }


    // ########################################################################
    // Shop Render/Draw Methods:
    // ########################################################################

    public void display() {

        // ##################
        // Shop Boundary Box:
        // ##################

        int shopMiddleX = GameConfig.CANVAS_WIDTH / 2;
        int shopY = GameConfig.CANVAS_HEIGHT / 7;
        int shopWidth = GameConfig.CANVAS_WIDTH / 2;
        int shopHeight = 5 * shopY;

        Game.sketch.fill(GameConfig.SHOP_BACK_COL);
        Game.sketch.stroke(0);
        Game.sketch.strokeWeight(5);

        Game.sketch.rect(shopMiddleX - (float) (shopWidth / 2), shopY, shopWidth, shopHeight);

        // ##################
        // Shop Title:
        // ##################

        Game.sketch.textAlign(PConstants.CENTER);
        Game.sketch.fill(255);
        Game.sketch.textSize(GameConfig.TITLE_TEXT_SIZE * 2);
        shopY += 50;
        Game.sketch.text("MERCHANT", shopMiddleX, shopY);

        Game.sketch.textSize(GameConfig.TITLE_TEXT_SIZE);
        shopY += 30;
        Game.sketch.text(" (Purchase Upgrades For Your Ship)", shopMiddleX, shopY);

        // ##################
        // Available Gold:
        // ##################

        Game.sketch.fill(GameConfig.GOLD_COL);
        Game.sketch.textSize(GameConfig.TITLE_TEXT_SIZE);
        shopY += 40;
        Game.sketch.text("GOLD: " + gameState.getPlayer().getGold(), shopMiddleX, shopY);

        // ##################
        // Upgrade Options:
        // ##################

        int optionWidth = (int) (shopWidth * 0.9);
        int optionHeight = 80;

        shopY += 60;
        for (GameConfig.SHOP_OPTION currOption : GameConfig.SHOP_OPTION.values()) {

            // Option box with mouse feedback:
            PVector currOptionTopLeft = new PVector(shopMiddleX - (float) (optionWidth / 2), shopY - (float) (optionHeight / 2));
            PVector currOptionBottomRight = new PVector(shopMiddleX + (float) (optionWidth / 2), shopY + (float) (optionHeight / 2));

            // Setting option box colour for feedback.
            int optionColour = Game.sketch.color(0, 128, 0);
            if (Game.sketch.mouseX > currOptionTopLeft.x && Game.sketch.mouseX < currOptionBottomRight.x &&
                    Game.sketch.mouseY > currOptionTopLeft.y && Game.sketch.mouseY < currOptionBottomRight.y) {
                optionColour = Game.sketch.color(50,205,50); // Hovering an option feedback.
            } if (this.getOptionCost(currOption) > gameState.getPlayer().getGold()) {
                optionColour = Game.sketch.color(255, 123, 123, 127); // Cannot afford option feedback.
            }
            Game.sketch.fill(optionColour);
            Game.sketch.rect(currOptionTopLeft.x, currOptionTopLeft.y, optionWidth, optionHeight);

            // Option text:
            String optionText = getOptionText(currOption);
            Game.sketch.textSize(GameConfig.TITLE_TEXT_SIZE);
            Game.sketch.fill(255);
            Game.sketch.text(optionText, shopMiddleX, shopY - 10);

            // Add current shop option to the co-ordinates of shop options for mouse interaction.
            PVector[] currOptionCoords = {currOptionTopLeft, currOptionBottomRight};
            shopOptionCoords.put(currOption, currOptionCoords);

            shopY += optionHeight + 10;

        }

        Game.sketch.strokeWeight(1); // Reset stroke weight to default.

    }


    // ########################################################################
    // Shop Utility Methods:
    // ########################################################################

    private int getOptionCost(GameConfig.SHOP_OPTION shopOption) {

        int optionCost = -1;

        switch (shopOption) {
            case INCR_HP -> optionCost = GameConfig.INCR_HP_COST;
            case INCR_VOLLEYS -> optionCost = GameConfig.INCR_VOLLEYS_COST;
            case INCR_RANGE -> optionCost = GameConfig.INCR_RANGE_COST;
            case INCR_SPR_SHOT -> optionCost = GameConfig.INCR_SPR_SHOT_COST;
            case INCR_BALL_DMG -> optionCost = GameConfig.INCR_BALL_DMG_COST;
        }

        return optionCost;

    }

    private String getOptionText(GameConfig.SHOP_OPTION shopOption) {

        String optionText = "";

        switch (shopOption) {
            case INCR_HP ->
                    optionText = GameConfig.INCR_HP_TXT +
                            "\nCost: " + GameConfig.INCR_HP_COST + " | " +
                            "Gain: " + GameConfig.INCR_HP_AMOUNT + " | " +
                            "Current: " + gameState.getPlayer().getCurrHealth() + " | " +
                            "After: " + (gameState.getPlayer().getCurrHealth() + GameConfig.INCR_HP_AMOUNT);
            case INCR_VOLLEYS ->
                    optionText = GameConfig.INCR_VOLLEYS_TXT +
                            "\nCost: " + GameConfig.INCR_VOLLEYS_COST + " | " +
                            "Gain: " + GameConfig.INCR_VOLLEYS_AMOUNT + " | " +
                            "Current: " + gameState.getPlayer().getTotalNumVolleys() + " | " +
                            "After: " + (gameState.getPlayer().getTotalNumVolleys() + GameConfig.INCR_VOLLEYS_AMOUNT);
            case INCR_RANGE ->
                    optionText = GameConfig.INCR_RANGE_TXT +
                            "\nCost: " + GameConfig.INCR_RANGE_COST + " | " +
                            "Gain: " + GameConfig.INCR_RANGE_AMOUNT + " | " +
                            "Current: " + gameState.getPlayer().getCannonRange() + " | " +
                            "After: " + (gameState.getPlayer().getCannonRange() + GameConfig.INCR_RANGE_AMOUNT);
            case INCR_SPR_SHOT ->
                    optionText = GameConfig.INCR_SPR_SHOT_TXT +
                            "\nCost: " + GameConfig.INCR_SPR_SHOT_COST + " | " +
                            "Gain: " + GameConfig.INCR_SPR_SHOT_AMOUNT + " | " +
                            "Current: " + gameState.getPlayer().getNumBallsPerVolley() + " | " +
                            "After: " + (gameState.getPlayer().getNumBallsPerVolley() + GameConfig.INCR_SPR_SHOT_AMOUNT);
            case INCR_BALL_DMG ->
                    optionText = GameConfig.INCR_BALL_DMG_TXT +
                            "\nCost: " + GameConfig.INCR_BALL_DMG_COST + " | " +
                            "Gain: " + GameConfig.INCR_BALL_DMG_AMOUNT + " | " +
                            "Current: " + gameState.getPlayer().getDamagePerBall() + " | " +
                            "After: " + (gameState.getPlayer().getDamagePerBall() + GameConfig.INCR_BALL_DMG_AMOUNT);
        }

        return optionText;

    }


}
