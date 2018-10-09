var BigNumber = require('bignumber.js');
var run = require('./framework.js');

contract('Dutch Auction', function (accounts) {

  it("creates a dutch auction", function (done) {

    run(accounts, done, {
      type:                "dutch",
      reservePrice:        500,
      judgeAddress:        0,
      biddingTimePeriod:   10,
      offerPriceDecrement: 25,
      actions: [],
    });

  });

});
