package nl._42.boot.docker;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(prefix = "docker.postgres", name = "enabled", matchIfMissing = false)
@AutoConfigureAfter({LiquibaseAutoConfiguration.class })
@EnableConfigurationProperties(SpringScriptHooksProperties.class)
public class SpringScriptHooksAutoConfiguration {

    public static final String DEPEND_ON_BEAN = "springScriptHooksBean";

    @Configuration
    @EnableConfigurationProperties(SpringScriptHooksProperties.class)
    public static class Docker42Configuration {

        private final SpringScriptHooksProperties properties;

        public Docker42Configuration(SpringScriptHooksProperties properties) {
            this.properties = properties;
        }

        @Bean
        @Conditional(OnSpringScriptHooksCondition.class)
        public SpringScriptHooksBean springScriptHooksBean() {
            return new SpringScriptHooksBean(properties);
        }

    }

    @Bean
    @Conditional(OnSpringScriptHooksCondition.class)
    public SpringScriptHooksDependencyPostProcessor docker42DatabaseBeanLiquibaseDependencyPostProcessor() {
        return new SpringScriptHooksDependencyPostProcessor(DEPEND_ON_BEAN);
    }

    static class OnSpringScriptHooksCondition extends AllNestedConditions {

        OnSpringScriptHooksCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean(SpringLiquibase.class)
        static class HasSpringLiquibase {}

    }

}