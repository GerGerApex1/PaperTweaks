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
package me.machinemaker.papertweaks;

import com.google.inject.AbstractModule;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;
import me.machinemaker.lectern.BaseConfig;
import me.machinemaker.papertweaks.cloud.CloudModule;
import me.machinemaker.papertweaks.db.DatabaseModule;
import me.machinemaker.papertweaks.db.DatabaseType;
import me.machinemaker.papertweaks.integrations.Integrations;
import me.machinemaker.papertweaks.migrations.ModulesFileMigrations;
import me.machinemaker.papertweaks.modules.ModuleManager;
import me.machinemaker.papertweaks.modules.ModuleRegistry;
import me.machinemaker.papertweaks.modules.teleportation.homes.Homes;
import me.machinemaker.papertweaks.utils.PlayerMapFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;

import static me.machinemaker.papertweaks.adventure.Components.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;
import static net.kyori.adventure.text.format.NamedTextColor.*;

public class PaperTweaks extends JavaPlugin {

    public static final Component PLUGIN_PREFIX = text().append(text("[", DARK_GRAY)).append(text(LoggerFactory.GLOBAL_PREFIX, BLUE)).append(text("] ", DARK_GRAY)).build();
    public static final Logger LOGGER = LoggerFactory.getLogger();
    static final Set<Locale> SUPPORTED_LOCALES = Set.of(
        Locale.ENGLISH
    );
    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(0);
    public static boolean RAN_CONFIG_MIGRATIONS = false;
    private final Path dataPath = this.getDataFolder().toPath();
    private final Path modulesPath = this.dataPath.resolve("modules");
    private final Path i18nPath = this.dataPath.resolve("i18n");
    @Inject
    private ModuleManager moduleManager;
    @Inject
    private PaperTweaksMetrics metrics;
    private @MonotonicNonNull PaperTweaksConfig config;
    private @MonotonicNonNull Jdbi jdbi;

    @Override
    public void onEnable() {
        if (this.getDataFolder().exists()) {
            try {
                Files.move(this.getDataFolder().toPath(), this.dataPath);
            } catch (final IOException e) {
                throw new RuntimeException("Could not migrate from old VanillaTweaks folder. Move all files inside the plugins/VanillaTweaks folder to the plugins/PaperTweaks folder", e);
            }
        }
        this.getLogger().info("Thank you for using PaperTweaks/VanillaTweaks!");
        this.getLogger().info("If you have any issues, please visit one of the following links for support:");
        this.getLogger().info("  - https://discord.gg/invite/Np6Pcb78rr");
        this.getLogger().info("  - https://github.com/MC-Machinations/VanillaTweaks/issues");
        this.config = BaseConfig.create(PaperTweaksConfig.class, this.dataPath);
        this.jdbi = DatabaseType.installPlugins(this.config.database.type.createJdbiInstance(this.dataPath, this.config));
        Integrations.load();
        try (final Handle handle = this.jdbi.open()) {
            handle.execute(this.config.database.type.readSchema(this.getClassLoader()));
            LOGGER.info("You are using the " + this.config.database.type.name() + " database type.");
        } catch (final Exception exception) {
            LOGGER.error("Unable to create/load the database of type " + this.config.database.type.name());
            LOGGER.error("You could try select a different database type by changing the type in the config.yml");
            this.getServer().getPluginManager().disablePlugin(this);
            throw new RuntimeException("Could not create database and load schema", exception);
        }

        ModulesFileMigrations.apply(this.dataPath, this);
        try {
            this.migrateModuleConfigs();
        } catch (final IOException e) {
            e.printStackTrace();
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        I18n.create(this.i18nPath, this.getClassLoader()).setupI18n();

        final PlayerMapFactory mapFactory = new PlayerMapFactory();
        final Injector pluginInjector;
        try {
            pluginInjector = Guice.createInjector(new DatabaseModule(this.jdbi), new AbstractModule() {
                @Override
                protected void configure() {
                    this.bind(PaperTweaksConfig.class).toInstance(PaperTweaks.this.config);
                    this.bind(PaperTweaks.class).toInstance(PaperTweaks.this);
                    this.bind(JavaPlugin.class).toInstance(PaperTweaks.this);
                    this.bind(Plugin.class).toInstance(PaperTweaks.this);
                    this.bind(PlayerMapFactory.class).toInstance(mapFactory);
                    this.bind(Path.class).annotatedWith(Names.named("data")).toInstance(PaperTweaks.this.dataPath);
                    this.bind(Path.class).annotatedWith(Names.named("modules")).toInstance(PaperTweaks.this.modulesPath);
                    this.bind(Path.class).annotatedWith(Names.named("i18n")).toInstance(PaperTweaks.this.i18nPath);
                    this.bind(ClassLoader.class).annotatedWith(Names.named("plugin")).toInstance(PaperTweaks.this.getClassLoader());
                    this.bind(CommandSender.class).annotatedWith(Names.named("console")).toInstance(PaperTweaks.this.getServer().getConsoleSender());
                }
            }, new ModuleRegistry(this, PaperTweaks.this.dataPath), new CloudModule(this, EXECUTOR_SERVICE));
            pluginInjector.injectMembers(this);
        } catch (final CreationException e) {
            throw new RuntimeException("Could not create injector!", e);
        }

        this.getServer().getConsoleSender().sendMessage(join(PLUGIN_PREFIX, translatable("plugin-lifecycle.on-enable.loaded-modules", GOLD, text(this.moduleManager.loadModules(), GRAY))));
        this.getServer().getConsoleSender().sendMessage(join(PLUGIN_PREFIX, translatable("plugin-lifecycle.on-enable.enabled-modules", GREEN, text(this.moduleManager.enableModules(), GRAY))));

        pluginInjector.getInstance(RootCommand.class).registerCommands();
        this.getServer().getPluginManager().registerEvents(pluginInjector.getInstance(GlobalListener.class), this);
        this.getServer().getPluginManager().registerEvents(mapFactory, this);
    }

    @Override
    public void onDisable() {
        if (this.moduleManager != null) {
            this.moduleManager.disableModules(true);
        }
        EXECUTOR_SERVICE.shutdownNow();
    }

    private void migrateModuleConfigs() throws IOException {
        if (Files.notExists(this.modulesPath)) {
            RAN_CONFIG_MIGRATIONS = true;
            Files.createDirectories(this.modulesPath);
            LOGGER.info("Moving module configurations to their new location");
            try (final Stream<Path> files = Files.list(this.dataPath)) {
                for (final Path path : files.toList()) {
                    if (Files.isDirectory(path) && !Files.isSameFile(path, this.modulesPath)) {
                        Files.move(path, this.modulesPath.resolve(path.getFileName()));
                    }
                }
            }
        }

        String current = "none";
        try {
            current = "playergraves";
            if (Files.exists(this.modulesPath.resolve(current)) && !Files.exists(this.modulesPath.resolve("graves"))) {
                Files.move(this.modulesPath.resolve(current), this.modulesPath.resolve("graves"));
                LOGGER.info("Moved '{}' config to 'graves' folder", current);
            }
            current = "wrench";
            if (Files.exists(this.modulesPath.resolve(current)) && !Files.exists(this.modulesPath.resolve("wrenches"))) {
                Files.move(this.modulesPath.resolve(current), this.modulesPath.resolve("wrenches"));
                LOGGER.info("Moved '{}' config to 'wrenches' folder", current);
            }
            current = "sethome";
            if (Files.exists(this.modulesPath.resolve(current)) && !Files.exists(this.modulesPath.resolve("homes"))) {
                Files.move(this.modulesPath.resolve(current), this.modulesPath.resolve("homes"));
                LOGGER.info("Moved '{}' config to 'homes' folder", current);
            }
            current = "homes.yml";
            if (Files.exists(this.modulesPath.resolve("homes").resolve("homes.yml"))) {
                Homes.migrateHomesYmlConfig(this.jdbi, this.modulesPath.resolve("homes").resolve("homes.yml"));
                Files.deleteIfExists(this.modulesPath.resolve("homes").resolve("homes.yml"));
                LOGGER.info("Migrated '{}' config to h2 database", "homes/" + current);
            }
        } catch (final IOException e) {
            LOGGER.error("Failed to migrate {} configuration!", current, e);
        }
    }
}
