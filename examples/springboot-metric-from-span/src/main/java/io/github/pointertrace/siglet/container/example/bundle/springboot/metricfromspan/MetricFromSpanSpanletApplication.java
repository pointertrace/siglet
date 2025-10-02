package io.github.pointertrace.siglet.container.example.bundle.springboot.metricfromspan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MetricFromSpanSpanletApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetricFromSpanSpanletApplication.class, args);
    }

}
