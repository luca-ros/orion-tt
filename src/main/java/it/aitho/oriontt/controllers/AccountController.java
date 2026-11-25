package it.aitho.oriontt.controllers;


import it.aitho.oriontt.dto.AccountDTO;
import it.aitho.oriontt.dto.RegisterRequestDTO;
import it.aitho.oriontt.services.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")   // autorizza le chiamate dal frontend React
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // POST API Register account (mandata da Ale su Teams)
    // POST /api/accounts → iscrizione di un nuovo giocatore (account PENDING)
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)   // 201 invece di 200: è stata creata una risorsa nuova
    public AccountDTO register(@RequestBody RegisterRequestDTO request) {   // @RequestBody: legge il JSON dal Body e riempie il DTO
        return accountService.register(request);   // il Controller non decide nulla: passa tutto al Service
    }

}
