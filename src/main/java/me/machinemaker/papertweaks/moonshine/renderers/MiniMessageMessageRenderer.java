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
package me.machinemaker.papertweaks.moonshine.renderers;

import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.intellij.lang.annotations.Subst;

public class MiniMessageMessageRenderer extends AbstractMessageRenderer<String> {

    @Override
    protected Component render(final String intermediateMessage, final Map<String, ? extends Component> resolvedPlaceholders) {
        final TagResolver.Builder builder = TagResolver.builder();
        for (final Map.Entry<String, ? extends Component> entry : resolvedPlaceholders.entrySet()) {
            @Subst("some-key") final String key = entry.getKey();
            builder.resolver(Placeholder.component(key, entry.getValue()));
        }

        return MiniMessage.miniMessage().deserialize(intermediateMessage, builder.build());
    }
}
