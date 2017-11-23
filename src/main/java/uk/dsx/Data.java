package uk.dsx;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import uk.dsx.driver.EnvironmentManager;
import uk.dsx.mapper.DataMapper;
import uk.dsx.node.ethereum.EthereumNode;

import java.util.List;

/**
 * Created by alexander on 22.11.17.
 */
@JsonDeserialize(using = DataMapper.class)
public class Data {
    @Setter @Getter
    private String hostIp;
    @Setter @Getter
    private List<String> sharedFiles;

    @Getter @Setter
    private EthereumNode<? extends EnvironmentManager> root;
    @Setter @Getter
    private List<EthereumNode<? extends EnvironmentManager>> common;

    @Override
    public String toString() {
        return "Data{" +
                "hostIp='" + hostIp + "\'\n" +
                "shared='" + sharedFiles + "\'\n" +
                ", root=" + root + "\'\n" +
                ", common=" + common +
                '}';
    }
}
