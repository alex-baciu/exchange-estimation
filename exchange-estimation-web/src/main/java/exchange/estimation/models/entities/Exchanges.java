package exchange.estimation.models.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class Exchanges {
    @Id
    private String _id;

    private Date date;

    private Double value;

    private String currency;

    public Exchanges(Date date, Double value, String currency) {
        this.date = date;
        this.value = value;
        this.currency = currency;
    }
}
