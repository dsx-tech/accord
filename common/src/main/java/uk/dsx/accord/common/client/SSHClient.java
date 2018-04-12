package uk.dsx.accord.common.client;

import com.jcraft.jsch.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.dsx.accord.common.Client;

import java.io.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SSHClient implements Client<SSHClient> {

    private String user;

    private String privateKey;

    private String host;

    private int port;

    private JSch jSch;

    private Session session;

    public SSHClient(String user, String privatevKey, String host, int port) {
        this.user = user;
        this.privateKey = privatevKey;
        this.host = host;
        this.port = port;
    }

    //TODO: that methods must be available only after connection
    @Override
    public SSHClient sendFile(String sourcePath, String targetPath) {
        try {
            connect();
            ChannelSftp sftp = (ChannelSftp) openChannel("sftp");
            sftp.connect();
            sftp.put(sourcePath, targetPath);
            sftp.disconnect();
        } catch (JSchException | SftpException e) {
            throw new RuntimeException("Could not send file", e);
        }
        return this;
    }

    @Override
    public SSHClient getFile(String sourcePath, String targetPath) {
        try {
            connect();
            ChannelSftp sftp = (ChannelSftp) openChannel("sftp");
            sftp.connect();
            sftp.get(sourcePath, targetPath);
            sftp.disconnect();
        } catch (JSchException | SftpException e) {
            throw new RuntimeException("Could not download file", e);
        }
        return this;
    }

    @Override
    public InputStream getFile(String targetPath) {
        try {
            connect();
            ChannelSftp sftp = (ChannelSftp) openChannel("sftp");
            sftp.connect();
            InputStream out = sftp.get(targetPath);
            sftp.disconnect();
            return out;
        } catch (JSchException | SftpException e) {
            throw new RuntimeException("Could not download file", e);
        }
    }

    @Override
    public InputStream exec(String command) {
        InputStream out = null;
        try {
            connect();
            System.out.println("Host " + host + " connected.");
            System.out.println("EXEC " + host + " command:" + command);
            ChannelExec channel = (ChannelExec) openChannel("exec");
            out = channel.getInputStream();
//            channel.setErrStream(System.err);
            channel.setCommand(command);
            channel.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(out));
            StringBuilder logs = new StringBuilder();
            String line = null;
            while (!channel.isEOF()) {
            }
            while ((line = reader.readLine()) != null) {
                logs.append(line).append("\n");
            }
            while (!channel.isClosed()) {
            }
            System.out.println(logs.toString());
            out = new ByteArrayInputStream(logs.toString().getBytes());
            channel.disconnect();
        } catch (IOException | JSchException e) {
            e.printStackTrace();
        }
        return out;
    }

    private void connect() {
        try {
            if (session == null || !session.isConnected()) {
                session = setupSession();
                session.connect();
            }
        } catch (JSchException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not startup session");
        }
    }

    public SSHClient close() {
        if (session.isConnected())
            session.disconnect();

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

    private Channel openChannel(String type) throws JSchException {
        if (session == null || !session.isConnected()) {
            throw new RuntimeException("Session is not initialized");
        }
        return session.openChannel(type);
    }

    private void wrapSession(Command command) {
        try {
            connect();
            command.run();
            close();
        } catch (Exception e) {
            throw new RuntimeException("Could not do ssh operation.", e);
        }
    }

    @FunctionalInterface
    private interface Command {
        void run() throws Exception;
    }
}
