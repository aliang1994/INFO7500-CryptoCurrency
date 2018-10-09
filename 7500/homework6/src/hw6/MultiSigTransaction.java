package hw6;

import static org.bitcoinj.script.ScriptOpCodes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

public class MultiSigTransaction extends ScriptTester {

    private DeterministicKey key1;
    private DeterministicKey key2;
    private DeterministicKey key3;

    public MultiSigTransaction(WalletAppKit kit) {
        super(kit);
        key1 = kit.wallet().freshReceiveKey();
        key2 = kit.wallet().freshReceiveKey();
        key3 = kit.wallet().freshReceiveKey();
    }

    @Override
    public Script createLockingScript() {
        //create a multisig locking script that unlocks when exactly one signature for one of the public keys (key1, key2, key3) are given
    	ScriptBuilder builder = new ScriptBuilder();
    	builder.op(OP_1);
    	builder.data(key1.getPubKey());
    	builder.data(key2.getPubKey());
    	builder.data(key3.getPubKey());
    	builder.op(OP_3);
        builder.op(OP_CHECKMULTISIG);
        return builder.build();
    }

    @Override
    public Script createUnlockingScript(Transaction unsignedTransaction) {
        //create a corresponding unlocking script.
    	TransactionSignature txSig1 = sign(unsignedTransaction, key1);
    	TransactionSignature txSig2 = sign(unsignedTransaction, key2);
    	TransactionSignature txSig3 = sign(unsignedTransaction, key3);
    	
    	
    	//getting one of the three scripts
    	ArrayList<TransactionSignature> randPool = new ArrayList<TransactionSignature>(Arrays.asList(txSig1, txSig2, txSig3));
    	int num = ThreadLocalRandom.current().nextInt(0, 2 + 1);  //(min, max+1)
    	
    	
        ScriptBuilder builder = new ScriptBuilder();
        //builder.op(OP_0);
        builder.smallNum(OP_0);
        builder.data(randPool.get(num).encodeToBitcoin());
        return builder.build();
    }

    public static void main(String[] args) throws InsufficientMoneyException, InterruptedException {
        WalletInitTest wit = new WalletInitTest();
        new MultiSigTransaction(wit.getKit()).run();
        wit.monitor();
    }

}
