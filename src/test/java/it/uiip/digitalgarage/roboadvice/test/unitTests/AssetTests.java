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
public class AssetTests {

    @Test
    public void typeAnnotations() {

        // assert

        AssertAnnotations.assertType(

                Asset.class, Entity.class, Table.class);

    }


    @Test
    public void fieldAnnotations() {

        // assert

        AssertAnnotations.assertField(Asset.class, "id", Id.class);

        AssertAnnotations.assertField(Asset.class, "assetClass", ManyToOne.class, JoinColumn.class);

        AssertAnnotations.assertField(Asset.class, "name", Column.class);

        AssertAnnotations.assertField(Asset.class, "quandlKey", Column.class);

        AssertAnnotations.assertField(Asset.class, "quandlId", Column.class);

        AssertAnnotations.assertField(Asset.class, "quandlColumn", Column.class);

        AssertAnnotations.assertField(Asset.class, "fixedPercentage", Column.class);

    }

    @Test
    public void methodAnnotations() {

        // assert

        AssertAnnotations.assertMethod(Asset.class, "getId");

        AssertAnnotations.assertMethod(Asset.class, "getAssetClass");

        AssertAnnotations.assertMethod(Asset.class, "getName");

        AssertAnnotations.assertMethod(Asset.class, "getQuandlKey");

        AssertAnnotations.assertMethod(Asset.class, "getQuandlId");

        AssertAnnotations.assertMethod(Asset.class, "getQuandlColumn");

        AssertAnnotations.assertMethod(Asset.class, "getFixedPercentage");
    }

    @Test
    public void entity() {

        // setup

        Entity a

                = ReflectTool.getClassAnnotation(Asset.class, Entity.class);

        // assert

        Assert.assertEquals("", a.name());

    }

    @Test
    public void table() {

        // setup

        Table t

                = ReflectTool.getClassAnnotation(Asset.class, Table.class);

        // assert

        Assert.assertEquals("Asset", t.name());

    }

}
