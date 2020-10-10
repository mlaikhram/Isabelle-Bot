package util;

import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.MessageReaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUtils {

    public static final String ID_FORMAT = "<@%s>";
    public static final String ID_REGEX = "<@[0-9]+>";

    public static final String CUSTOM_EMOTE_FORMAT = "<%s:%s>";
    public static final String EMOTE_FORMAT = "<%s>";

    public static Long mentionToUserID(String mention) {
        return Long.parseLong(mention.replaceAll("[<@!>]", ""));
    }

    public static String userIDToMention(long id) {
        return String.format(ID_FORMAT, id);
    }

    public static boolean isUserMention(String mention) {
        return mention.replaceAll("!", "").matches(ID_REGEX);
    }

    public static Collection<Long> getMentionsFromText(String text) {
        Collection<Long> matches = new ArrayList<>();
        Matcher m = Pattern.compile(ID_REGEX).matcher(text.replaceAll("!", ""));
        while (m.find()) {
            matches.add(mentionToUserID(m.group()));
        }
        return matches;
    }

    public static final String HELP_TEXT =
            "@Rotom guess [from gen [start gen] [to [end gen]]] [for [guess time]]\n" +
            "Start a \"Who's that Pokemon?\" game session. I will select a Pokemon from [start gen] to [end gen] and reveal the answer in [guess time] seconds.\n" +
            "\n" +
            "@Rotom end\n" +
            "End the current \"Who's that Pokemon?\" game session.\n";
}
