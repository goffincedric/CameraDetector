package be.kdg.processor.fine.controllers.web;

import be.kdg.processor.fine.dto.fineSettings.FineSettingsDTO;
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
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Cédric Goffin
 * 03/11/2018 15:13
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FineWebControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SettingService settingService;

    @Test
    @WithMockUser(authorities = {"WEBADMIN"})
    public void getFineFactors() throws Exception {
        FineSettingsDTO fineSettingsDTO = modelMapper.map(settingService.getFineSettings(), FineSettingsDTO.class);

        mockMvc.perform(get("/fine/settings"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> Assert.assertEquals(result.getModelAndView().getModelMap().get("fineSettingsDTO"), fineSettingsDTO));
    }

    @Test
    @WithMockUser(authorities = {"WEBADMIN"})
    public void postFineFactors() throws Exception {
        String url = "/fine/settings";
        FineSettingsDTO fineSettingsDTO = modelMapper.map(settingService.getFineSettings(), FineSettingsDTO.class);
        FineSettingsDTO updatedFineSettingsDTO = new FineSettingsDTO(
                fineSettingsDTO.getEmissionTimeframeDays() + 1,
                fineSettingsDTO.getEmissionFineFactor() + 100,
                fineSettingsDTO.getSpeedFineFactorSlow() + 1,
                fineSettingsDTO.getSpeedFineFactorFast() + 1,
                fineSettingsDTO.getPaymentDeadlingDays() - 1
        );

        mockMvc.perform(post(url)
                .param("emissionTimeframeDays", String.valueOf(updatedFineSettingsDTO.getEmissionTimeframeDays()))
                .param("emissionFineFactor", String.valueOf(updatedFineSettingsDTO.getEmissionFineFactor()))
                .param("speedFineFactorSlow", String.valueOf(updatedFineSettingsDTO.getSpeedFineFactorSlow()))
                .param("speedFineFactorFast", String.valueOf(updatedFineSettingsDTO.getSpeedFineFactorFast()))
                .param("paymentDeadlingDays", String.valueOf(updatedFineSettingsDTO.getPaymentDeadlingDays()))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(url + "?saved"))
                .andExpect(result -> Assert.assertEquals(result.getModelAndView().getModelMap().get("fineSettingsDTO"), updatedFineSettingsDTO));;
    }
}