package it.uiip.digitalgarage.roboadvice.businesslogic.quandl;


import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author Simone.
 */

@Configuration
@EnableAsync
@EnableScheduling
public class DailyExecutor {

    private class QuandlDailyUpdate implements Runnable {



        public QuandlDailyUpdate() {

        }

        public void run() {
            System.out.println("Starting daily Quandl update");

        }

    }

    private TaskExecutor taskExecutor;

    public DailyExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void quandlDailyUpdate() {

            taskExecutor.execute(new QuandlDailyUpdate());

    }

}