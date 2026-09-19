package com.retailbilling.controller;

import com.retailbilling.dto.RestockForecastDto;
import com.retailbilling.service.RestockForecastService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forecasts")
@CrossOrigin(origins = "http://localhost:5173")
public class RestockForecastController {

    private final RestockForecastService forecastService;

    public RestockForecastController(RestockForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @GetMapping
    public List<RestockForecastDto> getForecasts() {
        return forecastService.generateDemandForecast();
    }
}