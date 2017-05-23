package org.getmarco.view;

import org.getmarco.Game;
import org.getmarco.util.HasQuantityValidator;

/**
 * This view prompts for the number of users and the number of restaurants that
 * each user may suggest.
 */
public class SetupView extends GameView {

    private Game game;

    public SetupView(Game game) {
        super("* Setting game up", "Setup game");
        if (game == null)
            throw new IllegalArgumentException("null game");
        this.game = game;
    }

    @Override
    public void executeCustomAction() {
        boolean confirmed = false;
        do {
            this.game.setNumPlayers(this.prompt("Please enter number of players: ", Integer.class, new HasQuantityValidator()));
            this.game.setNumRestaurantsPerPlayer(this.prompt("Please enter number of restaurants per player: ", Integer.class,
              new HasQuantityValidator()));
            confirmed = this.confirmDialog(this.game.getNumPlayers() + " players, " + this.game.getNumRestaurantsPerPlayer()
              + " restaurants per player - is this correct?");
        } while (!confirmed);
    }
}
