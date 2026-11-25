package ga.inptic.scolarite_api.service;

import ga.inptic.scolarite_api.entity.*;
import ga.inptic.scolarite_api.exception.BusinessException;
import ga.inptic.scolarite_api.exception.ResourceNotFoundException;
import ga.inptic.scolarite_api.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogueService {

    private final FiliereRepository filiereRepository;
    private final NiveauRepository niveauRepository;
    private final UniteEnseignementRepository ueRepository;

    public CatalogueService(FiliereRepository filiereRepository,
                            NiveauRepository niveauRepository,
                            UniteEnseignementRepository ueRepository) {
        this.filiereRepository = filiereRepository;
        this.niveauRepository = niveauRepository;
        this.ueRepository = ueRepository;
    }

    // --- FILIERES ---
    public Filiere creerFiliere(Filiere filiere) {
        if (filiereRepository.existsByCode(filiere.getCode())) {
            throw new BusinessException("Une filière avec le code " + filiere.getCode() + " existe déjà.");
        }
        return filiereRepository.save(filiere);
    }

    public List<Filiere> listerFilieres() {
        return filiereRepository.findAll();
    }

    public void supprimerFiliere(Long id) {
        if (!filiereRepository.existsById(id)) {
            throw new ResourceNotFoundException("Filière introuvable : " + id);
        }
        filiereRepository.deleteById(id);
    }

    // --- NIVEAUX ---
    public Niveau creerNiveau(Niveau niveau) {
        return niveauRepository.save(niveau);
    }

    public List<Niveau> listerNiveaux() {
        return niveauRepository.findAll();
    }

    // --- UNITES D'ENSEIGNEMENT (UE) ---
    public UniteEnseignement creerUe(String codeUe, String libelle, Integer credits,
                                     Integer coefficient, Semestre semestre,
                                     Long filiereId, Long niveauId) {
        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() -> new ResourceNotFoundException("Filière introuvable : " + filiereId));
        Niveau niveau = niveauRepository.findById(niveauId)
                .orElseThrow(() -> new ResourceNotFoundException("Niveau introuvable : " + niveauId));

        UniteEnseignement ue = new UniteEnseignement();
        ue.setCodeUe(codeUe);
        ue.setLibelle(libelle);
        ue.setCredits(credits);
        ue.setCoefficient(coefficient != null ? coefficient : 1);
        ue.setSemestre(semestre);
        ue.setFiliere(filiere);
        ue.setNiveau(niveau);

        return ueRepository.save(ue);
    }

    public Page<UniteEnseignement> rechercherUes(String motCle, Pageable pageable) {
        if (motCle == null || motCle.isBlank()) {
            return ueRepository.findAll(pageable);
        }
        return ueRepository.findByLibelleContainingIgnoreCase(motCle, pageable);
    }
}