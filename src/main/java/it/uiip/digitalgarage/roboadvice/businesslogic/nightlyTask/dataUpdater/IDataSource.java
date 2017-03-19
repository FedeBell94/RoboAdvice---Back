package it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater;

import it.uiip.digitalgarage.roboadvice.persistence.model.Asset;
import it.uiip.digitalgarage.roboadvice.persistence.model.Data;

import java.sql.Date;
import java.util.List;

/**
 * The class which implements this interface should finds and returns the latest prices for the assets.
 */
public interface IDataSource {

    /**
     * Finds and returns the latest data from the given date to today.
     *
     * @param asset The assets to find the prices.
     * @param from  The date from which is needed to find the data.
     *
     * @return A list of the required {@link Data}.
     *
     * @throws ConnectionException Exception thrown in case the connection to the datasource of the data fails.
     */
    List<Data> getAllDataFrom(Asset asset, Date from) throws ConnectionException;

    /**
     * Exception thrown in case the connection to the datasource of the data fails.
     */
    class ConnectionException extends Exception {

        public ConnectionException() {
            super();
        }

        public ConnectionException(String s) {
            super(s);
        }
    }
}
