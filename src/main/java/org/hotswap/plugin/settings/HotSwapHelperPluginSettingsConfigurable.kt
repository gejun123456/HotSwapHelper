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
package org.hotswap.plugin.settings

import com.intellij.execution.RunManager
import com.intellij.ide.BrowserUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.ui.DocumentAdapter
import com.intellij.util.execution.ParametersListUtil
import org.apache.commons.lang.StringUtils
import org.hotswap.plugin.ui.HotSwapAgentPluginSettingsForm
import java.awt.CardLayout
import java.util.*
import javax.swing.JComponent
import javax.swing.event.DocumentEvent

/**
 * @author Dmitry Zhuravlev
 *         Date:  09.03.2017
 */
class HotSwapHelperPluginSettingsConfigurable(project: Project) : Configurable {
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

        val text = form.agentInstallPathField.text
        val useExternalAgentFile = form.useExternalAgentFileCheckBox.isSelected
        if(useExternalAgentFile&&StringUtils.isBlank(text)){
            Messages.showErrorDialog("when use external, agent must not null empty","agent file empty");
            return;
        }
        stateProvider.currentState.agentPath = text
        stateProvider.currentState.useExternalHotSwapAgentFile = useExternalAgentFile;
//        stateProvider.currentState.enableAgentForAllConfiguration = form.applyAgentToAllConfigurationsBox.isSelected
//        stateProvider.currentState.selectedRunConfigurations = form.configurationTableProvider.getSelectedConfigurationNames()
        stateProvider.currentState.disabledPlugins = form.disabledPluginsField.text.parse()
//        showUpdateButton()
        stateChanged = false
    }



    override fun createComponent(): JComponent? {
        setupFormComponents()
        //support it in later release version.
        form.disablePluginPanel.isVisible = false;
        return form.rootPanel
    }

    override fun reset() {
        form.agentInstallPathField.text = stateProvider.currentState.agentPath
//        form.applyAgentToAllConfigurationsBox.isSelected = stateProvider.currentState.enableAgentForAllConfiguration
        form.disabledPluginsField.text = stateProvider.currentState.disabledPlugins.joinString()
        form.useExternalAgentFileCheckBox.isSelected = stateProvider.currentState.useExternalHotSwapAgentFile
        stateChanged = false
    }

    override fun getHelpTopic() = null

    private fun setupFormComponents() {
//        projectRootManager.projectSdk?.let { sdk ->
//            form.dcevmVersionLabel.text = DCEVMUtil.determineDCEVMVersion(sdk) ?: DCEVM_NOT_DETERMINED
//        }
        form.agentInstallPathField.addBrowseFolderListener(null, null, null, FileChooserDescriptor(false, false, true, true, false, false))
        form.agentInstallPathField.textField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                stateChanged = form.agentInstallPathField.textField.text != stateProvider.currentState.agentPath
            }
        })
        form.disabledPluginsField.document.addDocumentListener(object: DocumentAdapter(){
            override fun textChanged(e: DocumentEvent) {
                stateChanged = form.disabledPluginsField.text != stateProvider.currentState.disabledPlugins.joinString()
            }
        })
        form.useExternalAgentFileCheckBox.addItemListener {
            stateChanged = form.useExternalAgentFileCheckBox.isSelected != stateProvider.currentState.useExternalHotSwapAgentFile;
        }
        form.updateButton.addActionListener {
            BrowserUtil.browse("https://github.com/HotswapProjects/HotswapAgent/releases")
        }
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

    private fun String.parse() = ParametersListUtil.COLON_LINE_PARSER.`fun`(this).map(String::trim).toMutableSet()

    private fun Set<String>.joinString() = ParametersListUtil.COLON_LINE_JOINER.`fun`(this.toList())
}
