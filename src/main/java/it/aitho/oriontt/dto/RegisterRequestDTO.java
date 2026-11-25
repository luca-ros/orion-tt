package it.aitho.oriontt.dto;

public class RegisterRequestDTO {
    private String email;            // obbligatoria
    private String firstName;        // obbligatorio
    private String lastName;         // obbligatorio
    private String fitetCardNumber;  // facoltativo: se non arriva nel JSON resta null

    public RegisterRequestDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
