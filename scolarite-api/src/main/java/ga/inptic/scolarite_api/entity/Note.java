
package ga.inptic.scolarite_api.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "note")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "note_cc", precision = 4, scale = 2)
    private BigDecimal noteCc;

    @Column(name = "note_examen", precision = 4, scale = 2)
    private BigDecimal noteExamen;

    @Column(name = "moyenne_ue", precision = 4, scale = 2)
    private BigDecimal moyenneUe;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_validation", nullable = false, length = 20)
    private StatutValidation statutValidation = StatutValidation.EN_COURS;

    @ManyToOne
    @JoinColumn(name = "inscription_id", nullable = false)
    private Inscription inscription;

    @ManyToOne
    @JoinColumn(name = "ue_id", nullable = false)
    private UniteEnseignement uniteEnseignement;

    public Note() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getNoteCc() { return noteCc; }
    public void setNoteCc(BigDecimal noteCc) { this.noteCc = noteCc; }
    public BigDecimal getNoteExamen() { return noteExamen; }
    public void setNoteExamen(BigDecimal noteExamen) { this.noteExamen = noteExamen; }
    public BigDecimal getMoyenneUe() { return moyenneUe; }
    public void setMoyenneUe(BigDecimal moyenneUe) { this.moyenneUe = moyenneUe; }
    public StatutValidation getStatutValidation() { return statutValidation; }
    public void setStatutValidation(StatutValidation statutValidation) { this.statutValidation = statutValidation; }
    public Inscription getInscription() { return inscription; }
    public void setInscription(Inscription inscription) { this.inscription = inscription; }
    public UniteEnseignement getUniteEnseignement() { return uniteEnseignement; }
    public void setUniteEnseignement(UniteEnseignement uniteEnseignement) { this.uniteEnseignement = uniteEnseignement; }
}