package ru.vavilov.notebook6.notebook.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.vavilov.notebook6.config.WebSecurityConfig;
import ru.vavilov.notebook6.notebook.entity.Notebook;
import ru.vavilov.notebook6.notebook.entity.User;
import ru.vavilov.notebook6.notebook.service.AuthService;
import ru.vavilov.notebook6.notebook.service.FavoriteService;
import ru.vavilov.notebook6.notebook.service.NotebookService;
import ru.vavilov.notebook6.notebook.service.SearchService;
import ru.vavilov.notebook6.notebook.service.TagService;
import ru.vavilov.notebook6.notebook.service.UserDetailService;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotebookController.class)
@Import(WebSecurityConfig.class)
@WithMockUser(roles = "USER")
class NotebookControllerAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotebookService notebookService;
    @MockBean
    private AuthService authService;
    @MockBean
    private SearchService searchService;
    @MockBean
    private TagService tagService;
    @MockBean
    private FavoriteService favoriteService;
    @MockBean
    private UserDetailService userDetailService;

    @Test
    void deletingSomeoneElsesNoteReturns403() throws Exception {
        when(authService.getUser()).thenReturn(new User());
        doThrow(new AccessDeniedException("not the owner")).when(notebookService).deleteNotebook(5);

        mockMvc.perform(delete("/notebook/5").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void editingSomeoneElsesNoteReturns403() throws Exception {
        User currentUser = new User();
        currentUser.setId(1);
        User otherOwner = new User();
        otherOwner.setId(2);
        Notebook othersNote = new Notebook();
        othersNote.setId(9);
        othersNote.setUser(otherOwner);

        when(authService.getUser()).thenReturn(currentUser);
        when(notebookService.findById(9)).thenReturn(othersNote);

        mockMvc.perform(get("/notebook/9/edit"))
                .andExpect(status().isForbidden());
    }
}
