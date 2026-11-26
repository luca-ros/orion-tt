package it.aitho.oriontt.dto;

public class AccountDTO {
    private Long id;          // serve al frontend per scegliere il giocatore (es. POST /api/matches)
    private String firstName;
    private String lastName;
    private String email;
    private String fitetCardNumber;

    public AccountDTO(Long id, String firstName, String lastName, String email, String fitetCardNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.fitetCardNumber = fitetCardNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
