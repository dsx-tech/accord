package uk.dsx.client;

import com.jcraft.jsch.*;
import uk.dsx.Client;

import java.io.IOException;
import java.io.PrintStream;

public class SSHClient implements Client {

    private String user;
    private String prvKey;
    private String host;
    private int port;

    private JSch jSch;
    private Session session;
    //TODO: what difference between exec and shell types
    private ChannelShell shell;
    private ChannelSftp sftp;


    public SSHClient(String user, String password, String host, int port) {
        this.user = user;
        this.prvKey = password;
        this.host = host;
        this.port = port;
    }

    //TODO: that should be connection factory
    @Override
    public SSHClient connect() throws InstantiationException {
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
            throw new InstantiationException("Could not startup session");
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
            sftp.chmod(777, targetPath);
            sftp.get(sourcePath, targetPath);
        } catch (SftpException e) {
            throw new RuntimeException("Could not download file", e);
        }
        return null;
    }

    @Override
    public SSHClient exec(String command) {
        try {
            PrintStream stream = new PrintStream(shell.getOutputStream());
            stream.println(command);
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public SSHClient close() {
        if (session.isConnected()) session.disconnect();
        if (sftp.isConnected()) sftp.disconnect();
        if (shell.isConnected()) shell.disconnect();
        return this;
    }

    private Session setupSession() throws JSchException {
        JSch jSch = new JSch();
        jSch.addIdentity(prvKey);
        Session session = jSch.getSession(user, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setDaemonThread(true);
        return session;
    }

    private Channel setupChannel(String type) throws InstantiationException, JSchException {
        if (session == null || !session.isConnected()) {
            throw new InstantiationException("Session is not initialized");
        }
        return session.openChannel(type);
    }
}
