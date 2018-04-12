# Accord
Blockchains unified API library and network initialisation module

only Ethereum available

### Dependency

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

### How to use

* Up instances on aws cloud
* Write or copy init.sh script for initialising your chain or use default
* Create .yml config and configure it (set paths to files, ip, ports, nodes, users fingerprint)
```yaml
instances:
  - name: ec-2-one
    user: ec2-user
    ip: -===ip==-
    port : 22
    fingerprint: ethereum-chord/src/main/resources/ethereum/eth-new.pem
    # Default is ./shared_dir
    working-dir: /home/ec2-user/shared_dir
    prepare-env-commands:
      - sudo yum update -y
      - sudo yum install -y docker git
      - sudo service docker start
      - sudo groupadd docker
      - sudo gpasswd -a $USER docker
      - sudo usermod -aG docker $USER
      
    # Upload into working-dir/
    instance-files:
      - ethereum-chord/src/main/resources/ethereum/genesis_ignore.json
      
    
    post-init-commands:
      - echo ./shared_dir/genesis_ignore.json
    post-init-files:
      - ethereum-chord/src/main/resources/ethereum/genesis_ignore.json

    nodes:
      - name: first
        type: observer
        peers:
          - second

        #If no peers set then discovery=all
      - name: second
        port: 30305
        rpc-port: 8102
        type: miner

      - name: third
        port: 30306
        rpc-port: 8103
        type: observer

  - name: ec-2-two
    user: ec2-user
    ip: -===ip==-
    port : 22
    fingerprint: ethereum-chord/src/main/resources/ethereum/eth-new.pem
    prepare-env-commands:
      - sudo yum update -y
      - sudo yum install -y docker git
      - sudo service docker start
      - sudo groupadd docker
      - sudo gpasswd -a $USER docker
      - sudo usermod -aG docker $USER
    nodes:
      - name: first2
        type: miner
        peers:
          - second

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
