package org.getmarco;

/**
 * Main for Lunch Game application.
 */
public class App {
    public static void main(String[] args) {
        new App().start();
    }

    public void start() {
        Game game = new Game();
        while (true)
            game.play();
    }
}
