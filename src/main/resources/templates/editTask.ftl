[#-- @ftlvariable name="uiConfigSupport" type="com.atlassian.bamboo.ww2.actions.build.admin.create.UIConfigSupport" --]

[#assign addExecutableLink][@ui.displayAddExecutableInline executableKey="pyfabric" /][/#assign]

[@ww.select
    name="runtime" labelKey="pyfabric.runtime" cssClass="builderSelectWidget"
    list=uiConfigSupport.getExecutableLabels("pyfabric") extraUtility=addExecutableLink
    required=true
/]

[@ww.textfield name="task" labelKey="pyfabric.task" cssClass="long-field" required=true /]
[@ww.textfield name="fabfile" labelKey="pyfabric.fabfile" cssClass="long-field" /]
[@ww.password name="password" labelKey="pyfabric.password" cssClass="long-field" /]

[@ww.textarea name="arguments" labelKey="pyfabric.arguments" cssClass="long-field" rows="4" /]
[@ww.textfield name="workingSubDirectory" labelKey="builder.common.sub" cssClass="long-field" /]

[#if !deploymentMode]
    [@ui.bambooSection titleKey="builder.common.tests.directory.description"]
        [@ww.checkbox labelKey="builder.common.tests.exists" name="testChecked" toggle="true" /]

        [@ui.bambooSection dependsOn="testChecked" showOn="true"]
            [@ww.textfield labelKey="builder.common.tests.directory.custom" name="testResultsDirectory" cssClass="long-field" /]
        [/@ui.bambooSection]
    [/@ui.bambooSection]
[#else]
    <p>[@ww.text name='builder.common.deployment.test.disabled' /]</p>
[/#if]