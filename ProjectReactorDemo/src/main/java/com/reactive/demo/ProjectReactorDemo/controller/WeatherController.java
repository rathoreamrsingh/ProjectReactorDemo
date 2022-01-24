package com.reactive.demo.ProjectReactorDemo.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.stream.Stream;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/weather/report")
@Log4j2
public class WeatherController {
    @GetMapping(value = "/continuous", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<Float> getContinuousFeed() {
        Flux<Float> result = getTempContinuousFeed();
        return result;
    }

    @GetMapping(value = "/continuous/explained", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<String> getContinuousFeedExplained() {
        Flux<String> result = getTempContinuousFeed()
                .map(data -> "The temperature at : " + Instant.now().toString() + " is : " + data);
        return result;
    }

    @GetMapping(value = "/continuous/temperature&humidity", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<String> getContinuousTempNHumidity() {
        Flux<Float> temp = getTempContinuousFeed();
        Flux<Float> humidity = getHumidityContinuousFeed();
        return temp.zipWith(humidity, (fluxTemp, fluxHumidity) -> "The temperature at : "
                + Instant.now().toString()
                + " is : " + fluxTemp
                + " and Humidity is : " + fluxHumidity
        );
    }

    private Flux<Float> getTempContinuousFeed() {
        Random random = new Random();
        return Flux
                .fromStream(Stream.generate(random::nextFloat))
                .log()
                .map(data -> data * 10)
                .delayElements(Duration.ofMillis(1000));
                //.log();
    }

    private Flux<Float> getHumidityContinuousFeed() {
        Random random = new Random();
        return Flux
                .fromStream(Stream.generate(random::nextFloat))
                //.log()
                .map(data -> data * 10)
                .delayElements(Duration.ofMillis(1000))
                .log();
    }
}
