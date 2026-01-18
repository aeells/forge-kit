package io.forge.kit.common.test;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;

/**
 * Quarkus test resource that loads environment variables from a configurable .env file.
 * <p>
 * Ensures tests use centralized port configuration instead of hardcoded values.
 *
 * <p>Usage: Add to your test class.
 * <pre>
 * {@code
 * @QuarkusTestResource(
 * value = QuarkusPortsEnvTestResource.class,
 * initArgs = {"portsEnvFile=ports-test-config.env"} // pass custom path
 * )
 * }
 * </pre>
 */
@IfBuildProfile("test")
public final class QuarkusPortsEnvTestResource implements QuarkusTestResourceLifecycleManager
{
    private static final Logger LOGGER = Logger.getLogger(QuarkusPortsEnvTestResource.class);

    private static final String PORTS_ENV_FILE_ARG = "portsEnvFile";

    private String portsEnvFilePath;

    @Override
    public void init(final Map<String, String> initArgs)
    {
        if (initArgs != null && initArgs.containsKey(PORTS_ENV_FILE_ARG))
        {
            this.portsEnvFilePath = initArgs.get(PORTS_ENV_FILE_ARG);
        }
        else
        {
            LOGGER.warn("No " + PORTS_ENV_FILE_ARG + " specified. No default ports.env is specified.");
        }
    }

    @Override
    public Map<String, String> start()
    {
        final Path envFile = Paths.get(System.getProperty("user.dir")).resolve(this.portsEnvFilePath);
        if (!Files.exists(envFile))
        {
            System.err.println("WARNING: Could not find " + this.portsEnvFilePath + ". Tests may fail if port env vars are not set.");
            return Collections.emptyMap();
        }

        try (Stream<String> lines = Files.lines(envFile))
        {
            final Map<String, String> envVars = lines
                .map(String::trim)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .map(this::parseKeyValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            envVars.forEach(System::setProperty);

            return Collections.unmodifiableMap(envVars);
        }
        catch (IOException e)
        {
            System.err.println("WARNING: Failed to read " + this.portsEnvFilePath + ": " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    @Override
    public void stop()
    {
        // No cleanup needed
    }

    /** Attempts to parse a line in KEY=VALUE format. Returns null if invalid. */
    private Map.Entry<String, String> parseKeyValue(final String line)
    {
        final int idx = line.indexOf('=');
        if (idx <= 0) return null;

        final String key = line.substring(0, idx).trim();
        final String value = line.substring(idx + 1).trim();

        return StringUtils.isAnyEmpty(key, value) ? null : Map.entry(key, value);
    }
}
