package exchange.estimation.models.repositories;

import exchange.estimation.models.entities.Exchanges;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface ExchangesRepository extends MongoRepository<Exchanges, String> {

}
