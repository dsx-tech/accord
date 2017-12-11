package uk.dsx.accord.ethereum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.dsx.accord.ethereum.config.NodeType;

import java.nio.file.Path;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EthNode {

    private String name;

    private String parentInstance;

    private NodeType type;

    private List<Path> nodeFiles;

}
