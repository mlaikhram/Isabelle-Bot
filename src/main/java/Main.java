import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import listener.IsabelleListener;
import model.YmlConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;

public class Main{

    public static void main(String[] args) throws LoginException, IOException, InterruptedException {
        File file = new File("bot.yml");
        YmlConfig config = new ObjectMapper(new YAMLFactory()).readValue(file, YmlConfig.class);

        JDA jda = JDABuilder.createDefault(config.getToken())
                .addEventListeners(new IsabelleListener((config)))
                .setActivity(Activity.watching("for task emotes"))
                .build();
        jda.awaitReady();
    }
}
