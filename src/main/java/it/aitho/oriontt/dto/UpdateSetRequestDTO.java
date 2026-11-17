package it.aitho.oriontt.dto;

public class UpdateSetRequestDTO {

    private Integer player1Points;
    private Integer player2Points;


    public UpdateSetRequestDTO(Integer player1Points, Integer player2Points) {

        this.player1Points = player1Points;
        this.player2Points = player2Points;
    }

    public Integer getPlayer1Points() {
        return player1Points;
    }

    public void setPlayer1Points(Integer player1Points) {
        this.player1Points = player1Points;
    }

    public Integer getPlayer2Points() {
        return player2Points;
    }

    public void setPlayer2Points(Integer player2Points) {
        this.player2Points = player2Points;
    }
}
