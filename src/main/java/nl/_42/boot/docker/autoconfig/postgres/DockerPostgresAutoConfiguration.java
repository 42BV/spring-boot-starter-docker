package nl._42.boot.docker.autoconfig.postgres;

import liquibase.integration.spring.SpringLiquibase;
import nl._42.boot.docker.postgres.DockerPostgresBootSequence;
import nl._42.boot.docker.postgres.DockerPostgresProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "docker.postgres", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(value = DockerPostgresBootSequence.class)
@AutoConfigureAfter({LiquibaseAutoConfiguration.class })
@EnableConfigurationProperties(DockerPostgresProperties.class)
public class DockerPostgresAutoConfiguration {

    public static final String DEPEND_ON_BEAN = "dockerPostgresBean";

    @Configuration
    @EnableConfigurationProperties({DataSourceProperties.class, DockerPostgresProperties.class})
    public static class Docker42Configuration {

        private final DockerPostgresProperties properties;
        private final DataSourceProperties dataSourceProperties;

        public Docker42Configuration(DockerPostgresProperties properties, DataSourceProperties dataSourceProperties) {
            this.properties = properties;
            this.dataSourceProperties = dataSourceProperties;
        }

        @Bean
        @Conditional(OnDockerPostgresCondition.class)
        public DockerPostgresBean dockerPostgresBean() {
            return new DockerPostgresBean(properties, dataSourceProperties);
        }

    }

    @Bean
    @Conditional(OnDockerPostgresCondition.class)
    public DockerPostgresDependencyPostProcessor docker42DatabaseBeanLiquibaseDependencyPostProcessor() {
        return new DockerPostgresDependencyPostProcessor(DEPEND_ON_BEAN);
    }

    static class OnDockerPostgresCondition extends AllNestedConditions {

        OnDockerPostgresCondition() {
            super(ConfigurationPhase.REGISTER_BEAN);
        }

        @ConditionalOnBean(SpringLiquibase.class)
        static class HasSpringLiquibase {}

    }

}