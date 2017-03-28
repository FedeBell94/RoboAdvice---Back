package it.uiip.digitalgarage.roboadvice.service.nightlyTask;

/**
 * Interface of the Nightly Task. The class which implements this interface should implement the behaviour of a Nightly
 * Task: update the asset prices and compute the portfolio for each user.
 */
public interface INightlyTask {

    /**
     * This method start the execution of the Nightly Task.
     *
     * @throws NightlyTaskFailedException In case something went wrong during the nightly task computation.
     */
    void executeNightlyTask() throws NightlyTaskFailedException;

    /**
     * Exception thrown in case the Nightly Task computation fail.
     */
    class NightlyTaskFailedException extends Exception {
        NightlyTaskFailedException(String s) {
            super(s);
        }
    }

}
