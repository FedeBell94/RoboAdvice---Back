package it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask;

import it.uiip.digitalgarage.roboadvice.persistence.model.User;

/**
 * This interface models the behaviour of the 'Nightly Task' of this application. The aim of this task is to update all
 * the portfolios of all users once per day, during the night.
 */
public interface INightlyTask {

    /**
     * Execute the computations needed to update all the portfolios of the user passed as parameter.
     *
     * @param users The users to which compute the portfolio.
     */
    void executeNightlyTask(Iterable<User> users);
}
