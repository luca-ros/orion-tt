package it.aitho.oriontt.entities;

import jakarta.persistence.*;

// Entity: Hibernate crea la tabella "accounts" partendo da questa class
@Entity
@Table(name = "accounts")
public class Account {

    // PK: generata dal database (AUTO_INCREMENT)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // email: obbligatoria e unica (UK), serve per l'accesso
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // ruolo salvato come testo (ADMIN / PLAYER), non come numero
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private AccountRole role;

    // stato dell'iscrizione salvato come testo (PENDING / APPROVED)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    // dati scritti dal giocatore nel modulo di iscrizione (tu li controlli prima di approvare)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    // facoltativa: chi non ha la tessera FITeT la lascia vuota
    @Column(name = "fitet_card_number")
    private String fitetCardNumber;

    // collegamento al giocatore: crea la colonna accounts.player_id (FK)
    // unique = true → un player può avere un solo account (uno a uno)
    // può essere NULL: resta vuota finché non approvi l'iscrizione
    // niente cascade: cancellare un account NON deve cancellare il player e il suo storico
    @OneToOne
    @JoinColumn(name = "player_id", unique = true)
    private Player player;

    // costruttore vuoto: JPA lo usa per creare l'oggetto prima di riempirlo coi dati del DB
    public Account() {
    }

    // getter e setter: JPA e Spring li usano per leggere e scrivere i campi
    public Long getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public AccountRole getRole() { return role; }
    public void setRole(AccountRole role) { this.role = role; }

    public AccountStatus getStatus() { return status; }
    public void setStatus(AccountStatus status) { this.status = status; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFitetCardNumber() { return fitetCardNumber; }
    public void setFitetCardNumber(String fitetCardNumber) { this.fitetCardNumber = fitetCardNumber; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
}