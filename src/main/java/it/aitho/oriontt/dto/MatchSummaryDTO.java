package it.aitho.oriontt.dto;

import it.aitho.oriontt.entities.MatchStatus;

import java.time.LocalDateTime;
import java.util.Date;

// DTO = oggetto che il backend manda al frontend (non è una tabella del DB)
// Rappresenta UNA partita, vista dal giocatore del profilo
public class MatchSummaryDTO {

    private Long matchId;               // id della partita (per aprire il dettaglio dei set)
    private String opponentFirstName;   // nome dell'avversario
    private String opponentLastName;    // cognome dell'avversario
    private MatchStatus status;         // SCHEDULED / IN_PROGRESS / FINISHED (enum: niente errori di battitura)
    private Boolean won;                // true = vinta, false = persa, null = non ancora finita
    private Date plannedDate;           // data prevista (stesso tipo di Match.getPlannedDate())
    private LocalDateTime finishedAt;   // data di fine, null se non ancora finita (stesso tipo di Match.getFinishedAt())
    private int setsWon;
    private int opponentSetsWon;

    // constructor: lo chiama il Service per riempire il DTO con i dati presi dal DB
    public MatchSummaryDTO(Long matchId, String opponentFirstName, String opponentLastName,
                           MatchStatus status, Boolean won, Date plannedDate, LocalDateTime finishedAt, int setsWon, int opponentSetsWon) {
        this.matchId = matchId;
        this.opponentFirstName = opponentFirstName;
        this.opponentLastName = opponentLastName;
        this.status = status;
        this.won = won;
        this.plannedDate = plannedDate;
        this.finishedAt = finishedAt;
        this.setsWon = setsWon;
        this.opponentSetsWon = opponentSetsWon;
    }

    // getter: Spring li usa per creare il JSON (senza getter → JSON vuoto {})
    // getPlannedDate() → "plannedDate" nel JSON, getFinishedAt() → "finishedAt"

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

    public Date getPlannedDate() {
        return plannedDate;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public int getSetsWon() {
        return setsWon;
    }

    public int getOpponentSetsWon() {
        return opponentSetsWon;
    }
}