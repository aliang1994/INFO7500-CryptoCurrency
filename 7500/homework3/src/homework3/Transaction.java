package homework3;

import java.security.*;
import java.util.*;

import static homework3.ScroogeCoinServer.*;

public class Transaction {

   public static enum Type {
      Create,
      Pay
   }

   public static class Input {
      private byte[] hashOfOutputTx;   // hash pointer of the Transaction whose output is being used
      private int indexOfTxOutput;     // used output's index in the previous transaction
      private byte[] signature;    // the signature produced to check validity

      public Input(byte[] hashOfOutputTx, int indexOfTxOutput) {
         this.hashOfOutputTx = hashOfOutputTx;
         this.indexOfTxOutput = indexOfTxOutput;
      }
      
      public byte[] getSignature() {
          return Arrays.copyOf(signature, signature.length);
       }

      public void setSignature(byte[] sig) {
         signature = Arrays.copyOf(sig, sig.length);
      }
      
      //
      
      public int getIndexOfTxOutput() {
          return indexOfTxOutput;
       }

      public byte[] getHashOfOutputTx() {
          return hashOfOutputTx;
       }
       
      //

      public byte[] getRawDataToSign2() { // create coin transaction
         ByteArrayBuffer buf = new ByteArrayBuffer();
         buf.put(getHashOfOutputTx());
         buf.put(getIndexOfTxOutput());
         return buf.getRawBytes();
      }

      public byte[] getRawBytes() { // pay coin transaction
         ByteArrayBuffer buf = new ByteArrayBuffer();
         buf.put(getHashOfOutputTx());
         buf.put(getIndexOfTxOutput());
         buf.put(getSignature());
         return buf.getRawBytes();
      }
      
      //

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Input)) return false;
         Input i = (Input) o;
         return Arrays.equals(hashOfOutputTx, i.hashOfOutputTx) &&
                 Objects.equals(indexOfTxOutput, i.indexOfTxOutput) &&
                 Arrays.equals(signature, i.signature);
      }

      @Override
      public int hashCode() {
         return Objects.hash(Arrays.hashCode(hashOfOutputTx), indexOfTxOutput, Arrays.hashCode(signature));
      }
   }

   public static class Output {
      private double value;  //Value in ScroogeCoin of the output
      private PublicKey address;   //The address (public key) of the recipient

      public Output(double v, PublicKey addr) {
         value = v;
         address = addr;
      }
   
      public double getValue() {
         return value;
      }

      public PublicKey getPublicKey() {
         return address;
      }

      public byte[] getRawBytes() {
         ByteArrayBuffer buf = new ByteArrayBuffer();
         buf.put(value);
         buf.put(getPublicKey().getEncoded());
         return buf.getRawBytes();
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Output)) return false;
         Output out = (Output) o;
         return value == out.value && Objects.equals(address, out.address);
      }

      @Override
      public int hashCode() {
         return Objects.hash(value, address);
      }
   }

   private String name;
   private Type type;
   private ArrayList<Input> inputs;   // inputs
   private ArrayList<Output> outputs; // outputs
   private byte[] signature;  //Scrooge's signature on CreateCoin transactions, unused for PayCoin transactions
   

   public Transaction(Type type) {
      this.type = type;
      inputs = new ArrayList<Input>();
      outputs = new ArrayList<Output>();
   }

   public String getName() {
      return name;
   }

   public Transaction setName(String n) {
      this.name = n;
      return this;
   }

   public Transaction(Transaction tx) {
      this.type = tx.type;
      inputs = new ArrayList<Input>(tx.inputs);
      outputs = new ArrayList<Output>(tx.outputs);
   }

   public Transaction sign(PrivateKey sk, SecureRandom random) {
      signature = Util.sign(getRawBytes(), sk, SIGNATURE_ALGORITHM, random);
      return this;
   }

   public void clearSignature() {
      signature = null;
   }

   public Type getType() {
      return type;
   }

   public void add(Input in) {  // add inputs to transaction
      this.inputs.add(in);
   }

   public int add(Output op) {  // add outputs to transaction
      outputs.add(op);
      return outputs.size()-1;
   }

   public List<Input> getInputs() {
      return Collections.unmodifiableList(inputs);
   }

   public List<Output> getOutputs() {
      return Collections.unmodifiableList(outputs);
   }

   public byte[] getSignature() {
      return signature;
   }

   public byte[] getRawDataToSign(int index) {
      ByteArrayBuffer buf = new ByteArrayBuffer();
      Input in = inputs.get(index);
      buf.put(in.getRawDataToSign2());
      for (Output op : outputs) {
        buf.put(op.getRawBytes());
      }
      return buf.getRawBytes();
   }

   public byte[] getRawBytes() {
      ByteArrayBuffer buf = new ByteArrayBuffer();
      for (Input in : inputs) {
         buf.put(in.getRawBytes());
      }
      for (Output op : outputs) {
         buf.put(op.getRawBytes());
      }
      return buf.getRawBytes();
   }
   
   public byte[] getHash() {
      try {
         MessageDigest md = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
         md.update(getRawBytes());
         return md.digest();
      } catch(NoSuchAlgorithmException x) {
         throw new RuntimeException(x);
      }
   }

   public Input getInput(int index) {
      if (index < inputs.size()) {
         return inputs.get(index);
      }
      return null;
   }

   public Output getOutput(int index) {
      if (index < outputs.size()) {
         return outputs.get(index);
      }
      return null;
   }

   public int numInputs() {
      return inputs.size();
   }

   public int numOutputs() {
      return outputs.size();
   }

   public int getIndex(Output o) {
      int i = outputs.indexOf(o);
      if (i < 0) throw new RuntimeException();
      return i;
   }

   @Override
   public String toString() {
      return name == null ? "transaction" : name;
   }

}