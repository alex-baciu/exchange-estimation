package exchange.estimation.models.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class Modifies {
    @Id
    private String _id;

    private Date date;

    public Modifies(Date date) {
        this.date = date;
    }
}
