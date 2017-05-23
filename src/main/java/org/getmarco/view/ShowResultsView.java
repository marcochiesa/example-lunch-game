package org.getmarco.view;

import org.getmarco.Game;

/**
 * This view shows the results of the voting. The restaurants will be shown in
 * descending order of votes. Restaurants with the same number of votes will be
 * shown in alphabetical order. The restaurant with the most votes will be
 * labelled as the 'winner'. If the voting resulted in a tie (the highest vote
 * count is shared by more than one choice), then the tied restaurants will be
 * labelled 'tie'.
 */
public class ShowResultsView extends GameView {
    private Game game;

    public ShowResultsView(Game game) {
        super("* Showing restaurant voting results", "Show restaurant vote results");
        if (game == null)
            throw new IllegalArgumentException("null game");
        this.game = game;
    }

    @Override
    public void executeCustomAction() {
        int maxVotes = this.game.getMaxVoteCount();
        boolean hasWinner = this.game.isWin();
        for (String restaurant : this.game.getVoteSortedRestaurants()) {
            int votes = this.game.getVotesForRestaurant(restaurant);
            String label = restaurant + " - " + votes + " vote(s)";
            if (votes == maxVotes)
                label += (hasWinner ? " ** winner **" : (votes > 0 ? " ** tie **" : ""));
            this.println(label);
        }
    }
}
