/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021-2025 Machine_Maker
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package me.machinemaker.papertweaks.modules.survival.afkdisplay;

import java.util.Collection;
import java.util.Set;
import me.machinemaker.papertweaks.annotations.ModuleInfo;
import me.machinemaker.papertweaks.modules.ModuleBase;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.pdc.PDCKey;
import me.machinemaker.papertweaks.utils.Keys;

@ModuleInfo(name = "AFKDisplay", configPath = "survival.afk-display", description = "Tracks player AFK status")
public class AFKDisplay extends ModuleBase {

    public static final PDCKey<Boolean> AFK_DISPLAY = PDCKey.bool(Keys.legacyKey("afk"));

    @Override
    protected Class<? extends ModuleLifecycle> lifecycle() {
        return Lifecycle.class;
    }

    @Override
    protected Collection<Class<? extends ModuleListener>> listeners() {
        return Set.of(PlayerListener.class);
    }

    @Override
    protected Collection<Class<? extends ModuleConfig>> configs() {
        return Set.of(Config.class);
    }
}
