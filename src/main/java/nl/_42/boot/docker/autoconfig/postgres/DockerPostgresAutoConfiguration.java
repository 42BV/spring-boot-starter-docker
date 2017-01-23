package nl._42.boot.docker.autoconfig.postgres;

import liquibase.integration.spring.SpringLiquibase;
import nl._42.boot.docker.postgres.DockerPostgresProperties;
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
@EnableConfigurationProperties(DockerPostgresProperties.class)
public class DockerPostgresAutoConfiguration {

    public static final String DEPEND_ON_BEAN = "springScriptHooksBean";

    @Configuration
    @EnableConfigurationProperties(DockerPostgresProperties.class)
    public static class Docker42Configuration {

        private final DockerPostgresProperties properties;

        public Docker42Configuration(DockerPostgresProperties properties) {
            this.properties = properties;
        }

        @Bean
        @Conditional(OnSpringScriptHooksCondition.class)
        public DockerPostgresBean springScriptHooksBean() {
            return new DockerPostgresBean(properties);
        }

    }

    @Bean
    @Conditional(OnSpringScriptHooksCondition.class)
    public DockerPostgresDependencyPostProcessor docker42DatabaseBeanLiquibaseDependencyPostProcessor() {
        return new DockerPostgresDependencyPostProcessor(DEPEND_ON_BEAN);
    }

    static class OnSpringScriptHooksCondition extends AllNestedConditions {

        OnSpringScriptHooksCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean(SpringLiquibase.class)
        static class HasSpringLiquibase {}

    }

}