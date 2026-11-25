
package ga.inptic.scolarite_api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_action")
public class JournalAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "acteur_email", nullable = false, length = 100)
    private String acteurEmail;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "date_action", nullable = false)
    private LocalDateTime dateAction = LocalDateTime.now();

    public JournalAction() {}

    public JournalAction(String acteurEmail, String action, String details) {
        this.acteurEmail = acteurEmail;
        this.action = action;
        this.details = details;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getActeurEmail() { return acteurEmail; }
    public void setActeurEmail(String acteurEmail) { this.acteurEmail = acteurEmail; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getDateAction() { return dateAction; }
    public void setDateAction(LocalDateTime dateAction) { this.dateAction = dateAction; }
}
