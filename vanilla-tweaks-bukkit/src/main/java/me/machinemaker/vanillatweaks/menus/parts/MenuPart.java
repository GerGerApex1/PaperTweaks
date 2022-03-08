/*
 * GNU General Public License v3
 *
 * PaperTweaks, a performant replacement for the VanillaTweaks datapacks.
 *
 * Copyright (C) 2021 Machine_Maker
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
package me.machinemaker.vanillatweaks.menus.parts;

import me.machinemaker.vanillatweaks.menus.BuildablePart;
import me.machinemaker.vanillatweaks.menus.MergedMenus;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface MenuPart<S> extends MenuPartLike<S> {

    @NotNull Component build(@NotNull S object, @NotNull String commandPrefix);

    @Override
    default @NotNull MenuPart<S> asMenuPart() {
        return this;
    }

    /**
     * For use with {@link MergedMenus.Menu1}s.
     * @param <S>
     * @see MenuPart#configure(String)
     */
    class Configured<S> implements BuildablePart<S> {

        private final MenuPart<S> part;
        private final String commandPrefix;

        Configured(MenuPart<S> part, String commandPrefix) {
            this.part = part;
            this.commandPrefix = commandPrefix;
        }

        @Override
        public @NotNull Component build(@NotNull S object) {
            return this.part.build(object, this.commandPrefix);
        }
    }
}
