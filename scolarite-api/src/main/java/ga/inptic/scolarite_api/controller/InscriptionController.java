package ga.inptic.scolarite_api.controller;

import ga.inptic.scolarite_api.dto.InscriptionRequest;
import ga.inptic.scolarite_api.entity.Etudiant;
import ga.inptic.scolarite_api.entity.Inscription;
import ga.inptic.scolarite_api.service.InscriptionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inscriptions")
public class InscriptionController {

    private final InscriptionService inscriptionService;

    public InscriptionController(InscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    @PostMapping
    public ResponseEntity<Inscription> inscrire(@Valid @RequestBody InscriptionRequest req,
                                                Authentication auth) {
        String acteur = (auth != null) ? auth.getName() : "scolarite@inptic.ga";
        Inscription saved = inscriptionService.inscrireNouvelEtudiant(
                req.getNom(), req.getPrenom(), req.getDateNaissance(), req.getTelephone(),
                req.getFiliereId(), req.getNiveauId(), req.getAnneeAcademique(), acteur);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // S3 : Recherche, tri et pagination des étudiants
    @GetMapping("/etudiants")
    public Page<Etudiant> rechercherEtudiants(@RequestParam(required = false) String motCle,
                                              Pageable pageable) {
        return inscriptionService.rechercherEtudiants(motCle, pageable);
    }

    @GetMapping
    public Page<Inscription> listerInscriptions(@RequestParam(required = false) String annee,
                                                Pageable pageable) {
        return inscriptionService.listerInscriptions(annee, pageable);
    }
}