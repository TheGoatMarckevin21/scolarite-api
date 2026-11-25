package ga.inptic.scolarite_api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class NoteRequest {
    @NotNull(message = "L'identifiant de l'inscription est obligatoire.")
    private Long inscriptionId;

    @NotNull(message = "L'identifiant de l'UE est obligatoire.")
    private Long ueId;

    @NotNull(message = "La note de CC est obligatoire.")
    @DecimalMin(value = "0.00", message = "La note de CC doit être >= 0.")
    @DecimalMax(value = "20.00", message = "La note de CC doit être <= 20.")
    private BigDecimal noteCc;

    @NotNull(message = "La note d'examen est obligatoire.")
    @DecimalMin(value = "0.00", message = "La note d'examen doit être >= 0.")
    @DecimalMax(value = "20.00", message = "La note d'examen doit être <= 20.")
    private BigDecimal noteExamen;

    // Getters et Setters
    public Long getInscriptionId() { return inscriptionId; }
    public void setInscriptionId(Long inscriptionId) { this.inscriptionId = inscriptionId; }
    public Long getUeId() { return ueId; }
    public void setUeId(Long ueId) { this.ueId = ueId; }
    public BigDecimal getNoteCc() { return noteCc; }
    public void setNoteCc(BigDecimal noteCc) { this.noteCc = noteCc; }
    public BigDecimal getNoteExamen() { return noteExamen; }
    public void setNoteExamen(BigDecimal noteExamen) { this.noteExamen = noteExamen; }
}