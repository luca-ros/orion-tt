package it.aitho.oriontt.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // TODO: sostituire @JsonIgnore con un DTO per le partite (per non esporre l'entity Account)
    @JsonIgnore
    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    // ruolo salvato come testo (ADMIN / PLAYER), non come numero
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private AccountRole role;

    // stato dell'iscrizione salvato come testo (PENDING / APPROVED / REJECTED)
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    // dati scritti dal giocatore nel modulo di iscrizione (tu li controlli prima di approvare)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "fitet_card_number")
    private String fitetCardNumber;


    // costruttore vuoto: JPA lo usa per creare l'oggetto prima di riempirlo coi dati del DB
    public Account() {
    }

    // getter e setter: JPA e Spring li usano per leggere e scrivere i campi
    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFitetCardNumber() {
        return fitetCardNumber;
    }

    public void setFitetCardNumber(String fitetCardNumber) {
        this.fitetCardNumber = fitetCardNumber;
    }
}