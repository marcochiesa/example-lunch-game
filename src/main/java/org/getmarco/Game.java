package org.getmarco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.getmarco.state.GameState;
import org.getmarco.state.GameStateFactory;
import org.getmarco.state.StateType;

/**
 * This class maintains the data and state of the current game run
 */
public class Game {
    private static final int NUM_VOTES_PER_PLAYER = 3; //hard-code this rule for now (positive integer)

    private final Logger logger = LogManager.getLogger(this.getClass());

    private boolean gameOver;
    private GameState state;
    private int numPlayers;
    private int numRestaurantsPerPlayer;
    private SortedSet<String> players = new TreeSet<>();
    private SortedMap<String, Integer> restaurants = new TreeMap<>();

    public Game() {
        this.changeState(StateType.START);
    }

    /**
     * Play one 'stage' of the game
     */
    public void play() {
        GameState currentState = this.state;
        logger.trace("Begin turn, state is '" + currentState.getClass().getSimpleName() + "'");
        StateType nextState = currentState.doAction(this);
        if (!this.stateChanged(currentState)) {
            // Unless player changed state during view via back, quit, etc.
            this.changeState(nextState);
        }
        logger.trace("Turn finished, new state is '" + this.state.getClass().getSimpleName() + "'");
    }

    /**
     * Choose the 'back' action.
     */
    public void back() {
        this.changeState(this.state.back());
    }

    /**
     * Choose the 'quit' action.
     */
    public void quit() {
        this.changeState(this.state.quit());
        this.resetState();
        this.gameOver = true;
    }

    /**
     * Has game state changed from the given state. Useful to confirm
     * expectation of state before doing something.
     * @param state the expected state
     * @return true if expectation was wrong
     */
    public boolean stateChanged(GameState state) {
        // Object identity for now
        return this.state != state;
    }

    /**
     * Use this method to effect a state transition in the game play
     * @param state the state to transition to
     */
    public void changeState(StateType state) {
        if (state == null)
            throw new IllegalArgumentException("null state type");
        if (this.isGameOver())
            throw new IllegalStateException("game is over");
        this.state = GameStateFactory.create(state);
    }

    /**
     * How many players are participating in this game run
     * @return the number of players
     */
    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    /**
     * How many restaurants can each player suggest for the game run
     * @return the maximum number of restaurant suggestions per player
     */
    public int getNumRestaurantsPerPlayer() {
        return numRestaurantsPerPlayer;
    }

    public void setNumRestaurantsPerPlayer(int numRestaurantsPerPlayer) {
        this.numRestaurantsPerPlayer = numRestaurantsPerPlayer;
    }

    /**
     * How many votes can a player cast
     * @return the number of votes per player
     */
    public int getNumVotesPerPlayer() {
        return NUM_VOTES_PER_PLAYER;
    }

    /**
     * Get the names of the players for this game run
     * @return the player names
     */
    public Set<String> getPlayers() {
        return players;
    }

    public void setPlayers(Set<String> players) {
        // Sanity check
        if (players.size() > this.numPlayers)
            throw new IllegalStateException("attempt to add '" + players.size() + "' players (max of '" + this.numPlayers + "')");
        this.players = new TreeSet<>(players);
    }

    /**
     * Get the names of the configured restaurants for this game run
     * @return the restaurant names
     */
    public Set<String> getRestaurants() {
        return this.restaurants.keySet();
    }

    /**
     * Setup the restaurants for this game run
     * @param restaurants the set of restaurants to choose from
     */
    public void addRestaurants(Set<String> restaurants) {
        for (String restaurant : restaurants) {
            if (restaurant == null || "".equals(restaurant))
                continue;
            // Do not accidentally reset vote count for a restaurant
            if (this.restaurants.containsKey(restaurant))
                continue;
            this.restaurants.put(restaurant, 0);
        }
        // Sanity check
        if (this.restaurants.size() > this.numPlayers * this.numRestaurantsPerPlayer) {
            throw new IllegalStateException("'" + this.restaurants.size() + "' restaurants exceeds max ('"
              + this.numRestaurantsPerPlayer + "' per '" + this.numPlayers + "' players)");
        }
    }

    /**
     * Is the game 'over'. Resources are released and state is no longer guaranteed to be consistent.
     * @return true if game is over, else false
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Register a vote for a restaurant
     * @param restaurant the restaurant being voted for
     */
    public void vote(String restaurant) {
        // Sanity check
        if (!this.restaurants.containsKey(restaurant))
            throw new IllegalStateException("vote for unknown restaurant '" + restaurant + "'");

        this.restaurants.put(restaurant, this.restaurants.get(restaurant) + 1);
    }

    /**
     * Get the number of votes for a restaurant.
     * @param restaurant the name of the restaurant
     * @return number of votes (always zero for unknown restaurant names)
     */
    public int getVotesForRestaurant(String restaurant) {
        if (!this.restaurants.containsKey(restaurant))
            return 0;
        return this.restaurants.get(restaurant);
    }

    /**
     * Reset the game state. Clears player, restaurant, and voting information.
     */
    public void resetState() {
        this.numPlayers = 0;
        this.numRestaurantsPerPlayer = 0;
        this.players.clear();
        this.restaurants.clear();
    }

    /**
     * Reset the restaurant vote tally. All restaurants will go back to zero votes.
     */
    public void resetVoting() {
        for (String restaurant : this.restaurants.keySet())
            this.restaurants.put(restaurant, 0);
    }

    /**
     * Get a listing of the restaurant choices sorted in the order of votes
     * they received and subsorted alphabetically by the restaurant name.
     * @return vote sorted alphabetical listing of restaurants
     */
    public List<String> getVoteSortedRestaurants() {
        if (this.restaurants.isEmpty())
            return Collections.<String>emptyList();

        SortedMap<Integer, SortedSet<String>> inverted = this.getInvertedRestaurants();
        List<Integer> counts = new ArrayList<>(inverted.keySet());
        // Natural order is ascending, so reverse to put higher vote counts first (descending)
        Collections.reverse(counts);

        List<String> result = new ArrayList<>();
        for (Integer votes : counts)
            for (String restaurant : inverted.get(votes))
                result.add(restaurant);
        return result;
    }

    /**
     * Get the highest number of votes received for a particular restaurant.
     * @return the max vote count
     */
    public int getMaxVoteCount() {
        if (this.restaurants.isEmpty())
            return 0;

        return Collections.max(this.restaurants.values());
    }

    /**
     * Get a numerically sorted mapping of vote counts to alphabetically sorted
     * names of restaurants that received that number of votes.
     * @return mapping of vote count to restaurant names
     */
    private SortedMap<Integer, SortedSet<String>> getInvertedRestaurants() {
        SortedMap<Integer, SortedSet<String>> inverted = new TreeMap<>();
        for (String restaurant : this.restaurants.keySet()) {
            Integer votes = this.restaurants.get(restaurant);
            SortedSet<String> restaurantNames = inverted.get(votes);
            if (restaurantNames == null) {
                restaurantNames = new TreeSet<>();
                inverted.put(votes, restaurantNames);
            }
            restaurantNames.add(restaurant);
        }
        return inverted;
    }

    /**
     * Is there a clear winner (one choice had more votes than any other), else there was a tie.
     * @return whether there is a winner for the voting
     */
    public boolean isWin() {
        if (this.restaurants.isEmpty())
            return false;

        int max = 0;
        int count = 0;
        for (Integer votes : this.restaurants.values()) {
            if (votes > max) {
                max = votes;
                count = 1;
            } else if (votes == max) {
                count++;
            }
        }

        // No votes
        if (max == 0)
            return false;

        return count == 1;
    }
}
