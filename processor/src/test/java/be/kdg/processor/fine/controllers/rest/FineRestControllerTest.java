package be.kdg.processor.fine.controllers.rest;

import be.kdg.processor.camera.services.CameraServiceAdapter;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static junit.framework.TestCase.fail;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FineRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FineService fineService;
    @Autowired
    private CameraServiceAdapter cameraServiceAdapter;

    private Licenseplate licenseplate;
    private LocalDateTime now;

    @Before
    public void prepareFines() {
        licenseplate = new Licenseplate(
                "1-ABC-123",
                "123456789",
                1,
                new ArrayList<>()
        );
        now = LocalDateTime.now();


    }

    @Test
    public void getFinesBetween() throws Exception {
        Fine presentFine = fineService.save(new SpeedingFine(
                100,
                now,
                now.plusDays(31),
                licenseplate,
                125,
                120,
                new LinkedList<>() {{
                    addFirst(cameraServiceAdapter.getCamera(1).get());
                    addLast(cameraServiceAdapter.getCamera(2).get());
                }}
        ));

        Fine pastFine = fineService.save(new SpeedingFine(
                100,
                presentFine.getTimestamp().minusDays(1),
                presentFine.getTimestamp().minusDays(1).plusDays(31),
                licenseplate,
                125,
                120,
                new LinkedList<>() {{
                    addFirst(cameraServiceAdapter.getCamera(1).get());
                    addLast(cameraServiceAdapter.getCamera(2).get());
                }}
        ));

        LocalDateTime from = presentFine.getTimestamp().minusSeconds(1);
        LocalDateTime to = presentFine.getTimestamp().plusSeconds(1);

        String url = "/api/fine/get?from=" + from + "&to=" + to;
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    List<FineDTO> fineDTOS = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<EmissionFineDTO>>() {
                    });
                    Assert.assertTrue(fineDTOS.size() >= 1);
                    Assert.assertTrue(fineDTOS.contains(modelMapper.map(presentFine, EmissionFineDTO.class)));
                    Assert.assertTrue(!fineDTOS.contains(modelMapper.map(pastFine, EmissionFineDTO.class)));
                });
    }

    @Test
    public void getIncorrectFinesBetween() throws Exception {
        LocalDateTime from = now.plusSeconds(1);
        LocalDateTime to = now.minusSeconds(1);

        String url = "/api/fine/get?from=" + from + "&to=" + to;
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void acceptFine() throws Exception {
        Fine emissionFine = fineService.save(new EmissionFine(
                100,
                now,
                now.plusDays(31),
                licenseplate,
                1,
                3,
                cameraServiceAdapter.getCamera(1).get()));

        mockMvc.perform(put("/api/fine/acceptFine/" + emissionFine.getFineId()).with(csrf()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void changeFineAmount() throws Exception {
        Fine emissionFine = fineService.save(new EmissionFine(
                100,
                now,
                now.plusDays(31),
                licenseplate,
                1,
                3,
                cameraServiceAdapter.getCamera(1).get()));

        Map<String, Object> finePatch = new HashMap<>();
        finePatch.put("amount", 200);
        finePatch.put("motivation", "just because");

        String patchJSON = objectMapper.writeValueAsString(finePatch);
        mockMvc.perform(patch("/api/fine/changeFineAmount/" + emissionFine.getFineId())
                .contentType(APPLICATION_JSON_UTF8)
                .content(patchJSON)
                .with(csrf()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}