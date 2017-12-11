package uk.dsx.accord.common;

import java.util.List;

public interface Instance {

    void start();

    void prepare();

    void run();

    void clean();

    void terminate();

    void addCommand(String command);

    void addCommands(List<String> commands);

    void exec();

    void uploadFiles(String path, List<String> files);

    List<String> downloadFiles(List<String> path);

}