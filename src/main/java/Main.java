import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import listener.IsabelleListener;
import model.YmlConfig;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Main{

    public static void main(String[] args) throws LoginException, IOException {
        File file = new File("bot.yml");
        YmlConfig config = new ObjectMapper(new YAMLFactory()).readValue(file, YmlConfig.class);

        JDABuilder builder = new JDABuilder(AccountType.BOT);
        IsabelleListener isabelleListener = new IsabelleListener(config);
        builder.setToken(config.getToken());
        builder.addEventListeners(isabelleListener);
        JDA jda = builder.build();
        isabelleListener.setJDA(jda);
        jda.getPresence().setActivity(Activity.watching("@Isabelle help"));
    }
}
