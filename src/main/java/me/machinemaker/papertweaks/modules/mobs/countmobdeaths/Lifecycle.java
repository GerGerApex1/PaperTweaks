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
package me.machinemaker.papertweaks.modules.mobs.countmobdeaths;

import com.google.inject.Inject;
import java.util.Set;
import me.machinemaker.papertweaks.modules.ModuleCommand;
import me.machinemaker.papertweaks.modules.ModuleConfig;
import me.machinemaker.papertweaks.modules.ModuleLifecycle;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.modules.ModuleRecipe;
import me.machinemaker.papertweaks.utils.boards.Scoreboards;
import org.bukkit.plugin.java.JavaPlugin;

import static net.kyori.adventure.text.Component.translatable;

final class Lifecycle extends ModuleLifecycle {

    private final CountMobDeaths countMobDeaths;

    @Inject
    Lifecycle(final JavaPlugin plugin, final Set<ModuleCommand> commands, final Set<ModuleListener> listeners, final Set<ModuleConfig> configs, final Set<ModuleRecipe<?>> moduleRecipes, final CountMobDeaths countMobDeaths) {
        super(plugin, commands, listeners, configs, moduleRecipes);
        this.countMobDeaths = countMobDeaths;
    }

    @Override
    public void onReload() {
        this.resetBoards("modules.mob-death-count.reload-msg");
    }

    @Override
    public void onDisable(final boolean isShutdown) {
        this.resetBoards("modules.mob-death-count.disabled-msg");
    }

    private void resetBoards(final String msg) {
        this.countMobDeaths.scoreboardPlayerMap.forEach((player, countingBoard) -> {
            if (player.getScoreboard() == countingBoard.scoreboard()) {
                player.setScoreboard(Scoreboards.main());
            }
            if (countingBoard.isCounting()) {
                player.sendMessage(translatable(msg));
            }
        });
        this.countMobDeaths.scoreboardPlayerMap.clear();
    }
}
