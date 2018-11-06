package be.kdg.processor.processor.controllers.web;

import be.kdg.processor.processor.Processor;
import be.kdg.processor.processor.dto.ProcessorSettingsDTO;
import be.kdg.processor.processor.services.SettingService;
import be.kdg.processor.user.dom.Role;
import be.kdg.processor.user.dom.User;
import be.kdg.processor.user.exceptions.UserException;
import be.kdg.processor.user.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Cédric Goffin
 * 03/11/2018 12:31
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProcessorWebControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private Processor processor;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SettingService settingService;
    @Autowired
    private UserService userService;

    private String testUserUserName = "testUser";
    private String testUserPass = "testUser";

    private String testSuperAdminUserName = "testSuperAdmin";
    private String testSuperAdminPass = "testSuperAdmin";


    @Before
    public void setupUsers() throws UserException {
        userService.save(new User(testUserUserName, testUserPass, List.of()));
        userService.save(new User(testSuperAdminUserName, testSuperAdminPass, List.of(new Role("DBADMIN"), new Role("WEBADMIN"))));
    }

    @Test
    public void testSuccessLogin() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", testSuperAdminUserName)
                .param("password", testSuperAdminPass)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    public void testUnauthorizedLogin() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", testUserUserName)
                .param("password", testUserPass)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void testBadCredentialsLogin() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", testSuperAdminUserName)
                .param("password", "testSuperAdminBadCredentials")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    public void testLoginNoCsrf() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", testSuperAdminUserName)
                .param("password", testSuperAdminPass))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"DBADMIN"})
    public void testUnauthorizedPageAccessDBAdmin() throws Exception {
        mockMvc.perform(get("/fine/settings")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"WEBADMIN"})
    public void testUnauthorizedPageAccessWebAdmin() throws Exception {
        mockMvc.perform(get("/h2-console")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"WEBADMIN"})
    public void toggleProcessor() throws Exception {
        boolean originalState = processor.isRunning();

        RequestBuilder requestBuilder = post("/toggleProcessor")
                .with(csrf());

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin?processorstatus=" + !originalState));
        Assert.assertEquals(processor.isRunning(), !originalState);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin?processorstatus=" + originalState));
        Assert.assertEquals(processor.isRunning(), originalState);
    }

    @Test
    @WithMockUser(authorities = {"WEBADMIN"})
    public void getProcessorSettings() throws Exception {
        ProcessorSettingsDTO processorSettingsDTO = modelMapper.map(settingService.getProcessorSettings(), ProcessorSettingsDTO.class);

        mockMvc.perform(get("/processor/settings"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> Assert.assertEquals(result.getModelAndView().getModelMap().get("processorSettingsDTO"), processorSettingsDTO));
    }

    @Test
    @WithMockUser(authorities = {"WEBADMIN"})
    public void postProcessorSettings() throws Exception {
        String url = "/processor/settings";
        ProcessorSettingsDTO processorSettingsDTO = modelMapper.map(settingService.getProcessorSettings(), ProcessorSettingsDTO.class);
        ProcessorSettingsDTO updatedProcessorSettingsDTO = new ProcessorSettingsDTO(processorSettingsDTO.getRetries() + 1, !processorSettingsDTO.isLogFailed(), processorSettingsDTO.getLogPath());

        mockMvc.perform(post(url)
                .param("retries", String.valueOf(updatedProcessorSettingsDTO.getRetries()))
                .param("logFailed", String.valueOf(updatedProcessorSettingsDTO.isLogFailed()))
                .param("logPath", String.valueOf(updatedProcessorSettingsDTO.getLogPath()))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(url + "?saved"))
                .andExpect(result -> Assert.assertEquals(result.getModelAndView().getModelMap().get("processorSettingsDTO"), updatedProcessorSettingsDTO));
    }
}