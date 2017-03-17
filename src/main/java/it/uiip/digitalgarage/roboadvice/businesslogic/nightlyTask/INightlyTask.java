package it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask;

import it.uiip.digitalgarage.roboadvice.persistence.model.User;

public interface INightlyTask {

    void executeNightlyTask(Iterable<User> users);
}
