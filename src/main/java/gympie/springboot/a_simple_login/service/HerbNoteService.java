package gympie.springboot.a_simple_login.service;

import gympie.springboot.a_simple_login.dto.HerbNoteForm;
import gympie.springboot.a_simple_login.dto.HerbNoteResponse;
import gympie.springboot.a_simple_login.entity.AppUser;
import gympie.springboot.a_simple_login.entity.Herb;
import gympie.springboot.a_simple_login.entity.HerbNote;
import gympie.springboot.a_simple_login.exception.ForbiddenOperationException;
import gympie.springboot.a_simple_login.exception.ResourceNotFoundException;
import gympie.springboot.a_simple_login.repository.AppUserRepository;
import gympie.springboot.a_simple_login.repository.HerbNoteRepository;
import gympie.springboot.a_simple_login.repository.HerbRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class HerbNoteService {

    private final HerbNoteRepository noteRepository;
    private final HerbRepository herbRepository;
    private final AppUserRepository userRepository;

    public HerbNoteService(HerbNoteRepository noteRepository, HerbRepository herbRepository,
                           AppUserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.herbRepository = herbRepository;
        this.userRepository = userRepository;
    }

    public List<HerbNoteResponse> findForUser(String username) {
        return noteRepository.findByOwnerUsernameOrderByCreatedAtDesc(username).stream().map(this::toResponse).toList();
    }

    public List<HerbNoteResponse> findAll() {
        return noteRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
    }

    public HerbNoteResponse findById(Long id, String actingUsername, boolean administrator) {
        HerbNote note = getNote(id);
        requireOwnerOrAdministrator(note, actingUsername, administrator);
        return toResponse(note);
    }

    @Transactional
    public HerbNoteResponse create(HerbNoteForm form, String actingUsername) {
        HerbNote note = new HerbNote(clean(form.getTitle()), clean(form.getBody()), getHerb(form.getHerbId()),
                getUser(actingUsername));
        return toResponse(noteRepository.save(note));
    }

    @Transactional
    public HerbNoteResponse update(Long id, HerbNoteForm form, String actingUsername, boolean administrator) {
        HerbNote note = getNote(id);
        requireOwnerOrAdministrator(note, actingUsername, administrator);
        note.update(clean(form.getTitle()), clean(form.getBody()), getHerb(form.getHerbId()));
        return toResponse(noteRepository.save(note));
    }

    @Transactional
    public void delete(Long id, String actingUsername, boolean administrator) {
        HerbNote note = getNote(id);
        requireOwnerOrAdministrator(note, actingUsername, administrator);
        noteRepository.delete(note);
    }

    private HerbNote getNote(Long id) {
        return noteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Herb note", id));
    }

    private Herb getHerb(Long id) {
        return herbRepository.findWithCategoryById(id).orElseThrow(() -> new ResourceNotFoundException("Herb", id));
    }

    private AppUser getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User account for " + username));
    }

    private void requireOwnerOrAdministrator(HerbNote note, String actingUsername, boolean administrator) {
        if (!administrator && !note.getOwner().getUsername().equals(actingUsername)) {
            throw new ForbiddenOperationException("You can only change your own notes.");
        }
    }

    private HerbNoteResponse toResponse(HerbNote note) {
        return new HerbNoteResponse(note.getId(), note.getHerb().getId(), note.getHerb().getName(), note.getTitle(),
                note.getBody(), note.getOwner().getUsername(), note.getCreatedAt());
    }

    private String clean(String value) {
        return value.trim();
    }
}
