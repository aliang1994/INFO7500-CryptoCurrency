package hw6;

import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import static org.bitcoinj.script.ScriptOpCodes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GroupMultiSigTransaction extends ScriptTester {

    private DeterministicKey keyBank;
    private DeterministicKey keyCus1;
    private DeterministicKey keyCus2;
    private DeterministicKey keyCus3;

    public GroupMultiSigTransaction(WalletAppKit kit) {
        super(kit);
        keyBank = kit.wallet().freshReceiveKey();
        keyCus1 = kit.wallet().freshReceiveKey();
        keyCus2 = kit.wallet().freshReceiveKey();
        keyCus3 = kit.wallet().freshReceiveKey();
    }

    @Override
    public Script createLockingScript() {
    	ScriptBuilder builder = new ScriptBuilder();
    	builder.op(OP_2);
    	builder.data(keyBank.getPubKey());
    	builder.data(keyCus1.getPubKey());
    	builder.data(keyCus2.getPubKey());
    	builder.data(keyCus3.getPubKey());
    	builder.op(OP_4);
        builder.op(OP_CHECKMULTISIG);
        return builder.build();
    }

    @Override
    public Script createUnlockingScript(Transaction unsignedTransaction) {
    	TransactionSignature txSig1 = sign(unsignedTransaction, keyCus1);
    	TransactionSignature txSig2 = sign(unsignedTransaction, keyCus2);
    	TransactionSignature txSig3 = sign(unsignedTransaction, keyCus3);
    	
    	TransactionSignature txSigBank = sign(unsignedTransaction, keyBank);
    	
    	//getting one of the three scripts
    	ArrayList<TransactionSignature> randPool = new ArrayList<TransactionSignature>(Arrays.asList(txSig1, txSig2, txSig3));
    	int num = ThreadLocalRandom.current().nextInt(0, 2 + 1);  //(min, max+1)
    	
    	
        ScriptBuilder builder = new ScriptBuilder();
        builder.smallNum(OP_0);
        builder.data(txSigBank.encodeToBitcoin());
        builder.data(randPool.get(num).encodeToBitcoin());
        return builder.build();
    }

    public static void main(String[] args) throws InsufficientMoneyException, InterruptedException {
        WalletInitTest wit = new WalletInitTest();
        new GroupMultiSigTransaction(wit.getKit()).run();
        wit.monitor();
    }

}
