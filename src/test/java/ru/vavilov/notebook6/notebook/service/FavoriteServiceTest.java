package ru.vavilov.notebook6.notebook.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.vavilov.notebook6.notebook.entity.FavoriteNote;
import ru.vavilov.notebook6.notebook.entity.Notebook;
import ru.vavilov.notebook6.notebook.entity.User;
import ru.vavilov.notebook6.notebook.entity.Visibility;
import ru.vavilov.notebook6.notebook.repository.FavoriteNoteRepository;
import ru.vavilov.notebook6.notebook.repository.NotebookRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteNoteRepository favoriteNoteRepository;
    @Mock
    private NotebookRepository notebookRepository;
    @Mock
    private AuthService authService;

    private FavoriteService favoriteService;

    private User currentUser;
    private User otherUser;
    private Notebook ownNote;
    private Notebook othersPersonalNote;
    private Notebook othersTeamNote;

    @BeforeEach
    void setUp() {
        favoriteService = new FavoriteService(favoriteNoteRepository, notebookRepository, authService);
        currentUser = new User();
        currentUser.setId(1);
        otherUser = new User();
        otherUser.setId(2);

        ownNote = new Notebook();
        ownNote.setId(10);
        ownNote.setUser(currentUser);
        ownNote.setVisibility(Visibility.PERSONAL);

        othersPersonalNote = new Notebook();
        othersPersonalNote.setId(11);
        othersPersonalNote.setUser(otherUser);
        othersPersonalNote.setVisibility(Visibility.PERSONAL);

        othersTeamNote = new Notebook();
        othersTeamNote.setId(12);
        othersTeamNote.setUser(otherUser);
        othersTeamNote.setVisibility(Visibility.TEAM);
    }

    @Test
    void toggleFavorite_addsFavorite_whenNotAlreadyFavorited() {
        when(authService.getUser()).thenReturn(currentUser);
        when(notebookRepository.findById(10)).thenReturn(Optional.of(ownNote));
        when(favoriteNoteRepository.findByUserIdAndNotebookId(1, 10)).thenReturn(Optional.empty());

        boolean result = favoriteService.toggleFavorite(10);

        assertThat(result).isTrue();
        verify(favoriteNoteRepository).save(any(FavoriteNote.class));
    }

    @Test
    void toggleFavorite_removesFavorite_whenAlreadyFavorited() {
        when(authService.getUser()).thenReturn(currentUser);
        when(notebookRepository.findById(10)).thenReturn(Optional.of(ownNote));
        FavoriteNote existing = new FavoriteNote(currentUser, ownNote);
        when(favoriteNoteRepository.findByUserIdAndNotebookId(1, 10)).thenReturn(Optional.of(existing));

        boolean result = favoriteService.toggleFavorite(10);

        assertThat(result).isFalse();
        verify(favoriteNoteRepository).delete(existing);
    }

    @Test
    void toggleFavorite_allowsFavoritingSomeoneElsesTeamNote() {
        when(authService.getUser()).thenReturn(currentUser);
        when(notebookRepository.findById(12)).thenReturn(Optional.of(othersTeamNote));
        when(favoriteNoteRepository.findByUserIdAndNotebookId(1, 12)).thenReturn(Optional.empty());

        boolean result = favoriteService.toggleFavorite(12);

        assertThat(result).isTrue();
    }

    @Test
    void toggleFavorite_throwsAccessDenied_forSomeoneElsesPersonalNote() {
        when(authService.getUser()).thenReturn(currentUser);
        when(notebookRepository.findById(11)).thenReturn(Optional.of(othersPersonalNote));

        assertThrows(AccessDeniedException.class, () -> favoriteService.toggleFavorite(11));
        verify(favoriteNoteRepository, never()).save(any());
    }

    @Test
    void getFavoriteNotes_returnsOnlyCurrentUsersFavorites() {
        when(authService.getUser()).thenReturn(currentUser);
        FavoriteNote favorite = new FavoriteNote(currentUser, ownNote);
        when(favoriteNoteRepository.findAllByUserId(1)).thenReturn(List.of(favorite));

        List<Notebook> result = favoriteService.getFavoriteNotes();

        assertThat(result).containsExactly(ownNote);
    }
}
