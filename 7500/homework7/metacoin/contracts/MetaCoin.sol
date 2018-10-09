pragma solidity ^0.4.4;

contract MetaCoin {

	mapping (address => uint) balances;
	address owner;

	event Transfer(address indexed _from, address indexed _to, uint256 _value);

	function MetaCoin() public {
		balances[msg.sender] = 10000;
		owner = msg.sender;
	}

	function sendCoin(address receiver, uint amount) public returns(bool sufficient) {
		if (balances[msg.sender] < amount) return false;
		balances[msg.sender] -= amount;
		balances[receiver] += amount;
		emit Transfer(msg.sender, receiver, amount);
		return true;
	}

	function getBalance(address addr) public returns(uint) {
		return balances[addr];
	}

	function mint(uint amount) public {
		if(owner != msg.sender) return;
		balances[msg.sender] += amount;
	}
}
