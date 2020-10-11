# Isabelle-Bot
Discord Bot that allows for quick and easy task management Kanban style

### How to Install
You will need Gradle to run the Bot. Clone this repository, and run the following Gradle command (I use Intellij to open and run the project):
```
gradle clean jar
```
This will build the .jar file into the `target` folder. Before running the .jar file, you will need to create a file called `bot.yml` and place it in the same directory as the .jar file. `bot.yml` should contain the following lines:
```
token: <BOT_TOKEN>
channels:
  - id: <id of the to-do channel>
    emote: <custom emote id associated with to-do tasks>
    
  - id: <id of an intermediary channel (i.e. in-progress)>
    emote: <custom emote id associated with this channel's tasks>
    
  - id: <id of the completed channel>
    emote: <custom emote id associated with completed tasks>
    
tasks:
  - <custom emote id associated with a task type (i.e. coding task)>
  - <another custom emote id associated with a task type>
```
Replace `<BOT_TOKEN>` with the token obtained from your own Discord Bot (More details on creating a Discord Bot can be found [here](https://discord.com/developers/docs/intro)).
The `channels` section will contain all information relevant to the task channels, which will be used to separate your tasks by status. the first channel will always be the channel that tasks start in, and the last channel will be where tasks are considered complete.
The `tasks` list will contain custom emotes that represent types of task users are allowed to make. If a user reacts to a message with one of those emotes, it will automatically turn that message into a task and place it in the `to-do` channel.
Tasks can be moved between channels by reacting to them with the appropriate status update emote. Note that once a task is moved out of `to-do`, only the task owner (the user who moved the task out of `to-do`) will be able to update the task status, unless it is once again moved into the `to-do` channel.
You may have as many channels and tasks added to the .yml file as you need, as long as the first and last channels are associated with `to-do` and `completed` respectively.

Once this is set up, you can run the .jar file using the following command:
```
java -jar isabelle-bot-1.0-SNAPSHOT.jar
```
Once the Bot is running, you can invite it to your Discord Server from the [Discord Developer Portal](https://discord.com/developers/applications) and interact with it from your text channels.

### Usage
Create custom emotes on your Discord server, and use those emotes to configure the .yml file as described above. When someone sends a message in a channel that could be a task, react to that message with an emote from the `tasks` list. That message will become a task in the `to-do` channel. Navigate to that channel, and select one of the pre-added reaction emotes to take that task and update its status. You will become that task's owner until it is moved back into `to-do` or `completed`.
