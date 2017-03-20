package it.uiip.digitalgarage.roboadvice.test.unitTests;

import it.uiip.digitalgarage.roboadvice.persistence.model.*;
import it.uiip.digitalgarage.roboadvice.test.testUtils.AssertAnnotations;
import it.uiip.digitalgarage.roboadvice.test.testUtils.ReflectTool;
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
public class PortfolioTests {

    @Test
    public void typeAnnotations() {

        // assert

        AssertAnnotations.assertType(

                Portfolio.class, Entity.class, Table.class);

    }


    @Test
    public void fieldAnnotations() {

        // assert

        AssertAnnotations.assertField(Portfolio.class, "id", Id.class, GeneratedValue.class);

        AssertAnnotations.assertField(Portfolio.class, "user", ManyToOne.class, JoinColumn.class);

        AssertAnnotations.assertField(Portfolio.class, "assetClass", ManyToOne.class, JoinColumn.class);

        AssertAnnotations.assertField(Portfolio.class, "asset", ManyToOne.class, JoinColumn.class);

        AssertAnnotations.assertField(Portfolio.class, "unit", Column.class);

        AssertAnnotations.assertField(Portfolio.class, "value", Column.class);

        AssertAnnotations.assertField(Portfolio.class, "date", Column.class);

    }

    @Test
    public void methodAnnotations() {

        // assert

        AssertAnnotations.assertMethod(Portfolio.class, "getId");

        AssertAnnotations.assertMethod(Portfolio.class, "getUser");

        AssertAnnotations.assertMethod(Portfolio.class, "getAssetClass");

        AssertAnnotations.assertMethod(Portfolio.class, "getAsset");

        AssertAnnotations.assertMethod(Portfolio.class, "getUnit");

        AssertAnnotations.assertMethod(Portfolio.class, "getValue");

        AssertAnnotations.assertMethod(Portfolio.class, "getDate");
    }

    @Test
    public void entity() {

        // setup

        Entity a

                = ReflectTool.getClassAnnotation(Portfolio.class, Entity.class);

        // assert

        Assert.assertEquals("", a.name());

    }

    @Test
    public void table() {

        // setup

        Table t

                = ReflectTool.getClassAnnotation(Portfolio.class, Table.class);

        // assert

        Assert.assertEquals("Portfolio", t.name());

    }


}
