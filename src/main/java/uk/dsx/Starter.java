package uk.dsx;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by alexander on 22.11.17.
 */
public class Starter {
    public static void main(String[] args) throws IOException {
        String json = "{\n" +
                "\"sharedFiles\": [\"pathForJson/Genesis.json\"],\n" +
                "\"commonDriverFiles\": [\"pathForJson/Genesis.json\"],\n" +
                "\"rootDriverFiles\": [\"pathForJson/Genesis.json\"],\n" +
                "\"machines\": [{\"user\": \"awsUser\", \"ip\": \"awsIp\",\"keyPath\": \"/pathForkey\", \"nodes\": [{\"dockerName\": \"common\", \"type\": \"eth\"}] }]}";
        final ObjectMapper mapper = new ObjectMapper();

        final Data readValue = mapper.readValue(json, Data.class);
        System.out.println(readValue);
    }
}
