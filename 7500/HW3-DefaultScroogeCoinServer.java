package homework3;

import java.security.*;
import java.util.*;

import homework3.Transaction.Input;
import homework3.Transaction.Output;

//Scrooge creates coins by adding outputs to a transaction to his public key.
//In ScroogeCoin, Scrooge can create as many coins as he wants.
//No one else can create a coin.
//A user owns a coin if a coin is transfer to him from its current owner


public class DefaultScroogeCoinServer implements ScroogeCoinServer {

	private KeyPair scroogeKeyPair;
	private ArrayList<Transaction> ledger = new ArrayList();

	/**
	 * Set scrooge's key pair
	 */
	@Override
	public synchronized void init(KeyPair scrooge) {
		if (scrooge == null) throw new RuntimeException();
		this.scroogeKeyPair = scrooge;
	}


	/**
	 * For every 10 minute epoch, this method is called with an unordered list of proposed transactions
	 * submitted during this epoch.
	 * This method goes through the list, checking each transaction for correctness, and accepts as
	 * many transactions as it can in a "best-effort" manner, but it does not necessarily return
	 * the maximum number possible.
	 * If the method does not accept an valid transaction, the user must try to submit the transaction
	 * again during the next epoch.
	 * Returns a list of hash pointers to transactions accepted for this epoch
	 */
	public synchronized List<HashPointer> epochHandler(List<Transaction> txs)  {
		List<HashPointer> listhp = new ArrayList<HashPointer>();
		while (!txs.isEmpty()) {
			List<Transaction> unaccpedtx = new ArrayList<Transaction>();
			for (int k=0; k<txs.size(); k++) { 
				if (isValid(txs.get(k))) {
					ledger.add(txs.get(k));
					HashPointer hp = new HashPointer(txs.get(k).getHash(), ledger.size()-1);
					listhp.add(hp);
				} 
				else {
					unaccpedtx.add(txs.get(k));
				}
			}
			if (unaccpedtx.size() > txs.size()) break;
			txs = unaccpedtx;
		}
		return listhp;
	}

	/**
	 * Returns true if and only if transaction tx meets the following conditions:
	 *
	 * CreateCoin transaction
	 * (1) no inputs
	 * (2) all outputs are given to Scrooge's public key -- verify with scrooge pk
	 * (3) all of tx’s output values are positive
	 * (4) Scrooge's signature of the transaction is included
	 *
	 * PayCoin transaction
	 * (1) all inputs claimed by tx are in the current unspent (i.e. in getUTXOs()),
	 * (2) the signatures on each input of tx are valid,
	 * (3) no UTXO is claimed multiple times by tx,
	 * (4) all of tx’s output values are positive, and
	 * (5) the sum of tx’s input values is equal to the sum of its output values; 
	 */
	
	@Override
	public synchronized boolean isValid(Transaction tx) {
		if(tx.getType().equals(Transaction.Type.Create)){
			if(tx.getInputs().size()!=0) return false;   //(1)
			
			List<Output> oplist = tx.getOutputs();
			for (Output out: oplist){ 
				if(!out.getPublicKey().equals(scroogeKeyPair.getPublic())) return false;  //(2)
				if(out.getValue()<0)  return false; //(3)
			}
			
			try { //(4)
				Signature sign = Signature.getInstance(SIGNATURE_ALGORITHM);
				sign.initVerify(scroogeKeyPair.getPublic());
				sign.update(tx.getRawBytes());
				if(!sign.verify(tx.getSignature())) return false; 
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			
			if(tx.getSignature() == null) return false;   
			return true;
		}
		
		if(tx.getType().equals(Transaction.Type.Pay)){
			Set<UTXO> utxo = getUTXOs();

			double inputtotal = 0;
			for (int i = 0; i < tx.numInputs(); i++) {

				Input in = tx.getInputs().get(i);
				int outputindex = in.getIndexOfTxOutput();
				int indexledger = getLedgerIndexForInput(in.getHashOfOutputTx(), in, utxo, outputindex);
				if (indexledger == -1) return false;
				HashPointer hp = new HashPointer(in.getHashOfOutputTx(), indexledger);
				UTXO inpututxo = new UTXO(hp, outputindex);
				if (!utxo.contains(inpututxo)) return false;  // (1)

				Output out = ledger.get(indexledger).getOutput(outputindex);
				inputtotal += out.getValue();
				PublicKey pk = out.getPublicKey();
				try { //(2)
					Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
					signature.initVerify(pk);
					signature.update(tx.getRawDataToSign(i));
					if (!signature.verify(in.getSignature())) {
						return false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			double outputtotal = 0;
			for (Output out : tx.getOutputs()) {
				if (out.getValue()<0) return false;  //(4)
				outputtotal += out.getValue();
			}
			if (Math.abs(inputtotal - outputtotal) < 0.00001) { //(5)
				return true;
			} else {
				return false;
			}
		}		
		return true;
	}
	
	/**
	 * Helper method for getting ledger index by comparing hash
	 * @param hashOfOutputTx
	 * @param outpututxo
	 * @param opindex
	 * @param input
	 * @return ledger index
	 */
	private int getLedgerIndexForInput(byte[] hashOfOutputTx, Input input, Set<UTXO> outpututxo, int opindex ) {
		for (int i = 0; i < ledger.size(); i++) {
			if (Arrays.equals(ledger.get(i).getHash(), hashOfOutputTx)) {
				
				HashPointer hp = new HashPointer(input.getHashOfOutputTx(), i);
				UTXO inpututxo = new UTXO(hp, opindex);
				if (outpututxo.contains(inpututxo)) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Returns the complete set of currently unspent transaction outputs on the ledger
	 */
	@Override
	public synchronized Set<UTXO> getUTXOs() {
		Set<UTXO> utxo = new HashSet<UTXO>();
		for (int ledgerindex = 0; ledgerindex < ledger.size(); ledgerindex++) {
			Transaction tx = ledger.get(ledgerindex);
			if(tx.getType().equals(Transaction.Type.Create)){
				HashPointer hp = new HashPointer(tx.getHash(), ledgerindex);
				for (Output o : tx.getOutputs()) {
					int index = tx.getIndex(o);
					UTXO u = new UTXO(hp, index);
					utxo.add(u);
				}
			}
			if(tx.getType().equals(Transaction.Type.Pay)){
				for (int i = 0; i < tx.numInputs(); i++) {
					Input ip = tx.getInputs().get(i);
					int index = ip.getIndexOfTxOutput();
					int ledgeind = getLedgerIndexForInput(ip.getHashOfOutputTx(), ip, utxo, index);
					if (ledgeind != -1){
						HashPointer hp = new HashPointer(ip.getHashOfOutputTx(), ledgeind);    
						UTXO uinput = new UTXO(hp, index);
						utxo.remove(uinput);
					}
				}
				for (Output o : tx.getOutputs()) {
					int index = tx.getIndex(o);
					HashPointer hp = new HashPointer(tx.getHash(), ledgerindex);
					UTXO uoutput = new UTXO(hp, index);
					utxo.add(uoutput);
				}
			}
		}
		return utxo;
	}
}