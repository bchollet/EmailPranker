import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class EmailPrankerRunner {
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private final List<Group> grps;
    private final List<File> emails;
    private final String ehloMsg;
    private final BufferedWriter out;
    private final BufferedReader in;

    /**
     * default constructor for EmailPranker
     * @param clientSocket The client socket
     * @param grps The list of the differents victim groups
     * @param emails The list of pranked emails
     * @param ehloMsg The domain send to SMTP server on EHLO command
     * @throws IOException When getting an incorrect in/output stream from client socket
     */
    public EmailPrankerRunner(Socket clientSocket, List<Group> grps, List<File> emails, String ehloMsg) throws IOException {
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        this.grps = grps;
        this.emails = emails;
        this.ehloMsg = ehloMsg;
    }

    /**
     * Use to send prank based on attribute sent to constructor
     */
    public void sendPrank() {
        try {
            readSrvResponse(StatusCodes.READY);
            ehloPhase();
            System.out.println("EHLO success");
            for (Group grp : grps) {
                if(grp.mail_to.length < 2){
                    System.out.println("Group skipped, too few recipient email");
                    continue;
                }
                mailFromPhase(grp);
                System.out.println("MAIL success");
                rcptToPhase(grp);
                System.out.println("RCPT success");
                dataPhase(grp);
                System.out.println("DATA success");
            }
            disconnectPhase();
            System.out.println("Prank success");
        } catch (IOException ioe) {
            System.out.println("Prank failed. Exception raised: " + ioe);
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException ex) {
                System.out.println("Something went wrong while closing BufferedWriter: " + ex);
            }
            try {
                if (in != null) in.close();
            } catch (IOException ex) {
                System.out.println("Something went wrong while closing BufferedReader: " + ex);
            }
        }
    }

    /**
     * EHLO command in SMTP protocol
     * @throws IOException On IO exception and on server response Invalid (SMTP status code > 400)
     */
    private void ehloPhase() throws IOException {
        out.write("EHLO " + ehloMsg + "\r\n");
        out.flush();

        for (int i = 0; i < 3; i++) {
            readSrvResponse(StatusCodes.OK);
        }
    }

    /**
     * MAIL FROM command in SMTP protocol
     * @param grp The group where the sender is from
     * @throws IOException On IO exception and on server response Invalid (SMTP status code > 400)
     */
    private void mailFromPhase(Group grp) throws IOException {
        if (!EMAIL_REGEX.matcher(grp.mail_from).find()) {
            throw new IOException(grp.mail_from + " is not a valid email address");
        }

        out.write("MAIL FROM:<" + grp.mail_from + ">\r\n");
        out.flush();

        readSrvResponse(StatusCodes.OK);
    }

    /**
     * RCPT TO command in SMTP protocol
     * @param grp The group where the victims are from
     * @throws IOException On IO exception and on server response Invalid (SMTP status code > 400)
     */
    private void rcptToPhase(Group grp) throws IOException {
        for(String mt : grp.mail_to){
            if(!EMAIL_REGEX.matcher(mt).find()){
                throw new IOException(mt + " is not a valid email address");
            }

            out.write("RCPT TO:<" + mt + "> \r\n");
            out.flush();

            readSrvResponse(StatusCodes.OK);
        }
    }

    /**
     * DATA command in SMTP protocol
     * @param grp The group that is concerned by the command
     * @throws IOException On IO exception and on server response Invalid (SMTP status code > 400)
     */
    private void dataPhase(Group grp) throws IOException {
        Random rand = new Random();
        String subject;
        File randomMail = emails.get(rand.nextInt(emails.size()));

        try (BufferedReader br = new BufferedReader(new FileReader(randomMail))) {
            subject = br.readLine();
        }

        out.write("DATA\r\nContent-Type: text/plain; charset=utf-8\r\n");
        out.flush();
        readSrvResponse(StatusCodes.DATA);
        out.write("From:" + grp.mail_from + "\r\n"
                + "To:" + Arrays.toString(grp.mail_to).replace("[","").replace("]","") + "\r\n"
                + "Subject:=?utf-8?B?" + Base64.getEncoder().encodeToString(subject.getBytes()) + "?=\r\n");
        out.write(Files.readString(Path.of(randomMail.toURI())));
        out.write("\r\n.\r\n");
        out.flush();

        readSrvResponse(StatusCodes.OK);
    }

    /**
     * Disconnect from the SMTP server
     * @throws IOException On IO exception and on server response Invalid (SMTP status code > 400)
     */
    private void disconnectPhase() throws IOException {
        out.write("quit\r\n");
        out.flush();

        readSrvResponse(StatusCodes.BYE);
    }

    /**
     * Read server response
     * @param expectedStatus The SMTP status code expected on the server response
     * @throws IOException On IO exception and on server response Invalid (SMTP status code > 400)
     */
    private void readSrvResponse(StatusCodes expectedStatus) throws IOException {
        String srv_response = in.readLine();
        if(!srv_response.startsWith(expectedStatus.value)){
            throw new RuntimeException(srv_response);
        }
    }
}
