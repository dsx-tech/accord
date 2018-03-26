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
    //    String port;
    String volume;

    @Singular
    List<String> params;

    @Singular
    List<String> ports;

    @Singular
    List<String> variables;

    String container;
    String entryPoint;
    String log_file;

    @Override
    public String toString() {
        String paramString = params.stream().collect(Collectors.joining(" ", " ", ""));
        String variablesString = variables.stream().collect(Collectors.joining(" -e ", " -e ", ""));
        String portsString = ports.stream().collect(Collectors.joining(" -p ", " -p ", ""));
        String docker = "docker " + command
                + " --name " + name
                + paramString
                + " -v " + volume
                + portsString
//                + " -p " + port
                + variablesString
                + " " + container
                + " " + entryPoint
                + " " + log_file;
        return docker.replace("  ", " ");
    }
}
