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
package me.machinemaker.papertweaks.cloud.dispatchers;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Base class for representing command senders.
 */
public abstract class CommandDispatcher implements ForwardingAudience.Single {

    private final CommandSourceStack sourceStack;

    protected CommandDispatcher(final CommandSourceStack sourceStack) {
        this.sourceStack = sourceStack;
    }

    public CommandSourceStack sourceStack() {
        return this.sourceStack;
    }

    /**
     * The Bukkit command sender.
     *
     * @return the Bukkit command sender
     */
    public CommandSender sender() {
        return this.sourceStack.getExecutor() != null ? this.sourceStack.getExecutor() : this.sourceStack.getSender();
    }

    @Override
    public Audience audience() {
        return this.sender();
    }

    public boolean isPlayer() {
        return this instanceof PlayerCommandDispatcher;
    }

    public abstract @Nullable UUID getUUID();

    public abstract Locale locale();

    public boolean hasPermission(final String permission) {
        return this.sender().hasPermission(permission);
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final CommandDispatcher that = (CommandDispatcher) o;
        return this.sourceStack.equals(that.sourceStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.sourceStack);
    }
}
