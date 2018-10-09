pragma solidity ^0.4.9;

contract DutchAuction {
    uint reservePrice;
    address judge;
    uint openBlocks;
    uint priceDecrement;

    address owner;
    mapping (address => uint) public bidprices;
    uint initPrice;
    uint startBlock;

    uint currPrice;
    bool auctionEnded;
    address bidWinner;
    mapping (address => uint) public balances;
    bool itemReceived;
    bool isFinalized;
    

    function DutchAuction(uint256 _reservePrice, address _judgeAddress, uint256 _numBlocksAuctionOpen, uint256 _offerPriceDecrement) public {
        reservePrice = _reservePrice;
        judge = _judgeAddress;
        openBlocks = _numBlocksAuctionOpen;
        priceDecrement = _offerPriceDecrement;

        owner = msg.sender;
        initPrice = _reservePrice + _numBlocksAuctionOpen * _offerPriceDecrement;
        startBlock = block.number;
    }

    function bid() public payable returns(address) {  
        currPrice = initPrice - priceDecrement * (block.number - startBlock);

        require(
            msg.value >= currPrice,
            "low bid price not accepted"
        );

        require(
            auctionEnded != true,
            "auction has already ended"
        );

        if (bidprices[msg.sender]>currPrice && !auctionEnded){
            bidWinner = msg.sender;
            auctionEnded = true;
            finalize();
            return bidWinner;
        }
    }

    function finalize() public {
        if (judge == 0) {
            balances[bidWinner] -= currPrice;
            balances[owner] += currPrice;
        }
        itemReceived = true;
        isFinalized = true;
    }

    function refund(uint256 refundAmount) public {
        require(
            isFinalized == false,
            "finalized and refund can only be called once"
        ); 
        balances[bidWinner] += currPrice;
        balances[owner] -= currPrice;
    }

    //for testing framework
    function nop() public returns(bool) {
        return true;
    }
}
