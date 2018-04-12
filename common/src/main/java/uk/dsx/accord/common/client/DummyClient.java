package uk.dsx.accord.common.client;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.dsx.accord.common.Client;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DummyClient implements Client<DummyClient> {

    private String user;

    private String privateKey;

    private String host;

    private int port;

    private JSch jSch;

    private Session session;

    public DummyClient(String user, String privatevKey, String host, int port) {
        this.user = user;
        this.privateKey = privatevKey;
        this.host = host;
        this.port = port;
    }

    @Override
    public DummyClient sendFile(String sourcePath, String targetPath) {
        System.out.println("SEND: from " + sourcePath + " to " + targetPath);
        return this;
    }

    @Override
    public DummyClient getFile(String sourcePath, String targetPath) {
        System.out.println("GET: from " + sourcePath + " to " + targetPath);
        return this;
    }

    @Override
    public InputStream getFile(String targetPath) {
        InputStream in = null;
        try {

            in = new FileInputStream(targetPath);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return in;
    }

    @Override
    public InputStream exec(String command) {
        InputStream in = null;
        System.out.println("EXEC: " + command);
        String logs = "\n\n\n\n\n ... \n\n\n Some logs \n\n\n ... \n\n\n\n\n ";
        if (command.matches(".*accord get enode .*")) {
            String enode = "enode://6f8a80d14311c39f35f516fa664deaaaa13e85b2f7493f37f6144d86991ec012937307647bd3b9a82abe2974e1407241d54947bbb39763a4cac9f77166ad92a0@10.3.58.6:30303?discport=30301";
            return new ByteArrayInputStream(enode.getBytes());
        }
        if (command.matches(".*accord get ports .*")) {
            String ports = "8545/tcp -> 0.0.0.0:4321\n" +
                    "30303/tcp -> 0.0.0.0:1234";
            return new ByteArrayInputStream(ports.getBytes());
        }
        in = new ByteArrayInputStream(logs.getBytes());
        return in;
    }

    @Override
    public DummyClient close() {
        return this;
    }

}
