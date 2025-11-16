package ru.noleg.prreviewerservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.noleg.api.controllers.StatisticsApi;
import ru.noleg.api.models.AssignmentStats;
import ru.noleg.prreviewerservice.mapper.StatisticMapper;
import ru.noleg.prreviewerservice.model.StatisticModel;
import ru.noleg.prreviewerservice.service.StatisticService;

@RestController
public class StatisticsController implements StatisticsApi {

  private final StatisticService statisticsService;
  private final StatisticMapper statisticMapper;

  public StatisticsController(StatisticService statisticsService, StatisticMapper statisticMapper) {
    this.statisticsService = statisticsService;
    this.statisticMapper = statisticMapper;
  }

  @Override
  public ResponseEntity<AssignmentStats> getStatistics() {
    StatisticModel statistics = statisticsService.getStatistics();
    return ResponseEntity.status(HttpStatus.OK).body(statisticMapper.toAssignmentStats(statistics));
  }
}
