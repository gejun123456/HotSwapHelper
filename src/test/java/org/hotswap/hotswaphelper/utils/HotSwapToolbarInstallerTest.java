package org.hotswap.hotswaphelper.utils;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HotSwapToolbarInstallerTest {
    @Test
    public void newUiChoosesOnlyNewToolbarEvenWhenBothGroupsExist() {
        Set<String> groups = bothToolbars();
        assertEquals(HotSwapToolbarInstaller.NEW_TOOLBAR,
                HotSwapToolbarInstaller.selectToolbar(true, groups::contains));
    }

    @Test
    public void classicUiChoosesOnlyClassicToolbarEvenWhenBothGroupsExist() {
        Set<String> groups = bothToolbars();
        assertEquals(HotSwapToolbarInstaller.CLASSIC_TOOLBAR,
                HotSwapToolbarInstaller.selectToolbar(false, groups::contains));
    }

    @Test
    public void missingNewGroupFallsBackToClassicToolbar() {
        assertEquals(HotSwapToolbarInstaller.CLASSIC_TOOLBAR,
                HotSwapToolbarInstaller.selectToolbar(true, HotSwapToolbarInstaller.CLASSIC_TOOLBAR::equals));
    }

    @Test
    public void missingClassicGroupFallsBackToNewToolbar() {
        assertEquals(HotSwapToolbarInstaller.NEW_TOOLBAR,
                HotSwapToolbarInstaller.selectToolbar(false, HotSwapToolbarInstaller.NEW_TOOLBAR::equals));
    }

    @Test
    public void noToolbarLeavesTheRunMenuAsFallback() {
        assertNull(HotSwapToolbarInstaller.selectToolbar(true, id -> false));
        assertNull(HotSwapToolbarInstaller.selectToolbar(false, id -> false));
    }

    private Set<String> bothToolbars() {
        return new HashSet<>(Arrays.asList(HotSwapToolbarInstaller.NEW_TOOLBAR, HotSwapToolbarInstaller.CLASSIC_TOOLBAR));
    }
}
