package it.aitho.oriontt.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "players") // la tabella sta nel database orion_tt su MySQL
public class Player {

    // Chiave primaria, generata dal database (auto increment)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", unique = true)
    private Account account;

    // costruttore vuoto: serve a JPA/Hibernate per creare l'oggetto Player
    // quando legge una riga dalla tabella players
    public Player() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
