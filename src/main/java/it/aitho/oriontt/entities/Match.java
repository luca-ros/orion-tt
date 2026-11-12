package it.aitho.oriontt.entities;


import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "matches") // la tabella sta nel database orion_tt su MySQL
public class Match {

    // Chiave primaria, generata dal database (auto increment)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // la colonna player_1_id contiene l'id della tabella players
    @ManyToOne
    @JoinColumn(name = "player_1_id")
    private Player player1;


    @ManyToOne
    @JoinColumn(name = "player_2_id")
    private Player player2;

    @OneToMany(mappedBy = "match")
    private List<MatchSet> matchSets;

    // vincitore: resta NULL finché la partita non è FINISHED
    @ManyToOne
    @JoinColumn(name = "winner_id")
    private Player winner;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MatchStatus status;

    @Column(name = "planned_date")
    private Date plannedDate;

    // costruttore vuoto: serve a JPA/Hibernate per creare l'oggetto Match
    // quando legge una riga dalla tabella matches
    public Match() {
    }


    public Date getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(Date plannedDate) {
        this.plannedDate = plannedDate;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public MatchStatus getStatus() {
        return status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }

    // getter: restituisce tutti i set della partita
    public List<MatchSet> getMatchSets() {
        return matchSets;
    }

    // setter: imposta la lista dei set
    public void setMatchSets(List<MatchSet> matchSets) {
        this.matchSets = matchSets;
    }
}
