package org.getmarco.view;

import org.getmarco.Game;

/**
 * This view will show a list of the restaurant names that were entered
 */
public class ShowDataView extends GameView {
    private Game game;

    public ShowDataView(Game game) {
        super("* Showing restaurant names", "Show restaurant names");
        if (game == null)
            throw new IllegalArgumentException("null game");
        this.game = game;
    }

    @Override
    public void executeCustomAction() {
        this.println("There are " + this.game.getRestaurants().size() + " (unique) restaurants");
        for (String restaurant : this.game.getRestaurants())
            this.println(restaurant);
    }
}
