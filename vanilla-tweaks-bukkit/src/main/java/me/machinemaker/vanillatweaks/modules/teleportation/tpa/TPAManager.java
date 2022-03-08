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
package me.machinemaker.vanillatweaks.modules.teleportation.tpa;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.machinemaker.vanillatweaks.cloud.cooldown.CommandCooldownManager;
import me.machinemaker.vanillatweaks.cloud.dispatchers.CommandDispatcher;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.event.ClickEvent.runCommand;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@Singleton
class TPAManager {

    private final BukkitAudiences audiences;
    private final CommandCooldownManager<CommandDispatcher, UUID> cooldownManager;
    final Map<UUID, Request> requestsBySender = new ConcurrentHashMap<>();
    final Multimap<UUID, Request> requestsByTarget = Multimaps.synchronizedSetMultimap(HashMultimap.create());

    @Inject
    TPAManager(BukkitAudiences audiences, CommandCooldownManager<CommandDispatcher, UUID> cooldownManager) {
        this.audiences = audiences;
        this.cooldownManager = cooldownManager;
    }

    public void startRequest(@NotNull Player from, @NotNull Player to) {
        if (this.requestsBySender.containsKey(from.getUniqueId())) {
            this.requestsBySender.get(from.getUniqueId()).playerTo().ifPresent(player -> {
                this.audiences.player(player).sendMessage(translatable("modules.tpa.teleport.fail.cancel", GOLD, text(player.getName(), YELLOW)));
            });
            this.requestsBySender.remove(from.getUniqueId());
        }

        final Audience fromAudience = this.audiences.player(from);
        final Audience toAudience = this.audiences.player(to);
        fromAudience.sendMessage(translatable("modules.tpa.teleport.request.sender", GOLD, text(to.getName(), YELLOW)));
        fromAudience.sendMessage(translatable("modules.tpa.teleport.request.sender.info", GOLD, text("/tpa cancel", YELLOW).hoverEvent(showText(translatable("modules.tpa.teleport.request.sender.info.hover", GOLD, text("/tpa cancel", YELLOW)))).clickEvent(runCommand("/tpa cancel"))));
        toAudience.sendMessage(translatable("modules.tpa.teleport.request.target", GOLD, text(from.getName(), YELLOW)));
        toAudience.sendMessage(translatable("modules.tpa.teleport.request.target.info.accept", GOLD, text("/tpa accept", YELLOW).hoverEvent(showText(translatable("modules.tpa.teleport.request.target.into.hover", GOLD, text("/tpa accept", YELLOW)))).clickEvent(runCommand("/tpa accept"))));
        toAudience.sendMessage(translatable("modules.tpa.teleport.request.target.info.deny", GOLD, text("/tpa deny", YELLOW).hoverEvent(showText(translatable("modules.tpa.teleport.request.target.into.hover", GOLD, text("/tpa deny", YELLOW)))).clickEvent(runCommand("/tpa deny"))));
        final Request request = new Request(from.getUniqueId(), to.getUniqueId(), System.currentTimeMillis() + (60 * 1000L));
        requestsBySender.put(from.getUniqueId(), request);
        requestsByTarget.put(to.getUniqueId(), request);
    }

    public void acceptRequest(@NotNull Player to, @NotNull Optional<Player> from) {
        if (this.requestsByTarget.containsKey(to.getUniqueId())) {
            final Optional<Request> request = getRequest(to, from);
            if (request != null) {
                request.ifPresentOrElse(req -> handleAcceptedRequest(to, req), () -> this.audiences.player(to).sendMessage(translatable("modules.tpa.commands.accept.fail.not-valid-request", RED, text(from.map(Player::getName).orElse("N/A")))));
            }
        } else {
            this.audiences.player(to).sendMessage(translatable("modules.tpa.commands.accept.fail.no-request", RED));
        }
    }

    public void denyRequest(@NotNull Player to, @NotNull Optional<Player> from) {
        if (this.requestsByTarget.containsKey(to.getUniqueId())) {
            final Optional<Request> request = getRequest(to, from);
            if (request != null) {
                request.ifPresentOrElse(req -> handleDeniedRequest(to, req), () -> this.audiences.player(to).sendMessage(translatable("modules.tpa.commands.deny.fail.not-valid-request", RED, text(from.map(Player::getName).orElse("N/A")))));
            }
        } else {
            this.audiences.player(to).sendMessage(translatable("modules.tpa.commands.deny.fail.no-request", RED));
        }
    }

    private @Nullable Optional<Request> getRequest(@NotNull Player to, @NotNull Optional<Player> from) {
        if (from.isEmpty()) {
            Collection<Request> requests = this.requestsByTarget.get(to.getUniqueId());
            if (requests.size() > 1) {
                this.audiences.player(to).sendMessage(translatable("modules.tpa.commands.request-response.fail.multiple", RED));
                return null;
            } else {
                return requests.stream().findAny();
            }
        } else {
            return this.requestsByTarget.get(to.getUniqueId()).stream().filter(r -> r.from().equals(from.get().getUniqueId())).findAny();
        }
    }

    private void handleAcceptedRequest(@NotNull Player to, @NotNull Request req) {
        req.complete();
        this.requestsBySender.remove(req.from());
        this.requestsByTarget.remove(to.getUniqueId(), req);
    }

    private void handleDeniedRequest(@NotNull Player to, @NotNull Request req) {
        req.playerFrom().ifPresent(player -> {
            this.audiences.player(player).sendMessage(translatable("modules.tpa.teleport.fail.denied", RED, text(to.getName())));
        });
        this.audiences.player(to).sendMessage(translatable("modules.tpa.commands.deny.success", GOLD, text(req.playerFrom().map(Player::getName).orElse("N/A"), YELLOW)));
        this.requestsBySender.remove(req.from());
        this.requestsByTarget.remove(to.getUniqueId(), req);
    }

    public void cancelRequestFrom(@NotNull Player from) {
        if (this.requestsBySender.containsKey(from.getUniqueId())) {
            this.cancelRequest(this.requestsBySender.get(from.getUniqueId()));
            this.requestsBySender.remove(from.getUniqueId());
        } else {
            this.audiences.player(from).sendMessage(translatable("modules.tpa.commands.cancel.fail.no-request", RED));
        }
    }

    public void cancelRequest(@NotNull Request request) {
        request.playerFrom().ifPresent(player -> {
            this.audiences.player(player).sendMessage(translatable("modules.tpa.teleport.fail.cancel", GOLD, text(player.getName(), YELLOW)));
        });
        // this.requestsBySender.remove(request.from()); // Removed in TPARunnable via the iterator
        this.requestsByTarget.remove(request.to(), request);
        this.cooldownManager.invalidate(request.from(), Commands.TPA_REQUEST_COOLDOWN_KEY);
    }
}
