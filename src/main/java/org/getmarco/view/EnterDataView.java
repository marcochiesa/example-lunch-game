package org.getmarco.view;

import java.util.HashSet;
import java.util.Set;

import org.getmarco.Game;
import org.getmarco.util.UniqueNameValidator;

/**
 * This view prompts for the player name and the name of the restaurants that
 * the user wants to suggest.
 */
public class EnterDataView extends GameView {
    private Game game;

    public EnterDataView(Game game) {
        super("* Entering player information", "Enter player information");
        if (game == null)
            throw new IllegalArgumentException("null game");
        this.game = game;
    }

    @Override
    public void executeCustomAction() {
        Set<String> currentPlayers = new HashSet<>(this.game.getPlayers());
        int nextPlayerNum = currentPlayers.size() + 1;

        String name;
        do {
            name = this.prompt("Please enter name for player " + nextPlayerNum + " (or blank to restart): ", String.class,
              new UniqueNameValidator(currentPlayers));
            if (name != null && !name.isEmpty())
                break;
            if (this.confirmDialog("Are you sure you want to restart?")) {
                this.game.back();
                return;
            }
        } while (true);
        currentPlayers.add(name.trim());

        Set<String> restaurants = new HashSet<>();
        for (int i = 1; i <= game.getNumRestaurantsPerPlayer(); i++) {
            String restaurant;
            do {
                restaurant = this.prompt("Please enter restaurant " + i + " for player '" + name + "' (or blank to restart): ",
                  String.class, new UniqueNameValidator(restaurants));
                if (restaurant != null && !restaurant.isEmpty())
                    break;
                if (this.confirmDialog("Are you sure you want to restart?")) {
                    this.game.back();
                    return;
                }
            } while (true);
            restaurants.add(restaurant.trim());
        }

        this.game.setPlayers(currentPlayers);
        this.game.addRestaurants(restaurants);
    }
}
