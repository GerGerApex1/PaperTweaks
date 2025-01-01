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
package me.machinemaker.papertweaks.integrations;

import java.util.LinkedHashSet;
import java.util.Set;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public final class Interactions {

    private static final Set<Handler> HANDLERS = new LinkedHashSet<>();

    private Interactions() {
    }

    public static void registerHandler(final Handler handler) {
        HANDLERS.add(handler);
    }

    public static boolean isAllowedInteraction(final Player player, final Block clickedBlock) {
        return HANDLERS.stream().allMatch(handler -> handler.checkBlock(player, clickedBlock));
    }

    @FunctionalInterface
    public interface Handler {

        boolean checkBlock(Player player, Block clickedBlock);
    }
}
