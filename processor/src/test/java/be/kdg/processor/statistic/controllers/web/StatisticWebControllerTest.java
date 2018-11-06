package be.kdg.processor.statistic.controllers.web;

import be.kdg.processor.statistic.dom.DateTimeStatistic;
import be.kdg.processor.statistic.dom.ProcessorStatistics;
import be.kdg.processor.statistic.dto.ProcessorStatisticsDTO;
import be.kdg.processor.statistic.services.StatisticService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Cédric Goffin
 * 06/11/2018 20:22
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StatisticWebControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private StatisticService statisticService;

    @Before
    public void init() {
        TypeMap<ProcessorStatistics, ProcessorStatisticsDTO> typeMap = modelMapper.getTypeMap(ProcessorStatistics.class, ProcessorStatisticsDTO.class);
        if (typeMap == null) {
            modelMapper.createTypeMap(ProcessorStatistics.class, ProcessorStatisticsDTO.class).setConverter(context ->
                    new ProcessorStatisticsDTO(
                            context.getSource().getSuccessfulMessages(),
                            context.getSource().getFailedMessages(),
                            context.getSource().getTotalMessages(),
                            context.getSource().getStartupTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                    )
            );
        }
    }

    @Test
    @WithMockUser(authorities = {"WEBADMIN"})
    public void getProcessorStatistics() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        int successfulMessages = 20;
        int failedMessages = 1;
        int totalMessages = successfulMessages + failedMessages;
        statisticService.incrementIntStatistic("successfulMessages", successfulMessages);
        statisticService.incrementIntStatistic("failedMessages", failedMessages);
        statisticService.incrementIntStatistic("totalMessages", totalMessages);
        statisticService.saveDateTimeStatistic("startupTimestamp", now);

        ProcessorStatisticsDTO processorStatisticsDTO = modelMapper.map(statisticService.getProcessorStatistics(), ProcessorStatisticsDTO.class);

        mockMvc.perform(get("/statistics"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Assert.assertEquals(result.getModelAndView().getModelMap().get("processorStatisticsDTO"), processorStatisticsDTO);
                });
    }
}