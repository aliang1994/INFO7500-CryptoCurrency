var BigNumber = require('bignumber.js');
var run = require('./framework.js');

contract('Dutch Auction', function (accounts) {

  it("doesn't reject a finalize even though no winner", function (done) {
     run(accounts, done, {
       type:                "dutch",
       reservePrice:        500,
       judgeAddress:        accounts[3],
       biddingTimePeriod:   10,
       offerPriceDecrement: 25,
       actions: [
         { block: 2, action: "finalize", account: 3,               succeed: false, on_error: "Invalid finalize accepted" },
       ],
     });
   });
});
