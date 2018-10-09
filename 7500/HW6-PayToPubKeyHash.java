package hw6;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import static org.bitcoinj.script.ScriptOpCodes.*;

public class PayToPubKeyHash extends ScriptTester {
	private DeterministicKey key;

    public PayToPubKeyHash(WalletAppKit kit) {
        super(kit);
        key = kit.wallet().freshReceiveKey(); 
    }

    @Override
    public Script createLockingScript() {
    	ScriptBuilder builder = new ScriptBuilder();
    	builder.op(OP_DUP);
    	builder.op(OP_HASH160);
    	builder.data(key.getPubKeyHash());
    	builder.op(OP_EQUALVERIFY);
        builder.op(OP_CHECKSIG);
        return builder.build();
    }

    @Override
    public Script createUnlockingScript(Transaction unsignedTransaction) {
    	TransactionSignature txSig = sign(unsignedTransaction, key);
        ScriptBuilder builder = new ScriptBuilder();
        builder.data(txSig.encodeToBitcoin());
        builder.data(key.getPubKey());
        return builder.build();
    }

    public static void main(String[] args) throws InsufficientMoneyException, InterruptedException {
        WalletInitTest wit = new WalletInitTest();
        new PayToPubKeyHash(wit.getKit()).run();
        wit.monitor();
    }

}
