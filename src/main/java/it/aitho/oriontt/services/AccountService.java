package it.aitho.oriontt.services;

import it.aitho.oriontt.dto.AccountDTO;
import it.aitho.oriontt.dto.PlayerDTO;
import it.aitho.oriontt.dto.RegisterRequestDTO;
import it.aitho.oriontt.entities.Account;
import it.aitho.oriontt.entities.AccountRole;
import it.aitho.oriontt.entities.AccountStatus;
import it.aitho.oriontt.entities.Player;
import it.aitho.oriontt.repositories.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // POST iscrizione: crea un account PENDING (ruolo PLAYER, senza password)
    public AccountDTO register(RegisterRequestDTO request) {

        String email = request.getEmail().trim().toLowerCase();

        // 1. email già usata → 409 CONFLICT (email è UK nel DB: meglio fermarsi prima del save)
        if (accountRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        // 2. nuovo oggetto Account vuoto (id lo genera il DB al save)
        Account account = new Account();
        account.setFirstName(request.getFirstName());
        account.setLastName(request.getLastName());
        account.setEmail(email);
        account.setStatus(AccountStatus.PENDING);
        account.setRole(AccountRole.PLAYER);

        accountRepository.save(account);    // id null → INSERT nel DB
        return filterEntityToDTO(account);  // Account → AccountDTO per la risposta
    }

    private AccountDTO filterEntityToDTO(Account account) {
        return new AccountDTO(account.getId(), account.getFirstName(), account.getLastName(), account.getEmail());
    }

}
