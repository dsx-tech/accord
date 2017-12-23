#!/usr/bin/env bash
# INSTALL DOCKER AND GIVE PERMISSIONS
    sudo yum update -y
    sudo yum install -y docker
    sudo service docker start
#sudo groupadd docker
#sudo gpasswd -a $USER docker
#newgrp docker

# CREATE GENESIS AND RUN CONTAINER
#SHARED_DIR=shared_dir
#mkdir $SHARED_DIR

# NODES ARRAY (used for creating directories)
#NODES=(node0 node1)

#HARCODED genesis.json
#echo '{ "config": { "chainId": 497, "homesteadBlock": 10, "eip155Block": 0, "eip158Block": 0 }, "difficulty": "0x1000", "gasLimit": "0x4c4b40", "alloc": { } }' > $SHARED_DIR/genesis.json

#CREATE DIRS AND START CONTAINERS
#PORT=30300
#for NODE in ${NODES[@]}
#do
#    let "PORT++"
#    mkdir $SHARED_DIR/$NODE
#    cp $SHARED_DIR/genesis.json $SHARED_DIR/$NODE
#    docker run --name $NODE -idt -p $PORT:30303 -v $SHARED_DIR/$NODE':/node_dir/' chai0103/eth
#done

