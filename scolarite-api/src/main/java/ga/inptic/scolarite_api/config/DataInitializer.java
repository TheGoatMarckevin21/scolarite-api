package ga.inptic.scolarite_api.config;

import ga.inptic.scolarite_api.entity.Role;
import ga.inptic.scolarite_api.entity.Utilisateur;
import ga.inptic.scolarite_api.repository.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        creerSiAbsent("scolarite@inptic.ga", "pass1234", Role.SCOLARITE);
        creerSiAbsent("comptable@inptic.ga", "pass1234", Role.COMPTABLE);
        creerSiAbsent("directeur@inptic.ga", "pass1234", Role.DIRECTEUR_ETUDES);
        creerSiAbsent("etudiant@inptic.ga", "pass1234", Role.ETUDIANT);
    }

    private void creerSiAbsent(String email, String motDePasseClair, Role role) {
        if (!utilisateurRepository.existsByEmail(email)) {
            Utilisateur u = new Utilisateur();
            u.setEmail(email);
            u.setMotDePasse(passwordEncoder.encode(motDePasseClair));
            u.setRole(role);
            utilisateurRepository.save(u);
        }
    }
}