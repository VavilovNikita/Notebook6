package ru.vavilov.notebook6.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.vavilov.notebook6.codesage.controller.ExportController;
import ru.vavilov.notebook6.codesage.service.GitExportService;
import ru.vavilov.notebook6.notebook.service.AuthService;
import ru.vavilov.notebook6.notebook.service.UserDetailService;
import ru.vavilov.notebook6.wordsTranslator.controller.TranslateController;
import ru.vavilov.notebook6.wordsTranslator.service.WordsService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CodeSage/SubEditor/wordsTranslator are admin-only tooling (see WebSecurityConfig).
 * Covers two representative controllers, one per allowlisted path pattern.
 */
@WebMvcTest(controllers = {ExportController.class, TranslateController.class})
@Import(WebSecurityConfig.class)
class AdminOnlyModulesAccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitExportService gitExportService;
    @MockBean
    private WordsService wordsService;
    @MockBean
    private AuthService authService;
    @MockBean
    private UserDetailService userDetailService;

    @Test
    void anonymousGetsForbiddenOnCodesaga() throws Exception {
        mockMvc.perform(get("/codesaga/export")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void regularUserGetsForbiddenOnCodesaga() throws Exception {
        mockMvc.perform(get("/codesaga/export")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanAccessCodesaga() throws Exception {
        mockMvc.perform(get("/codesaga/export")).andExpect(status().isOk());
    }

    @Test
    void anonymousGetsForbiddenOnTranslator() throws Exception {
        mockMvc.perform(get("/translator")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void regularUserGetsForbiddenOnTranslator() throws Exception {
        mockMvc.perform(get("/translator")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanAccessTranslator() throws Exception {
        mockMvc.perform(get("/translator")).andExpect(status().isOk());
    }
}
