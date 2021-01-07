package exchange.estimation;

import exchange.estimation.controllers.ExchangesController;
import exchange.estimation.models.entities.Exchanges;
import exchange.estimation.services.interfaces.ExchangesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExchangesController.class)
public class ExchangesControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExchangesService mockedExchangesService;

    @Test
    public void shouldTriggerControllerGetFunctionWhenApiIsCalledForGet() throws Exception {
        String mockedType = "Year";
        String mockedCurrency = "EUR";
        String mockedMethode = "Linear";
        Long mockedNumber = 5l;

        doNothing().when(mockedExchangesService).GetGraph(mockedType, mockedNumber, mockedCurrency, mockedMethode);

        this.mockMvc
                .perform(get("/api/exchanges" +
                        "/type/" + mockedType +
                        "/number/" + mockedNumber +
                        "/currency/" + mockedCurrency +
                        "/methode/" + mockedMethode))
                .andExpect(handler().handlerType(ExchangesController.class))
                .andExpect(handler().methodName("GetGraph"));

    }

    @Test
    public void controllerShouldReturnStatusOkIfNoExceptionRiseUp() throws Exception {
        String mockedType = "Year";
        String mockedCurrency = "EUR";
        String mockedMethode = "Linear";
        Long mockedNumber = 5l;

        doNothing().when(mockedExchangesService).GetGraph(mockedType, mockedNumber, mockedCurrency, mockedMethode);

        this.mockMvc
                .perform(get("/api/exchanges" +
                        "/type/" + mockedType +
                        "/number/" + mockedNumber +
                        "/currency/" + mockedCurrency +
                        "/methode/" + mockedMethode))
                .andExpect(status().isOk());
    }

    @Test
    public void controllerShouldReturnStatusNotFoundIfExceptionRiseUp() throws Exception {
        String mockedType = "Year";
        String mockedCurrency = "EUR";
        String mockedMethode = "Linear";
        Long mockedNumber = 5l;
        doThrow(new Exception("test")).
        when(mockedExchangesService).GetGraph(mockedType, mockedNumber, mockedCurrency, mockedMethode);

        this.mockMvc
                .perform(get("/api/exchanges" +
                        "/type/" + mockedType +
                        "/number/" + mockedNumber +
                        "/currency/" + mockedCurrency +
                        "/methode/" + mockedMethode))
                .andExpect(status().isNotFound());
    }


}
