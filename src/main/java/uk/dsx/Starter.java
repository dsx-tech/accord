package uk.dsx;

import com.fasterxml.jackson.databind.ObjectMapper;
import uk.dsx.node.ethereum.EthereumNode;

import java.io.IOException;

/**
 * Created by alexander on 22.11.17.
 */
public class Starter {
    public static void main(String[] args) throws IOException {
        String json = "{ \"hostIp\" : \"someIp\",\n" +
                "\"sharedFiles\": [\"pathForJson/Genesis.json\"],\n" +
                "\"root\" : {\"user\": \"awsUser\", \"ip\": \"awsIp\", \"dockerName\": \"root_node\", \"keyPath\": \"path\"},\n" +
                "\"common\": [{\"user\": \"awsUser\", \"ip\": \"awsIp\", \"dockerName\": \"common_node\", \"keyPath\": \"path\"}]}";
        final ObjectMapper mapper = new ObjectMapper();

        final Data readValue = mapper.readValue(json, Data.class);
        System.out.println(readValue);
        EthereumNode root_node = readValue.getRoot();
    }
}
