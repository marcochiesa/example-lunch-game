package org.getmarco.view;

import java.util.ArrayList;
import java.util.List;

import org.getmarco.Game;

import io.bretty.console.view.AbstractView;
import io.bretty.console.view.MenuView;
import io.bretty.console.view.ViewConfig;


/**
 * This view allows a player to vote for restaurant choices.
 */
public class VoteView extends GameView {
    private Game game;
    private String player;
    private boolean doneVoting;
    private boolean restartVoting; //bail immediately
    private List<String> votes = new ArrayList<>();

    public VoteView(Game game, String player) {
        super("* Voting for restaurants", "Vote for restaurants");
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
                this.println("Oops, there are no restaurants to vote for. Quitting ...");
                this.game.quit();
                return;
            }

            this.doneVoting = false;
            while (!this.doneVoting) {
                int votesLeft = this.game.getNumVotesPerPlayer() - this.votes.size();
                String votePrompt = player + ", please vote for a restaurant, or pass - " + votesLeft + " vote(s) remaining";
                final MenuView menuView = new MenuView(votePrompt, "Vote for " + player, viewConfig) {
                    @Override
                    protected void onBack() {
                        // onBack will be called when quit instead of onQuit since menu view has parent
                        VoteView.this.game.quit();
                    }
                };
                menuView.setParentView(this);
                for (String restaurant : restaurants) {
                    menuView.addMenuItem(this.voteRestaurantMenuItem(restaurant));
                }
                menuView.addMenuItem(this.passMenuItem());
                menuView.addMenuItem(this.restartMenuItem());
                menuView.display();
                if (this.restartVoting || this.game.isGameOver()) {
                    // State changed need to bail
                    return;
                }
                if (this.votes.size() == this.game.getNumVotesPerPlayer())
                    this.doneVoting = true;
            }
            printVotes(this.votes);

            if (votes.isEmpty()) {
                // Player passed on voting
                if (this.confirmDialog("Are you sure you don't want to vote?"))
                    return;
            } else if (this.votes.size() < this.game.getNumVotesPerPlayer()) {
                // Player passed on additional voting
                if (this.confirmDialog("Are you sure you're done?"))
                    break;
            } else {
                // Player finished voting
                if (this.confirmDialog("Do you want to try again?"))
                    this.votes.clear(); // Do-over
                else
                    break;
            }
        } while (true);

        for (String vote : this.votes)
            this.game.vote(vote);
    }

    private AbstractView voteRestaurantMenuItem(String restaurant) {
        String label = "Vote for " + restaurant;
        if (this.votes.contains(restaurant))
            label += " - 1 vote";
        return new AbstractView("Voted for " + restaurant, label) {
            @Override
            public void display() {
                if (VoteView.this.votes.contains(restaurant)) {
                    VoteView.this.println("Duplicate votes are not allowed, try again");
                    return;
                }
                VoteView.this.votes.add(restaurant);
            }
        };
    }

    private AbstractView passMenuItem() {
        return new AbstractView("Passed", "Pass") {
            @Override
            public void display() {
                // Player passing on (additional) voting
                VoteView.this.doneVoting = true;
            }
        };
    }

    private AbstractView restartMenuItem() {
        return new AbstractView("Restarting Voting", "Restart Voting") {
            @Override
            public void display() {
                VoteView.this.restartVoting = true;
                VoteView.this.game.resetVoting();
                VoteView.this.game.back();
            }
        };
    }

    private void printVotes(List<String> votes) {
        if (votes.isEmpty())
            return;
        String msg = "";
        for (String vote : votes) {
            if (msg.length() > 0)
                msg += ", ";
            msg += vote;
        }
        msg = "You used " + votes.size() + " vote(s): " + msg;
        this.println(msg);
    }
}
