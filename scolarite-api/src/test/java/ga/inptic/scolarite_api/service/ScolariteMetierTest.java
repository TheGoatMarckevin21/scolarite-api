package ga.inptic.scolarite_api.service;

import ga.inptic.scolarite_api.entity.*;
import ga.inptic.scolarite_api.exception.BusinessException;
import ga.inptic.scolarite_api.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScolariteMetierTest {

    @Mock private EtudiantRepository etudiantRepository;
    @Mock private InscriptionRepository inscriptionRepository;
    @Mock private FiliereRepository filiereRepository;
    @Mock private NiveauRepository niveauRepository;
    @Mock private PaiementRepository paiementRepository;
    @Mock private NoteRepository noteRepository;
    @Mock private UniteEnseignementRepository ueRepository;
    @Mock private JournalActionService journalActionService;

    @InjectMocks private InscriptionService inscriptionService;
    @InjectMocks private PaiementService paiementService;
    @InjectMocks private NoteService noteService;

    // TEST 1 : Règle R1 — Génération automatique du matricule ANNEE-FILIERE-NNNN
    @Test
    void testInscriptionGenereMatriculeEtStatutEnAttente() {
        Filiere gi = new Filiere("GI", "Génie Informatique");
        gi.setId(1L);
        Niveau l2 = new Niveau("L2", "Licence 2", new BigDecimal("500000.00"));
        l2.setId(1L);

        when(filiereRepository.findById(1L)).thenReturn(Optional.of(gi));
        when(niveauRepository.findById(1L)).thenReturn(Optional.of(l2));
        when(etudiantRepository.countByMatriculeStartingWith("2025-GI-")).thenReturn(0L);
        when(etudiantRepository.save(any(Etudiant.class))).thenAnswer(inv -> inv.getArgument(0));
        when(inscriptionRepository.save(any(Inscription.class))).thenAnswer(inv -> inv.getArgument(0));

        Inscription ins = inscriptionService.inscrireNouvelEtudiant(
                "MBA", "Kevin", LocalDate.of(2005, 5, 10), "077000000",
                1L, 1L, "2025-2026", "scolarite@inptic.ga");

        assertEquals("2025-GI-0001", ins.getEtudiant().getMatricule());
        assertEquals(StatutInscription.EN_ATTENTE, ins.getStatut());
        assertEquals(new BigDecimal("500000.00"), ins.getMontantTotal());
    }

    // TEST 2 : Règle R2 — 1re tranche passe l'inscription à VALIDEE
    @Test
    void testPaiementPremiereTrancheValideInscription() {
        Inscription ins = new Inscription();
        ins.setId(10L);
        ins.setMontantTotal(new BigDecimal("500000.00"));
        ins.setStatut(StatutInscription.EN_ATTENTE);

        when(inscriptionRepository.findById(10L)).thenReturn(Optional.of(ins));
        when(paiementRepository.sumMontantByInscriptionId(10L)).thenReturn(BigDecimal.ZERO);
        when(paiementRepository.save(any(Paiement.class))).thenAnswer(inv -> inv.getArgument(0));

        Paiement p = paiementService.enregistrerPaiement(
                10L, new BigDecimal("200000.00"), ModePaiement.MOBILE_MONEY, "comptable@inptic.ga");

        assertNotNull(p.getNumeroRecu());
        assertEquals(StatutInscription.VALIDEE, ins.getStatut());
    }

    // TEST 3 : Règle R2 — Paiement du solde complet passe l'inscription à SOLDEE
    @Test
    void testPaiementCompletSoldeInscription() {
        Inscription ins = new Inscription();
        ins.setId(10L);
        ins.setMontantTotal(new BigDecimal("500000.00"));
        ins.setStatut(StatutInscription.VALIDEE);

        when(inscriptionRepository.findById(10L)).thenReturn(Optional.of(ins));
        when(paiementRepository.sumMontantByInscriptionId(10L)).thenReturn(new BigDecimal("200000.00"));
        when(paiementRepository.save(any(Paiement.class))).thenAnswer(inv -> inv.getArgument(0));

        paiementService.enregistrerPaiement(
                10L, new BigDecimal("300000.00"), ModePaiement.ESPECES, "comptable@inptic.ga");

        assertEquals(StatutInscription.SOLDEE, ins.getStatut());
    }

    // TEST 4 : Sécurité métier — Refus d'un paiement supérieur au reste à payer
    @Test
    void testPaiementTropPercuLanceBusinessException() {
        Inscription ins = new Inscription();
        ins.setId(10L);
        ins.setMontantTotal(new BigDecimal("500000.00"));

        when(inscriptionRepository.findById(10L)).thenReturn(Optional.of(ins));
        when(paiementRepository.sumMontantByInscriptionId(10L)).thenReturn(new BigDecimal("400000.00"));

        assertThrows(BusinessException.class, () ->
                paiementService.enregistrerPaiement(
                        10L, new BigDecimal("150000.00"), ModePaiement.VIREMENT, "comptable@inptic.ga"));
    }

    // TEST 5 : Règle R3 — Délibération valide les UE >= 10 et cumule les crédits
    @Test
    void testDeliberationValideUeEtCumuleCredits() {
        Etudiant etu = new Etudiant();
        etu.setMatricule("2025-GI-0001");
        Inscription ins = new Inscription();
        ins.setId(1L);
        ins.setEtudiant(etu);

        UniteEnseignement ueJava = new UniteEnseignement();
        ueJava.setCredits(6);
        ueJava.setCoefficient(2);

        Note noteJava = new Note();
        noteJava.setUniteEnseignement(ueJava);
        noteJava.setMoyenneUe(new BigDecimal("14.00"));

        when(inscriptionRepository.findById(1L)).thenReturn(Optional.of(ins));
        when(noteRepository.findByInscriptionIdAndUniteEnseignementSemestre(1L, Semestre.S1))
                .thenReturn(List.of(noteJava));

        Map<String, Object> resultat = noteService.delibererSemestre(1L, Semestre.S1, "directeur@inptic.ga");

        assertEquals(StatutValidation.VALIDE, noteJava.getStatutValidation());
        assertEquals(6, resultat.get("creditsAcquis"));
        assertEquals("ADMIS", resultat.get("decision"));
    }
}