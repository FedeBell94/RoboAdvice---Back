package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate;

import it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate.dateProvider.DateProvider;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;

public interface INightlyTask {

    void executeNightlyTask(DateProvider dateProvider, Iterable<User> users);
}
