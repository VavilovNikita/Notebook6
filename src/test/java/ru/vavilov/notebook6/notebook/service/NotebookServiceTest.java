package ru.vavilov.notebook6.notebook.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.vavilov.notebook6.notebook.entity.Notebook;
import ru.vavilov.notebook6.notebook.entity.User;
import ru.vavilov.notebook6.notebook.repository.NotebookRepository;

import java.util.Optional;

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

    private NotebookService notebookService;

    private User owner;
    private User otherUser;

    @BeforeEach
    void setUp() {
        notebookService = new NotebookService(notebookRepository, authService);
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

        notebookService.saveNotebook(newNote);

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

        assertThrows(AccessDeniedException.class, () -> notebookService.saveNotebook(update));
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

        notebookService.saveNotebook(update);

        assertThat(update.getUser()).isEqualTo(owner);
        verify(notebookRepository).save(update);
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
}
