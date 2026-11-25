package ga.inptic.scolarite_api.controller;

import ga.inptic.scolarite_api.dto.UeRequest;
import ga.inptic.scolarite_api.entity.*;
import ga.inptic.scolarite_api.service.CatalogueService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogue")
public class CatalogueController {

    private final CatalogueService catalogueService;

    public CatalogueController(CatalogueService catalogueService) {
        this.catalogueService = catalogueService;
    }

    @PostMapping("/filieres")
    public ResponseEntity<Filiere> creerFiliere(@RequestBody Filiere filiere) {
        return new ResponseEntity<>(catalogueService.creerFiliere(filiere), HttpStatus.CREATED);
    }

    @GetMapping("/filieres")
    public List<Filiere> listerFilieres() {
        return catalogueService.listerFilieres();
    }

    @DeleteMapping("/filieres/{id}")
    public ResponseEntity<Void> supprimerFiliere(@PathVariable Long id) {
        catalogueService.supprimerFiliere(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/niveaux")
    public ResponseEntity<Niveau> creerNiveau(@RequestBody Niveau niveau) {
        return new ResponseEntity<>(catalogueService.creerNiveau(niveau), HttpStatus.CREATED);
    }

    @GetMapping("/niveaux")
    public List<Niveau> listerNiveaux() {
        return catalogueService.listerNiveaux();
    }

    @PostMapping("/ues")
    public ResponseEntity<UniteEnseignement> creerUe(@Valid @RequestBody UeRequest req) {
        UniteEnseignement ue = catalogueService.creerUe(
                req.getCodeUe(), req.getLibelle(), req.getCredits(),
                req.getCoefficient(), req.getSemestre(), req.getFiliereId(), req.getNiveauId());
        return new ResponseEntity<>(ue, HttpStatus.CREATED);
    }

    // S3 : Recherche, tri et pagination (?motCle=java&page=0&size=5&sort=libelle,asc)
    @GetMapping("/ues")
    public Page<UniteEnseignement> rechercherUes(@RequestParam(required = false) String motCle,
                                                 Pageable pageable) {
        return catalogueService.rechercherUes(motCle, pageable);
    }
}