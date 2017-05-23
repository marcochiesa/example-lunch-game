package org.getmarco;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Test;

//CHECKSTYLE:OFF
import static org.junit.Assert.*;
//CHECKSTYLE:ON

/**
 * Tests for {@link org.getmarco.Game}.
 */
public class GameTest {

    @Test(expected = java.lang.IllegalStateException.class)
    public void testDenyTooManyPlayers() {
        Game game = new Game();

        game.setNumPlayers(2);
        game.setPlayers(new HashSet<>(Arrays.asList(new String[] {"Bart", "Lisa", "Homer"})));
    }

    @Test(expected = java.lang.IllegalStateException.class)
    public void testDenyTooManyRestaurants() {
        Game game = new Game();

        game.setNumPlayers(2);
        game.setNumRestaurantsPerPlayer(2);
        game.addRestaurants(new HashSet<>(Arrays.asList(new String[] {"Zoes", "Panera", "Saw's", "Moe's", "Chuy's"})));
    }

    @Test
    public void testVoteCountNotResetOnAddRestaurant() {
        Game game = new Game();
        String restaurant = "Newk's";
        game.setNumPlayers(1);
        game.setNumRestaurantsPerPlayer(1);

        game.addRestaurants(Collections.singleton(restaurant));
        game.vote(restaurant);
        assertEquals(1, game.getVotesForRestaurant(restaurant));

        game.addRestaurants(Collections.singleton(restaurant));
        assertEquals(1, game.getVotesForRestaurant(restaurant));
    }

    @Test
    public void ignoreBlankRestaurantNames() {
        Game game = new Game();
        game.setNumPlayers(1);
        game.setNumRestaurantsPerPlayer(1);
        assertEquals(0, game.getRestaurants().size());
        game.addRestaurants(Collections.singleton(null));
        assertEquals(0, game.getRestaurants().size());
        game.addRestaurants(Collections.singleton(""));
        assertEquals(0, game.getRestaurants().size());
    }

    @Test
    public void testVote() {
        Game game = new Game();
        game.setNumPlayers(1);
        game.setNumRestaurantsPerPlayer(1);
        String restaurant = "Rojo";

        game.addRestaurants(Collections.singleton(restaurant));
        assertEquals(0, game.getVotesForRestaurant(restaurant));
        game.vote(restaurant);
        assertEquals(1, game.getVotesForRestaurant(restaurant));
        game.vote(restaurant);
        assertEquals(2, game.getVotesForRestaurant(restaurant));
    }

    @Test(expected = java.lang.IllegalStateException.class)
    public void testDenyVoteForUnknownRestaurant() {
        Game game = new Game();
        game.vote("unknown");
    }

    @SuppressWarnings("checkstyle:magicnumber")
    @Test
    public void testReset() {
        Game game = new Game();

        game.setNumPlayers(2);
        game.setNumRestaurantsPerPlayer(2);
        game.setPlayers(new HashSet<>(Arrays.asList(new String[] {"Bart", "Lisa"})));
        game.addRestaurants(new HashSet<>(Arrays.asList(new String[] {"Zoes", "Panera", "Saw's", "Moe's"})));

        assertEquals(2, game.getNumPlayers());
        assertEquals(2, game.getNumRestaurantsPerPlayer());
        assertEquals(2, game.getPlayers().size());
        assertEquals(4, game.getRestaurants().size());

        game.resetState();

        assertEquals(0, game.getNumPlayers());
        assertEquals(0, game.getNumRestaurantsPerPlayer());
        assertEquals(0, game.getPlayers().size());
        assertEquals(0, game.getRestaurants().size());
    }

    @SuppressWarnings("checkstyle:magicnumber")
    @Test
    public void testResetVoting() {
        Game game = new Game();
        String restaurant1 = "Jason's";
        String restaurant2 = "Dreamland";
        String[] restaurants = new String[] {restaurant1, restaurant2};

        game.setNumPlayers(1);
        game.setNumRestaurantsPerPlayer(2);
        game.addRestaurants(new HashSet<>(Arrays.asList(restaurants)));

        for (String restaurant : restaurants) {
            game.vote(restaurant);
            assertEquals(1, game.getVotesForRestaurant(restaurant));
        }
        game.resetVoting();
        for (String restaurant : restaurants)
            assertEquals(0, game.getVotesForRestaurant(restaurant));
    }

    @SuppressWarnings("checkstyle:magicnumber")
    @Test
    public void testVoteResults() {
        Game game = new Game();
        String restaurant1 = "Jason's";
        String restaurant2 = "Dreamland";
        String restaurant3 = "Mugshots";

        game.setNumPlayers(1);
        game.setNumRestaurantsPerPlayer(3);

        // Short-circuit false when restaurants empty
        assertFalse(game.isWin());
        // Short-circuit zero when restaurants empty
        assertEquals(0, game.getMaxVoteCount());

        game.addRestaurants(new HashSet<>(Arrays.asList(new String[] {restaurant1, restaurant2, restaurant3})));

        assertFalse(game.isWin());
        game.vote(restaurant1);
        game.vote(restaurant2);
        game.vote(restaurant3);
        assertFalse(game.isWin());

        game.vote(restaurant2);
        game.vote(restaurant3);
        game.vote(restaurant3);
        assertTrue(game.isWin());
        assertEquals(3, game.getMaxVoteCount());
        assertArrayEquals(new String[] {restaurant3, restaurant2, restaurant1},
          game.getVoteSortedRestaurants().toArray(new String[0]));
    }
}
