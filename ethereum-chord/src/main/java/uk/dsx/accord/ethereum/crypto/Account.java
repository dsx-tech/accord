package uk.dsx.accord.ethereum.crypto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {

    String address;
    String password;
    String keyFile;
    String initialBalance = "0";


}
