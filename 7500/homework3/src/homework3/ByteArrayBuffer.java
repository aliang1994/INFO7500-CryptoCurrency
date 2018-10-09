package homework3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteArrayBuffer {
	private ByteArrayOutputStream bos = new ByteArrayOutputStream();

	public byte[] getRawBytes() {
		return bos.toByteArray();
	}

	public ByteArrayBuffer put(byte[] arr) {
		try {
			bos.write(arr);
			return this;
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public ByteArrayBuffer put(int i) {
		bos.write(i);
		return this;
	}

	public ByteArrayBuffer put(double i) {
		byte[] a = new byte[Double.BYTES];
		ByteBuffer b = ByteBuffer.allocate(Double.BYTES);
		b.putDouble(i);
		put(b.array());
		return this;
	}
}
