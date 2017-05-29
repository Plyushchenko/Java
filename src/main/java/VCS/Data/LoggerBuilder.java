package VCS.Data;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Logger builder*/
public class LoggerBuilder {

    /** Setup logger configuration*/
    @NotNull
    public static Logger buildLogger(@NotNull FileSystem fileSystem) throws IOException {
        Path path = fileSystem.getLoggerLocation();
        fileSystem.createDirectory(path);
        final ConfigurationBuilder<BuiltConfiguration> builder =
                ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setConfigurationName("Logger");
        builder.setStatusLevel(Level.OFF);
        final LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
                .addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable");
        final ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies")
                .addComponent(builder.newComponent("SizeBasedTriggeringPolicy")
                        .addAttribute("size", "1MB"));
        final ComponentBuilder<?> rolloverStrategy =
                builder.newComponent("DefaultRolloverStrategy").addAttribute("max", 3);
        final AppenderComponentBuilder appenderBuilder = builder.newAppender("file", "ROLLINGFILE")
                .addAttribute("fileName", Paths.get(path.toString(), "git.log").toString())
                .addAttribute("filePattern", Paths.get(path.toString(), "git%i.log").toString())
                .add(layoutBuilder)
                .addComponent(triggeringPolicy)
                .addComponent(rolloverStrategy);
        builder.add(appenderBuilder);
        final RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.TRACE)
                .add(builder.newAppenderRef("file"))
                .addAttribute("additivity", false);
        builder.add(rootLogger);
        return Configurator.initialize(builder.build()).getRootLogger();
    }

}