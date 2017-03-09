package it.uiip.digitalgarage.roboadvice.tests;

import it.uiip.digitalgarage.roboadvice.persistence.model.*;
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
public class AssetClassTests {

    @Test

    public void typeAnnotations() {

        // assert

        AssertAnnotations.assertType(

                AssetClass.class, Entity.class, Table.class);

    }


    @Test
    public void fieldAnnotations() {

        // assert

        AssertAnnotations.assertField(AssetClass.class, "id", Id.class);

        AssertAnnotations.assertField(AssetClass.class, "name", Column.class);

    }

    @Test

    public void methodAnnotations() {

        // assert

        AssertAnnotations.assertMethod(AssetClass.class, "getId");

        AssertAnnotations.assertMethod(AssetClass.class, "getName");
    }

    @Test
    public void entity() {

        // setup

        Entity a

                = ReflectTool.getClassAnnotation(AssetClass.class, Entity.class);

        // assert

        Assert.assertEquals("", a.name());

    }

    @Test
    public void table() {

        // setup

        Table t

                = ReflectTool.getClassAnnotation(AssetClass.class, Table.class);

        // assert

        Assert.assertEquals("AssetClass", t.name());

    }

}
