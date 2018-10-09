package homework3;

import junit.framework.TestCase;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.*;

import static homework3.ScroogeCoinServer.*;
import static homework3.TestKeys.*;
import static java.util.Arrays.*;

public class BasicTests extends TestCase {

	public KeyPair scrooge;
	public SecureRandom random;
	public Map<Integer,KeyPair> users = new HashMap();

	@Override
	protected void setUp() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		random = new SecureRandom();//.getInstanceStrong();
		scrooge = getTestKeyPair(SCROOGE_PK_STR, SCROOGE_SK_STR);
		users.put(1, getTestKeyPair(USER1_PK_STR, USER1_SK_STR));
		users.put(2, getTestKeyPair(USER2_PK_STR, USER2_SK_STR));
		users.put(3, getTestKeyPair(USER3_PK_STR, USER3_SK_STR));
		users.put(4, getTestKeyPair(USER4_PK_STR, USER4_SK_STR));
		users.put(5, getTestKeyPair(USER5_PK_STR, USER5_SK_STR));
	}

	protected ScroogeCoinServer get() throws Exception  {
		ScroogeCoinServer s = new DefaultScroogeCoinServer();
		s.init(scrooge);
		return s;
	}

	//test multiple tx's at a time

	protected HashPointer getHashPointer(List<HashPointer> l, Transaction tx) {
		for (HashPointer p : l) {
			if (Arrays.equals(p.getHash(), tx.getHash())) {
				return p;
			}
		}
		return null;
	}

	protected byte[] sign(byte[] buf, PrivateKey sk) {
		return Util.sign(buf, sk, SIGNATURE_ALGORITHM, random);
	}


	@Test
	public void test0() throws Exception { // empty ledger
		ScroogeCoinServer s = get();

		assertEquals(new HashSet<UTXO>(), s.getUTXOs());
		assertEquals(new ArrayList<Transaction>(), s.epochHandler(Collections.EMPTY_LIST));
	}

	@Test  //Create coin, pk is not from Scrooge
	public void test1() throws Exception {
		ScroogeCoinServer s = get();

		Transaction tx = new Transaction(Transaction.Type.Create);
		tx.add(new Transaction.Output(3.4, users.get(1).getPublic()));
		tx.sign(scrooge.getPrivate(), random);
		assertFalse(s.isValid(tx));
	}

	@Test  //Create coin, value is negative
	public void test4() throws Exception {
		ScroogeCoinServer s = get();

		Transaction tx = new Transaction(Transaction.Type.Create);
		tx.add(new Transaction.Output(-3.4, scrooge.getPublic()));
		tx.sign(scrooge.getPrivate(), random);
		assertFalse(s.isValid(tx));
	}

	@Test
	public void test11() throws Exception {
		ScroogeCoinServer s = get();

		Transaction tx1 = new Transaction(Transaction.Type.Create);
		tx1.add(new Transaction.Output(3.4, scrooge.getPublic()));
		tx1.sign(scrooge.getPrivate(), random);

		Transaction tx2 = new Transaction(Transaction.Type.Create);
		tx2.add(new Transaction.Output(2.1, scrooge.getPublic()));
		tx2.sign(scrooge.getPrivate(), random);

		List<HashPointer> hptrlist = s.epochHandler(asList(tx1, tx2));
		UTXO o1 = new UTXO(getHashPointer(hptrlist, tx1), 0);
		UTXO o2 = new UTXO(getHashPointer(hptrlist, tx2), 0);
		assertTrue(s.getUTXOs().size() == 2);
		assertTrue(s.getUTXOs().contains(o1));
		assertTrue(s.getUTXOs().contains(o2));
	}

	@Test
	public void test16() throws Exception {
		ScroogeCoinServer s = get();

		Transaction tx1 = new Transaction(Transaction.Type.Create);
		tx1.add(new Transaction.Output(3.4, scrooge.getPublic()));
		tx1.sign(scrooge.getPrivate(), random);
		s.epochHandler(asList(tx1));

		Transaction.Input in2 = new Transaction.Input(tx1.getHash(), 0);
		Transaction tx2 = new Transaction(Transaction.Type.Pay);
		tx2.add(in2);
		tx2.add(new Transaction.Output(3.4, users.get(1).getPublic()));
		in2.setSignature(sign(tx2.getRawDataToSign(0), scrooge.getPrivate()));
		s.epochHandler(asList(tx2));

		Transaction.Input in3 = new Transaction.Input(tx2.getHash(), 0);
		Transaction tx3 = new Transaction(Transaction.Type.Pay);
		tx3.add(in3);
		tx3.add(new Transaction.Output(3.4, users.get(2).getPublic()));
		in3.setSignature(sign(tx3.getRawDataToSign(0), users.get(1).getPrivate()));
		assertTrue(s.isValid(tx3));

		List<HashPointer> hptrs = s.epochHandler(asList(tx3));

		UTXO o = new UTXO(getHashPointer(hptrs, tx3), 0);
		assertTrue(s.getUTXOs().size() == 1);
		assertTrue(s.getUTXOs().contains(o));
	}


	@Test
	public void test23() throws Exception {
		ScroogeCoinServer s = get();

		Transaction tx1 = new Transaction(Transaction.Type.Create);
		tx1.add(new Transaction.Output(3.4, scrooge.getPublic()));
		tx1.sign(scrooge.getPrivate(), random);

		Transaction.Input in2 = new Transaction.Input(tx1.getHash(), 0);
		Transaction tx2 = new Transaction(Transaction.Type.Pay);
		tx2.add(in2);
		tx2.add(new Transaction.Output(1.2, users.get(1).getPublic()));
		tx2.add(new Transaction.Output(2.2, users.get(2).getPublic()));
		in2.setSignature(sign(tx2.getRawDataToSign(0), scrooge.getPrivate()));

		Transaction tx3 = new Transaction(Transaction.Type.Create);
		tx3.add(new Transaction.Output(3.4, scrooge.getPublic()));
		tx3.sign(scrooge.getPrivate(), random);

		Transaction.Input ina = new Transaction.Input(tx2.getHash(), 0);
		Transaction.Input inb = new Transaction.Input(tx2.getHash(), 1);
		Transaction.Input inc = new Transaction.Input(tx3.getHash(), 0);
		Transaction tx4 = new Transaction(Transaction.Type.Pay);
		tx4.add(ina);
		tx4.add(inb);
		tx4.add(inc);
		tx4.add(new Transaction.Output(6.8, users.get(3).getPublic()));
		ina.setSignature(sign(tx4.getRawDataToSign(0), users.get(1).getPrivate()));
		inb.setSignature(sign(tx4.getRawDataToSign(1), users.get(2).getPrivate()));
		inc.setSignature(sign(tx4.getRawDataToSign(2), scrooge.getPrivate()));

		List<HashPointer> hptrs = s.epochHandler(asList(tx2, tx1, tx4, tx3));

		UTXO o = new UTXO(getHashPointer(hptrs, tx4), 0);
		assertTrue(s.getUTXOs().size() == 1);
		assertTrue(s.getUTXOs().contains(o));
	}
}
