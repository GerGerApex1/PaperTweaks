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
package me.machinemaker.papertweaks.menus.parts;

import net.kyori.adventure.text.Component;

import static net.kyori.adventure.text.Component.text;

public record LabelLine<S>(Component label) implements MenuPart<S> {

    public static <S> LabelLine<S> of(final String label) {
        return new LabelLine<>(text(label.endsWith("\n") ? label : label + "\n"));
    }

    @Override
    public Component build(final S object, final String commandPrefix) {
        return this.label;
    }

}
