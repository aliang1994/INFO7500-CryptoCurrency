package hw6;

import com.google.common.collect.ImmutableSet;
import org.bitcoinj.core.*;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.SendRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

public abstract class ScriptTester implements AutoCloseable {
    private final static Logger LOGGER = LoggerFactory.getLogger(ScriptTester.class);
    private final WalletAppKit kit;

    public ScriptTester(WalletAppKit kit) {
        this.kit = kit;
    }

    public abstract Script createLockingScript();

    public abstract Script createUnlockingScript(Transaction unsignedTransaction);

    protected TransactionSignature sign(Transaction transaction, ECKey key) {
        Script script = transaction.getInput(0).getConnectedOutput().getScriptPubKey();
        return transaction.calculateSignature(0, key, script, Transaction.SigHash.ALL, false);
    }

    public void testScript(Script inputScript, Script spendingScript,Transaction redemptionTransaction) {
        spendingScript.correctlySpends(redemptionTransaction, 0, inputScript, ImmutableSet.of(Script.VerifyFlag.P2SH));
    }

    public void sendTransaction(Transaction transaction) throws InsufficientMoneyException {
        LOGGER.info("Transaction hex you can directly submit this to a block explorer:\n{}", new String(Hex.encode(transaction.bitcoinSerialize())));
        kit.wallet().commitTx(transaction);
        kit.peerGroup().broadcastTransaction(transaction);
        LOGGER.info("Broadcasted transaction: {}", transaction.getHashAsString());
    }

    public WalletAppKit getKit() {
        return kit;
    }

    @Override
    public void close() throws Exception {
        kit.stopAsync();
        kit.awaitTerminated();
    }

    public void run() throws InsufficientMoneyException {
        WalletAppKit kit = getKit();

        final Script lockingScript = createLockingScript();

        Transaction txA = new Transaction(kit.params());
        TransactionOutput txAout = txA.addOutput(Coin.CENT, lockingScript);
        SendRequest request = SendRequest.forTx(txA);
        kit.wallet().completeTx(request);
        sendTransaction(txA);

        Transaction txB = new Transaction(kit.params());
        txB.addOutput(txAout.getValue().subtract(Coin.MILLICOIN.multiply(2)), kit.wallet().currentReceiveAddress());
        txB.addInput(txAout);
        Script unlockingScript = createUnlockingScript(txB);
        testScript(lockingScript, unlockingScript, txB);
        txB.getInput(0).setScriptSig(unlockingScript);
        sendTransaction(txB);
    }
}
