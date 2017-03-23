package it.uiip.digitalgarage.roboadvice.test.unitTests;

import it.uiip.digitalgarage.roboadvice.businesslogic.controller.PortfolioRESTController;
import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.PortfolioDTO;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import it.uiip.digitalgarage.roboadvice.persistence.repository.AssetRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.DataRepository;
import it.uiip.digitalgarage.roboadvice.persistence.repository.PortfolioRepository;
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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Simone on 17/03/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {PersistenceContext.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
                                TransactionalTestExecutionListener.class})
public class PortfolioRESTControllerTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private DataRepository dataRepository;

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private ModelMapper modelMapper;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private PortfolioRESTController portfolioRESTController;

    private CustomDate customDate;

    @Before
    public void before() {

        portfolioRESTController =
                new PortfolioRESTController(userRepository, portfolioRepository, dataRepository, assetRepository,
                        modelMapper);

        customDate = CustomDate.getToday();

        List<PortfolioDTO> portfolioDTOList = new ArrayList<>();

        User user =
                User.builder().username("TestUser").nickname("TestUser").password("HASCED12345").enabled(true).id(1L)
                        .isNewUser(false).build();

        PortfolioDTO portfolioDTO = new PortfolioDTO();
        portfolioDTO.setAssetClassId(1L);
        portfolioDTO.setDate(customDate.getYesterdayUtils());
        portfolioDTO.setValue(new BigDecimal("1200"));

        portfolioDTOList.add(portfolioDTO);

        when(authentication.getName()).thenReturn("TestUser");
        when(userRepository.findByUsername("TestUser")).thenReturn(user);
        when(portfolioRepository.findPortfolioHistory(any(), any())).thenReturn(portfolioDTOList);

    }

    @Test
    public void testRequestMyData() {

        List<PortfolioDTO> result = (List<PortfolioDTO>) portfolioRESTController
                .requestMyData(authentication, customDate.getYesterdaySql().toLocalDate()).getData();

        Boolean check1 = result.get(0).getValue().equals(new BigDecimal("1200"));
        Boolean check2 = customDate.getYesterdayUtils().toString().equals(result.get(0).getDate().toString());
        Boolean check3 = result.get(0).getAssetClassId() == 1;

        assertTrue(check1);
        assertTrue(check2);
        assertTrue(check3);

    }

}
