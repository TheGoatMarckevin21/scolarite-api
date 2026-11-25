
package ga.inptic.scolarite_api.repository;

import ga.inptic.scolarite_api.entity.Etudiant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    Optional<Etudiant> findByMatricule(String matricule);
    Optional<Etudiant> findByUtilisateurId(Long utilisateurId);
    // Recherche + Pagination (Exigence S3)
    Page<Etudiant> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom, Pageable pageable);
    // Utile pour générer le numéro séquentiel du matricule (R1)
    long countByMatriculeStartingWith(String prefix);
}