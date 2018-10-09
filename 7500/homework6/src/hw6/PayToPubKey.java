package hw6;

import org.bitcoinj.core.*;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import static org.bitcoinj.script.ScriptOpCodes.*;

public class PayToPubKey extends ScriptTester {
    private DeterministicKey key;

    public PayToPubKey(WalletAppKit kit) {
        super(kit);
        key = kit.wallet().freshReceiveKey();
    }

    @Override
    public Script createLockingScript() {
        ScriptBuilder builder = new ScriptBuilder();
        builder.data(key.getPubKey());
        builder.op(OP_CHECKSIG);
        return builder.build();
    }

    @Override
    public Script createUnlockingScript(Transaction unsignedTransaction) {
        TransactionSignature txSig = sign(unsignedTransaction, key);
        ScriptBuilder builder = new ScriptBuilder();
        builder.data(txSig.encodeToBitcoin());
        return builder.build();
    }

    public static void main(String[] args) throws InsufficientMoneyException, InterruptedException {
        WalletInitTest wit = new WalletInitTest();
        new PayToPubKey(wit.getKit()).run();
        wit.monitor();
    }
}
