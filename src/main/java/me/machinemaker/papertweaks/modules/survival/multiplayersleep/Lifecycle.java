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
package me.machinemaker.papertweaks.modules.survival.multiplayersleep;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.modules.ModuleRecipe;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

class Lifecycle extends ModuleLifecycle {

    static final Map<UUID, BossBar> BOSS_BARS = Maps.newHashMap();

    private final Config config;

    @Inject
    Lifecycle(final JavaPlugin plugin, final Set<ModuleCommand> commands, final Set<ModuleListener> listeners, final Set<ModuleConfig> configs, final Config config, final Set<ModuleRecipe<?>> moduleRecipes) {
        super(plugin, commands, listeners, configs, moduleRecipes);
        this.config = config;
    }

    @Override
    public void onEnable() {
        this.validateWorldList();
    }

    @Override
    public void onReload() {
        this.validateWorldList();
        this.resetSleepContexts(true);
    }

    @Override
    public void onDisable(final boolean isShutdown) {
        this.resetSleepContexts(false);
    }

    private void validateWorldList() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            BOSS_BARS.values().forEach(player::hideBossBar);
        });
        BOSS_BARS.clear();
        this.config.worlds(true).forEach(world -> {
            BOSS_BARS.put(world.getUID(), BossBar.bossBar(translatable("modules.multiplayer-sleep.display.boss-bar.title", text(0), text(0)), 0.0f, this.config.bossBarColor, BossBar.Overlay.PROGRESS));
        });

    }

    private void resetSleepContexts(final boolean kickOut) {
        MultiplayerSleep.SLEEP_CONTEXT_MAP.forEach((uuid, sleepContext) -> {
            sleepContext.reset(kickOut);
        });
        MultiplayerSleep.SLEEP_CONTEXT_MAP.clear();
    }
}
