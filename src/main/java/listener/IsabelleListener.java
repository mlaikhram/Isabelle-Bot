package listener;

import model.TaskChannel;
import model.YmlConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.*;

public class IsabelleListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(IsabelleListener.class);
    private static final String TASK_MESSAGE_TEMPLATE = "%s %s%s";

    private List<Long> taskEmotes;
    private List<TaskChannel> taskChannels;


    public IsabelleListener(YmlConfig config) {
        taskEmotes = config.getTasks();
        taskChannels = config.getChannels();
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
            // if task emote added to regular message, create new task out of message
            if (!isTaskChannel(eventChannel.getIdLong()) && taskEmotes.contains(emote.getIdLong()) && !alreadyContainsTaskEmote(message.getReactions())) {
                TextChannel channel = guild.getTextChannelById(taskChannels.get(0).getId());
                if (channel != null) {
                    channel.sendMessage(createTaskMessage(emote, null, message.getContentRaw())).queue((sentMessage -> {
//                        sentMessage.addReaction(guild.getEmoteById(taskChannels.get(0).getEmote())).queue();
                        attachEmotes(guild, 0, sentMessage);
                    }));
                }
            }
            // else if progression emote added to task message, move to appropriate channel
            else if (isTaskChannel(eventChannel.getIdLong())) {
                boolean validEmote = false;
                for (int i = 0; i < taskChannels.size(); ++i) {
                    if (emote.getIdLong() == taskChannels.get(i).getEmote() && eventChannel.getIdLong() != taskChannels.get(i).getId()) {

                        // if it is in the to do channel, or if the task owner reacted, move to the appropriate channel
                        if (eventChannel.getIdLong() == taskChannels.get(0).getId() || message.getMentionedUsers().contains(user)) {
                            Emote taskEmote = message.getEmotes().get(0);
                            String taskText = getTaskText(message);
                            final int targetChannelIndex = i;
                            guild.getTextChannelById(taskChannels.get(i).getId()).sendMessage(createTaskMessage(taskEmote, i == 0 ? null : user, taskText)).queue((sentMessage) -> {
                               message.delete().queue();
                               attachEmotes(guild, targetChannelIndex, sentMessage);
//                               if (targetChannelIndex != taskChannels.size() - 1) {
//                                   for (int j = 0; j < taskChannels.size(); ++j) {
//                                       if (j != targetChannelIndex) {
//                                           sentMessage.addReaction(guild.getEmoteById(taskChannels.get(j).getEmote())).queue();
//                                       }
//                                   }
//                               }
                            });
                        }
                        validEmote = true;
                        break;
                    }
                }
                if (!validEmote) {
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
        return taskChannels.stream().anyMatch((channel) -> channel.getId() == id);
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

    private void attachEmotes(Guild guild, int targetChannelIndex, Message sentMessage) {
        if (targetChannelIndex != taskChannels.size() - 1) {
            for (int i = 0; i < taskChannels.size(); ++i) {
                if (i != targetChannelIndex) {
                    sentMessage.addReaction(guild.getEmoteById(taskChannels.get(i).getEmote())).queue();
                }
            }
        }
    }

    private String createTaskMessage(Emote taskEmote, User user, String message) {
        return String.format(TASK_MESSAGE_TEMPLATE, taskEmote.getAsMention(), user == null ? "" : (user.getAsMention() + " "), message);
    }

    private String getTaskText(Message message) {
        int splitSpaceCount = message.getMentionedUsers().isEmpty() ? 1 : 2;
        int splitIndex = -1;
        while (splitSpaceCount > 0) {
            splitIndex = message.getContentRaw().indexOf(" ", splitIndex + 1);
            --splitSpaceCount;
        }
        return message.getContentRaw().substring(splitIndex + 1);
    }
}
