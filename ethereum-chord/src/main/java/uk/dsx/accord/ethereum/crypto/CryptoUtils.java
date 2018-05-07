package uk.dsx.accord.ethereum.crypto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.RandomStringUtils;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.WalletUtils;
import uk.dsx.accord.ethereum.config.ChainConfig;
import uk.dsx.accord.ethereum.config.ChainConfig.Genesis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class CryptoUtils {

    public static void addAccountoGenesis(String address, String initialBalance, Genesis genesis) {
        ChainConfig.Alloc alloc = new ChainConfig.Alloc(initialBalance);
        genesis.getAlloc().put(address, alloc);
    }

    public static void addAccountoGenesis(Account account, Genesis genesis) {
        ChainConfig.Alloc alloc = new ChainConfig.Alloc(account.getInitialBalance());
        String address = account.getAddress();
        genesis.getAlloc().put(address, alloc);
    }

    public static void addAccountsToGenesis(List<Account> accounts, Genesis genesis) {
        accounts.forEach(account -> addAccountoGenesis(account, genesis));
    }


    public static List<Account> generateAccounts(int count, String initialBalance, String keyStorePath) {

        List<Account> accounts = IntStream.range(0, count)
                .mapToObj(i -> createAccount(keyStorePath)).collect(Collectors.toList());

        accounts.forEach(account -> account.setInitialBalance(initialBalance));

        return accounts;
    }

    public static Account createAccount(String keystorePath) {
        try {
            Files.createDirectories(Paths.get(keystorePath));

            String password = RandomStringUtils.random(16, true, true);
            String walletFileName = WalletUtils.generateFullNewWalletFile(password, new File(keystorePath));
            String address = extractAccountFromFileName(walletFileName);
            String walletFilePath = keystorePath + "/" + walletFileName;
            return Account.builder()
                    .address(address)
                    .password(password)
                    .keyFile(walletFilePath)
                    .build();

        } catch (NoSuchAlgorithmException | NoSuchProviderException | CipherException | InvalidAlgorithmParameterException | IOException e) {
            throw new RuntimeException("Could not create wallet", e);
        }
    }

    public static String extractAccountFromFileName(String walletFileName) {
        String[] fetchAddress = walletFileName.split("--");
        String address = fetchAddress[fetchAddress.length - 1].split("\\.")[0];
        return address;
    }

    public static Path createGenesisFile(Genesis genesis, String genesisDir) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            Path genesisDirPath = Paths.get(genesisDir);
            Files.createDirectories(genesisDirPath);

            Path genesisFilePath = genesisDirPath.resolve("genesis.json");
            Files.createFile(genesisFilePath);

            mapper.writerWithDefaultPrettyPrinter().writeValue(genesisFilePath.toFile(), genesis);

            return genesisFilePath;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
