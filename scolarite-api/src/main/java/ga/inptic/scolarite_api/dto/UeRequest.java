package ga.inptic.scolarite_api.dto;

import ga.inptic.scolarite_api.entity.Semestre;
import jakarta.validation.constraints.*;

public class UeRequest {
    @NotBlank(message = "Le code de l'UE est obligatoire.")
    private String codeUe;

    @NotBlank(message = "Le libellé de l'UE est obligatoire.")
    private String libelle;

    @NotNull(message = "Le nombre de crédits est obligatoire.")
    @Min(value = 1, message = "Les crédits doivent être supérieurs ou égaux à 1.")
    private Integer credits;

    @Min(value = 1, message = "Le coefficient doit être au moins 1.")
    private Integer coefficient = 1;

    @NotNull(message = "Le semestre (S1 ou S2) est obligatoire.")
    private Semestre semestre;

    @NotNull(message = "La filière est obligatoire.")
    private Long filiereId;

    @NotNull(message = "Le niveau est obligatoire.")
    private Long niveauId;

    // Getters et Setters
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
    public Long getFiliereId() { return filiereId; }
    public void setFiliereId(Long filiereId) { this.filiereId = filiereId; }
    public Long getNiveauId() { return niveauId; }
    public void setNiveauId(Long niveauId) { this.niveauId = niveauId; }
}