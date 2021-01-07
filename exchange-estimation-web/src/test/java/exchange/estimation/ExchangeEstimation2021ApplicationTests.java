package exchange.estimation;

import exchange.estimation.controllers.ExchangesController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
class ExchangeEstimation2021ApplicationTests {
    @Autowired
    private ExchangesController controller;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }
}
