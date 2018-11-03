package be.kdg.processor.fine.controllers.rest;

import be.kdg.processor.fine.dom.EmissionFine;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.dom.SpeedingFine;
import be.kdg.processor.fine.dto.fine.EmissionFineDTO;
import be.kdg.processor.fine.dto.fine.FineDTO;
import be.kdg.processor.fine.services.FineService;
import be.kdg.processor.licenseplate.dom.Licenseplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * @author C&eacute;dric Goffin
 * 17/10/2018 11:55
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class FineRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FineService fineService;

    @Test
    public void getFinesBetween() throws Exception {
        Fine fine = fineService.save(new SpeedingFine(
                100,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(31),
                new Licenseplate(
                        "1-ABC-123",
                        "123456789",
                        1,
                        new ArrayList<>()
                ),
                125,
                120));
        LocalDateTime from = fine.getTimestamp().minusSeconds(1);
        LocalDateTime to = fine.getTimestamp().plusSeconds(1);

        String url = "/api/fine/get?from=" + from + "&to=" + to;
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    List<FineDTO> fineDTOS = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<EmissionFineDTO>>() {
                    });
                    Assert.assertTrue(fineDTOS.size() >= 1);
                    Assert.assertTrue(fineDTOS.contains(modelMapper.map(fine, EmissionFineDTO.class)));
                });
    }

    @Test
    public void getIncorrectFinesBetween() throws Exception {
        LocalDateTime fineTimestamp = LocalDateTime.now();
        LocalDateTime from = fineTimestamp.plusSeconds(1);
        LocalDateTime to = fineTimestamp.minusSeconds(1);

        String url = "/api/fine/get?from=" + from + "&to=" + to;
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void acceptFine() throws Exception {
        Fine fine = fineService.save(new EmissionFine(
                100,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(31),
                new Licenseplate(
                        "1-ABC-123",
                        "123456789",
                        1,
                        new ArrayList<>()
                ),
                1,
                3));

        mockMvc.perform(put("/api/fine/acceptFine/" + fine.getFineId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    public void changeFineAmount() throws Exception {
        Fine fine = fineService.save(new EmissionFine(
                100,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(31),
                new Licenseplate(
                        "1-ABC-123",
                        "123456789",
                        1,
                        new ArrayList<>()
                ),
                1,
                3));

        Map<String, Object> finePatch = new HashMap<>();
        finePatch.put("amount", 200);
        finePatch.put("motivation", "just because");

        String patchJSON = objectMapper.writeValueAsString(finePatch);
        mockMvc.perform(patch("/api/fine/changeFineAmount/" + fine.getFineId())
                .contentType(APPLICATION_JSON_UTF8)
                .content(patchJSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
}