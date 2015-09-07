[#-- @ftlvariable name="uiConfigSupport" type="com.atlassian.bamboo.ww2.actions.build.admin.create.UIConfigSupport" --]

[@ww.label labelKey='executable.type' name='label' /]
[@ww.label labelKey='pyfabric.task' name='task' /]
[@ww.label labelKey="pyfabric.fabfile" name="fabfile" hideOnNull='true' /]
[@ww.label labelKey="pyfabric.arguments" name="arguments" hideOnNull='true' /]
[@ww.label labelKey='builder.common.sub' name='workingSubDirectory' hideOnNull='true' /]

[#if hasTests ]
    [@ww.label labelKey='builder.common.tests.directory' name='testResultsDirectory' hideOnNull='true' /]
[/#if]
