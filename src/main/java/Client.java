import java.io.*;

import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Client {

    public static void main(String[] args){
        Gson gson = new Gson();

        final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = null;

        //TODO : mettre au bon endroit pour que les throw soient traités
        //TODO : c'est dégeu
        Config conf = null;
        Group grp = null;

        Socket clientSocket = null;
        BufferedWriter out = null;
        BufferedReader in = null;

        try {
            //TODO : jsp si ça doit aller là
            conf = gson.fromJson(Files.readString(Path.of(".\\config\\config.json")), Config.class);
            grp = gson.fromJson(Files.readString(Path.of(conf.group_path)), Group.class);

            clientSocket = new Socket(conf.srv_ip, conf.srv_port);
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String srv_response = "";

            //Get server greetings
            //TODO : Factoriser
            srv_response = in.readLine();
            if (!srv_response.startsWith("220")) {
                throw new IOException(srv_response);
            }

            System.out.println("Connexion success");

            //EHLO
            out.write("EHLO heig-vd.ch\r\n");
            out.flush();

            //TODO : Factoriser
            srv_response = in.readLine();
            srv_response = in.readLine();
            srv_response = in.readLine();
            if(!srv_response.startsWith("250")){
                throw new IOException(srv_response);
            }


            System.out.println("EHLO success");

            //MAIL FROM
            //TODO : factoriser
            matcher = EMAIL_REGEX.matcher(grp.mail_from);
            if(!matcher.find()){
                throw new IOException("Bad from email format");
            }

            out.write("MAIL FROM:<" + grp.mail_from + ">\r\n");
            out.flush();

            //TODO : factoriser
            srv_response = in.readLine();
            if(!srv_response.startsWith("250")){
                throw new IOException(srv_response);
            }

            System.out.println("Mail from success");

            //RCPT TO
            for(String mt : grp.mail_to){
                //TODO : factoriser
                matcher = EMAIL_REGEX.matcher(mt);
                if(!matcher.find()){
                    throw new IOException("Bad to email format");
                }
                out.write("RCPT TO:<" + mt + "> \r\n");
                out.flush();
                srv_response = in.readLine();
                if(!srv_response.startsWith("250")){
                    throw new IOException(srv_response);
                }
            }

            System.out.println("RCPT to success");


            //TODO : Rajouter les TO: dans la partie data

            //DATA
            out.write("DATA\r\nContent-Type: text/plain; charset=utf-8\r\n");
            out.flush();
            out.write(Files.readString(Path.of(conf.mail_path)));
            out.flush();
            out.write("\r\n.\r\n");
            out.flush();


            //TODO : factoriser
            srv_response = in.readLine();
            srv_response = in.readLine();
            if(!srv_response.startsWith("250")){
                throw new IOException(srv_response);
            }

            System.out.println("Data write success");

            out.write("quit\r\n");
            out.flush();

            //TODO : factoriser
            srv_response = in.readLine();
            if(!srv_response.startsWith("221")){
                throw new IOException(srv_response);
            }

            System.out.println("Quit success");

            //TODO : Des messages d'erreur moins pire ?
        } catch (IOException ex) {
            System.out.println("Something went wrong : " + ex.toString());
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException ex) {
                System.out.println("Something went wrong : " + ex.toString());
            }
            try {
                if (in != null) in.close();
            } catch (IOException ex) {
                System.out.println("Something went wrong : " + ex.toString());
            }
            try {
                if (clientSocket != null && ! clientSocket.isClosed()) clientSocket.close();
            } catch (IOException ex) {
                System.out.println("Something went wrong : " + ex.toString());
            }
        }

    }


}
