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
package me.machinemaker.vanillatweaks.menus.config.types;

import me.machinemaker.lectern.ValueNode;
import me.machinemaker.vanillatweaks.menus.config.ConfigMenuOptionBuilder;
import me.machinemaker.vanillatweaks.menus.config.OptionBuilder;
import me.machinemaker.vanillatweaks.menus.options.EnumMenuOption;
import me.machinemaker.vanillatweaks.menus.options.SelectableEnumMenuOption;
import me.machinemaker.vanillatweaks.menus.options.MenuOption;
import me.machinemaker.vanillatweaks.menus.parts.enums.MenuEnum;
import me.machinemaker.vanillatweaks.modules.MenuModuleConfig;
import me.machinemaker.vanillatweaks.settings.types.ConfigSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class EnumOptionBuilderFactory implements OptionBuilder {

    @Override
    public <C extends MenuModuleConfig<C, ?>> MenuOption.@Nullable Builder<? extends Enum<?>, ?, C, ?> buildOption(@NotNull ValueNode<?> valueNode, @NotNull Map<String, ConfigSetting<?, C>> settings) {
        if (valueNode.type().isEnumImplType() && MenuEnum.class.isAssignableFrom(valueNode.type().getRawClass())) {
            return createMenuEnumOption(valueNode.type().getRawClass(), valueNode, settings);
        } else if (valueNode.type().isEnumImplType()) {
            return createEnumOption(valueNode.type().getRawClass(), valueNode, settings);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <E extends Enum<E> & MenuEnum<E>, C extends MenuModuleConfig<C, ?>> MenuOption.@NotNull Builder<E, ?, C, ?> createMenuEnumOption(Class<?> enumClass, @NotNull ValueNode<?> valueNode, @NotNull Map<String, ConfigSetting<?, C>> settings) {
        final var setting = ConfigSetting.<E, C>ofEnum(valueNode, (Class<E>) enumClass);
        settings.put(setting.indexKey(), setting);
        return SelectableEnumMenuOption.builder(setting.valueType(), ConfigMenuOptionBuilder.labelKey(valueNode), setting);
    }

    @SuppressWarnings("unchecked")
    private <E extends Enum<E>, C extends MenuModuleConfig<C, ?>> MenuOption.@NotNull Builder<E, ?, C, ?> createEnumOption(Class<?> enumClass, @NotNull ValueNode<?> valueNode, @NotNull Map<String, ConfigSetting<?, C>> settings) {
        final var setting = ConfigSetting.<E, C>ofEnum(valueNode, (Class<E>) enumClass);
        settings.put(setting.indexKey(), setting);
        final var builder = EnumMenuOption.builder(ConfigMenuOptionBuilder.labelKey(valueNode), setting);
        if (valueNode.meta().containsKey("desc")) {
            builder.extendedDescription(valueNode.meta().get("desc").toString());
        }
        return builder;
    }

}
