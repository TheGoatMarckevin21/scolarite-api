
package ga.inptic.scolarite_api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "unite_enseignement")
public class UniteEnseignement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_ue", nullable = false, unique = true, length = 20)
    private String codeUe;

    @Column(nullable = false, length = 100)
    private String libelle;

    @Column(nullable = false)
    private Integer credits;

    @Column(nullable = false)
    private Integer coefficient = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Semestre semestre;

    @ManyToOne
    @JoinColumn(name = "filiere_id", nullable = false)
    private Filiere filiere;

    @ManyToOne
    @JoinColumn(name = "niveau_id", nullable = false)
    private Niveau niveau;

    public UniteEnseignement() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodeUe() { return codeUe; }
    public void setCodeUe(String codeUe) { this.codeUe = codeUe; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }
    public Integer getCoefficient() { return coefficient; }
    public void setCoefficient(Integer coefficient) { this.coefficient = coefficient; }
    public Semestre getSemestre() { return semestre; }
    public void setSemestre(Semestre semestre) { this.semestre = semestre; }
    public Filiere getFiliere() { return filiere; }
    public void setFiliere(Filiere filiere) { this.filiere = filiere; }
    public Niveau getNiveau() { return niveau; }
    public void setNiveau(Niveau niveau) { this.niveau = niveau; }
}