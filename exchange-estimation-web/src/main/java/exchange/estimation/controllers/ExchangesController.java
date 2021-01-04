package exchange.estimation.controllers;

import exchange.estimation.services.interfaces.ExchangesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/exchanges")
@CrossOrigin(origins = "*", maxAge = 3600, allowedHeaders = "*")
public class ExchangesController {
    private final ExchangesService exchangesService;

    public ExchangesController(ExchangesService exchangesService) {
        this.exchangesService = exchangesService;
    }

    @GetMapping("/type/{type}/number/{number}/currency/{currency}/methode/{methode}")
    ResponseEntity<?> GetUnseenMessage(@PathVariable String type, @PathVariable Long number, @PathVariable String currency, @PathVariable String methode) {
        try {
            this.exchangesService.GetGraph(type,number, currency, methode);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
