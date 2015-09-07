package sk.hts.bamboo.plugins;

import com.atlassian.bamboo.build.test.TestCollationService;
import com.atlassian.bamboo.task.*;
import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.process.ExternalProcessBuilder;
import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.util.Narrow;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;

import com.atlassian.bamboo.build.logger.interceptors.ErrorMemorisingInterceptor;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityDefaultsHelper;
import com.atlassian.utils.process.ExternalProcess;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class PyFabricTask implements CommonTaskType
{
    private final ProcessService processService;
    private final CapabilityContext capabilityContext;
    private final TestCollationService testCollationService;

    public static final String CAPABILITY_PREFIX = CapabilityDefaultsHelper.CAPABILITY_BUILDER_PREFIX + ".pyfabric";

    private ExternalProcess createFabricProcess(@NotNull CommonTaskContext taskContext, @NotNull List<String> commandsList)
    {
        return processService.executeExternalProcess(taskContext, new ExternalProcessBuilder()
                        .commandFromString(StringUtils.join(commandsList, " "))
                        .workingDirectory(taskContext.getWorkingDirectory())
        );
    }

    private String getTestParsingPattern(@NotNull TaskContext taskContext)
    {
        return taskContext.getConfigurationMap().get(TaskConfigConstants.CFG_TEST_RESULTS_FILE_PATTERN);
    }

    public PyFabricTask(final ProcessService processService,
                         final TestCollationService testCollationService,
                         CapabilityContext capabilityContext)
    {
        this.processService = processService;
        this.testCollationService = testCollationService;
        this.capabilityContext = capabilityContext;
    }

    @NotNull
    @Override
    public TaskResult execute(final CommonTaskContext commonTaskContext) throws TaskException
    {
        final ErrorMemorisingInterceptor errorLines = ErrorMemorisingInterceptor.newInterceptor();
        commonTaskContext.getBuildLogger().getInterceptorStack().add(errorLines);

        try
        {
            final TaskResultBuilder taskResultBuilder = TaskResultBuilder.newBuilder(commonTaskContext);
            final ConfigurationMap configurationMap = commonTaskContext.getConfigurationMap();
            final String fabricPath = Preconditions.checkNotNull(
                    capabilityContext.getCapabilityValue(
                            PyFabricTask.CAPABILITY_PREFIX + "." + configurationMap.get(PyFabricTaskConfigurator.RUNTIME)
                    ), "Builder path is not defined"
            );

            final String fabfile = configurationMap.get(PyFabricTaskConfigurator.FABFILE);
            final String task = configurationMap.get(PyFabricTaskConfigurator.TASK);
            final String password = configurationMap.get(PyFabricTaskConfigurator.PASSWORD);
            final String arguments = configurationMap.get(PyFabricTaskConfigurator.ARGUMENTS);
            final List<String> commandsList = Lists.newArrayList(fabricPath, task);

            if (StringUtils.isNotBlank(fabfile))
            {
                commandsList.add(String.format("-f %s", fabfile));
            }

            if (StringUtils.isNotBlank(password)) {
                commandsList.add(String.format("-p %s", password));
            }

            if (StringUtils.isNotBlank(arguments))
            {
                commandsList.add(arguments);
            }

            taskResultBuilder.checkReturnCode(createFabricProcess(commonTaskContext, commandsList));
            final TaskContext taskContext = Narrow.to(commonTaskContext, TaskContext.class);

            if (taskContext != null)
            {
                final String testParsingPattern = getTestParsingPattern(taskContext);
                final boolean hasTests = taskContext.getConfigurationMap()
                    .getAsBoolean(TaskConfigConstants.CFG_HAS_TESTS);

                if (hasTests && testParsingPattern != null)
                {
                    testCollationService.collateTestResults(taskContext, testParsingPattern);
                    taskResultBuilder.checkTestFailures();
                }
            }

            return taskResultBuilder.build();
        }
        finally
        {
            commonTaskContext.getCommonContext().getCurrentResult().addBuildErrors(errorLines.getErrorStringList());
        }
    }
}