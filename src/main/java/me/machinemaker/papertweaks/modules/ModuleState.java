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
package me.machinemaker.papertweaks.modules;

public enum ModuleState {

    ENABLED(true, false),
    DISABLED(false, false),
    ENABLED_FAILED(false, true),
    DISABLE_FAILED(false, true),
    RELOAD_FAILED(true, true);

    private final boolean running;
    private final boolean error;

    ModuleState(final boolean running, final boolean error) {
        this.running = running;
        this.error = error;
    }

    public boolean isRunning() {
        return this.running;
    }

    public boolean isErrored() {
        return this.error;
    }
}
