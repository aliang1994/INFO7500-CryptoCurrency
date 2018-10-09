var MetaCoin = artifacts.require("./MetaCoin.sol");

contract('MetaCoin', function(accounts) {

  it("should put 10000 MetaCoin in the first account", function() {

    return MetaCoin.deployed().then(function(instance) {
      return instance.getBalance.call(accounts[0]);
    }).then(function(balance) {
      assert.equal(balance.valueOf(), 10000, "10000 wasn't in the first account");
    });
  });
  
  it("should send coin correctly", function() {
    var meta;

    // Get initial balances of first and second account.
    var account_one = accounts[0];
    var account_two = accounts[1];

    var account_one_starting_balance;
    var account_two_starting_balance;
    var account_one_ending_balance;
    var account_two_ending_balance;

    var amount = 10;

    return MetaCoin.deployed().then(function(instance) {
      meta = instance;
      return meta.getBalance.call(account_one);
    }).then(function(balance) {
      account_one_starting_balance = balance.toNumber();
      return meta.getBalance.call(account_two);
    }).then(function(balance) {
      account_two_starting_balance = balance.toNumber();
      return meta.sendCoin(account_two, amount, {from: account_one});
    }).then(function() {
      return meta.getBalance.call(account_one);
    }).then(function(balance) {
      account_one_ending_balance = balance.toNumber();
      return meta.getBalance.call(account_two);
    }).then(function(balance) {
      account_two_ending_balance = balance.toNumber();

      assert.equal(account_one_ending_balance, account_one_starting_balance - amount, "Amount wasn't correctly taken from the sender");
      assert.equal(account_two_ending_balance, account_two_starting_balance + amount, "Amount wasn't correctly sent to the receiver");
    });
  });

  it("should allow owner to mint coins", function() {
    //call the mint function on behalf of the owner and check that owner's account balance increased correctly
    var meta;
    var amount = 10;
    var beg_balance;
    var end_balance;
    var owner = accounts[0];
    
    return MetaCoin.deployed().then(function(instance) {
      meta = instance;
      return meta.getBalance.call(owner);
    }).then(function(balance){
      beg_balance = balance.toNumber();
      return meta.mint(amount);
    }).then(function(){
      return meta.getBalance.call(owner);
    }).then(function(balance){
      end_balance = balance.toNumber();

      assert.equal(beg_balance+amount, end_balance, "Amount wasn't correctly minted to the owner");
    })
    
  });

  it("should not allow a non-owner address to mint coins", function() {
    //call the mint function on behalf of a non-owner address and check that the non-owner address balance stays the same
    var meta;
    var amount = 10;
    var beg_balance_owner;
    var end_balance_owner;
    var beg_balance_other;
    var end_balance_other;

    var owner = accounts[0];
    var other = accounts[1];
    
    return MetaCoin.deployed().then(function(instance) {
      meta = instance;
      return meta.getBalance.call(owner);
    }).then(function(balance){
      beg_balance_owner = balance.toNumber();
      return meta.getBalance.call(other);
    }).then(function(balance){
      beg_balance_other = balance.toNumber();
      return meta.mint(amount);
    }).then(function(){
      return meta.getBalance.call(owner);
    }).then(function(balance){
      end_balance_owner = balance.toNumber();
      return meta.getBalance.call(other);
    }).then(function(balance){
      end_balance_other = balance.toNumber();
      assert.equal(beg_balance_other, end_balance_other, "Non-owner allowed to mint coins");
    })
  });
});
