package it.uiip.digitalgarage.roboadvice.test.unitTests;

import it.uiip.digitalgarage.roboadvice.persistence.model.Data;
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
public class DataTests {

    @Test
    public void typeAnnotations() {

        // assert

        AssertAnnotations.assertType(

                Data.class, Entity.class, Table.class);

    }


    @Test
    public void fieldAnnotations() {

        // assert

        AssertAnnotations.assertField(Data.class, "id", Id.class, GeneratedValue.class);

        AssertAnnotations.assertField(Data.class, "asset", ManyToOne.class, JoinColumn.class);

        AssertAnnotations.assertField(Data.class, "date", Column.class);

        AssertAnnotations.assertField(Data.class, "value", Column.class);

    }

    @Test
    public void methodAnnotations() {

        // assert

        AssertAnnotations.assertMethod(Data.class, "getId");

        AssertAnnotations.assertMethod(Data.class, "getAsset");

        AssertAnnotations.assertMethod(Data.class, "getDate");

        AssertAnnotations.assertMethod(Data.class, "getValue");
    }

    @Test
    public void entity() {

        // setup

        Entity a

                = ReflectTool.getClassAnnotation(Data.class, Entity.class);

        // assert

        Assert.assertEquals("", a.name());

    }

    @Test
    public void table() {

        // setup

        Table t

                = ReflectTool.getClassAnnotation(Data.class, Table.class);

        // assert

        Assert.assertEquals("Data", t.name());

    }


}
