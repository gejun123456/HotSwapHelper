package org.hotswap.hotswaphelper.utils;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.ModuleRunProfile;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.externalSystem.model.ProjectSystemId;
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings;
import com.intellij.openapi.externalSystem.service.execution.ExternalSystemRunConfiguration;
import com.intellij.openapi.project.Project;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GradleUtilsTest {

    @Test
    public void testPatchGradleScriptParametersWhenEmpty() {
        ExternalSystemTaskExecutionSettings settings = new ExternalSystemTaskExecutionSettings();
        settings.setScriptParameters("");

        File initScript = new File("C:/tmp/.hotswap/hotswap-init.gradle");
        GradleUtils.patchGradleScriptParameters(settings, initScript);

        Assert.assertNotNull(settings.getScriptParameters());
        Assert.assertTrue(settings.getScriptParameters().contains("--init-script"));
        Assert.assertTrue(settings.getScriptParameters().contains("hotswap-init.gradle"));
    }

    @Test
    public void testPatchGradleScriptParametersWhenExistingParams() {
        ExternalSystemTaskExecutionSettings settings = new ExternalSystemTaskExecutionSettings();
        settings.setScriptParameters("--info --stacktrace");

        File initScript = new File("C:/tmp/.hotswap/hotswap-init.gradle");
        GradleUtils.patchGradleScriptParameters(settings, initScript);

        Assert.assertTrue(settings.getScriptParameters().startsWith("--info --stacktrace --init-script"));
        Assert.assertTrue(settings.getScriptParameters().contains("hotswap-init.gradle"));
    }

    @Test
    public void testPatchGradleScriptParametersIdempotent() {
        ExternalSystemTaskExecutionSettings settings = new ExternalSystemTaskExecutionSettings();
        settings.setScriptParameters("--info");

        File initScript = new File("C:/tmp/.hotswap/hotswap-init.gradle");
        // Patch first time
        GradleUtils.patchGradleScriptParameters(settings, initScript);
        String firstPass = settings.getScriptParameters();

        // Patch second time
        GradleUtils.patchGradleScriptParameters(settings, initScript);
        String secondPass = settings.getScriptParameters();

        Assert.assertEquals(firstPass, secondPass);
    }

    @Test
    public void testIsGradleRunConfigurationWithNull() {
        Assert.assertFalse(GradleUtils.isGradleRunConfiguration(null));
        Assert.assertFalse(GradleUtils.isIdeaJavaRunConfiguration(null));
    }
}
