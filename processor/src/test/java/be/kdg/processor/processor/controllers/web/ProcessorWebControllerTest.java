package be.kdg.processor.processor.controllers.web;

import be.kdg.processor.processor.Processor;
import be.kdg.processor.processor.dto.ProcessorSettingsDTO;
import be.kdg.processor.processor.services.SettingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
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

    @Test
    public void testSuccessLogin() throws Exception {
        RequestBuilder requestBuilder = post("/login")
                .param("username", "admin")
                .param("password", "admin")
                .with(csrf());
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    public void testBadLogin() throws Exception {
        RequestBuilder requestBuilder = post("/login")
                .param("username", "admin")
                .param("password", "notAnAdmin")
                .with(csrf());
        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    public void testLoginNoCsrf() throws Exception {
        RequestBuilder requestBuilder = post("/login")
                .param("username", "admin")
                .param("password", "admin");
        mockMvc.perform(requestBuilder)
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
                .andExpect(result -> {
                    model().attribute("retries", processorSettingsDTO.getRetries());
                    model().attribute("logPath", processorSettingsDTO.getLogPath());
                    model().attribute("logFailed", processorSettingsDTO.isLogFailed());
                });
    }

    @Test
    @WithMockUser(authorities = {"WEBADMIN"})
    public void postProcessorSettings() throws Exception {
        String url = "/processor/settings";
        ProcessorSettingsDTO processorSettingsDTO = modelMapper.map(settingService.getProcessorSettings(), ProcessorSettingsDTO.class);
        ProcessorSettingsDTO updatedProcessorSettingsDTO = new ProcessorSettingsDTO(processorSettingsDTO.getRetries() + 1, !processorSettingsDTO.isLogFailed(), processorSettingsDTO.getLogPath());

        mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(updatedProcessorSettingsDTO))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(url + "?saved"))
                .andExpect(result -> {
                    model().attribute("retries", processorSettingsDTO.getRetries() + 1);
                    model().attribute("logPath", processorSettingsDTO.getLogPath());
                    model().attribute("logFailed", !processorSettingsDTO.isLogFailed());
                });
    }
}