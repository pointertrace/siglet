package io.github.pointertrace.siglet.impl.test.bundle.springboot.suffix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SuffixSpanletApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuffixSpanletApplication.class, args);
    }

}
