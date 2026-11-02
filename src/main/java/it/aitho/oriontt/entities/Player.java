package it.aitho.oriontt.entities;


import jakarta.persistence.*;

@Entity
@Table(name = "players") // la tabella sta nel database orion_tt su MySQL
public class Player {

    // Chiave primaria, generata dal database (auto increment)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    // tante partite possono avere lo stesso vincitore
    @ManyToOne
    // colonna winner_id: id del Player vincitore (NULL finché la partita non è finita)
    @JoinColumn(name = "winner_id")
    private Player winner;

    @ManyToOne
    @JoinColumn(name = "player_1_id")
    private Player player1;

    @ManyToOne
    @JoinColumn(name = "player_2_id")
    private Player player2;

    // costruttore vuoto: serve a JPA/Hibernate per creare l'oggetto Player
    // quando legge una riga dalla tabella players
    public Player() {
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
