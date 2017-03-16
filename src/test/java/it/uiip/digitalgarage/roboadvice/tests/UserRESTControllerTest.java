package it.uiip.digitalgarage.roboadvice.tests;

import it.uiip.digitalgarage.roboadvice.businesslogic.controller.UserRESTController;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.UserDTO;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.persistence.PersistenceContext;
import javax.validation.constraints.AssertTrue;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by Simone on 16/03/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {PersistenceContext.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class UserRESTControllerTest {

    @Mock
    UserRepository userRepository;

    @Mock
    Authentication authentication;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private UserRESTController userRESTController;

    @Before
    public void before(){

        userRESTController = new UserRESTController(userRepository);
        when(authentication.getName()).thenReturn("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(User.builder().id(1L).username("testUser").password("12345").nickname("testUser").build());

    }

    @Test
    public void loginTest(){
        UserDTO user = (UserDTO) userRESTController.loginUser(authentication).getData();
        Boolean check = user.getUsername().equals("testUser");
        assertTrue(check);
    }

}
