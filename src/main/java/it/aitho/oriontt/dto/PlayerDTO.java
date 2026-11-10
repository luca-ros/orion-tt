package it.aitho.oriontt.dto;

public class PlayerDTO {
    // DTO (Data Transfer Object): contenitore dei soli dati da mandare al client
    // NON è una Entity: niente @Entity, niente @Column, niente tabella
    // NON è un anello della catena: non chiama nessuno, non riceve niente da Spring
    // Player = tutti i campi (per JPA) → PlayerDTO = solo quelli da mostrare in Postman


    private String firstName;
    private String lastName;
    private String email;
    // campi: gli stessi di Player, SENZA l'id (è proprio il motivo del DTO)
    // il tipo si sceglie come sempre: testo → String

    public PlayerDTO(String firstName, String lastName, String email) {
        // costruttore CON parametri (in Player è vuoto perché lo usa JPA)
        // il DTO lo creiamo noi nel Service: new PlayerDTO("Long", "Ma")
        // parametro = tipo + nome (String firstName), mai due tipi

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        // campo          parametro
        // campo e parametro dello stesso tipo (String), e il parametro viene salvato nel campo
        // prendi il nome e il cognome che mi ha passato il Service (parametri) e salvali nei miei campi
        //   es. new PlayerDTO("Long", "Ma") → firstName = "Long", lastName = "Ma"
        // this = "di questa class": distingue il campo dal parametro (hanno lo stesso nome)
        // qui Spring NON c'entra: i valori arrivano da chi crea il DTO, cioè il Service
        // senza queste righe i campi resterebbero null → nel JSON "firstName": null
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() { return email;}
}
