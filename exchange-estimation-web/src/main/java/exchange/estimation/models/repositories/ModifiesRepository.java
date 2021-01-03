package exchange.estimation.models.repositories;

import exchange.estimation.models.entities.Modifies;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModifiesRepository extends MongoRepository<Modifies, String> {
    List<Modifies> findAllByOrderByDate();
}

