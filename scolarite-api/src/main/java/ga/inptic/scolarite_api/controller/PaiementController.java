package ga.inptic.scolarite_api.controller;

import ga.inptic.scolarite_api.dto.PaiementRequest;
import ga.inptic.scolarite_api.entity.Paiement;
import ga.inptic.scolarite_api.service.PaiementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService paiementService;

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @PostMapping
    public ResponseEntity<Paiement> encaisser(@Valid @RequestBody PaiementRequest req,
                                              Authentication auth) {
        String acteur = (auth != null) ? auth.getName() : "comptable@inptic.ga";
        Paiement saved = paiementService.enregistrerPaiement(
                req.getInscriptionId(), req.getMontant(), req.getModePaiement(), acteur);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/impayes/filiere/{filiereId}")
    public List> impayesParFiliere(@PathVariable Long filiereId) {
        return paiementService.listerImpayesParFiliere(filiereId);
    }
}