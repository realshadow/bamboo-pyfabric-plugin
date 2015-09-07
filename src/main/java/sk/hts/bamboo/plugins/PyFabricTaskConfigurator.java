package sk.hts.bamboo.plugins;

import com.atlassian.bamboo.build.Job;
import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.BuildTaskRequirementSupport;
import com.atlassian.bamboo.task.TaskConfigConstants;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.bamboo.v2.build.agent.capability.Requirement;
import com.atlassian.bamboo.v2.build.agent.capability.RequirementImpl;
import com.atlassian.bamboo.ww2.actions.build.admin.create.UIConfigSupport;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;

public class PyFabricTaskConfigurator extends AbstractTaskConfigurator implements BuildTaskRequirementSupport
{
    private static final String DEFAULT_TEST_RESULTS_PATTERN =  "**/test-reports/*.xml";

    protected static final Set<String> FIELDS = Sets.newHashSet();

    public static final String RUNTIME = "runtime";
    public static final String TASK = "task";
    public static final String PASSWORD = "password";
    public static final String FABFILE = "fabfile";
    public static final String ARGUMENTS = "arguments";

    public static final String CTX_UI_CONFIG_BEAN = "uiConfigSupport";

    public com.opensymphony.xwork2.TextProvider textProvider;
    public UIConfigSupport uiConfigSupport;

    static {
        FIELDS.add(TaskConfigConstants.CFG_WORKING_SUB_DIRECTORY);
        FIELDS.add(TaskConfigConstants.CFG_TEST_RESULTS_FILE_PATTERN);
        FIELDS.add(TaskConfigConstants.CFG_HAS_TESTS_BOOLEAN);
        FIELDS.add(TaskConfigConstants.CFG_HAS_TESTS);
        FIELDS.add(RUNTIME);
        FIELDS.add(TASK);
        FIELDS.add(PASSWORD);
        FIELDS.add(FABFILE);
        FIELDS.add(ARGUMENTS);
    }

    @NotNull
    @Override
    public Set<Requirement> calculateRequirements(@NotNull TaskDefinition taskDefinition, @NotNull Job job)
    {
        final String runtime = taskDefinition.getConfiguration().get(RUNTIME);
        Preconditions.checkNotNull(runtime, "No Fabric executable was selected");

        return Sets.<Requirement>newHashSet(new RequirementImpl(
            PyFabricTask.CAPABILITY_PREFIX + "." + runtime, true, ".*"
        ));
    }

    @Override
    public void validate(@NotNull ActionParametersMap params, @NotNull ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);

        if (StringUtils.isEmpty(params.getString(PyFabricTaskConfigurator.RUNTIME)))
        {
            errorCollection.addError(PyFabricTaskConfigurator.RUNTIME, textProvider.getText("pyfabric.runtime.error"));
        }

        if (StringUtils.isEmpty(params.getString(PyFabricTaskConfigurator.TASK)))
        {
            errorCollection.addError(PyFabricTaskConfigurator.TASK, textProvider.getText("pyfabric.target.error"));
        }
    }

    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull ActionParametersMap params, @Nullable TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
        taskConfiguratorHelper.populateTaskConfigMapWithActionParameters(
                config, params, FIELDS
        );

        return config;
    }

    @Override
    public void populateContextForCreate(@NotNull Map<String, Object> context)
    {
        super.populateContextForCreate(context);
        populateContextForAllOperations(context);
    }

    @Override
    public void populateContextForEdit(@NotNull Map<String, Object> context, @NotNull TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);
        populateContextForAllOperations(context);

        taskConfiguratorHelper.populateContextWithConfiguration(
                context, taskDefinition, FIELDS
        );
    }

    public void populateContextForAllOperations(@NotNull Map<String, Object> context)
    {
        context.put(TaskConfigConstants.CFG_TEST_RESULTS_FILE_PATTERN, DEFAULT_TEST_RESULTS_PATTERN);
        context.put(TaskConfigConstants.CFG_HAS_TESTS_BOOLEAN, Boolean.FALSE);
        context.put(TaskConfigConstants.CFG_HAS_TESTS, Boolean.FALSE);
        context.put(CTX_UI_CONFIG_BEAN, uiConfigSupport);
    }

    public void setTextProvider(com.opensymphony.xwork2.TextProvider textProvider)
    {

        this.textProvider = textProvider;
    }

    public void setUiConfigSupport(UIConfigSupport uiConfigSupport)
    {

        this.uiConfigSupport = uiConfigSupport;
    }
}