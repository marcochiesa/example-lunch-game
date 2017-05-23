package org.getmarco.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.bretty.console.view.ActionView;
import io.bretty.console.view.ViewConfig;

/**
 * Abstract superclass for game views. Subclass to implement appropriate behaviors.
 */
public abstract class GameView extends ActionView {
    private final Logger logger = LogManager.getLogger(this.getClass());

    public GameView(String runningTitle, String nameInParentMenu) {
        super(runningTitle, nameInParentMenu);
    }

    public GameView(String runningTitle, String nameInParentMenu, ViewConfig viewConfig) {
        super(runningTitle, nameInParentMenu, viewConfig);
    }

    /**
     * This implementation just satisfies the superclass contract, but does
     * nothing. Subclasses should override to define necessary behavior.
     */
    @Override
    public void executeCustomAction() {
    }

    @Override
    public void display() {
        this.display(true);
    }

    public void display(boolean pauseAfter) {
        logger.trace(this.getClass().getSimpleName() + " display start");
        this.println();
        this.println(this.runningTitle);
        this.executeCustomAction();
        if (pauseAfter)
            this.pause();
        // Leave out this.goBack() since these views are chosen based on state objects not a menu tree
        logger.trace(this.getClass().getSimpleName() + " display finished");
    }
}
