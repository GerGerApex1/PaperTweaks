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
package me.machinemaker.papertweaks.moonshine.resolvers.simple;

import java.util.function.Function;
import net.kyori.adventure.text.TextComponent;

import static net.kyori.adventure.text.Component.text;

public class NumberPlaceholderResolver<N extends Number> extends SimplePlaceholderResolver<N> {

    private final Function<N, String> toStringFunction;

    public NumberPlaceholderResolver(final Function<N, String> toStringFunction) {
        this.toStringFunction = toStringFunction;
    }

    @Override
    public TextComponent.Builder toComponent(final N value) {
        return text().content(this.toStringFunction.apply(value));
    }
}
