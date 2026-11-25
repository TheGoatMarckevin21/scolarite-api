package ga.inptic.scolarite_api.dto;

import ga.inptic.scolarite_api.entity.ModePaiement;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class PaiementRequest {
    @NotNull(message = "L'identifiant de l'inscription est obligatoire.")
    private Long inscriptionId;

    @NotNull(message = "Le montant est obligatoire.")
    @DecimalMin(value = "1.00", message = "Le montant doit être supérieur à 0.")
    private BigDecimal montant;

    @NotNull(message = "Le mode de paiement est obligatoire.")
    private ModePaiement modePaiement;

    
   public Long getInscriptionId() { return inscriptionId; }
    public void setInscriptionId(Long inscriptionId) { this.inscriptionId = inscriptionId; }
    public BigDecimal getMontant() { return montant; }
    public void setMontant(BigDecimal montant) { this.montant = montant; }
    public ModePaiement getModePaiement() { return modePaiement; }
    public void setModePaiement(ModePaiement modePaiement) { this.modePaiement = modePaiement; }
}