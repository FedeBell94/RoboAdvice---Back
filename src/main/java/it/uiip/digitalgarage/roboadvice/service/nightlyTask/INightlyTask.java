package it.uiip.digitalgarage.roboadvice.service.nightlyTask;

public interface INightlyTask {

    void executeNightlyTask() throws NightlyTaskFailedException;

    class NightlyTaskFailedException extends Exception{
        NightlyTaskFailedException(String s){
            super(s);
        }
    }

}
