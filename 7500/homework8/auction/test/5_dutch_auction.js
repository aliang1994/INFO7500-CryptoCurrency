var BigNumber = require('bignumber.js');
var run = require('./framework.js');

contract('Dutch Auction', function (accounts) {

  it("rejects second bid", function (done) {
    run(accounts, done, {
      type:                "dutch",
      reservePrice:        500,
      judgeAddress:        0,
      biddingTimePeriod:   10,
      offerPriceDecrement: 25,
      actions: [
        { block: 1, action: "bid", account: 1, payment: 725, succeed: true,  on_error: "Valid bid rejected" },
        { block: 2, action: "bid", account: 2, payment: 750, succeed: false, on_error: "Second bid accepted" },
      ],
    });
  });

});
