package sk.hts.bamboo.plugins;

import com.atlassian.bamboo.v2.build.agent.capability.AbstractFileCapabilityDefaultsHelper;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityDefaultsHelper;

import org.apache.commons.lang.SystemUtils;
import org.jetbrains.annotations.NotNull;

public class PyFabricDefaultsCapabilityHelper extends AbstractFileCapabilityDefaultsHelper
{
    public static final String CAPABILITY_NAME = CapabilityDefaultsHelper.CAPABILITY_BUILDER_PREFIX + ".pyfabric";
    public static final String CAPABILITY_KEY = CAPABILITY_NAME + ".Fabric";

    @NotNull
    @Override
    protected String getExecutableName()
    {
        final String executable = "fab";

        return SystemUtils.IS_OS_WINDOWS ? executable + ".exe" : executable;
    }

    @NotNull
    @Override
    protected String getCapabilityKey()
    {
        return CAPABILITY_KEY;
    }
}