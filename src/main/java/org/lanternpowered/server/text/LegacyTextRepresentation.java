package org.lanternpowered.server.text;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.TextRepresentation;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.TextMessageException;

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class LegacyTextRepresentation implements TextRepresentation {

    private static final BiMap<Object, Character> FORMATS = ImmutableBiMap.<Object, Character>builder()
            .put(TextColors.BLACK, '0')
            .put(TextColors.DARK_BLUE, '1')
            .put(TextColors.DARK_GREEN, '2')
            .put(TextColors.DARK_AQUA, '3')
            .put(TextColors.DARK_RED, '4')
            .put(TextColors.DARK_PURPLE, '5')
            .put(TextColors.GOLD, '6')
            .put(TextColors.GRAY, '7')
            .put(TextColors.DARK_GRAY, '8')
            .put(TextColors.BLUE, '9')
            .put(TextColors.GREEN, 'a')
            .put(TextColors.AQUA, 'b')
            .put(TextColors.RED, 'c')
            .put(TextColors.LIGHT_PURPLE, 'd')
            .put(TextColors.YELLOW, 'e')
            .put(TextColors.WHITE, 'f')
            .put(TextColors.RESET, 'r')
            .put(TextStyles.OBFUSCATED, 'k')
            .put(TextStyles.BOLD, 'l')
            .put(TextStyles.STRIKETHROUGH, 'm')
            .put(TextStyles.UNDERLINE, 'n')
            .put(TextStyles.ITALIC, 'o')
            .build();

    private static boolean isFormat(char format) {
        boolean flag = FORMATS.containsValue(format);
        if (!flag) {
            flag = FORMATS.containsValue(Character.toLowerCase(format));
        }
        return flag;
    }

    @Nullable
    private static Object getFormat(char format) {
        Object obj = FORMATS.inverse().get(format);
        if (obj == null) {
            obj = FORMATS.inverse().get(Character.toLowerCase(format));
        }
        return obj;
    }

    private final char legacyChar;

    public LegacyTextRepresentation(char colorCode) {
        this.legacyChar = colorCode;
    }

    @Override
    public String to(Text text) {
        return this.to(text, Locale.ENGLISH);
    }

    @Override
    public String to(Text text, Locale locale) {
        return to(checkNotNull(text, "text"), checkNotNull(locale, "locale"), new StringBuilder(), this.legacyChar).toString();
    }

    @Override
    public Text from(String input) throws TextMessageException {
        return this.fromUnchecked(input);
    }

    @Override
    public Text fromUnchecked(String input) {
        checkNotNull(input, "input");

        int next = input.lastIndexOf(this.legacyChar, input.length() - 2);
        if (next == -1) {
            return Texts.of(input);
        }

        List<Text> parts = Lists.newArrayList();

        TextBuilder.Literal current = null;
        boolean reset = false;

        int pos = input.length();
        do {
            Object format = getFormat(input.charAt(next + 1));
            if (format != null) {
                int from = next + 2;
                if (from != pos) {
                    if (current != null) {
                        if (reset) {
                            parts.add(current.build());
                            reset = false;
                            current = Texts.builder("");
                        } else {
                            current = Texts.builder("").append(current.build());
                        }
                    } else {
                        current = Texts.builder("");
                    }

                    current.content(input.substring(from, pos));
                } else if (current == null) {
                    current = Texts.builder("");
                }

                reset |= applyStyle(current, format);
                pos = next;
            }

            next = input.lastIndexOf(this.legacyChar, next - 1);
        } while (next != -1);

        if (current != null) {
            parts.add(current.build());
        }

        Collections.reverse(parts);
        return Texts.builder(pos > 0 ? input.substring(0, pos) : "").append(parts).build();
    }

    private static boolean applyStyle(TextBuilder builder, Object format) {
        if (format instanceof TextStyle) {
            builder.style((TextStyle) format);
            return false;
        } else if (format == TextColors.RESET) {
            return true;
        } else {
            if (builder.getColor() == TextColors.NONE) {
                builder.color((TextColor) format);
            }
            return true;
        }
    }

    static StringBuilder to(Text text, Locale locale, StringBuilder builder, @Nullable Character colorCode) {
        return to(text, locale, builder, colorCode, null);
    }

    private static StringBuilder to(Text text, Locale locale, StringBuilder builder, @Nullable Character colorCode,
            @Nullable ResolvedChatStyle current) {
        ResolvedChatStyle style = null;

        if (colorCode != null) {
            style = resolve(current, text.getFormat());

            if (current == null || (current.color != style.color) || (current.bold && !style.bold) ||
                    (current.italic && !style.italic) || (current.underlined && !style.underlined) ||
                    (current.strikethrough && !style.strikethrough) || (current.obfuscated && !style.obfuscated)) {
                if (style.color != null) {
                    apply(builder, colorCode, FORMATS.get(style.color));
                } else if (current != null) {
                    apply(builder, colorCode, FORMATS.get(TextColors.RESET));
                }

                apply(builder, colorCode, FORMATS.get(TextStyles.BOLD), style.bold);
                apply(builder, colorCode, FORMATS.get(TextStyles.ITALIC), style.italic);
                apply(builder, colorCode, FORMATS.get(TextStyles.UNDERLINE), style.underlined);
                apply(builder, colorCode, FORMATS.get(TextStyles.STRIKETHROUGH), style.strikethrough);
                apply(builder, colorCode, FORMATS.get(TextStyles.OBFUSCATED), style.obfuscated);
            } else {
                apply(builder, colorCode, FORMATS.get(TextStyles.BOLD), current.bold != style.bold);
                apply(builder, colorCode, FORMATS.get(TextStyles.ITALIC), current.italic != style.italic);
                apply(builder, colorCode, FORMATS.get(TextStyles.UNDERLINE), current.underlined != style.underlined);
                apply(builder, colorCode, FORMATS.get(TextStyles.STRIKETHROUGH), current.strikethrough != style.strikethrough);
                apply(builder, colorCode, FORMATS.get(TextStyles.OBFUSCATED), current.obfuscated != style.obfuscated);
            }
        }

        if (text instanceof Text.Literal) {
            builder.append(((Text.Literal) text).getContent());
        } else if (text instanceof Text.Selector) {
            builder.append(((Text.Selector) text).getSelector().toPlain());
        } else if (text instanceof Text.Translatable) {
            Text.Translatable text0 = (Text.Translatable) text;

            Translation translation = text0.getTranslation();
            ImmutableList<Object> args = text0.getArguments();

            builder.append(translation.get(locale, args.toArray(new Object[] {})));
        } else if (text instanceof Text.Placeholder) {
            Text.Placeholder text0 = (Text.Placeholder) text;

            Optional<Text> fallback = text0.getFallback();
            if (fallback.isPresent()) {
                to(fallback.get(), locale, builder, colorCode);
            } else {
                builder.append(text0.getKey());
            }
        } else if (text instanceof Text.Score) {
            Text.Score text0 = (Text.Score) text;

            Optional<String> override = text0.getOverride();
            if (override.isPresent()) {
                builder.append(override.get());
            } else {
                builder.append(text0.getScore().getScore());
            }
        }

        for (Text child : text.getChildren()) {
            to(child, locale, builder, colorCode, style);
        }

        return builder;
    }

    private static void apply(StringBuilder builder, char code, char formattingCode) {
        builder.append(code).append(formattingCode);
    }
    
    private static void apply(StringBuilder builder, char code, char formattingCode, boolean state) {
        if (state) {
            apply(builder, code, formattingCode);
        }
    }

    private static ResolvedChatStyle resolve(@Nullable ResolvedChatStyle current, TextFormat format) {
        TextColor color = format.getColor();
        TextStyle style = format.getStyle();
        if (current == null) {
            if (color == TextColors.NONE) {
                color = null;
            }
            return new ResolvedChatStyle(color, style.isBold().or(false), style.isItalic().or(false),
                    style.hasUnderline().or(false), style.hasStrikethrough().or(false), style.isObfuscated().or(false));
        }
        if (color == TextColors.NONE) {
            color = current.color;
        }
        return new ResolvedChatStyle(color, style.isBold().or(current.bold), style.isItalic().or(current.italic),
                style.hasUnderline().or(current.underlined), style.hasStrikethrough().or(current.strikethrough),
                style.isObfuscated().or(current.obfuscated));
    }

    private static class ResolvedChatStyle {

        @Nullable
        public final TextColor color;
        public final boolean bold;
        public final boolean italic;
        public final boolean underlined;
        public final boolean strikethrough;
        public final boolean obfuscated;

        public ResolvedChatStyle(@Nullable TextColor color, boolean bold, boolean italic,
                boolean underlined, boolean strikethrough, boolean obfuscated) {
            this.color = color;
            this.bold = bold;
            this.italic = italic;
            this.underlined = underlined;
            this.strikethrough = strikethrough;
            this.obfuscated = obfuscated;
        }
    }

    static String replace(String text, char from, char to) {
        int pos = text.indexOf(from);
        int last = text.length() - 1;
        if (pos == -1 || pos == last) {
            return text;
        }

        char[] result = text.toCharArray();
        for (; pos < last; pos++) {
            if (result[pos] == from && isFormat(result[pos + 1])) {
                result[pos] = to;
            }
        }

        return new String(result);
    }

    static String strip(String text, char code) {
        return strip(text, code, false);
    }

    static String strip(String text, char code, boolean all) {
        int next = text.indexOf(code);
        int last = text.length() - 1;
        if (next == -1 || next == last) {
            return text;
        }

        StringBuilder result = new StringBuilder(text.length());

        int pos = 0;
        do {
            if (pos != next) {
                result.append(text, pos, next);
            }

            pos = next;

            if (isFormat(text.charAt(next + 1))) {
                pos = next += 2; // Skip formatting
            } else if (all) {
                pos = next += 1; // Skip code only
            } else {
                next++;
            }

            next = text.indexOf(code, next);
        } while (next != -1 && next < last);

        return result.append(text, pos, text.length()).toString();
    }

}