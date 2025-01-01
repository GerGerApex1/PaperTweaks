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
package me.machinemaker.papertweaks.utils.boards;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Scoreboards {

    private Scoreboards() {
    }

    public static ScoreboardManager manager() {
        return Bukkit.getScoreboardManager();
    }

    public static Scoreboard main() {
        return manager().getMainScoreboard();
    }

    public static Team getTeam(final String name, final NamedTextColor color) {
        @Nullable Team team = main().getTeam(name);
        if (team == null) {
            team = main().registerNewTeam(name);
        }
        team.color(color);
        return team;
    }

    public static Objective getDummyObjective(final String name, final Component displayName) {
        @Nullable Objective objective = main().getObjective(name);
        if (objective == null) {
            objective = main().registerNewObjective(name, Criteria.DUMMY, displayName);
        }
        return objective;
    }
}
