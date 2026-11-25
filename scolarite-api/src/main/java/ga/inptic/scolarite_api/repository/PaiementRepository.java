
package ga.inptic.scolarite_api.repository;

import ga.inptic.scolarite_api.entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {
    List<Paiement> findByInscriptionId(Long inscriptionId);

    // Requête JPQL pour additionner directement les paiements d'une inscription (R2 & F7)
    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p WHERE p.inscription.id = :inscriptionId")
    BigDecimal sumMontantByInscriptionId(@Param("inscriptionId") Long inscriptionId);
}