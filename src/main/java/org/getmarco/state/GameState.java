package org.getmarco.state;

import org.getmarco.Game;

/**
 * Contract for states controlling game flow.
 */
public interface GameState {
    StateType doAction(Game game);
    StateType back();
    StateType quit();
}
