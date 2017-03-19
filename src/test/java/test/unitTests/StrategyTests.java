package test.unitTests;

import it.uiip.digitalgarage.roboadvice.persistence.model.Strategy;
import test.testUtils.AssertAnnotations;
import test.testUtils.ReflectTool;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.persistence.*;


/**
 * Created by Simone on 09/03/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {PersistenceContext.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class StrategyTests {

    @Test
    public void typeAnnotations() {

        // assert

        AssertAnnotations.assertType(

                Strategy.class, Entity.class, Table.class);

    }


    @Test
    public void fieldAnnotations() {

        // assert

        AssertAnnotations.assertField(Strategy.class, "id", Id.class, GeneratedValue.class);

        AssertAnnotations.assertField(Strategy.class, "user", ManyToOne.class, JoinColumn.class);

        AssertAnnotations.assertField(Strategy.class, "assetClass", ManyToOne.class, JoinColumn.class);

        AssertAnnotations.assertField(Strategy.class, "percentage", Column.class);

        AssertAnnotations.assertField(Strategy.class, "active", Column.class);

        AssertAnnotations.assertField(Strategy.class, "startingDate", Column.class);

    }

    @Test
    public void methodAnnotations() {

        // assert

        AssertAnnotations.assertMethod(Strategy.class, "getId");

        AssertAnnotations.assertMethod(Strategy.class, "getUser");

        AssertAnnotations.assertMethod(Strategy.class, "getAssetClass");

        AssertAnnotations.assertMethod(Strategy.class, "getPercentage");

        AssertAnnotations.assertMethod(Strategy.class, "getActive");

        AssertAnnotations.assertMethod(Strategy.class, "getStartingDate");
    }

    @Test
    public void entity() {

        // setup

        Entity a

                = ReflectTool.getClassAnnotation(Strategy.class, Entity.class);

        // assert

        Assert.assertEquals("", a.name());

    }

    @Test
    public void table() {

        // setup

        Table t

                = ReflectTool.getClassAnnotation(Strategy.class, Table.class);

        // assert

        Assert.assertEquals("Strategy", t.name());

    }


}
