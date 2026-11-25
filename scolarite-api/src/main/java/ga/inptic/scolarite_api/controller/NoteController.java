package ga.inptic.scolarite_api.controller;

import ga.inptic.scolarite_api.dto.NoteRequest;
import ga.inptic.scolarite_api.entity.Note;
import ga.inptic.scolarite_api.entity.Semestre;
import ga.inptic.scolarite_api.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping
    public ResponseEntity<Note> saisirNote(@Valid @RequestBody NoteRequest req,
                                           Authentication auth) {
        String acteur = (auth != null) ? auth.getName() : "scolarite@inptic.ga";
        Note saved = noteService.saisirNote(
                req.getInscriptionId(), req.getUeId(), req.getNoteCc(), req.getNoteExamen(), acteur);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PostMapping("/deliberer/{inscriptionId}/{semestre}")
    public Map<String, Object> deliberer(@PathVariable Long inscriptionId,
                                         @PathVariable Semestre semestre,
                                         Authentication auth) {
        String acteur = (auth != null) ? auth.getName() : "directeur@inptic.ga";
        return noteService.delibererSemestre(inscriptionId, semestre, acteur);
    }

    @GetMapping("/releve/{inscriptionId}/{semestre}")
    public Map<String, Object> releve(@PathVariable Long inscriptionId,
                                      @PathVariable Semestre semestre) {
        return noteService.genererReleveNotes(inscriptionId, semestre);
    }
}