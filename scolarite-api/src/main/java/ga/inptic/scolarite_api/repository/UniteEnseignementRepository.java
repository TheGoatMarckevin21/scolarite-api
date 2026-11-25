
package ga.inptic.scolarite_api.repository;

import ga.inptic.scolarite_api.entity.Semestre;
import ga.inptic.scolarite_api.entity.UniteEnseignement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UniteEnseignementRepository extends JpaRepository<UniteEnseignement, Long> {
    List<UniteEnseignement> findByFiliereIdAndNiveauId(Long filiereId, Long niveauId);
    List<UniteEnseignement> findByFiliereIdAndNiveauIdAndSemestre(Long filiereId, Long niveauId, Semestre semestre);
    Page<UniteEnseignement> findByLibelleContainingIgnoreCase(String libelle, Pageable pageable);
}