package crm.spring.rest.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"crm.spring.rest.api.v1","crm.spring.rest.mapper","crm.spring.rest.service"})
public class WebConfig implements WebMvcConfigurer{

}
