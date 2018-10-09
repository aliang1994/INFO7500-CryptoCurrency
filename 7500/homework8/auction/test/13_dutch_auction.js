var BigNumber = require('bignumber.js');
var run = require('./framework.js');

contract('Dutch Auction', function (accounts) {

  it("accepts a bid on the last round", function (done) {
    run(accounts, done, {
      type:                "dutch",
      reservePrice:        500,
      judgeAddress:        0,
      biddingTimePeriod:   10,
      offerPriceDecrement: 25,
      actions: [
        { block: 9, action: "bid", account: 1, payment: 750, succeed: true, on_error: "Valid bid rejected" },
      ],
    });
  });

});
