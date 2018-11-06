package be.kdg.processor.statistic.controllers.web;

import be.kdg.processor.statistic.dom.ProcessorStatistics;
import be.kdg.processor.statistic.dto.ProcessorStatisticsDTO;
import be.kdg.processor.statistic.services.StatisticService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.format.DateTimeFormatter;

/**
 * @author Cédric Goffin
 * 06/11/2018 19:27
 */
@Controller
public class StatisticWebController {
    private final StatisticService statisticService;
    private final ModelMapper modelMapper;

    @Autowired
    public StatisticWebController(StatisticService statisticService, ModelMapper modelMapper) {
        this.statisticService = statisticService;
        this.modelMapper = modelMapper;

        modelMapper.createTypeMap(ProcessorStatistics.class, ProcessorStatisticsDTO.class).setConverter(context ->
                new ProcessorStatisticsDTO(
                        context.getSource().getSuccessfulMessages(),
                        context.getSource().getFailedMessages(),
                        context.getSource().getTotalMessages(),
                        context.getSource().getStartupTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                )
        );
    }

    /**
     * Listens to requests made on the /admin url
     *
     * @return a ModelAndView with the current status of the processor
     */
    @GetMapping(value = "/statistics")
    public ModelAndView getProcessorStatistics() {
        ProcessorStatisticsDTO processorStatisticsDTO = modelMapper.map(statisticService.getProcessorStatistics(), ProcessorStatisticsDTO.class);
        return new ModelAndView("statistics", "processorStatisticsDTO", processorStatisticsDTO);
    }
}
