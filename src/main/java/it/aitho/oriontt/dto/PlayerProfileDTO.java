package it.aitho.oriontt.dto;

import java.util.List;

public class PlayerProfileDTO {

    private Long id;          // serve al frontend per scegliere il giocatore: api/players/6/profile
    private String firstName;
    private String lastName;
    private String fitetCardNumber;
    private List<MatchSummaryDTO> matches;

    public PlayerProfileDTO(Long id, String firstName, String lastName, String fitetCardNumber, List<MatchSummaryDTO> matches) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fitetCardNumber = fitetCardNumber;
        this.matches = matches;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFitetCardNumber() {
        return fitetCardNumber;
    }

    public List<MatchSummaryDTO> getMatches() {
        return matches;
    }
}
