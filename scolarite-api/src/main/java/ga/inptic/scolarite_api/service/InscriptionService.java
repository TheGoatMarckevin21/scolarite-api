package ga.inptic.scolarite_api.service;

import ga.inptic.scolarite_api.entity.*;
import ga.inptic.scolarite_api.exception.ResourceNotFoundException;
import ga.inptic.scolarite_api.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class InscriptionService {

    private final EtudiantRepository etudiantRepository;
    private final InscriptionRepository inscriptionRepository;
    private final FiliereRepository filiereRepository;
    private final NiveauRepository niveauRepository;
    private final JournalActionService journalActionService;

    public InscriptionService(EtudiantRepository etudiantRepository,
                              InscriptionRepository inscriptionRepository,
                              FiliereRepository filiereRepository,
                              NiveauRepository niveauRepository,
                              JournalActionService journalActionService) {
        this.etudiantRepository = etudiantRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.filiereRepository = filiereRepository;
        this.niveauRepository = niveauRepository;
        this.journalActionService = journalActionService;
    }

    @Transactional
    public Inscription inscrireNouvelEtudiant(String nom, String prenom, LocalDate dateNaissance,
                                              String telephone, Long filiereId, Long niveauId,
                                              String anneeAcademique, String acteurEmail) {
        Filiere filiere = filiereRepository.findById(filiereId)
                .orElseThrow(() -> new ResourceNotFoundException("Filière introuvable avec l'id : " + filiereId));

        Niveau niveau = niveauRepository.findById(niveauId)
                .orElseThrow(() -> new ResourceNotFoundException("Niveau introuvable avec l'id : " + niveauId));

        // R1 : Génération automatique du matricule ANNEE-FILIERE-NNNN
        String anneeDebut = anneeAcademique.split("-")[0];
        String prefix = anneeDebut + "-" + filiere.getCode().toUpperCase() + "-";
        long compteur = etudiantRepository.countByMatriculeStartingWith(prefix) + 1;
        String matricule = prefix + String.format("%04d", compteur);

        Etudiant etudiant = new Etudiant();
        etudiant.setMatricule(matricule);
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        etudiant.setDateNaissance(dateNaissance);
        etudiant.setTelephone(telephone);
        etudiant = etudiantRepository.save(etudiant);

        // Création de l'inscription en statut EN_ATTENTE (R2)
        Inscription inscription = new Inscription();
        inscription.setAnneeAcademique(anneeAcademique);
        inscription.setMontantTotal(niveau.getFraisScolarite());
        inscription.setStatut(StatutInscription.EN_ATTENTE);
        inscription.setEtudiant(etudiant);
        inscription.setFiliere(filiere);
        inscription.setNiveau(niveau);

        Inscription saved = inscriptionRepository.save(inscription);

        journalActionService.enregistrer(acteurEmail, "NOUVELLE_INSCRIPTION",
                "Inscription de " + matricule + " en " + filiere.getCode() + " " + niveau.getCode());

        return saved;
    }

    public Page<Etudiant> rechercherEtudiants(String motCle, Pageable pageable) {
        if (motCle == null || motCle.isBlank()) {
            return etudiantRepository.findAll(pageable);
        }
        return etudiantRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(motCle, motCle, pageable);
    }

    public Page<Inscription> listerInscriptions(String annee, Pageable pageable) {
        if (annee == null || annee.isBlank()) {
            return inscriptionRepository.findAll(pageable);
        }
        return inscriptionRepository.findByAnneeAcademique(annee, pageable);
    }
}