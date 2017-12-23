package uk.dsx.accord.ethereum;

import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode
@Builder
public class DockerCommand {
    String command;
    String name;
    String port;
    String volume;

    @Singular
    List<String> params;

    @Singular
    List<String> variables;

    String container;
    String entryPoint;
    String log_file;

    @Override
    public String toString() {
        String paramString = params.stream().collect(Collectors.joining(" ", " ", ""));
        String variablesString = variables.stream().collect(Collectors.joining(" -e ", " -e ", ""));
        String docker = "docker " + command
                + " --name " + name
                + paramString
                + " -v " + volume
                + " -p " + port
                + variablesString
                + " " + container
                + " " + entryPoint
                + " " + log_file;
        return docker.replace("  ", " ");
    }
}
