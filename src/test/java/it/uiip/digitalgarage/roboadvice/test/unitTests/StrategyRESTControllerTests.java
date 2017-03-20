package it.uiip.digitalgarage.roboadvice.test.unitTests;

import it.uiip.digitalgarage.roboadvice.businesslogic.controller.StrategyRESTController;
import it.uiip.digitalgarage.roboadvice.businesslogic.exception.BadRequestException;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.StrategyDTO;
import it.uiip.digitalgarage.roboadvice.persistence.model.AssetClass;
import it.uiip.digitalgarage.roboadvice.persistence.model.Strategy;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetClassRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.StrategyRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.UserRepository;
import it.uiip.digitalgarage.roboadvice.utils.CustomDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by Simone on 17/03/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {PersistenceContext.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
                                TransactionalTestExecutionListener.class})
public class StrategyRESTControllerTests {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StrategyRepository strategyRepository;

    @Mock
    private AssetClassRepository assetClassRepository;

    @Mock
    private Authentication authentication;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private StrategyRESTController strategyRESTController;

    private CustomDate customDate;

    private List<StrategyDTO> strategyDTOList;

    @Before
    public void before() {

        customDate = CustomDate.getToday();

        strategyRESTController =
                new StrategyRESTController(modelMapper, userRepository, strategyRepository, assetClassRepository);

        List<Strategy> strategyList = new ArrayList<>();

        User user =
                User.builder().username("TestUser").nickname("TestUser").password("HASCED12345").enabled(true).id(1L)
                        .isNewUser(false).lastPortfolioComputation(customDate.getDateSql()).build();

        AssetClass assetClass = AssetClass.builder().name("TestClass").id(1L).build();

        Strategy strategy =
                Strategy.builder().startingDate(customDate.getYesterdaySql()).percentage(new BigDecimal("100"))
                        .active(true).user(user).id(1L).assetClass(assetClass).build();

        strategyList.add(strategy);

        strategyDTOList = new ArrayList<>();

        StrategyDTO strategyDTO = StrategyDTO.builder().percentage(new BigDecimal("100")).assetClassId(1L).build();

        strategyDTOList.add(strategyDTO);


        when(authentication.getName()).thenReturn("TestUser");
        when(userRepository.findByUsername("TestUser")).thenReturn(user);
        when(strategyRepository.findByUserAndActiveTrue(user)).thenReturn(strategyList);
        when(modelMapper.map(strategy, StrategyDTO.class)).thenReturn(strategyDTO);

    }

    @Test
    public void testGetActiveStrategy() {

        List<StrategyDTO> result =
                (List<StrategyDTO>) strategyRESTController.getActiveStrategy(authentication).getData();

        Boolean check1 = result.get(0).getAssetClassId() == 1L;
        Boolean check2 = result.get(0).getPercentage().equals(new BigDecimal("100"));

        assertTrue(check1);
        assertTrue(check2);

    }

    @Test
    public void testUpdateStrategy() {

        Boolean check = null;
        try {
             check = strategyRESTController.updateStrategy(authentication, strategyDTOList).getResponse() == 1;
        } catch (BadRequestException e){
            assertTrue(false);
        }

        assertTrue(check);
    }
}
