package nl._42.boot.docker;

import nl._42.boot.docker.postgres.ProcessRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * Responsible for the actual setting up and tearing down of the container. The setting up
 * is triggered at @PostConstruct time. The tearing down is triggered at @PreDestroy time.
 */
public class SpringScriptHooksBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringScriptHooksBean.class);

    private final SpringScriptHooksProperties properties;

    public SpringScriptHooksBean(SpringScriptHooksProperties properties) {
        this.properties = properties;
    }

    private ProcessRunner processRunner;

    @Autowired
    private AbstractApplicationContext applicationContext;

    @PostConstruct
    public void postConstruct() throws IOException {
        LOGGER.info(">>> Configuring Docker Postgres");
        LOGGER.info("| Docker Postgres Properties");
        LOGGER.info("| * Image name: " + properties.getImageName());
        LOGGER.info("| * Image version: " + properties.getImageVersion());
        LOGGER.info("| * Timeout: " + properties.getTimeout());
        LOGGER.info("| * Container name: " + properties.getContainerName());
        LOGGER.info("| * Port: " + properties.getPort());
        LOGGER.info("| * Password: " + properties.getPassword());
        LOGGER.info("| * Startup Verification Text: [" + properties.getStartupVerificationText() + "]");
        LOGGER.info("| * Std out: " + properties.getStdOutFilename());
        LOGGER.info("| * Std err: " + properties.getStdErrFilename());

        processRunner = new ProcessRunner(properties);
        processRunner.start();
        if (processRunner.verify()) {
            LOGGER.info("| Postgres container successfully started");
        } else {
            LOGGER.error("| Postgres failed to initialize");
            throw new ExceptionInInitializerError("The Docker Container failed to properly initialize.");
        }
        applicationContext.registerShutdownHook();
    }

    @PreDestroy
    public void preDestroy() {
        LOGGER.info(">>> Tearing down Docker 42");
        processRunner.interrupt();
    }

}
