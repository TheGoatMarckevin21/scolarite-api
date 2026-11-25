package ga.inptic.scolarite_api.service;

import ga.inptic.scolarite_api.entity.JournalAction;
import ga.inptic.scolarite_api.repository.JournalActionRepository;
import org.springframework.stereotype.Service;

@Service
public class JournalActionService {

    private final JournalActionRepository journalActionRepository;

    public JournalActionService(JournalActionRepository journalActionRepository) {
        this.journalActionRepository = journalActionRepository;
    }

    public void enregistrer(String acteurEmail, String action, String details) {
        JournalAction log = new JournalAction(acteurEmail, action, details);
        journalActionRepository.save(log);
    }
}