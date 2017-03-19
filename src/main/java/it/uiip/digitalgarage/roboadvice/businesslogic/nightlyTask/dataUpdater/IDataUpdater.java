package it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dataUpdater;

public interface IDataUpdater {

    void updateAssetData() throws DataUpdateException;

    class DataUpdateException extends Exception {

        public DataUpdateException() {
            super();
        }

        public DataUpdateException(String s){
            super(s);
        }
    }
}
