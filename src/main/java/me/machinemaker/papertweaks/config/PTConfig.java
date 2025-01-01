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
package me.machinemaker.papertweaks.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;
import me.machinemaker.lectern.ConfigurationNode;
import me.machinemaker.lectern.YamlConfiguration;
import me.machinemaker.lectern.annotations.Configuration;
import me.machinemaker.lectern.contexts.InvalidKeyHandler;
import me.machinemaker.lectern.supplier.ConfigurationSupplier;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Configuration(supplier = PTConfig.PaperTweaksConfigSupplier.class)
public @interface PTConfig {

    String fileName() default "config.yml";

    class PaperTweaksConfigSupplier implements ConfigurationSupplier<PTConfig> {
        @Override
        public ConfigurationNode createConfiguration(final Path parentDir, final PTConfig annotation) {
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER).enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR));
            Mixins.registerMixins(mapper);
            return YamlConfiguration.builder(parentDir.resolve(annotation.fileName())).withYamlMapper(mapper).withInvalidKeyHandler(InvalidKeyHandler.Preset.SILENT).build();
        }
    }
}
