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

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

/**
 * @author Dmitry Zhuravlev
 *         Date:  09.03.2017
 */
@State(
        name = "HotSwapHelperPluginSettingsProvider",
        storages = [(Storage("HotSwapHelper.xml"))]
)
class HotSwapHelperPluginSettingsProvider : PersistentStateComponent<HotSwapHelperPluginSettingsProvider.State> {
    companion object{
        fun getInstance(project: Project): HotSwapHelperPluginSettingsProvider {
            return ServiceManager.getService(project, HotSwapHelperPluginSettingsProvider::class.java)
        }
    }
    class State {
        var useExternalHotSwapAgentFile = false;
        var agentPath = ""
        var jdkDirectory = ""
//        var enableAgentForAllConfiguration = false
//        var selectedRunConfigurations = mutableSetOf<String>()
        var disabledPlugins = mutableSetOf<String>()

        var dontCheckJdk = false;
        var useOldDebuggerAgentAfter243 = true;
    }

    var currentState = State()

    override fun getState() = currentState

    override fun loadState(state: State) {
        currentState.agentPath = state.agentPath
        currentState.jdkDirectory = state.jdkDirectory
//        currentState.enableAgentForAllConfiguration = state.enableAgentForAllConfiguration
//        currentState.selectedRunConfigurations = state.selectedRunConfigurations
        currentState.disabledPlugins = state.disabledPlugins
        currentState.useExternalHotSwapAgentFile = state.useExternalHotSwapAgentFile;
        currentState.dontCheckJdk = state.dontCheckJdk;
        currentState.useOldDebuggerAgentAfter243 = state.useOldDebuggerAgentAfter243;
    }
}
