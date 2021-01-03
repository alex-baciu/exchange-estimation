package exchange.estimation.controllers;

import exchange.estimation.services.interfaces.ExchangesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/exchanges")
public class ExchangesController {
    private final ExchangesService exchangesService;

    public ExchangesController(ExchangesService exchangesService) {
        this.exchangesService = exchangesService;
    }

    @GetMapping("/type/{type}/number/{number}")
    ResponseEntity<?> GetUnseenMessage(@PathVariable String type, @PathVariable Long number) {
        try {
            this.exchangesService.GetGraph("luna",2l);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
