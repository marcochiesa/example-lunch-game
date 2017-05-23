package org.getmarco.state;

import org.getmarco.Game;
import org.getmarco.view.EliminateView;
import org.getmarco.view.EnterDataView;
import org.getmarco.view.FinishView;
import org.getmarco.view.SetupView;
import org.getmarco.view.ShowDataView;
import org.getmarco.view.ShowResultsView;
import org.getmarco.view.StartView;
import org.getmarco.view.VoteView;

/**
 * Factory to return the correct {@link GameState} instance
 */
public final class GameStateFactory {

    private GameStateFactory() {
        // Not used
    }

    public static GameState create(StateType state) {
        switch (state) {
            case START:
                return new StartState();
            case SETUP:
                return new SetupState();
            case ENTER_DATA:
                return new EnterDataState();
            case SHOW_DATA:
                return new ShowDataState();
            case ELIMINATE:
                return new EliminateState();
            case VOTE:
                return new VoteState();
            case RESULTS:
                return new ResultsState();
            case FINISH:
                return new FinishState();
            case QUIT:
                return new QuitState();
            default:
                throw new RuntimeException("impossible");
        }
    }

    /**
     * This class provides some default behavior for the game's states.
     */
    private abstract static class AbstractGameState implements GameState {
        /**
         * This default behavior is to not support a backward movement.
         * Subclasses should override to return an appropriate 'back' previous
         * state if they want to support it.
         * @return the 'back' state
         */
        @Override
        public StateType back() {
            return null;
        }

        /**
         * The state to use when ready to quit the game.
         * @return the 'quit' state
         */
        @Override
        public StateType quit() {
            return StateType.QUIT;
        }
    }

    private static class StartState extends AbstractGameState {
        @Override
        public StateType doAction(Game game) {
            new StartView().display(false);
            return StateType.SETUP;
        }
    }

    private static class SetupState extends AbstractGameState {
        @Override
        public StateType doAction(Game game) {
            game.resetState();
            new SetupView(game).display(false);
            return StateType.ENTER_DATA;
        }
    }

    private static class EnterDataState extends AbstractGameState {
        @Override
        public StateType doAction(Game game) {
            for (int i = 0; i < game.getNumPlayers(); i++) {
                new EnterDataView(game).display(false);
                if (game.stateChanged(this))
                    break;
            }
            return StateType.SHOW_DATA;
        }

        @Override
        public StateType back() {
            return StateType.SETUP;
        }
    }

    private static class ShowDataState extends AbstractGameState {
        @Override
        public StateType doAction(Game game) {
            new ShowDataView(game).display();
            return StateType.ELIMINATE;
        }
    }

    private static class EliminateState extends AbstractGameState {
        @Override
        public StateType doAction(Game game) {
            for (String player : game.getPlayers()) {
                new EliminateView(game, player).display(false);
                if (game.stateChanged(this))
                    break;
            }
            return StateType.VOTE;
        }

        @Override
        public StateType back() {
            return StateType.SETUP;
        }
    }

    private static class VoteState extends AbstractGameState {
        @Override
        public StateType doAction(Game game) {
            for (String player : game.getPlayers()) {
                new VoteView(game, player).display(false);
                if (game.stateChanged(this))
                    break;
            }
            return StateType.RESULTS;
        }

        @Override
        public StateType back() {
            return StateType.VOTE;
        }
    }

    private static class ResultsState extends AbstractGameState {
        @Override
        public StateType doAction(Game game) {
            new ShowResultsView(game).display();
            return StateType.FINISH;
        }
    }

    private static class FinishState extends AbstractGameState {
        @Override
        public StateType doAction(Game game) {
            new FinishView().display(false);
            return StateType.QUIT;
        }
    }

    private static class QuitState extends AbstractGameState {
        @Override
        public StateType doAction(Game game) {
            System.exit(0);
            throw new RuntimeException("impossible");
        }
    }
}
