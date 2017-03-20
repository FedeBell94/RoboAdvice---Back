package it.uiip.digitalgarage.roboadvice.test.unitTests;

import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * Created by Simone on 20/03/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class CustomDateTests {

    @Before
    public void before() {


    }
}
