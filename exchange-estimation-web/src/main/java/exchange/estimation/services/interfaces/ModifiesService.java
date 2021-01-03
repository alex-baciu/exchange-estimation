package exchange.estimation.services.interfaces;

import exchange.estimation.models.entities.Modifies;

public interface ModifiesService {
    Modifies GetLastModify() throws Exception;
    void PostModify(Modifies modify);
}
