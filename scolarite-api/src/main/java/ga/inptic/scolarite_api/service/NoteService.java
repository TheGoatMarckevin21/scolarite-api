package ga.inptic.scolarite_api.service;

import ga.inptic.scolarite_api.entity.*;
import ga.inptic.scolarite_api.exception.BusinessException;
import ga.inptic.scolarite_api.exception.ResourceNotFoundException;
import ga.inptic.scolarite_api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final InscriptionRepository inscriptionRepository;
    private final UniteEnseignementRepository ueRepository;
    private final JournalActionService journalActionService;

    public NoteService(NoteRepository noteRepository,
                       InscriptionRepository inscriptionRepository,
                       UniteEnseignementRepository ueRepository,
                       JournalActionService journalActionService) {
        this.noteRepository = noteRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.ueRepository = ueRepository;
        this.journalActionService = journalActionService;
    }

    // F4 : Saisie des notes par UE
    @Transactional
    public Note saisirNote(Long inscriptionId, Long ueId, BigDecimal noteCc,
                           BigDecimal noteExamen, String acteurEmail) {
        validerBorneNote(noteCc, "CC");
        validerBorneNote(noteExamen, "Examen");

        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription introuvable : " + inscriptionId));
        UniteEnseignement ue = ueRepository.findById(ueId)
                .orElseThrow(() -> new ResourceNotFoundException("UE introuvable : " + ueId));

        Note note = noteRepository.findByInscriptionIdAndUniteEnseignementId(inscriptionId, ueId)
                .orElse(new Note());

        note.setInscription(inscription);
        note.setUniteEnseignement(ue);
        note.setNoteCc(noteCc);
        note.setNoteExamen(noteExamen);

        // Calcul immédiat de la moyenne de l'UE : 40% CC + 60% Examen
        BigDecimal moyenne = noteCc.multiply(new BigDecimal("0.40"))
                .add(noteExamen.multiply(new BigDecimal("0.60")))
                .setScale(2, RoundingMode.HALF_UP);
        note.setMoyenneUe(moyenne);
        note.setStatutValidation(StatutValidation.EN_COURS);

        Note saved = noteRepository.save(note);
        journalActionService.enregistrer(acteurEmail, "SAISIE_NOTE",
                "Note enregistrée pour inscription #" + inscriptionId + " / UE " + ue.getCodeUe());
        return saved;
    }

    // F5 + R3 : Délibération du semestre (validation des UE >= 10 et cumul des crédits)
    @Transactional
    public Map<String, Object> delibererSemestre(Long inscriptionId, Semestre semestre, String acteurEmail) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription introuvable : " + inscriptionId));

        List<Note> notes = noteRepository.findByInscriptionIdAndUniteEnseignementSemestre(inscriptionId, semestre);
        if (notes.isEmpty()) {
            throw new BusinessException("Aucune note saisie pour ce semestre.");
        }

        BigDecimal sommePonderee = BigDecimal.ZERO;
        int totalCoefficients = 0;
        int creditsAcquis = 0;
        int creditsTotaux = 0;

        for (Note n : notes) {
            UniteEnseignement ue = n.getUniteEnseignement();
            creditsTotaux += ue.getCredits();

            // R3 : Une UE est validée si moyenne >= 10
            if (n.getMoyenneUe().compareTo(new BigDecimal("10.00")) >= 0) {
                n.setStatutValidation(StatutValidation.VALIDE);
                creditsAcquis += ue.getCredits();
            } else {
                n.setStatutValidation(StatutValidation.NON_VALIDE);
            }
            noteRepository.save(n);

            BigDecimal coeff = BigDecimal.valueOf(ue.getCoefficient());
            sommePonderee = sommePonderee.add(n.getMoyenneUe().multiply(coeff));
            totalCoefficients += ue.getCoefficient();
        }

        BigDecimal moyenneSemestre = sommePonderee.divide(
                BigDecimal.valueOf(totalCoefficients), 2, RoundingMode.HALF_UP);

        journalActionService.enregistrer(acteurEmail, "DELIBERATION",
                "Délibération " + semestre + " pour " + inscription.getEtudiant().getMatricule()
                        + " - Moyenne: " + moyenneSemestre);

        Map<String, Object> resultat = new HashMap<>();
        resultat.put("matricule", inscription.getEtudiant().getMatricule());
        resultat.put("semestre", semestre);
        resultat.put("moyenneSemestre", moyenneSemestre);
        resultat.put("creditsAcquis", creditsAcquis);
        resultat.put("creditsTotaux", creditsTotaux);
        resultat.put("decision", moyenneSemestre.compareTo(new BigDecimal("10.00")) >= 0 ? "ADMIS" : "AJOURNE");
        return resultat;
    }

    // F6 : Édition du relevé de notes (format JSON structuré pour l'API)
    public Map<String, Object> genererReleveNotes(Long inscriptionId, Semestre semestre) {
        Inscription inscription = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription introuvable : " + inscriptionId));

        List<Note> notes = noteRepository.findByInscriptionIdAndUniteEnseignementSemestre(inscriptionId, semestre);

        List<Map<String, Object>> lignesNotes = new ArrayList<>();
        BigDecimal sommePonderee = BigDecimal.ZERO;
        int totalCoefficients = 0;
        int creditsAcquis = 0;

        for (Note n : notes) {
            UniteEnseignement ue = n.getUniteEnseignement();
            Map<String, Object> ligne = new HashMap<>();
            ligne.put("codeUe", ue.getCodeUe());
            ligne.put("libelle", ue.getLibelle());
            ligne.put("credits", ue.getCredits());
            ligne.put("coefficient", ue.getCoefficient());
            ligne.put("noteCc", n.getNoteCc());
            ligne.put("noteExamen", n.getNoteExamen());
            ligne.put("moyenneUe", n.getMoyenneUe());
            ligne.put("statut", n.getStatutValidation());
            lignesNotes.add(ligne);

            if (n.getStatutValidation() == StatutValidation.VALIDE) {
                creditsAcquis += ue.getCredits();
            }
            sommePonderee = sommePonderee.add(n.getMoyenneUe().multiply(BigDecimal.valueOf(ue.getCoefficient())));
            totalCoefficients += ue.getCoefficient();
        }

        BigDecimal moyenneGenerale = totalCoefficients > 0
                ? sommePonderee.divide(BigDecimal.valueOf(totalCoefficients), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Map<String, Object> releve = new HashMap<>();
        releve.put("matricule", inscription.getEtudiant().getMatricule());
        releve.put("nom", inscription.getEtudiant().getNom());
        releve.put("prenom", inscription.getEtudiant().getPrenom());
        releve.put("filiere", inscription.getFiliere().getLibelle());
        releve.put("niveau", inscription.getNiveau().getCode());
        releve.put("anneeAcademique", inscription.getAnneeAcademique());
        releve.put("semestre", semestre);
        releve.put("notes", lignesNotes);
        releve.put("moyenneSemestre", moyenneGenerale);
        releve.put("creditsAcquis", creditsAcquis);
        return releve;
    }

    private void validerBorneNote(BigDecimal note, String type) {
        if (note == null || note.compareTo(BigDecimal.ZERO) < 0 || note.compareTo(new BigDecimal("20.00")) > 0) {
            throw new BusinessException("La note de " + type + " doit être comprise entre 0 et 20.");
        }
    }
}