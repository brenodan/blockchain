package wallet;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

import main.LogChain;
import util.LogFile;
import util.StringUtil;

public class LogTransaction {
	public String transactionId;//hash of the transaction 
	public PublicKey sender;
	public PublicKey recipient;
	public float value;
	public ArrayList<LogFile> files;
	public byte[] signature;
	
	public ArrayList<LogTransactionInput> inputs = new ArrayList<LogTransactionInput>();
	public ArrayList<LogTransactionOutput> outputs = new ArrayList<LogTransactionOutput>();
	
	private static int sequence = 0;
	
	public LogTransaction(PublicKey from, PublicKey to, float value, ArrayList<LogTransactionInput> inputs) {
		
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
		
		for (LogTransactionInput i : inputs) {
			
			i.UTXO = LogChain.UTXOs.get(i.transactionOutputId);
			
		}
		
		if(getInputsValue() < LogChain.minimumTransaction) {
			
			System.out.println("Transaction inputs to small: " + getInputsValue());
			return false;
		}
		
		ArrayList<LogFile> leftOverLogFiles = getInputsLogFileValue();
		
		float leftOver = getInputsValue() - value;
		transactionId = calculateHash();
		outputs.add(new LogTransactionOutput(this.recipient, value, files, transactionId));
		outputs.add(new LogTransactionOutput(this.sender, leftOver, leftOverLogFiles, transactionId));
	
		for (LogTransactionOutput o: outputs) {
			LogChain.UTXOs.put(o.id, o);
			
		}
		
		return true;
		
	}
	
	public ArrayList<LogFile> removeLogFiles(ArrayList<LogFile> leftOverLogFiles, ArrayList<LogFile> files){
		ArrayList<LogFile> returnArray = leftOverLogFiles;
		for (int i = 0; i < leftOverLogFiles.size(); i++) {
			LogFile temp = leftOverLogFiles.get(i);
			for (int j = 0; j < files.size(); j++) {
				if(temp == files.get(j)){
					leftOverLogFiles.remove(i);
					break;
				}
			}
		}
		
		return returnArray;
	}
	
	public ArrayList<LogFile> getInputsLogFileValue() {
		
		ArrayList<LogFile> totalLogFiles = new ArrayList<LogFile>();
		
		for (LogTransactionInput i : inputs) {
			if(i.UTXO == null) continue;
			
			for(int n = 0; n < i.UTXO.files.size(); n++) {
				totalLogFiles.add(i.UTXO.files.get(n));
			}
		}
	
		return totalLogFiles;
	}
	
	public ArrayList<LogFile> getOutputsLogFileValue() {
		
		ArrayList<LogFile> totalLogfiles = new ArrayList<LogFile>();
		for (LogTransactionOutput o: outputs) {
			
			for(int i = 0; i < o.files.size(); i++){
				totalLogfiles.add(o.files.get(i));
			}
		}
		
		return totalLogfiles;
	}
	
	public float getInputsValue() {
		
		float total = 0;
		
		for (LogTransactionInput i : inputs) {
			if(i.UTXO == null) continue;
			total += i.UTXO.value;
		}
		return total;
	}
	
	public float getOutputsValue() {
		
		float total = 0;
		
		for(LogTransactionOutput o: outputs) {
			
			total += o.value;
		}
		
		return total;
	}
}
