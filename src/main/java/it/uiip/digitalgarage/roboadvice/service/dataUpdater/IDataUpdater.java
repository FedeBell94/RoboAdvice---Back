package it.uiip.digitalgarage.roboadvice.service.dataUpdater;

import it.uiip.digitalgarage.roboadvice.utils.CustomDate;

/**
 * Download and update the prices for the assets.
 */
public interface IDataUpdater {

    /**
     * Download and update the prices for the assets.
     *
     * @throws DataUpdateException If something goes wrong during the update, i.e. if there is no Internet connection
     *                             and the update of the data fails.
     */
    void updateAssetData() throws DataUpdateException;


    /**
     * This method returns the date of the last computation of the data.
     *
     * @return The date of the last computation of the data.
     */
    CustomDate getLastComputationData();

    /**
     * Exception thrown when the update of the data fails.
     */
    class DataUpdateException extends Exception {

        public DataUpdateException() {
            super();
        }

        public DataUpdateException(String s) {
            super(s);
        }
    }
}
