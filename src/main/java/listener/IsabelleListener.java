package listener;

import model.TaskChannel;
import model.YmlConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.MessageUtils;

import javax.annotation.Nonnull;
import java.util.*;

public class IsabelleListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(IsabelleListener.class);

//    private static final String NATIONAL_DEX_ENDPOINT = "/wiki/List_of_Pokemon_by_National_Pokedex_number";
//    private static final int DEFAULT_TIMER_SECONDS = 10;

//    private final String bulbapedia;

    // Used to keep track of which channels have an active "who's that pokemon?" challenge
//    private final Map<String, GuessPokemon> activeWTPChannels;

    private JDA jda;

//    private YmlConfig config;

    private static final String TASK_MESSAGE_TEMPLATE = "%s %s%s";

    private List<Long> taskEmotes;

    private TaskChannel toDo;
    private TaskChannel inProgress;
    private TaskChannel completed;


    public IsabelleListener(YmlConfig config) {
        taskEmotes = config.getTasks();

        toDo = config.getChannels().getToDo();
        inProgress = config.getChannels().getInProgress();
        completed = config.getChannels().getCompleted();
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
        TextChannel eventChannel = event.getTextChannel();
        Guild guild = event.getGuild();
        Message message = event.retrieveMessage().complete();
        User user = event.getUser();

        if (event.isFromType(ChannelType.TEXT) && user != null && !user.isBot() && event.getReactionEmote().isEmote()) {
            logger.info("retrieving message from " + event.getChannel().getName());
            Emote emote = event.getReactionEmote().getEmote();

            logger.info("emote: " + emote);
            // if task emote added to regular message
            if (!isTaskChannel(eventChannel.getIdLong()) && taskEmotes.contains(emote.getIdLong()) && !alreadyContainsTaskEmote(message.getReactions())) {
                TextChannel channel = guild.getTextChannelById(toDo.getId());
                if (channel != null) {
                    channel.sendMessage(createTaskMessage(emote, null, message.getContentRaw())).queue((sentMessage -> {
                        sentMessage.addReaction(guild.getEmoteById(toDo.getEmote())).queue();
                    }));
                }
            }
            // else if progression emote added to task message
            else if (isTaskChannel(eventChannel.getIdLong())) {
                // set to to do
                if (emote.getIdLong() == toDo.getEmote() && eventChannel.getIdLong() != toDo.getId()) {

                }
                // set to in progress
                else if (emote.getIdLong() == inProgress.getEmote() && eventChannel.getIdLong() != inProgress.getId()) {

                }
                // set to complete
                else if (emote.getIdLong() == completed.getEmote() && eventChannel.getIdLong() != completed.getId()) {

                }
                else {
                    logger.info("removing emote");
                    message.removeReaction(emote, user).queue();
                }
            }
        }
        else if (user != null && !user.isBot() && isTaskChannel(eventChannel.getIdLong())) {
            logger.info("removing emoji");
            message.removeReaction(event.getReactionEmote().getEmoji(), user).queue();
        }
    }

    private boolean isTaskChannel(long id) {
        return id == toDo.getId() || id == inProgress.getId() || id == completed.getId();
    }

    private boolean alreadyContainsTaskEmote(List<MessageReaction> reactions) {
        int taskEmoteCount = reactions.stream().map((reaction) -> {
            if (reaction.getReactionEmote().isEmote() && taskEmotes.contains(reaction.getReactionEmote().getEmote().getIdLong())) {
                return reaction.getCount();
            }
            else {
                return 0;
            }
        }).reduce(0, Integer::sum);
        logger.info("total task reacts: " + taskEmoteCount);
        return taskEmoteCount > 1;
    }

    private String createTaskMessage(Emote taskEmote, User user, String message) {
        return String.format(TASK_MESSAGE_TEMPLATE, taskEmote.getAsMention(), user == null ? "" : (user.getAsMention() + " "), message);
    }
}
