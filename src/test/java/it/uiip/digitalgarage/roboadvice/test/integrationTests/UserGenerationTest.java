package it.uiip.digitalgarage.roboadvice.test.integrationTests;

import it.uiip.digitalgarage.roboadvice.businesslogic.controller.UserRESTController;
import it.uiip.digitalgarage.roboadvice.businesslogic.exception.BadRequestException;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.UserDTO;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.junit.Assert.assertTrue;

/**
 * Created by Simone on 21/03/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        TransactionalTestExecutionListener.class})
public class UserGenerationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    private UserRESTController userRESTController;

    private UserDTO testUserDTO;

    private User testUser = null;

    @Before
    public void setUp(){

        userRESTController = new UserRESTController(modelMapper, userRepository,
        passwordEncoder);

        testUserDTO = UserDTO.builder().nickname("testUser").password("1234").username("test@user.com").isNewUser(true).build();

    }

    @Test
    public void userGenerationTest(){

        try {
            userRESTController.registerUser(testUserDTO);
            testUser = userRepository.findByUsername("test@user.com");
        } catch (BadRequestException e) {
            e.printStackTrace();
            assertTrue(false);
        }
        Boolean check1 = testUser.getUsername().equals("test@user.com");
        Boolean check2 = testUser.getNickname().equals("testUser");
        Boolean check3 = testUser.getIsNewUser().toString().equals("true");
        Boolean check4 = passwordEncoder.matches("1234",testUser.getPassword());

        assertTrue(check1);
        assertTrue(check2);
        assertTrue(check3);
        assertTrue(check4);

    }

    @After
    public void cleanUp(){
        userRepository.delete(testUser);
    }
}
