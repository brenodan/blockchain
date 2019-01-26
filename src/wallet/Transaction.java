package wallet;

import java.security.*;
import java.util.ArrayList;

import main.Chain;
import util.StringUtil;


public class Transaction {

	public String transactionId;//hash of the transaction 
	public PublicKey sender;
	public PublicKey recipient;
	public float value;
	public byte[] signature;
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	private static int sequence = 0;
	
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		
		this.sender = from;
		this.recipient = to;
		this.value = value;
		this.inputs = inputs;
		
	}
	
	public void generateSignature(PrivateKey privateKey) {
		
		String data = StringUtil.getStringFromKey(sender) + 
				StringUtil.getStringFromKey(recipient) + 
				Float.toString(value);
		signature = StringUtil.applyECDSASig(privateKey, data);
 		
	}
	
	public boolean verifySignature(){
		String data = StringUtil.getStringFromKey(sender) +
				StringUtil.getStringFromKey(recipient) + 
				Float.toString(value);
		return StringUtil.verifyECFSASig(sender, data, signature);
	}
	
	private String calculateHash(){
		
		sequence++;
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender) + 
				StringUtil.getStringFromKey(recipient) + 
				Float.toString(value) + sequence
				);
			
	}
	
	public boolean processTransaction() {
		
		if(verifySignature() == false) {
			
			System.out.println("Transaction Signature failed to verify");
			return false;
			
		}
		
		for (TransactionInput i : inputs) {
			
			i.UTXO = Chain.UTXOs.get(i.transactionOutputId);
			
		}
		
		if(getInputsValue() < Chain.minimumTransaction) {
			
			System.out.println("Transaction inputs to small: " + getInputsValue());
			return false;
		}
		
		float leftOver = getInputsValue() - value;
		transactionId = calculateHash();
		outputs.add(new TransactionOutput(this.recipient, value, transactionId));
		outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));
	
		for (TransactionOutput o: outputs) {
			Chain.UTXOs.put(o.id, o);
			
		}
		
		return true;
		
	}
	
	public float getInputsValue() {
		
		
		float total = 0;
		
		for (TransactionInput i : inputs) {
			if(i.UTXO == null) continue;
			total += i.UTXO.value;
		}
		return total;
	}
	
	public float getOutputsValue() {
		
		float total = 0;
		
		for(TransactionOutput o: outputs) {
			
			total += o.value;
		}
		
		return total;
	}
	
	
}
