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
package me.machinemaker.vanillatweaks.cloud.arguments;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.captions.CaptionVariable;
import cloud.commandframework.captions.StandardCaptionKeys;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import cloud.commandframework.exceptions.parsing.ParserException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiFunction;

public class PseudoEnumArgument<C> extends CommandArgument<C, String> {

    private final cloud.commandframework.arguments.standard.StringArgument.StringMode stringMode;

    protected PseudoEnumArgument(
            final boolean required,
            final @NonNull String name,
            final cloud.commandframework.arguments.standard.StringArgument.@NonNull StringMode stringMode,
            final @NonNull String defaultValue,
            final @NonNull Set<String> allowedValues,
            final @Nullable BiFunction<@NonNull CommandContext<C>, @NonNull String,
                    @NonNull List<@NonNull String>> suggestionsProvider,
            final @NonNull ArgumentDescription defaultDescription
    ) {
        super(
                required,
                name,
                new PseudoEnumParser<>(stringMode, allowedValues),
                defaultValue,
                String.class,
                suggestionsProvider,
                defaultDescription
        );
        this.stringMode = stringMode;
    }

    /**
     * Create a new builder
     *
     * @param name Name of the argument
     * @param allowedValues Allowed values
     * @param <C>  Command sender type
     * @return Created builder
     * @since 1.6.0
     */
    public static <C> PseudoEnumArgument.@NonNull Builder<C> builder(
            final @NonNull String name,
            final @NonNull Set<String> allowedValues
    ) {
        return new Builder<>(name, allowedValues);
    }

    /**
     * Create a new required single string command argument
     *
     * @param name Argument name
     * @param allowedValues Allowed values
     * @param <C>  Command sender type
     * @return Created argument
     * @since 1.6.0
     */
    public static <C> @NonNull CommandArgument<C, String> of(
            final @NonNull String name,
            final @NonNull Set<String> allowedValues
    ) {
        return PseudoEnumArgument.<C>builder(name, allowedValues).single().asRequired().build();
    }

    /**
     * Create a new required command argument
     *
     * @param name       Argument name
     * @param allowedValues Allowed values
     * @param stringMode String mode
     * @param <C>        Command sender type
     * @return Created argument
     * @since 1.6.0
     */
    public static <C> @NonNull CommandArgument<C, String> of(
            final @NonNull String name,
            final @NonNull Set<String> allowedValues,
            final cloud.commandframework.arguments.standard.StringArgument.@NonNull StringMode stringMode
    ) {
        return PseudoEnumArgument.<C>builder(name, allowedValues).withMode(stringMode).asRequired().build();
    }

    /**
     * Create a new optional single string command argument
     *
     * @param name Argument name
     * @param allowedValues Allowed values
     * @param <C>  Command sender type
     * @return Created argument
     * @since 1.6.0
     */
    public static <C> @NonNull CommandArgument<C, String> optional(
            final @NonNull String name,
            final @NonNull Set<String> allowedValues
    ) {
        return PseudoEnumArgument.<C>builder(name, allowedValues).single().asOptional().build();
    }

    /**
     * Create a new optional command argument
     *
     * @param name       Argument name
     * @param allowedValues Allowed values
     * @param stringMode String mode
     * @param <C>        Command sender type
     * @return Created argument
     * @since 1.6.0
     */
    public static <C> @NonNull CommandArgument<C, String> optional(
            final @NonNull String name,
            final @NonNull Set<String> allowedValues,
            final cloud.commandframework.arguments.standard.StringArgument.@NonNull StringMode stringMode
    ) {
        return PseudoEnumArgument.<C>builder(name, allowedValues).withMode(stringMode).asOptional().build();
    }

    /**
     * Create a new required command argument with a default value
     *
     * @param name          Argument name
     * @param allowedValues Allowed values
     * @param defaultString Default string
     * @param <C>           Command sender type
     * @return Created argument
     * @since 1.6.0
     */
    public static <C> @NonNull CommandArgument<C, String> optional(
            final @NonNull String name,
            final @NonNull Set<String> allowedValues,
            final @NonNull String defaultString
    ) {
        return PseudoEnumArgument.<C>builder(name, allowedValues).asOptionalWithDefault(defaultString).build();
    }

    /**
     * Create a new required command argument with the 'single' parsing mode
     *
     * @param name Argument name
     * @param allowedValues Allowed values
     * @param <C>  Command sender type
     * @return Created argument
     * @since 1.6.0
     */
    public static <C> @NonNull CommandArgument<C, String> single(
            final @NonNull String name,
            final @NonNull Set<String> allowedValues
    ) {
        return of(name, allowedValues, cloud.commandframework.arguments.standard.StringArgument.StringMode.SINGLE);
    }

    /**
     * Create a new required command argument with the 'greedy' parsing mode
     *
     * @param name Argument name
     * @param allowedValues Allowed values
     * @param <C>  Command sender type
     * @return Created argument
     * @since 1.6.0
     */
    public static <C> @NonNull CommandArgument<C, String> greedy(
            final @NonNull String name,
            final @NonNull Set<String> allowedValues
    ) {
        return of(name, allowedValues, cloud.commandframework.arguments.standard.StringArgument.StringMode.GREEDY);
    }

    /**
     * Create a new required command argument with the 'quoted' parsing mode
     *
     * @param name Argument name
     * @param allowedValues Allowed values
     * @param <C>  Command sender type
     * @return Created argument
     * @since 1.6.0
     */
    public static <C> @NonNull CommandArgument<C, String> quoted(
            final @NonNull String name,
            final @NonNull Set<String> allowedValues
    ) {
        return of(name, allowedValues, cloud.commandframework.arguments.standard.StringArgument.StringMode.QUOTED);
    }

    /**
     * Get the string mode
     *
     * @return String mode
     * @since 1.6.0
     */
    public cloud.commandframework.arguments.standard.StringArgument.@NonNull StringMode getStringMode() {
        return this.stringMode;
    }


    /**
     * Builder for {@link PseudoEnumArgument}.
     *
     *
     * @param <C> Command sender type
     * @since 1.6.0
     */
    public static final class Builder<C> extends CommandArgument.TypedBuilder<C, String, Builder<C>> {

        private cloud.commandframework.arguments.standard.StringArgument.StringMode stringMode = cloud.commandframework.arguments.standard.StringArgument.StringMode.SINGLE;
        private final Set<String> allowedValues;

        private Builder(final @NonNull String name, final Set<String> allowedValues) {
            super(String.class, name);
            this.allowedValues = allowedValues;
        }

        /**
         * Set the String mode
         *
         * @param stringMode String mode to parse with
         * @return Builder instance
         * @since 1.6.0
         */
        private @NonNull Builder<C> withMode(final cloud.commandframework.arguments.standard.StringArgument.@NonNull StringMode stringMode) {
            this.stringMode = stringMode;
            return this;
        }

        /**
         * Set the string mode to greedy
         *
         * @return Builder instance
         * @since 1.6.0
         */
        public @NonNull Builder<C> greedy() {
            this.stringMode = cloud.commandframework.arguments.standard.StringArgument.StringMode.GREEDY;
            return this;
        }

        /**
         * Set the string mode to single
         *
         * @return Builder instance
         * @since 1.6.0
         */
        public @NonNull Builder<C> single() {
            this.stringMode = cloud.commandframework.arguments.standard.StringArgument.StringMode.SINGLE;
            return this;
        }

        /**
         * Set the string mode to greedy
         *
         * @return Builder instance
         * @since 1.6.0
         */
        public @NonNull Builder<C> quoted() {
            this.stringMode = cloud.commandframework.arguments.standard.StringArgument.StringMode.QUOTED;
            return this;
        }

        /**
         * Builder a new string argument
         *
         * @return Constructed argument
         * @since 1.6.0
         */
        @Override
        public @NonNull PseudoEnumArgument<C> build() {
            return new PseudoEnumArgument<>(this.isRequired(), this.getName(), this.stringMode,
                    this.getDefaultValue(), this.allowedValues, this.getSuggestionsProvider(), this.getDefaultDescription()
            );
        }

    }


    /**
     * Parser for pseudo-enums.
     *
     * @param <C> Command sender type
     * @since 1.6.0
     */
    public static final class PseudoEnumParser<C> implements ArgumentParser<C, String> {

        private final Set<String> allowedValues;
        private final cloud.commandframework.arguments.standard.StringArgument.StringParser<C> stringParser;

        public PseudoEnumParser(
                final cloud.commandframework.arguments.standard.StringArgument.@NonNull StringMode stringMode,
                final @NonNull Set<String> allowedValues
        ) {
            this.stringParser = new cloud.commandframework.arguments.standard.StringArgument.StringParser<>(stringMode, (context, s) -> new ArrayList<>(allowedValues));
            this.allowedValues = allowedValues;
        }

        @Override
        public @NonNull ArgumentParseResult<@NonNull String> parse(
                @NonNull final CommandContext<@NonNull C> commandContext,
                @NonNull final Queue<@NonNull String> inputQueue
        ) {
            ArgumentParseResult<String> result = this.stringParser.parse(commandContext, inputQueue);
            if (result.getFailure().isPresent()) {
                return result;
            } else if (result.getParsedValue().isPresent()) {
                String input = result.getParsedValue().get();
                if (!allowedValues.contains(input)) {
                    return ArgumentParseResult.failure(new PseudoEnumParseException(input, this.allowedValues, commandContext));
                } else {
                    return result;
                }
            } else {
                return ArgumentParseResult.failure(new NoInputProvidedException(PseudoEnumParser.class, commandContext));
            }
        }

        @Override
        public @NonNull List<@NonNull String> suggestions(
                final @NonNull CommandContext<C> commandContext,
                final @NonNull String input
        ) {
            return this.stringParser.suggestions(commandContext, input);
        }

        @Override
        public boolean isContextFree() {
            return true;
        }

        /**
         * Get the string mode
         *
         * @return String mode
         * @since 1.6.0
         */
        public StringArgument.@NonNull StringMode getStringMode() {
            return this.stringParser.getStringMode();
        }
    }


    public static final class PseudoEnumParseException extends ParserException {

        private static final long serialVersionUID = 5198435213837796433L;
        private final String input;
        private final Set<String> acceptableValues;

        /**
         * Construct a new pseudo-enum parse exception
         *
         * @param input Input
         * @param acceptableValues Acceptable values
         * @param context Command context
         * @since 1.6.0
         */
        public PseudoEnumParseException(
                final @NonNull String input,
                final @NonNull Set<String> acceptableValues,
                final @NonNull CommandContext<?> context
        ) {
            super(
                    PseudoEnumParser.class,
                    context,
                    StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_ENUM,
                    CaptionVariable.of("input", input),
                    CaptionVariable.of("acceptableValues", String.join(", ", acceptableValues))
            );
            this.input = input;
            this.acceptableValues = Collections.unmodifiableSet(acceptableValues);
        }

        /**
         * Get the input provided by the sender
         *
         * @return Input
         * @since 1.6.0
         */
        public @NonNull String getInput() {
            return this.input;
        }

        /**
         * Get the acceptable values for this argument
         *
         * @return The acceptable values
         * @since 1.6.0
         */
        public @NonNull Set<String> getAcceptableValues() {
            return this.acceptableValues;
        }

    }

}
