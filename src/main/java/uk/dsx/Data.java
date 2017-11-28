package uk.dsx;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by alexander on 22.11.17.
 */
public class Data {

    @Getter
    private List<Path> sharedFiles;

    @Getter
    private List<String> commonDriverFiles;

    @Getter
    private List<String> rootDriverFiles;

    @Getter
    private List<Instance> machines;

    @JsonCreator
    public Data(@JsonProperty("sharedFiles") List<Path> sharedFiles,
                @JsonProperty("commonDriverFiles") List<String> commonDriverFiles,
                @JsonProperty("rootDriverFiles") List<String> rootDriverFiles,
                @JsonProperty("machines") List<Instance> machines) {
        Paths.get("")
        this.sharedFiles = sharedFiles;
        this.commonDriverFiles = commonDriverFiles;
        this.rootDriverFiles = rootDriverFiles;
        this.machines = machines;
    }

    @Override
    public String toString() {
        return "Data{" +
                "sharedFiles=" + sharedFiles +
                ", commonDriverFiles=" + commonDriverFiles +
                ", rootDriverFiles=" + rootDriverFiles +
                ", machines=" + machines +
                '}';
    }
}
