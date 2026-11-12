package it.aitho.oriontt.dto;

import java.util.Date;

public class CreateMatchRequestDTO {

    private Long player1Id;
    private Long player2Id;
    private Date plannedDate;


    public CreateMatchRequestDTO(Long player1Id, Long player2Id, Date plannedDate) {
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.plannedDate = plannedDate;
    }

    public Long getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(Long player1Id) {
        this.player1Id = player1Id;
    }

    public Long getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(Long player2Id) {
        this.player2Id = player2Id;
    }

    public Date getPlannedDate() {
        return plannedDate;
    }

    public void setPlannedDate(Date plannedDate) {
        this.plannedDate = plannedDate;
    }
}
