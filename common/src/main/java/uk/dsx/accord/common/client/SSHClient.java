package uk.dsx.accord.common.client;

import com.jcraft.jsch.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.dsx.accord.common.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SSHClient implements Client {

    private String user;
    private String privateKey;
    private String host;
    private int port;

    private JSch jSch;
    private Session session;
    //TODO: what difference between exec and shell types
    private ChannelShell shell;
    private ChannelSftp sftp;


    public SSHClient(String user, String privatevKey, String host, int port) {
        this.user = user;
        this.privateKey = privatevKey;
        this.host = host;
        this.port = port;
    }

    //TODO: that should be connection factory
    @Override
    public SSHClient connect() {
        try {
            //TODO: think about we must ignore connection step if session.isconnected or throw smth
            if (session == null || !session.isConnected()) {
                session = setupSession();
                session.connect();

                shell = (ChannelShell) setupChannel("shell");
                sftp = (ChannelSftp) setupChannel("sftp");

                shell.connect();
                sftp.connect();
            }
        } catch (JSchException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not startup session");
        }
        return this;
    }

    //TODO: that methods must be available only after connection
    @Override
    public SSHClient send(String sourcePath, String targetPath) {
        try {
            sftp.put(sourcePath, targetPath);
        } catch (SftpException e) {
            throw new RuntimeException("Could not send file", e);
        }
        return this;
    }

    @Override
    public SSHClient get(String sourcePath, String targetPath) {
        try {
            sftp.get(sourcePath, targetPath);
        } catch (SftpException e) {
            throw new RuntimeException("Could not download file", e);
        }
        return this;
    }

    @Override
    public InputStream get(String targetPath) {
        try {
            return sftp.get(targetPath);
        } catch (SftpException e) {
            throw new RuntimeException("Could not download file", e);
        }
    }

    @Override
    public SSHClient exec(String command) {
        try {

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            InputStream in = channel.getInputStream();
            channel.setErrStream(System.err);
            channel.setCommand(command);
            channel.connect();

            StringBuilder message = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                message.append(line).append("\n");
            }
            channel.disconnect();
            while (!channel.isClosed()) {
            }
            System.out.println(message.toString());

//            PrintStream stream = new PrintStream(shell.getOutputStream());
//            stream.println(command);
//            stream.flush();
        } catch (IOException | JSchException e) {
            e.printStackTrace();
        }
        return this;
    }

    public SSHClient mkdir(String dir) {
        exec("mkdir -p " + dir);
        return this;
    }

    @Override
    public SSHClient close() {
        if (session.isConnected()) session.disconnect();
        if (sftp.isConnected()) sftp.disconnect();
        if (shell.isConnected()) shell.disconnect();
        return this;
    }

    public SSHClient reconnect() {
        close();
        connect();
        return this;
    }

    private Session setupSession() throws JSchException {
        JSch jSch = new JSch();
        jSch.addIdentity(privateKey);
        Session session = jSch.getSession(user, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setDaemonThread(true);
        return session;
    }

    private Channel setupChannel(String type) throws JSchException {
        if (session == null || !session.isConnected()) {
            throw new RuntimeException("Session is not initialized");
        }
        return session.openChannel(type);
    }
}
