package wallet;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import util.LogFile;
import main.LogChain;

public class LogWallet {
	
	public PrivateKey privateKey;
	public PublicKey publicKey;
	
	public HashMap<String, LogTransactionOutput> UTXOs = new HashMap<String, LogTransactionOutput>();	
	
	public LogWallet(){
		
		generateKeyPair();
		
	}
	
	public void generateKeyPair() {
		
		try {
			
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// initialize the key generator and generate a keypair
			keyGen.initialize(ecSpec, random); // 256 bytes provides an acceptable security level
			KeyPair keyPair = keyGen.generateKeyPair();
			
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public float getBalance() {
		
		float total = 0;
		
		for (Map.Entry<String, LogTransactionOutput> item: LogChain.UTXOs.entrySet()) {
		
			LogTransactionOutput UTXO = item.getValue();
			
			if(UTXO.isMine(publicKey)) {
			
				UTXOs.put(UTXO.id,  UTXO);
				total += UTXO.value;
			
			}
		}
		
		return total;
		
	}
	
	public ArrayList<LogFile> getLogHistory() {
		
		ArrayList<LogFile> logfiles = new ArrayList<LogFile>();
		
		for (Map.Entry<String, LogTransactionOutput> item: LogChain.UTXOs.entrySet()) {
		
			LogTransactionOutput UTXO = item.getValue();
			if(UTXO.isMine(publicKey)) {
			
				UTXOs.put(UTXO.id,  UTXO);
				for(int i = 0; i < UTXO.files.size(); i++){
					logfiles.add(UTXO.files.get(i));
				}
			}
		}
		
		return logfiles;
		
	}
	
	public LogTransaction sendFunds(PublicKey _recipient, float value) {
		
		if(getBalance() < value) {
			
			System.out.println("Not enough funds to send transaction. Transaction Discarded.");
			return null;
			
		}
		
		ArrayList<LogTransactionInput> inputs = new ArrayList<LogTransactionInput>();
		
		float total = 0;
		
		for (Map.Entry<String, LogTransactionOutput> item: UTXOs.entrySet()) {
			
			LogTransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new LogTransactionInput(UTXO.id));
			
			if(total > value){
				break;
			}
		}
		
		LogTransaction newTransaction = new LogTransaction(publicKey, _recipient, value, inputs);
		newTransaction.generateSignature(privateKey);
		
		for (LogTransactionInput input: inputs) {
		
			UTXOs.remove(input.transactionOutputId);
		
		}
		
		return newTransaction;
	}
}
