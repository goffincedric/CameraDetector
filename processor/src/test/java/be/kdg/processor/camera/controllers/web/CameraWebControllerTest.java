package be.kdg.processor.camera.controllers.web;

import be.kdg.processor.camera.dom.Camera;
import be.kdg.processor.camera.services.CameraServiceAdapter;
import be.kdg.processor.fine.dom.EmissionFine;
import be.kdg.processor.fine.dom.Fine;
import be.kdg.processor.fine.services.FineService;
import be.kdg.processor.licenseplate.dom.Licenseplate;
import be.kdg.processor.licenseplate.services.LicenseplateServiceAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Cédric Goffin
 * 05/11/2018 23:01
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CameraWebControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FineService fineService;
    @Autowired
    private CameraServiceAdapter cameraServiceAdapter;
    @Autowired
    private LicenseplateServiceAdapter licenseplateServiceAdapter;

    @Test
    @WithMockUser(authorities = {"WEBADMIN"})
    public void heatmap() throws Exception {
        Camera camera = cameraServiceAdapter.getCamera(3).get();
        Licenseplate licenseplate = licenseplateServiceAdapter.saveLicenseplate(new Licenseplate(
                "1-ABC-123",
                "123456789",
                1,
                new ArrayList<>()));
        LocalDateTime now = LocalDateTime.now();
        fineService.save(new EmissionFine(
                100,
                now,
                now.plusDays(31),
                licenseplate,
                1,
                4,
                camera
        ));

        mockMvc.perform(get("/camera/heatmap"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String geoJson = (String) result.getModelAndView().getModelMap().get("geoJsonData");
                    Map jsonMap = objectMapper.readValue(geoJson, Map.class);
                    List list = ((ArrayList) jsonMap.get("features"));
                    Assert.assertTrue(list.stream().anyMatch(o -> {
                        int count = (int) ((LinkedHashMap) ((LinkedHashMap) o).get("properties")).get("count");
                        List<Double> coords = (List<Double>) ((LinkedHashMap) ((LinkedHashMap) o).get("geometry")).get("coordinates");

                        return (
                                count >= 1 &&
                                        coords.get(0).equals(camera.getLocation().getLongitude()) &&
                                        coords.get(1).equals(camera.getLocation().getLatitude())
                        );
                    }));
                });
    }
}