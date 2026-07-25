package ru.vavilov.notebook6.notebook.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.vavilov.notebook6.notebook.entity.Notebook;
import ru.vavilov.notebook6.notebook.entity.Tag;
import ru.vavilov.notebook6.notebook.entity.User;
import ru.vavilov.notebook6.notebook.entity.Visibility;
import ru.vavilov.notebook6.notebook.repository.NotebookRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotebookServiceTest {

    @Mock
    private NotebookRepository notebookRepository;
    @Mock
    private AuthService authService;
    @Mock
    private TagService tagService;

    private NotebookService notebookService;

    private User owner;
    private User otherUser;

    @BeforeEach
    void setUp() {
        notebookService = new NotebookService(notebookRepository, authService, tagService);
        owner = new User();
        owner.setId(1);
        otherUser = new User();
        otherUser.setId(2);
    }

    @Test
    void saveNotebook_assignsCurrentUserAsOwner_onCreate() {
        when(authService.getUser()).thenReturn(owner);
        Notebook newNote = new Notebook();
        when(notebookRepository.findById(0)).thenReturn(Optional.empty());

        notebookService.saveNotebook(newNote, null);

        assertThat(newNote.getUser()).isEqualTo(owner);
        verify(notebookRepository).save(newNote);
    }

    @Test
    void saveNotebook_throwsAccessDenied_whenEditingSomeoneElsesNote() {
        Notebook existing = new Notebook();
        existing.setId(5);
        existing.setUser(owner);
        when(authService.getUser()).thenReturn(otherUser);
        when(notebookRepository.findById(5)).thenReturn(Optional.of(existing));

        Notebook update = new Notebook();
        update.setId(5);

        assertThrows(AccessDeniedException.class, () -> notebookService.saveNotebook(update, null));
        verify(notebookRepository, never()).save(any());
    }

    @Test
    void saveNotebook_succeeds_whenEditingOwnNote() {
        Notebook existing = new Notebook();
        existing.setId(5);
        existing.setUser(owner);
        when(authService.getUser()).thenReturn(owner);
        when(notebookRepository.findById(5)).thenReturn(Optional.of(existing));

        Notebook update = new Notebook();
        update.setId(5);
        update.setTitle("new title");

        notebookService.saveNotebook(update, null);

        assertThat(update.getUser()).isEqualTo(owner);
        verify(notebookRepository).save(update);
    }

    @Test
    void saveNotebook_throwsAccessDenied_whenNonAdminPublishesToTeam() {
        when(authService.getUser()).thenReturn(owner);
        when(authService.isAdmin()).thenReturn(false);
        when(notebookRepository.findById(0)).thenReturn(Optional.empty());

        Notebook newNote = new Notebook();
        newNote.setVisibility(Visibility.TEAM);

        assertThrows(AccessDeniedException.class, () -> notebookService.saveNotebook(newNote, null));
        verify(notebookRepository, never()).save(any());
    }

    @Test
    void saveNotebook_allowsAdminToPublishToTeam() {
        when(authService.getUser()).thenReturn(owner);
        when(authService.isAdmin()).thenReturn(true);
        when(notebookRepository.findById(0)).thenReturn(Optional.empty());

        Notebook newNote = new Notebook();
        newNote.setVisibility(Visibility.TEAM);

        notebookService.saveNotebook(newNote, null);

        assertThat(newNote.getVisibility()).isEqualTo(Visibility.TEAM);
        verify(notebookRepository).save(newNote);
    }

    @Test
    void deleteNotebook_throwsAccessDenied_whenNotOwner() {
        Notebook existing = new Notebook();
        existing.setId(7);
        existing.setUser(owner);
        when(authService.getUser()).thenReturn(otherUser);
        when(notebookRepository.findById(7)).thenReturn(Optional.of(existing));

        assertThrows(AccessDeniedException.class, () -> notebookService.deleteNotebook(7));
        verify(notebookRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteNotebook_succeeds_whenOwner() {
        Notebook existing = new Notebook();
        existing.setId(7);
        existing.setUser(owner);
        when(authService.getUser()).thenReturn(owner);
        when(notebookRepository.findById(7)).thenReturn(Optional.of(existing));

        notebookService.deleteNotebook(7);

        verify(notebookRepository).deleteById(7);
    }

    @Test
    void getMyNotes_filtersByTag() {
        Notebook salesNote = new Notebook();
        salesNote.setId(1);
        salesNote.setTags(Set.of(new Tag("sales")));
        Notebook supportNote = new Notebook();
        supportNote.setId(2);
        supportNote.setTags(Set.of(new Tag("support")));
        owner.setNotes(List.of(salesNote, supportNote));
        when(authService.getUser()).thenReturn(owner);

        List<Notebook> filtered = notebookService.getMyNotes("sales");

        assertThat(filtered).containsExactly(salesNote);
    }

    @Test
    void getMyNotes_withoutTagFilter_returnsAll() {
        Notebook salesNote = new Notebook();
        salesNote.setId(1);
        Notebook supportNote = new Notebook();
        supportNote.setId(2);
        owner.setNotes(List.of(salesNote, supportNote));
        when(authService.getUser()).thenReturn(owner);

        List<Notebook> all = notebookService.getMyNotes(null);

        assertThat(all).containsExactly(salesNote, supportNote);
    }

    @Test
    void getTeamLibrary_filtersByTag() {
        Notebook salesNote = new Notebook();
        salesNote.setId(1);
        salesNote.setTags(Set.of(new Tag("sales")));
        Notebook supportNote = new Notebook();
        supportNote.setId(2);
        supportNote.setTags(Set.of(new Tag("support")));
        when(notebookRepository.findAllByVisibilityOrderByPositionDesc(Visibility.TEAM))
                .thenReturn(List.of(salesNote, supportNote));

        List<Notebook> filtered = notebookService.getTeamLibrary("support");

        assertThat(filtered).containsExactly(supportNote);
    }
}
