package org.getmarco.view;

import java.util.ArrayList;
import java.util.List;

import org.getmarco.Game;

import io.bretty.console.view.AbstractView;
import io.bretty.console.view.MenuView;
import io.bretty.console.view.ViewConfig;

/**
 * This view allows a player to eliminate a restaurant from the list
 */
public class EliminateView extends GameView {
    private Game game;
    private String player;

    // The restaurant to eliminate
    private String restaurant;

    public EliminateView(Game game, String player) {
        super("* Eliminating restaurants", "Eliminate restaurants");
        if (game == null)
            throw new IllegalArgumentException("null game");
        if (player == null)
            throw new IllegalArgumentException("null player");
        this.game = game;
        this.player = player;
    }

    @Override
    public void executeCustomAction() {
        ViewConfig viewConfig = new ViewConfig.Builder().setBackMenuName("Quit").build();
        do {
            List<String> restaurants = new ArrayList<>(this.game.getRestaurants());

            if (restaurants.isEmpty()) {
                this.println("Oops, there are no restaurants left. Quitting ...");
                this.game.quit();
                return;
            }

            MenuView menuView = new MenuView(player + ", please select a restaurant to eliminate, or pass",
              "Eliminate restaurant for " + player, viewConfig) {
                @Override
                protected void onBack() {
                    // onBack will be called when quit instead of onQuit since menu view has parent
                    EliminateView.this.restaurant = null;
                    EliminateView.this.game.quit();
                }
            };
            menuView.setParentView(this);
            for (String name : restaurants)
                menuView.addMenuItem(this.eliminateRestaurantMenuItem(name));
            menuView.addMenuItem(this.passMenuItem());
            menuView.addMenuItem(this.restartMenuItem());
            menuView.display();

            if (this.restaurant == null) {
                // Player passed (or restarted or quit)
                return;
            }
            if (this.confirmDialog("Are you sure you want to eliminate '" + this.restaurant + "'?"))
                break;
        } while (true);

        if (this.restaurant != null)
            eliminateRestaurant(this.restaurant);
    }

    private AbstractView eliminateRestaurantMenuItem(String name) {
        return new AbstractView("Eliminated " + name, "Eliminate " + name) {
            @Override
            public void display() {
                EliminateView.this.restaurant = name;
            }
        };
    }

    private AbstractView passMenuItem() {
        return new AbstractView("Passed", "Pass") {
            @Override
            public void display() {
                EliminateView.this.restaurant = null;
            }
        };
    }

    private AbstractView restartMenuItem() {
        return new AbstractView("Restarting", "Restart") {
            @Override
            public void display() {
                EliminateView.this.restaurant = null;
                EliminateView.this.game.back();
            }
        };
    }

    private void eliminateRestaurant(String restaurantName) {
        this.game.getRestaurants().remove(restaurantName);
    }
}
