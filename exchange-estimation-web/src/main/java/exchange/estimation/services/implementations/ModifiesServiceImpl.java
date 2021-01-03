package exchange.estimation.services.implementations;

import exchange.estimation.models.entities.Modifies;
import exchange.estimation.models.repositories.ModifiesRepository;
import exchange.estimation.services.interfaces.ModifiesService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModifiesServiceImpl implements ModifiesService {
    private final ModifiesRepository modifiesRepository;

    public ModifiesServiceImpl(ModifiesRepository modifiesRepository) {
        this.modifiesRepository = modifiesRepository;
    }

    @Override
    public Modifies GetLastModify() throws Exception {
        List<Modifies> modifies = modifiesRepository.findAllByOrderByDate();

        if (modifies.size() == 0) {
            throw new Exception("No date modify exists");
        }

        return modifies.get(modifies.size() - 1);
    }

    @Override
    public void PostModify(Modifies modify) {
        modifiesRepository.save(modify);
    }
}
