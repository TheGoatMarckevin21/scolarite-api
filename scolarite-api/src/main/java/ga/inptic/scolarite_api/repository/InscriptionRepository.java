
package ga.inptic.scolarite_api.repository;

import ga.inptic.scolarite_api.entity.Inscription;
import ga.inptic.scolarite_api.entity.StatutInscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {
    List<Inscription> findByEtudiantId(Long etudiantId);
    List<Inscription> findByFiliereIdAndStatutNot(Long filiereId, StatutInscription statut);
    Page<Inscription> findByAnneeAcademique(String anneeAcademique, Pageable pageable);
    long countByStatut(StatutInscription statut);
}