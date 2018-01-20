package uk.dsx.accord.ethereum;

import lombok.*;
import uk.dsx.accord.common.InstanceContainer;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class EthInstanceContainer implements InstanceContainer {

    @Singular
    List<EthInstance> instances;

}
