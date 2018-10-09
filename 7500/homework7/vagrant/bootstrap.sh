#!/bin/bash

set -eu -o pipefail

DEBIAN_FRONTEND=noninteractive add-apt-repository -y ppa:ethereum/ethereum
DEBIAN_FRONTEND=noninteractive apt-get update -y
DEBIAN_FRONTEND=noninteractive curl -sL https://deb.nodesource.com/setup_6.x | bash -
DEBIAN_FRONTEND=noninteractive apt-get install -y git nodejs python2.7 make g++ python-pip solc
DEBIAN_FRONTEND=noninteractive pip install --upgrade pip

npm config set python python2.7

npm -g install truffle ganache-cli

# done!
echo
echo 'The homework 7 vagrant instance has been provisioned.'
echo "Use 'vagrant ssh' to open a terminal, 'vagrant suspend' to stop the instance, and 'vagrant destroy' to remove it."
