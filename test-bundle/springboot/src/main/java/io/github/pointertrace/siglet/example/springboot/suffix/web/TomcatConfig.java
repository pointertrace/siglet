package io.github.pointertrace.siglet.example.springboot.suffix.web;

import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {

        return new TomcatServletWebServerFactory() {

            @Override
            protected void postProcessContext(org.apache.catalina.Context context) {
                TomcatURLStreamHandlerFactory.disable();
            }
        };
    }
}
