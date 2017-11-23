package uk.dsx.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.dsx.Data;
import uk.dsx.driver.EnvironmentManager;
import uk.dsx.driver.SSHEnvironmentManager;
import uk.dsx.node.ethereum.EthereumNode;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alexander on 23.11.17.
 */
//TODO botleneck, need more abstract mapper
public class DataMapper extends JsonDeserializer<Data> {

    public DataMapper(){
    }

    @Override
    public Data deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
        Data data = new Data();
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        data.setHostIp(node.get("hostIp").asText());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode sharedFiles = node.findValue("sharedFiles");
        List<String> shared = mapper.convertValue(sharedFiles, new TypeReference<List<String>>() {});
        data.setSharedFiles(shared);

        JsonNode rootNode = node.findValue("root");
        data.setRoot(deserealizeNode(rootNode));

        JsonNode commonNodes = node.findValue("common");
        List<JsonNode> common = mapper.convertValue(commonNodes, new TypeReference<List<JsonNode>>() {});
        data.setCommon(common.stream().map(x -> deserealizeNode(x)).collect(Collectors.toList()));
        return data;
    }

    protected EthereumNode<? extends EnvironmentManager> deserealizeNode(JsonNode jsonNode){
        String ip = jsonNode.get("ip").asText();
        EthereumNode node;
        if (ip == "172.0.0.1" || ip == "localhost") {
            //TODO use localEnvManager
            node = new EthereumNode<EnvironmentManager>();
        }
        else{
            node = new EthereumNode<SSHEnvironmentManager>();
            SSHEnvironmentManager manager = new SSHEnvironmentManager();
            manager.setKeyPath(jsonNode.get("keyPath").asText());
            node.setManager(manager);
        }
        node.setIp(ip);
        node.setUser(jsonNode.get("user").asText());
        node.setDockerName(jsonNode.get("user").asText());
        return node;
    }
}
