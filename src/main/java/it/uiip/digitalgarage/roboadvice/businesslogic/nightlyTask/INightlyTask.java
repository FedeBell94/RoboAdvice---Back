package it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask;

import it.uiip.digitalgarage.roboadvice.businesslogic.nightlyTask.dateProvider.DateProvider;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;

public interface INightlyTask {

    void executeNightlyTask(DateProvider dateProvider, Iterable<User> users);
}
