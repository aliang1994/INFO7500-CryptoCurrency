var BigNumber = require('bignumber.js');
var run = require('./framework.js');

contract('Dutch Auction', function (accounts) {

  it("rejects a bid after the last round", function (done) {
    run(accounts, done, {
      type:                "dutch",
      reservePrice:        500,
      judgeAddress:        accounts[3],
      biddingTimePeriod:   10,
      offerPriceDecrement: 25,
      actions: [
        { block: 10, action: "bid",      account: 1, payment: 750, succeed: false, on_error: "Invalid bid accepted" },
        { block: 11, action: "finalize", account: 3,               succeed: false, on_error: "finalize rejected" },
        { block: 12, action: "finalize", account: 1,               succeed: false, on_error: "finalize rejected" },
      ],
    });
  });

});
