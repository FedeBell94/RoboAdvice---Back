package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;


@Controller
@SuppressWarnings("unused")
public class DailyTaskTester {

    @Autowired
    private IDailyTaskUpdate task;

    @PostConstruct
    void executeTest(){
        //task.executeUpdateTask();
    }
}
