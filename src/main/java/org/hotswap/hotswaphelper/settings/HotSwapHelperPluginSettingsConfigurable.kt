/*
 *  Copyright (c) 2017 Dmitry Zhuravlev, Sergei Stepanov
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.hotswap.hotswaphelper.settings

import com.google.common.base.Joiner
import com.intellij.execution.RunManager
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.ui.DocumentAdapter
import org.apache.commons.lang.StringUtils
import org.hotswap.hotswaphelper.JdkManager
import org.hotswap.hotswaphelper.ui.CopyTextDialog
import org.hotswap.hotswaphelper.ui.HotSwapAgentPluginSettingsForm
import org.hotswap.hotswaphelper.utils.MyUtils
import org.hotswap.hotswaphelper.utils.MyUtils.allOpens
import java.awt.CardLayout
import java.util.*
import javax.swing.JComponent
import javax.swing.event.DocumentEvent

/**
 * @author Dmitry Zhuravlev
 *         Date:  09.03.2017
 */
class HotSwapHelperPluginSettingsConfigurable(var project: Project) : Configurable {
    companion object {
        val bundle = ResourceBundle.getBundle("HotSwapHelperIntellijPluginBundle")!!
        private const val DCEVM_NOT_DETERMINED = "<not determined>"
    }

    private var stateChanged: Boolean = false
    private val form = HotSwapAgentPluginSettingsForm()
    private val projectRootManager = ProjectRootManager.getInstance(project)
    private val stateProvider = HotSwapHelperPluginSettingsProvider.getInstance(project)
    private val runManager = RunManager.getInstance(project)

    override fun isModified() = stateChanged

    override fun getDisplayName() = bundle.getString("settings.hotswap.plugin.name")


    override fun apply() {

        val agentPath = form.agentInstallPathField.text
        val jdkDirectory = form.jdkDirectoryField.text
        val useExternalAgentFile = form.useExternalAgentFileCheckBox.isSelected
        val currentDontCheckJdk = form.dontCheckJdkCheckBox.isSelected
        if (useExternalAgentFile && StringUtils.isBlank(agentPath)) {
            Messages.showErrorDialog("when use external, agent must not null empty", "agent file empty");
            return;
        }
        stateProvider.currentState.agentPath = agentPath
        stateProvider.currentState.jdkDirectory = jdkDirectory
        stateProvider.currentState.useExternalHotSwapAgentFile = useExternalAgentFile;
        stateProvider.currentState.dontCheckJdk = currentDontCheckJdk
//        stateProvider.currentState.enableAgentForAllConfiguration = form.applyAgentToAllConfigurationsBox.isSelected
//        stateProvider.currentState.selectedRunConfigurations = form.configurationTableProvider.getSelectedConfigurationNames()
        stateProvider.currentState.disabledPlugins = form.disabledPluginsField.text.parse()
//        showUpdateButton()
        stateChanged = false
    }


    override fun createComponent(): JComponent? {
        setupFormComponents()
        //support it in later release version.
        form.disablePluginPanel.isVisible = true;
        return form.rootPanel
    }

    override fun reset() {
        form.agentInstallPathField.text = stateProvider.currentState.agentPath
        form.jdkDirectoryField.text = stateProvider.currentState.jdkDirectory
//        form.applyAgentToAllConfigurationsBox.isSelected = stateProvider.currentState.enableAgentForAllConfiguration
        form.disabledPluginsField.text = stateProvider.currentState.disabledPlugins.joinString()
        form.useExternalAgentFileCheckBox.isSelected = stateProvider.currentState.useExternalHotSwapAgentFile
        form.dontCheckJdkCheckBox.isSelected = stateProvider.currentState.dontCheckJdk
        stateChanged = false
    }

    override fun getHelpTopic() = null

    private fun setupFormComponents() {
//        projectRootManager.projectSdk?.let { sdk ->
//            form.dcevmVersionLabel.text = DCEVMUtil.determineDCEVMVersion(sdk) ?: DCEVM_NOT_DETERMINED
//        }
        form.agentInstallPathField.addBrowseFolderListener(
            null,
            null,
            null,
            FileChooserDescriptor(false, false, true, true, false, false)
        )
        form.agentInstallPathField.textField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                stateChanged = form.agentInstallPathField.textField.text != stateProvider.currentState.agentPath
            }
        })
        form.jdkDirectoryField.addBrowseFolderListener(
            null,
            null,
            null,
            FileChooserDescriptor(false, true, true, false, false, false)
        )
        form.jdkDirectoryField.textField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                stateChanged = form.jdkDirectoryField.textField.text != stateProvider.currentState.jdkDirectory
            }
        })
        form.disabledPluginsField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                stateChanged = form.disabledPluginsField.text != stateProvider.currentState.disabledPlugins.joinString()
            }
        })
        form.useExternalAgentFileCheckBox.addItemListener {
            stateChanged =
                form.useExternalAgentFileCheckBox.isSelected != stateProvider.currentState.useExternalHotSwapAgentFile;
        }

        form.dontCheckJdkCheckBox.addItemListener {
            stateChanged = form.dontCheckJdkCheckBox.isSelected != stateProvider.currentState.dontCheckJdk
        }
        form.updateButton.addActionListener {
            BrowserUtil.browse("https://github.com/HotswapProjects/HotswapAgent/releases")
        }

        form.exampleButton.addActionListener({
            val allPluginName =
                "Hotswapper, JdkPlugin, AnonymousClassPatch, ClassInitPlugin, WatchResources, Hibernate, HibernateJakarta, Hibernate3JPA, Hibernate3, Spring, SpringBoot, Jersey1, Jersey2, Jetty, Tomcat, ZK, Logback, Log4j2, MyFaces, Mojarra, Omnifaces, ELResolver, WildFlyELResolver, OsgiEquinox, Owb, OwbJakarta, Proxy, WebObjects, Weld, WeldJakarta, JBossModules, ResteasyRegistry, Deltaspike, DeltaspikeJakarta, GlassFish, Weblogic, Vaadin, Wicket, CxfJAXRS, FreeMarker, Undertow, MyBatis, MyBatisPlus, IBatis, JacksonPlugin, Idea, Thymeleaf, Velocity, Sponge"
            var split = allPluginName.split(",");
            //trim all names.
            split = split.map { it.trim() }
            var join = Joiner.on(",").join(split)
            val text = "all plugin names:\n" + join + "\n\n";
            val disableSpringText = "disable spring plugins:\n" + "Spring,Springboot";
            CopyTextDialog(project, text + disableSpringText,"Plugin name to disable").show()
        })

        //set the linkListener

        form.whatWillHappen.setListener({ label, linkData ->
            // Your implementation here
            BrowserUtil.browse("https://github.com/HotswapProjects/HotswapAgent")
        }, null)

        form.showVmParametersForButton.addActionListener({
            val builder = StringBuilder()
            builder.append(buildVmCommandFor(8));
            builder.append(buildVmCommandFor(11));
            builder.append(buildVmCommandFor(17));
            CopyTextDialog(project, builder.toString(),"Vm Parameters").show()
        })
//        form.dcevmDownloadSuggestionLabel.apply {
//            setHtmlText("""
//                   DCEVM installation not found for JDK specified for the current project.
//                   You should <a>download</a> and""")
//            foreground = Color.red
//            setHyperlinkTarget(DCEVM_RELEASES_URL)
//            isVisible = form.dcevmVersionLabel.text == DCEVM_NOT_DETERMINED
//        }
//        form.dcevmHowToInstallLabel.apply {
//            setHtmlText("""<a>install</a> it.""")
//            foreground = Color.red
//            setHyperlinkTarget(DCEVM_HOW_TO_INSTALL_URL)
//            isVisible = form.dcevmVersionLabel.text == DCEVM_NOT_DETERMINED
//        }
//        form.configurationTableProvider.apply {
//            addModelChangeListener {
//                stateChanged = stateProvider.currentState.selectedRunConfigurations != form.configurationTableProvider.getSelectedConfigurationNames()
//            }
//            setItems(runManager.allConfigurationsList.toTableItems())
//            setSelected(stateProvider.currentState.selectedRunConfigurations)
//        }
    }

    private fun showUpdateButton() {
        (form.updateButtonPanel.layout as CardLayout).show(form.updateButtonPanel, "cardWithUpdateButton")
//        val currentVersion = HotSwapAgentPathUtil.determineAgentVersionFromPath(stateProvider.currentState.agentPath)
//        val show = currentVersion != null && File(stateProvider.currentState.agentPath).exists() && downloadManager.isLatestAgentVersionAvailable(currentVersion)
//        if (show) {
//            (form.updateButtonPanel.layout as CardLayout).show(form.updateButtonPanel, "cardWithUpdateButton")
//        } else {
//            (form.updateButtonPanel.layout as CardLayout).show(form.updateButtonPanel, "emptyCard")
//        }
    }

    fun buildVmCommandFor(jdkVersion: Int): String {
        val builder = StringBuilder()
        builder.append("java$jdkVersion:\n")
        val isUseExternal = form.useExternalAgentFileCheckBox.isSelected
        val agentPath = if (isUseExternal) {
            form.agentInstallPathField.text
        } else {
            MyUtils.getHotSwapJarPath().absolutePath
        }
        //get current project jdk
        val sdk = ProjectRootManager.getInstance(project).projectSdk
        var isJbr = false;
        if (sdk == null) {
            return "no project jdk found for current project"
        }
        val homePath = sdk.homePath
        if (homePath == null) {
            return "no project jdk found for current project"
        }
        val result = JdkManager.checkJdkHome(homePath, true)
        if (result.isJbr) {
            isJbr = true;
        }
        builder.append("-javaagent:\"" + agentPath + "\"")
        builder.append("\n")
        if (jdkVersion == 8) {
            builder.append("-XXaltjvm=dcevm\n")
        } else {
            builder.append("-XX:HotswapAgent=external\n")
            if (jdkVersion == 11 && isJbr) {
                builder.append("-XX:+AllowEnhancedClassRedefinition\n")
            }
            if (jdkVersion >= 17) {
                builder.append("-XX:+AllowEnhancedClassRedefinition\n")
                builder.append("-XX:+ClassUnloading\n");
                for (item in allOpens) {
                    builder.append(item + "\n");
                }
            }
        }
        val texts = form.disabledPluginsField.text
        val disablePlugins = texts.parse()
        if (texts.isNotBlank() && disablePlugins.isNotEmpty()) {
            val join = Joiner.on(",").join(disablePlugins)
            builder.append("-Dhotswapagent.disablePlugin=" + join + "\n")
        }

        builder.append("\n\n");
        return builder.toString()
    }

    private fun String.parse() = this.split(",").map(String::trim).toMutableSet()

    private fun Set<String>.joinString() = Joiner.on(",").join(this);
}
