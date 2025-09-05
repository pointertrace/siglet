package io.github.pointertrace.siglet.container.test.bundle.springboot.suffix.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}
