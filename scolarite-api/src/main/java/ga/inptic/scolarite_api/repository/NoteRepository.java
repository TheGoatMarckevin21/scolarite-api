
package ga.inptic.scolarite_api.repository;

import ga.inptic.scolarite_api.entity.Note;
import ga.inptic.scolarite_api.entity.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByInscriptionId(Long inscriptionId);
    List<Note> findByInscriptionIdAndUniteEnseignementSemestre(Long inscriptionId, Semestre semestre);
    Optional<Note> findByInscriptionIdAndUniteEnseignementId(Long inscriptionId, Long ueId);
}