package ga.inptic.scolarite_api.service;

import ga.inptic.scolarite_api.entity.*;
import ga.inptic.scolarite_api.exception.BusinessException;
import ga.inptic.scolarite_api.exception.ResourceNotFoundException;
import ga.inptic.scolarite_api.repository.InscriptionRepository;
import ga.inptic.scolarite_api.repository.PaiementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final InscriptionRepository inscriptionRepository;
    private final JournalActionService journalActionService;

    public PaiementService(PaiementRepository paiementRepository,
                           InscriptionRepository inscriptionRepository,
                           JournalActionService journalActionService) {
        this.paiementRepository = paiementRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.journalActionService = journalActionService;
    }

    @Transactional
    public Paiement enregistrerPaiement(Long inscriptionId, BigDecimal montant,
                                        ModePaiement modePaiement, String acteurEmail) {
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Le montant du paiement doit être strictement positif.");
        }

        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription introuvable : " + inscriptionId));

        BigDecimal dejaPaye = paiementRepository.sumMontantByInscriptionId(inscriptionId);
        BigDecimal nouveauTotal = dejaPaye.add(montant);

        if (nouveauTotal.compareTo(inscription.getMontantTotal()) > 0) {
            throw new BusinessException("Le montant dépasse le reste à payer (" +
                    inscription.getMontantTotal().subtract(dejaPaye) + " FCFA).");
        }

        Paiement paiement = new Paiement();
        paiement.setNumeroRecu("REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        paiement.setMontant(montant);
        paiement.setModePaiement(modePaiement);
        paiement.setInscription(inscription);
        Paiement saved = paiementRepository.save(paiement);

        // R2 : Mise à jour automatique du statut de l'inscription
        if (nouveauTotal.compareTo(inscription.getMontantTotal()) == 0) {
            inscription.setStatut(StatutInscription.SOLDEE);
        } else {
            inscription.setStatut(StatutInscription.VALIDEE);
        }
        inscriptionRepository.save(inscription);

        journalActionService.enregistrer(acteurEmail, "ENCAISSEMENT_PAIEMENT",
                "Reçu " + saved.getNumeroRecu() + " de " + montant + " FCFA pour l'inscription #" + inscriptionId);

        return saved;
    }

    // F7 : État des impayés par filière
    public List<Map<String, Object>> listerImpayesParFiliere(Long filiereId) {
        List<Inscription> nonSoldees = inscriptionRepository.findByFiliereIdAndStatutNot(
                filiereId, StatutInscription.SOLDEE);

        List<Map<String, Object>> resultat = new ArrayList<>();
        for (Inscription ins : nonSoldees) {
            BigDecimal paye = paiementRepository.sumMontantByInscriptionId(ins.getId());
            BigDecimal reste = ins.getMontantTotal().subtract(paye);

            Map<String, Object> ligne = new HashMap<>();
            ligne.put("inscriptionId", ins.getId());
            ligne.put("matricule", ins.getEtudiant().getMatricule());
            ligne.put("etudiant", ins.getEtudiant().getNom() + " " + ins.getEtudiant().getPrenom());
            ligne.put("montantTotal", ins.getMontantTotal());
            ligne.put("dejaPaye", paye);
            ligne.put("resteAPayer", reste);
            ligne.put("statut", ins.getStatut());
            resultat.add(ligne);
        }
        return resultat;
    }
}