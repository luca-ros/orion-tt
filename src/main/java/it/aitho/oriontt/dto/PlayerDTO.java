package it.aitho.oriontt.dto;

public class PlayerDTO {
    // DTO (Data Transfer Object): contenitore dei soli dati da mandare al client
    // NON è una Entity: niente @Entity, niente @Column, niente tabella
    // NON è un anello della catena: non chiama nessuno, non riceve niente da Spring
    // Player = tutti i campi (per JPA) → PlayerDTO = solo quelli da mandare al client

    private Long id;          // serve al frontend per scegliere il giocatore (es. POST /api/matches)
    private String firstName;
    private String lastName;
    private String email;

    public PlayerDTO(Long id, String firstName, String lastName, String email) {
        // costruttore CON parametri (in Player è vuoto perché lo usa JPA)
        // il DTO lo creiamo noi nel Service: new PlayerDTO(1L, "Long", "Ma", "test@tocca.it")
        // parametro = tipo + nome (Long id), mai due tipi
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        // campo ← parametro: salvo i valori passati dal Service nei campi del DTO
        // this = "di questa class": distingue il campo dal parametro (hanno lo stesso nome)
        // senza queste righe i campi resterebbero null → nel JSON "firstName": null
    }

    // getter: Spring li usa per scrivere il JSON (senza getter il campo non compare)
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}