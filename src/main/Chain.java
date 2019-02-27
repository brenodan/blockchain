package main;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import util.StringUtil;
import wallet.Transaction;
import wallet.TransactionInput;
import wallet.TransactionOutput;
import wallet.Wallet;

import com.google.gson.GsonBuilder;

public class Chain {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
	
	public static int difficulty = 3;
	public static float minimumTransaction = 0.1f;
	public static Wallet walletA;
	public static Wallet walletB;
	public static Transaction genesisTransaction;
	
	
	public static void main(String[] args) {
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		walletA = new Wallet();
		walletB = new Wallet();
		
//		System.out.println("Private and public keys:");
//		System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
//		System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
//		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
//		transaction.generateSignature(walletA.privateKey);
//		System.out.println("Is signature verified");
//		System.out.println(transaction.verifySignature());
	
		Wallet coinbase = new Wallet();
		
		genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
		genesisTransaction.generateSignature(coinbase.privateKey);
		genesisTransaction.transactionId = "0";
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId));
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		System.out.println("Creating and mining genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);
		
		Block block1 = new Block(genesis.hash);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
		addBlock(block1);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		Block block2 = new Block(block1.hash);
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
		addBlock(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		Block block3 = new Block(block2.hash);
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds( walletA.publicKey, 20));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
	
		isChainValid();
		
	}
	
	/*
	public static void main(String[] args) {
		
		blockchain.add(new Block("I am the alpha block", "0"));
		System.out.println("Trying to mine block 1...");
		blockchain.get(0).mineBlock(difficulty);
		
		System.out.println("Trying to Mine block 2... ");
		blockchain.add(new Block("I am the second block", blockchain.get(blockchain.size()-1).hash));
		blockchain.get(1).mineBlock(difficulty);
		
		System.out.println("Trying to Mine block 3... ");
		blockchain.add(new Block("I am the third block", blockchain.get(blockchain.size()-1).hash));
		blockchain.get(2).mineBlock(difficulty);
		
		System.out.println("Blockchain is Valid: " + isChainValid());
		
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe blockchain is valid: " + isChainValid());
		System.out.println(blockchainJson);
		
		//blockchain.add(new Block("I am the third block", blockchain.get(blockchain.size()-1).hash));
		//Block genesisBlock = new Block("I am the alpha block", "0");
		//System.out.println("Hash for alpha block: " + genesisBlock.hash);
		
		//Block secondBlock = new Block("I am the second block", genesisBlock.hash);
		//System.out.println("Hash for 2nd block: " + secondBlock.hash);
		
		//Block thirdBlock = new Block("I am the third block", secondBlock.hash);
		//System.out.println("Hash for 3rd block: " + thirdBlock.hash);
		
		
	}
	*/
	
	
	public static Boolean isChainValid() {
		
		Block cBlock;
		Block pBlock;
		
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		for (int i = 1; i < blockchain.size(); i++) {
			
			cBlock = blockchain.get(i);
			pBlock = blockchain.get(i - 1);
			
			if(!cBlock.hash.equals(cBlock.calculateHash())){
				
				System.out.println("Current hashes are not equal");
				return false;
			
			}
			
			if(!pBlock.hash.equals(cBlock.previousHash)) {
				System.out.println("Previous hashes are not equal");
				return false;
			}
			
			if(!cBlock.hash.substring(0, difficulty).equals(hashTarget)){
				System.out.println("This block hasn't been mined");
				return false;
			}
			
			TransactionOutput tempOutput;
			for (int j = 0; j < cBlock.transactions.size(); j++) {
				
				Transaction cTransaction = cBlock.transactions.get(j);
				if(!cTransaction.verifySignature()){
				
					System.out.println("Signature on Transaction (" + j + ") is *invalid");
					return false;
				}
				
				if(cTransaction.getInputsValue() != cTransaction.getOutputsValue()) {
				
					System.out.println("Inputs are not equal to outputs on Transaction (" + j + ")");
					return false;
				}
				
				for (TransactionInput input : cTransaction.inputs){
					
					tempOutput = tempUTXOs.get(input.transactionOutputId);
					
					if(tempOutput == null) {
						
						System.out.println("Referenced input on Transaction (" + j + ") is *missing");
						return false;
						
					}
					
					if(input.UTXO.value != tempOutput.value) {
						
						System.out.println("Referenced input on Transaction (" + j + ") is *invalid");
						return false;
					
					}
					
					tempUTXOs.remove(input.transactionOutputId);
					
				}
				
				for (TransactionOutput output: cTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}
				
				if (cTransaction.outputs.get(0).recipient != cTransaction.recipient) {
					
					System.out.println("Transaction (" + j + ") output 'change' is not who it should be.");
					return false;
				
				}
				
				if (cTransaction.outputs.get(1).recipient != cTransaction.sender) {
					
					System.out.println("Transaction (" + j + ") output 'change' is not sender.");
					return false;
				
				}
			}
		}
		
		System.out.println("Blockchain is valid");
		return true;
		
	}
	
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
	
}
