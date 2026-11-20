package it.aitho.oriontt.dto;

public class PlayerMatchDTO {

    private Long matchId;
    private String opponentFirstName;
    private String opponentLastName;
    private String status;

    public PlayerMatchDTO(Long matchId, String opponentFirstName, String opponentLastName, String status ) {

        this.matchId = matchId;
        this.opponentFirstName = opponentFirstName;
        this.opponentLastName = opponentLastName;
        this.status = status;

    }

    public Long getMatchId() {
        return matchId;
    }

    public String getOpponentFirstName() {
        return opponentFirstName;
    }

    public String getStatus() {
        return status;
    }

    public String getOpponentLastName() {
        return opponentLastName;
    }
}
