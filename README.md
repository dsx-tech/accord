# Accord
Blockchains unified API library and network initialisation module

only Ethereum available

###Dependency
Add to build.gradle
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    compile 'com.github.dsx-tech:accord:master-SNAPSHOT'
}
```

###How to use
* Up instances on aws cloud
* Write or copy init.sh script for initialising your chain or use default
* Create .yml config and configure it (set paths to files, ip, ports, nodes, users fingerprint)
```yaml
instances:
  - name: ec-2-one
    user: ec2-user
    ip: ip
    port : 22
    fingerprint: path_to_fingerprint_file.pem
    prepare-env-commands:
      - sudo yum update -y
      - sudo yum install -y docker
      - sudo service docker start
      - sudo groupadd docker
      - sudo gpasswd -a $USER docker
      - sudo usermod -aG docker $USER
    # Files that not-shared (maybe need for env initialization)
    instance-files:
      - ethereum-chord/src/main/resources/ethereum/init.sh
      - ethereum-chord/src/main/resources/ethereum/genesis.json
    nodes:
      - name: boot
        port: 30304
        rpc-port: 8101
        type: ETH

      - name: first
        port: 30305
        rpc-port: 8102
        type: ETH

      - name: second
        port: 30306
        rpc-port: 8103
        type: ETH
```
* Use deploy api like
```java
        EthInstanceManager manager = new EthInstanceManager();
        manager.withConfig("/home/andrey/IdeaProjects/accord/ethereum-chord/src/main/resources/ethereum/machine.yaml", DefaultConfiguration.class);
        manager.run();
        manager.terminate();
```
* Getting ip and ports for rpc api
```java
        manager.getAdresses();
```
