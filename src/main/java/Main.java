import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.readString;
import static java.util.Objects.requireNonNull;

public class Main {
    public static void main(String[] args) {
        Gson gson = new Gson();
        Config conf;
        List<Group> grps = new ArrayList<>();
        Socket clientSocket;
        EmailPrankerRunner epr;

        try {
            conf = gson.fromJson(readString(Path.of(".\\config\\config.json")), Config.class);
            File groupFolder = new File(conf.group_path);
            File emailFolder = new File(conf.mail_path);
            for (final File file : requireNonNull(groupFolder.listFiles())) {
                grps.add(gson.fromJson(readString(Path.of(file.toURI())), Group.class));
            }
            List<File> emails = new ArrayList<>(List.of(requireNonNull(emailFolder.listFiles())));

            clientSocket = new Socket(conf.srv_ip, conf.srv_port);
            epr = new EmailPrankerRunner(clientSocket, grps, emails);

            epr.sendPrank();
        } catch (JsonSyntaxException jse) {
            System.out.println("Something went wrong when parsing JSON: " + jse.getMessage());
        } catch (IOException ioe) {
            System.out.println("An exception has raised: " + ioe.getMessage());
        }
    }
}
