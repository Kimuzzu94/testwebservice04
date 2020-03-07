package org.kimuzzu.toby.testwebservice04;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.awt.*;
import java.time.Duration;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
@RestController
@SpringBootApplication
public class Testwebservice04Application {

    @GetMapping("/event/{id}")
    Mono<Event > event(@PathVariable long id) {
        return Mono.just(new Event(id, "event" + id));

    }

    //http stream
    @GetMapping(value = "events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Event> events() {
        //List<Event> list = Arrays.asList(new Event(1L, "event1"), new Event(2L, "evnet2"), new Event(3L, "event3"));
        //return Flux.fromIterable(list);

/*
        return Flux
                //.fromStream(Stream.generate(() -> new Event(System.currentTimeMillis(), "value")))
                //.<Event>generate(sink->sink.next(new Event(System.currentTimeMillis(), "value")))
                .<Event, Long>generate(() -> 1L, (id, sink)->{
                    sink.next(new Event(id, "value" + id));
                    return id + 1;
                })
                .delayElements(Duration.ofSeconds(1))
                .take(10);

 */
/*
        Flux<Event> es = Flux
                .<Event, Long>generate(() -> 1L, (id, sink) -> {
                    sink.next(new Event(id, "value" + id));
                    return id + 1;
                });

 */
        Flux<String> es = Flux.<String>generate(sink -> sink.next("Value"));
        Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

        //두가지 이상의 데이터를 지퍼처럼 묶어서 tuple을 생성
        //interval flux의 데이터를 가져오는데 1초의 대기시간이 걸리는 것을 이용하여 딜레이를 준다.
        return Flux.zip(es, interval).map(tu -> new Event(tu.getT2(), tu.getT1() + tu.getT2())).take(10);
        //return Flux.zip(es, interval).map(tu -> tu.getT1());
    }
    public static void main(String[] args) {
        SpringApplication.run(Testwebservice04Application.class, args);
    }

    @Data
    @AllArgsConstructor
    public static class Event {
        long id;
        String value;
    }

}
