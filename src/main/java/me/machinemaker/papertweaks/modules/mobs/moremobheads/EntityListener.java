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
package me.machinemaker.papertweaks.modules.mobs.moremobheads;

import com.google.inject.Inject;
import java.util.Collection;
import me.machinemaker.papertweaks.modules.ModuleListener;
import me.machinemaker.papertweaks.utils.PTUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.checkerframework.checker.nullness.qual.Nullable;

class EntityListener implements ModuleListener {

    private final MoreMobHeads moreMobHeads;
    private final Config config;

    @Inject
    EntityListener(final MoreMobHeads moreMobHeads, final Config config) {
        this.moreMobHeads = moreMobHeads;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDeath(final EntityDeathEvent event) {
        final LivingEntity entity = event.getEntity();
        final @Nullable Player killer = entity.getKiller();
        if (this.config.requirePlayerKill && killer == null) {
            return;
        }
        if (killer != null && !killer.hasPermission("vanillatweaks.moremobheads")) {
            return;
        }

        if (event.getEntity() instanceof Wither) { // Special handling for withers for the moment
            event.getDrops().add(PTUtils.random(this.moreMobHeads.getMobHeads(EntityType.WITHER)).createSkull());
            return;
        }

        int lootingLevel = 0;
        if (event.getEntity().getKiller() != null) {
            lootingLevel = event.getEntity().getKiller().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.FORTUNE);
        }

        final Collection<MobHead> heads = this.moreMobHeads.getMobHeads(entity.getType());
        if (heads.isEmpty()) {
            return;
        }
        for (final MobHead head : heads) {
            if (head.test(event.getEntity())) {
                if (head.chance(lootingLevel)) {
                    event.getDrops().add(head.createSkull());
                }
                break;
            }
        }
    }
}
