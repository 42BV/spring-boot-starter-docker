package nl._42.boot.docker.autoconfig.postgres;

import nl._42.boot.docker.postgres.DockerPostgresBootSequence;
import nl._42.boot.docker.postgres.DockerPostgresContainer;
import nl._42.boot.docker.postgres.DockerPostgresProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.support.AbstractApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Responsible for the actual setting up and tearing down of the container. The setting up
 * is triggered at @PostConstruct time. The tearing down is triggered at @PreDestroy time.
 */
public class DockerPostgresBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(DockerPostgresBean.class);

    private final DockerPostgresProperties properties;
    private final DataSourceProperties dataSourceProperties;

    private DockerPostgresContainer postgresContainer;

    public DockerPostgresBean(DockerPostgresProperties properties, DataSourceProperties dataSourceProperties) {
        this.properties = properties;
        this.dataSourceProperties = dataSourceProperties;
    }

    @Autowired
    private AbstractApplicationContext applicationContext;

    @PostConstruct
    public void postConstruct() throws Exception {
        LOGGER.info(">>> Configuring Docker Postgres");
        postgresContainer = new DockerPostgresBootSequence(properties, dataSourceProperties).execute();
        applicationContext.registerShutdownHook();
    }

    @PreDestroy
    public void preDestroy() {
        LOGGER.info(">>> Tearing down Postgres Docker");
        postgresContainer.interrupt();
    }

}
