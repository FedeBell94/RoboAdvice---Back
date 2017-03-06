package it.uiip.digitalgarage.roboadvice.businesslogic.dailyUpdate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;


@SpringBootApplication
public class TestDailyUpdate {


    @PostConstruct
    void func() {
        DailyTaskUpdate dailyTaskUpdate = new DailyTaskUpdate();
        dailyTaskUpdate.executeUpdateTask();
    }

    public static void main(String[] args) {
        SpringApplication.run(TestDailyUpdate.class, args);
    }
}
