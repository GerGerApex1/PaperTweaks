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
package me.machinemaker.papertweaks.modules.teleportation.tpa;

import com.google.inject.Inject;
import java.util.Set;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.modules.ModuleRecipe;
import org.bukkit.plugin.java.JavaPlugin;

class Lifecycle extends ModuleLifecycle {

    private final TPARunnable tpaRunnable;

    @Inject
    Lifecycle(final JavaPlugin plugin, final Set<ModuleCommand> commands, final Set<ModuleListener> listeners, final Set<ModuleConfig> configs, final Set<ModuleRecipe<?>> moduleRecipes, final TPARunnable tpaRunnable) {
        super(plugin, commands, listeners, configs, moduleRecipes);
        this.tpaRunnable = tpaRunnable;
    }

    @Override
    public void onEnable() {
        this.tpaRunnable.runTaskTimerAsynchronously(1L, 1L);
    }

    @Override
    public void onDisable(final boolean isShutdown) {
        this.tpaRunnable.cancel();
    }
}
