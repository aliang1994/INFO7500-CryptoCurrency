 package homework3;

import java.util.Arrays;
import java.util.Objects;

public class HashPointer {
	private byte[] hash;
	private int ledgerIndex;

	public HashPointer(byte[] h, int ledgerIndex) {
		if (h == null) throw new RuntimeException();
		
		this.hash = Arrays.copyOf(h, h.length);
		this.ledgerIndex = ledgerIndex;
	}

	public int getLedgerIndex() {
		return ledgerIndex;
	}

	public byte[] getHash() {
		return Arrays.copyOf(hash, hash.length);
	}

	@Override
	public int hashCode() {
		return Objects.hash(Arrays.hashCode(hash), ledgerIndex);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof HashPointer)) return false;
		
		HashPointer hp = (HashPointer) o;
		return Arrays.equals(hash, hp.hash) && ledgerIndex == hp.ledgerIndex;
	}

}
