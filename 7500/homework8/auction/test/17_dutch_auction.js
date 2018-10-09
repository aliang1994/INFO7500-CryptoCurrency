var BigNumber = require('bignumber.js');
var run = require('./framework.js');

contract('Dutch Auction', function (accounts) {

    it("doesn't reject a second finalize", function (done) {
      run(accounts, done, {
        type:                "dutch",
        reservePrice:        500,
        judgeAddress:        accounts[3],
        biddingTimePeriod:   10,
        offerPriceDecrement: 25,
        actions: [
          { block: 1, action: "bid",      account: 1, payment: 725, succeed: true, on_error: "Valid bid accepted" },
          { block: 2, action: "finalize", account: 1,               succeed: true, on_error: "Valid finalize rejected" },
        ],
      });
    });
});
