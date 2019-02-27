package wallet;

public class LogTransactionInput {
	public String transactionOutputId;
	public LogTransactionOutput UTXO;
	
	public LogTransactionInput(String transactionOutputId) {
		
		this.transactionOutputId = transactionOutputId;
		
	}
}
