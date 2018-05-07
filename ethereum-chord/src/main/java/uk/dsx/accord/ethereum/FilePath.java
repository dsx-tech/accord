package uk.dsx.accord.ethereum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Paths;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilePath {

    String localPath;
    String remotePath;

    public String getRemotePath() {
        String fileName = Paths.get(localPath).getFileName().toString();
        return isNotBlank(remotePath) ? remotePath : fileName;
    }

    public static FilePath get(String localPath) {
        String fileName = Paths.get(localPath).getFileName().toString();
        return FilePath.builder()
                .localPath(localPath)
                .remotePath(fileName)
                .build();
    }

    public static FilePath get(String localPath, String remotePath) {
        return FilePath.builder()
                .localPath(localPath)
                .remotePath(remotePath)
                .build();
    }
}
