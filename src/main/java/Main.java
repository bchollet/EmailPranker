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
        // Variables
        Gson gson = new Gson();
        Config conf;
        List<Group> grps = new ArrayList<>();
        Socket clientSocket = null;
        EmailPrankerRunner epr;

        try {
            //Parsing json config files
            conf = gson.fromJson(readString(Path.of(".\\config\\config.json")), Config.class);
            File groupFolder = new File(conf.group_path);
            File emailFolder = new File(conf.mail_path);
            for (final File file : requireNonNull(groupFolder.listFiles())) {
                grps.add(gson.fromJson(readString(Path.of(file.toURI())), Group.class));
            }
            List<File> emails = new ArrayList<>(List.of(requireNonNull(emailFolder.listFiles())));

            //Connecting to server
            clientSocket = new Socket(conf.srv_ip, conf.srv_port);

            //Running Pranker
            epr = new EmailPrankerRunner(clientSocket, grps, emails, conf.ehlo_msg);
            epr.sendPrank();

        }
        //Exception handling
        catch (JsonSyntaxException jse) {
            System.out.println("ERROR when parsing JSON: " + jse.getMessage());
        } catch (IOException ioe) {
            System.out.println("ERROR IO: " + ioe);
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            } catch (IOException ex) {
                System.out.println("Something went wrong : " + ex);
            }
        }
    }
}
