package sn.uam.polytech.misid.config;

import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import sn.uam.polytech.misid.aop.logging.LoggingAspect;
import tech.jhipster.config.JHipsterConstants;

@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {

    @Bean
    @Profile(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)
    public LoggingAspect loggingAspect(Environment env) {
        return new LoggingAspect(env);
    }
}
