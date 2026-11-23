package it.aitho.oriontt.dto;

import it.aitho.oriontt.entities.MatchStatus;

// DTO = oggetto che il backend manda al frontend
// Rappresenta UNA partita, vista dal giocatore del profilo
public class MatchSummaryDTO {

    private Long matchId;               // id della partita (per aprire il dettaglio dei set)
    private String opponentFirstName;   // nome dell'avversario
    private String opponentLastName;    // cognome dell'avversario
    private MatchStatus status;         // SCHEDULED / IN_PROGRESS / FINISHED (enum: niente errori di battitura)
    private Boolean won;                // true = vinta, false = persa, null = non ancora finita

    // constructor: lo chiama il Service per riempire il DTO con i dati presi dal DB
    public MatchSummaryDTO(Long matchId, String opponentFirstName, String opponentLastName, MatchStatus status, Boolean won) {
        this.matchId = matchId;
        this.opponentFirstName = opponentFirstName;
        this.opponentLastName = opponentLastName;
        this.status = status;
        this.won = won;
    }

    // getter: Spring li usa per creare il JSON (senza getter → JSON vuoto {})
    // getMatchId() → "matchId" nel JSON

    public Long getMatchId() {
        return matchId;
    }

    public String getOpponentFirstName() {
        return opponentFirstName;
    }

    public String getOpponentLastName() {
        return opponentLastName;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public Boolean getWon() {
        return won;
    }
}