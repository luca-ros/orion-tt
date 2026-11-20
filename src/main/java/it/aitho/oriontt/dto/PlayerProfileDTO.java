package it.aitho.oriontt.dto;

public class PlayerProfileDTO {

    private Long id;          // serve al frontend per scegliere il giocatore: api/players/6/profile
    private String firstName;
    private String lastName;
    private String fitetCardNumber;

    public PlayerProfileDTO(Long id, String firstName, String lastName, String fitetCardNumber) {
        // parametro = tipo + nome (Long id), mai due tipi
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fitetCardNumber = fitetCardNumber;
        // campo ← parametro: salvo i valori passati dal Service nei campi del DTO
        // this = "di questa class": distingue il campo dal parametro (hanno lo stesso nome)
        // senza queste righe i campi resterebbero null → nel JSON "firstName": null
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFitetCardNumber() {
        return fitetCardNumber;
    }

    public String getLastName() {
        return lastName;
    }
}
