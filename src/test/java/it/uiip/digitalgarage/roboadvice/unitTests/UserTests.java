package it.uiip.digitalgarage.roboadvice.unitTests;

import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.testUtils.AssertAnnotations;
import it.uiip.digitalgarage.roboadvice.testUtils.ReflectTool;
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
public class UserTests {

    @Test
    public void typeAnnotations() {

        // assert

        AssertAnnotations.assertType(

                User.class, Entity.class, Table.class);

    }


    @Test
    public void fieldAnnotations() {

        // assert

        AssertAnnotations.assertField(User.class, "id", Id.class, GeneratedValue.class);

        AssertAnnotations.assertField(User.class, "username", Column.class);

        AssertAnnotations.assertField(User.class, "password", Column.class);

        AssertAnnotations.assertField(User.class, "nickname", Column.class);

        AssertAnnotations.assertField(User.class, "enabled", Column.class);

        AssertAnnotations.assertField(User.class, "registration", Column.class);

        AssertAnnotations.assertField(User.class, "isNewUser", Column.class);

        AssertAnnotations.assertField(User.class, "lastPortfolioComputation", Column.class);

    }

    @Test
    public void methodAnnotations() {

        // assert

        AssertAnnotations.assertMethod(User.class, "getId");

        AssertAnnotations.assertMethod(User.class, "getUsername");

        AssertAnnotations.assertMethod(User.class, "getPassword");

        AssertAnnotations.assertMethod(User.class, "getNickname");

        AssertAnnotations.assertMethod(User.class, "getEnabled");

        AssertAnnotations.assertMethod(User.class, "getRegistration");

        AssertAnnotations.assertMethod(User.class, "getIsNewUser");

        AssertAnnotations.assertMethod(User.class, "getLastPortfolioComputation");
    }

    @Test
    public void entity() {

        // setup

        Entity a

                = ReflectTool.getClassAnnotation(User.class, Entity.class);

        // assert

        Assert.assertEquals("", a.name());

    }

    @Test
    public void table() {

        // setup

        Table t

                = ReflectTool.getClassAnnotation(User.class, Table.class);

        // assert

        Assert.assertEquals("User", t.name());

    }


}
