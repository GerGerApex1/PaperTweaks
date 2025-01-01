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
package me.machinemaker.papertweaks.modules.survival.pillagertools;

import me.machinemaker.lectern.annotations.Description;
import me.machinemaker.lectern.annotations.Key;
import me.machinemaker.papertweaks.config.I18nKey;
import me.machinemaker.papertweaks.config.PTConfig;
import me.machinemaker.papertweaks.menus.Menu;
import me.machinemaker.papertweaks.modules.SimpleMenuModuleConfig;
import net.kyori.adventure.text.Component;

@Menu(commandPrefix = "/pillagertools admin config")
@PTConfig
class Config extends SimpleMenuModuleConfig<Config> {

    @Key("bad-omen")
    @I18nKey("modules.pillager-tools.settings.bad_omen")
    @Description("modules.pillager-tools.settings.bad_omen.extended")
    public boolean badOmen = true;

    @Key("patrol-leaders")
    @I18nKey("modules.pillager-tools.settings.patrol_leaders")
    @Description("modules.pillager-tools.settings.patrol_leaders.extended")
    public boolean patrolLeaders = true;

    @Key("patrols")
    @I18nKey("modules.pillager-tools.settings.patrols")
    @Description("modules.pillager-tools.settings.patrols.extended")
    public boolean patrols = true;

    boolean getSettingValue(final PillagerTools.ToggleOption option) {
        return switch (option) {
            case PATROLS -> this.patrols;
            case BAD_OMEN -> this.badOmen;
            case PATROL_LEADERS -> this.patrolLeaders;
        };
    }

    @Override
    public Component title() {
        return buildDefaultTitle("Pillager Tools");
    }
}
