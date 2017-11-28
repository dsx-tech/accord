package uk.dsx.driver;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jcraft.jsch.*;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by alexander on 22.11.17.
 */
public class SSHEnvironmentManager extends EnvironmentManager {
    private final static Logger logger = LogManager.getLogger(SSHEnvironmentManager.class);
    //TODO make good logPath
    private Path logPath = Paths.get("fileDirectory for logger");

    //TODO set via json DataFile
    @Setter
    protected String keyPath = "pathForKey/ethereum.pem";

    @JsonCreator
    public SSHEnvironmentManager(@JsonProperty("keyPath") String keyPath){
        this.keyPath = keyPath;
    }

    protected JSch jsch = new JSch();
    private Session session;
    private Channel channel;


    @Override
    public boolean executeCommands(List<String> commands, String userName, String ip) {
        //TODO use propertyfile for log name
        File logFile = logPath.resolve("log" + "_deploy.log").toFile();
        System.out.println(logFile.getPath());
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try (FileOutputStream logStream = new FileOutputStream(logPath.resolve("log" + "_deploy.log").toFile(), true)){
            Channel channel = getChannelWithType(userName, ip, "shell");
            logger.debug("Executing commands on: " + channel.getSession().getHost());
            channel.setOutputStream(logStream);
            PrintStream shellStream = new PrintStream(channel.getOutputStream());
            channel.connect();
            for (String command : commands) {
                logger.debug("Executing command on: " + channel.getSession().getHost());
                shellStream.println(command);
                shellStream.flush();
            }
            while(channel.isConnected())
            {
                logger.info("----- Executing commads... ----");
                Thread.sleep(10000);
                shellStream.println("exit");
                shellStream.flush();
            }
            return true;
        } catch (Exception e) {
            logger.error("some mistake");
            e.printStackTrace();
        }
        finally {
            channel.disconnect();
        }
        return false;
    }

    @Override
    public boolean uploadFiles(List<Path> files, String userName, String ip) {
        try (FileOutputStream logStream = new FileOutputStream(logPath.resolve("log" + "_deploy.log").toFile(), true)) {
            ChannelSftp channel = (ChannelSftp) getChannelWithType(userName, ip, "sftp");
            logger.error("Uploading files to: " + channel.getSession().getHost());
            channel.setOutputStream(logStream);
            channel.connect();
            files.forEach(f -> {
                try (FileInputStream fis = new FileInputStream(f.toFile())) {

                    channel.put(fis, "/environment/shared/genesys.json");
                    logger.error("File {} uploaded to: {}", f.getFileName().toString(), channel.getSession().getHost());
                } catch (Exception e) {
                    logger.error(e);
                }
            });
            return true;
        } catch (Exception e) {
            logger.error(e);
        }
        finally {
            channel.disconnect();
        }
        return false;
    }

    @Override
    public boolean uploadFile(Path from, Path to, String user, String ip) {
        try (FileOutputStream logStream = new FileOutputStream(logPath.resolve("log" + "_deploy.log").toFile(), true)) {
            ChannelSftp channel = (ChannelSftp) getChannelWithType(user, ip, "sftp");
            logger.error("Uploading files to: " + channel.getSession().getHost());
            channel.setOutputStream(logStream);
            channel.connect();
            try (FileInputStream fis = new FileInputStream(from.toFile())) {
                channel.put(fis, to.toString());
                logger.debug("File {} uploaded to: {}", from.getFileName().toString(), channel.getSession().getHost());
            } catch (SftpException e) {
                e.printStackTrace();
            }
            return true;
        }
        catch (JSchException e) {
            e.printStackTrace();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            channel.disconnect();
        }
        return false;
    }

    protected Channel getChannelWithType(String user, String ip, String type) throws JSchException {
        channel = getOrCreateSession(user, ip, keyPath).openChannel(type);
        return channel;
    }

    protected Session getOrCreateSession(String user, String ip, String keyPath) throws JSchException{
        if (session == null || !session.isConnected()) {
            logger.error(user);
            logger.error(ip);
            jsch.addIdentity(keyPath);
            session = jsch.getSession(user, ip, 22);
            System.out.println("SEssion created");
            session.setConfig("StrictHostKeyChecking", "no");
            session.setDaemonThread(true);
            session.connect();
            //System.out.println("connected");
        }
        return session;
    }

    @Override
    public void close() throws IOException {
        channel.disconnect();
        session.disconnect();
    }
}
