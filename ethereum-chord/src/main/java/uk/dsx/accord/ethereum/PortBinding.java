package uk.dsx.accord.ethereum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author abulgako
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PortBinding {

    String hostPort;

    String exposedPort;

    String protocol = "tcp";

}
