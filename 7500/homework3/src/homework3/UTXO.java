package homework3;

import java.util.Objects;

public class UTXO { // Unspent Transaction Output
   private HashPointer txptr;  // Hash pointer to the transaction from which this UTXO originates
   private int outputIndex;   // Index of the corresponding output in said transaction
   
   // Creates a new UTXO corresponding to the output with index <index> in the transaction whose hash is <txHash>
   public UTXO(HashPointer txptr, int outputIndex) {
      if (txptr == null) throw new RuntimeException();
      this.txptr = txptr;
      this.outputIndex = outputIndex;
   }
   
   // Returns the transaction hash of this UTXO
   public HashPointer getTxPtr() {
      return txptr;
   }
   
   // Returns the index of this UTXO
   public int getOutputIndex() {
      return outputIndex;
   }
   
   // Compares this UTXO to the one specified by <other>, considering them equal if they have <txHash> arrays with equal contents and equal <index> values
   public boolean equals(Object o) {
      if (!(o instanceof UTXO)) {
         return false;
      }

      UTXO utxo2 = (UTXO) o;
      return Objects.equals(txptr, utxo2.txptr) && outputIndex == utxo2.outputIndex;
   }

   public int hashCode() {
     return Objects.hash(txptr, outputIndex);
   }
}
