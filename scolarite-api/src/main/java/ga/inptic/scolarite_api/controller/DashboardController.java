package ga.inptic.scolarite_api.controller;

import ga.inptic.scolarite_api.entity.StatutInscription;
import ga.inptic.scolarite_api.repository.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final EtudiantRepository etudiantRepository;
    private final FiliereRepository filiereRepository;
    private final InscriptionRepository inscriptionRepository;
    private final PaiementRepository paiementRepository;

    public DashboardController(EtudiantRepository etudiantRepository,
                               FiliereRepository filiereRepository,
                               InscriptionRepository inscriptionRepository,
                               PaiementRepository paiementRepository) {
        this.etudiantRepository = etudiantRepository;
        this.filiereRepository = filiereRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.paiementRepository = paiementRepository;
    }

    @GetMapping
    public Map<String, Object> getStatistiques() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEtudiants", etudiantRepository.count());
        stats.put("totalFilieres", filiereRepository.count());
        stats.put("totalInscriptions", inscriptionRepository.count());
        stats.put("inscriptionsEnAttente", inscriptionRepository.countByStatut(StatutInscription.EN_ATTENTE));
        stats.put("inscriptionsValidees", inscriptionRepository.countByStatut(StatutInscription.VALIDEE));
        stats.put("inscriptionsSoldees", inscriptionRepository.countByStatut(StatutInscription.SOLDEE));
        stats.put("totalRecettesFcfa", paiementRepository.sumTotalPaiements());
        return stats;
    }
}