// Each test should consist of a single call to run with the list of accounts, the done callback,
// and an auction schema describing the auction to run.
//
// An auction schema is an object with the following properties:
//    type:               A string denoting the type of auction to run.
//    reservePrice:       The auction reserve price.
//    judgeAddress:       For auctions with a judge, the address of the judge.
//    actions:            An ordered list of actions to run.
//
// An action is an object with the following properties:
//    block:              The block in which the action should occur.
//    action:             A string or callback function. If the action is a function, then running
//                        the action will cause the callback to be invoked with the list of
//                        accounts as its argument.
//
// If action is a string, the action must have the following additional properties:
//    succeed:            Whether the action should succeed.
//    on_error:           A message to show if the action succeeded when succeed is false or vice
//                        versa.
//
// "bid" actions can have the following additional properties:
//    account:            The index of the account that should bid.
//    payment:            The number of wei to bid.
//
// "finalize" actions take no additional properties.
//

// Implementation

var BigNumber = require('bignumber.js');
var DutchAuction = artifacts.require("./DutchAuction.sol");
var abi = require('ethereumjs-abi');

// Run a test with the given accounts, done callback, and auction schema.
module.exports = function (accounts, done, schema) {
  var gasPrice = new BigNumber(15000000000);
  if (schema.gasPrice) {
    gasPrice = schema.gasPrice;
  }

  var gasAllocated = new BigNumber(21000).times(5);
  if (schema.gasAllocated) {
    gasAllocated = schema.gasAllocated;
  }

  var C;
  // Start the contract.
  switch (schema.type) {
    case "dutch":
        C = DutchAuction;
        break;
    default:
      return error("Unknown contract type " + schema.type);
  }

  C.new(schema.reservePrice, schema.judgeAddress, schema.biddingTimePeriod, schema.offerPriceDecrement, {from: accounts[0]}).then(function (instance) {
  //      console.log("Accounts: " + accounts.length);
      var nopaccount = accounts[accounts.length-1];

      //console.log(schema.action);
      var actions = schema.actions;
      var nonces = [];

      function logDetails(result) {
          var gasPrice = new BigNumber(web3.eth.gasPrice);
          var gasUsed = new BigNumber(result.receipt.gasUsed);

          console.log("gasPrice: " + gasPrice);
          console.log("gasUsed: " + gasUsed);
          console.log(gasPrice.times(gasUsed));
          console.log(result);
      }

      function error(message) {
        done(Error("Test: " + message));
      }

      function fail(action, message) {
        done(Error("Action " + action + ": " + message));
      }

      function print(accounts, c) {
        for (i = 0; i < c; i++) {
          //console.log("accounts[" + i + "]: " + accounts[i] + " " + web3.eth.getBalance(accounts[i]));
        }
      }

      // Internal run actions function.
      function run_(block, index) {
        //console.log("web3.eth.blockNumber: " + web3.eth.blockNumber + " block=" + block + " index=" + index);
        // If we've run out of actions, the test has passed.
        if (index >= actions.length) {
          done();
          return;
        }
        var action = actions[index];
        var nextBlock = block + 1;
        var nextIndex = index + 1;
        // If the next action takes place in a future block, delay.
        if (action.block > block) {
          instance.nop({from: nopaccount}).then(function (result) {
            run_(nextBlock, index);
          });
          return;
        }
        // If the next action takes place in a previous block, error.
        if (action.block != block) {
          return error("Current block is " + block + ", but action " + index +
              " takes place in prior block " + action.block);
        }
        // If the next action is a callback, execute it.
        if (typeof(action.action) == "function") {
          var result = action.action();
          if (result) {
            return fail(index, result);
          }
          // On successful evaluation of the callback, reinvoke ourselves.
          return run_(block, nextIndex);
        }

        //console.log("Running action: " + action.action);
        // Run the action and get a promise.
        var promise;
        var account = accounts[action.account];
        var nonce = nonces[action.account];
        print(accounts, 3);
        switch (action.action) {
          case "bid":
            //console.log("Send bid from: " + account);
            promise = instance.bid({ value: action.payment, from:account, gasPrice: gasPrice, gas: gasAllocated });
            break;
          case "finalize":
            promise = instance.finalize({from: account, gasPrice: gasPrice, gas: gasAllocated});
            break;
          default:
            return error("Unknown action " + action.action);
        }
        // Continue the computation after the promise.
        promise.then(function (result) {
          print(accounts, 3);
          if (!action.succeed) {
            return fail(index, action.on_error);
          }

          if (action.post) {
            action.post(result);
          }

          run_(nextBlock, nextIndex);

        }).catch(function (error) {
          print(accounts, 3);
          if (action.succeed) {
            return fail(index, action.on_error + ": " + error.toString().replace(/^error: /i, ""));
          }

          if (action.post) {
            action.post(error);
          }

          run_(nextBlock, nextIndex);
        });
      }
      //console.log("Create contract " + schema.type + " at block number: " + web3.eth.blockNumber);
      // Start the contract.
      run_(1, 0);
  });
}