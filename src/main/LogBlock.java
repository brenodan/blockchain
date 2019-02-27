package main;

import java.util.ArrayList;
import java.util.Date;

import util.StringUtil;
import wallet.LogTransaction;

public class LogBlock {
	
	public String hash;
	public String previousHash;
	public String merkleRoot;
	public ArrayList<LogTransaction> transactions = new ArrayList<LogTransaction>();
	//private String data;
	private long timeStamp;
	private int nonce;	
	
//	public Block(String data, String previousHash) {
//		
//		this.data = data;
//		this.previousHash = previousHash;
//		this.timeStamp = new Date().getTime();	
//		this.hash = calculateHash();
//		
//	}
//	
	
	public LogBlock(String previousHash) {
		
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();	
		this.hash = calculateHash();
		
	}
	
	public String calculateHash() {
		
		return StringUtil.applySha256(
				previousHash 				+ 
				Long.toString(timeStamp) 	+ 
				Integer.toString(nonce) 	+
				merkleRoot);

	}
	
	public void mineLogBlock(int difficulty) {
		
		merkleRoot = StringUtil.getLogMerkleRoot(transactions);
		String target = new String(new char[difficulty]).replace('\0', '0');
		
		while (!hash.substring(0, difficulty).equals(target)) {
			
			nonce ++;
			hash = calculateHash();
			
		}
		
		System.out.println("Block Mined!!! : " + hash);
		
	}
	
	public boolean addTransaction(LogTransaction transaction) {
				
		if(transaction == null) {
			return false;
		} 
		
		if((previousHash != "0")){
			
			if(transaction.processTransaction() != true) {
				
				System.out.println("Transaction failed to process. Discarded");
				return false;
			}
		}
		transactions.add(transaction);
		System.out.println("Transaction successfully added to Block");
		return true;
	}
}
