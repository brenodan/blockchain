package main;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import util.ArrayUtil;
import album.PhotoTransaction;
import album.PhotoIn;
import album.PhotoOut;
import album.Album;

public class AlbumChain {

	public static ArrayList<PhotoBlock> blockchain = new ArrayList<PhotoBlock>();
	public static HashMap<String, PhotoOut> UTXOs = new HashMap<String, PhotoOut>();
	
	public static int difficulty = 3;
	public static float minimumTransaction = 0.1f;
	public static Album albumA;
	public static Album albumB;
	public static PhotoTransaction genesisTransaction;
	
	
	public static void main(String[] args) {
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		albumA = new Album();
		albumA = new Album();
		
//		System.out.println("Private and public keys:");
//		System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
//		System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
//		
//		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
//		transaction.generateSignature(walletA.privateKey);
//		
//		System.out.println("Is signature verified");
//		System.out.println(transaction.verifySignature());
	
		Album photoBase = new Album();
		
		genesisTransaction = new PhotoTransaction(photoBase.publicKey, albumA.publicKey, ArrayUtil.populateArray(""), null);
		genesisTransaction.generateSignature(photoBase.privateKey);
		genesisTransaction.transactionId = "0";
		genesisTransaction.outputs.add(new PhotoOut(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId));
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		System.out.println("Creating and mining genesis block... ");
		PhotoBlock genesis = new PhotoBlock("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);
		
		PhotoBlock block1 = new PhotoBlock(genesis.hash);
		System.out.println("\nAlbumA's balance is: " + albumA.getBalance());
		System.out.println("\nAlbumA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(albumA.sendPhotos(albumB.publicKey, 40f));
		addBlock(block1);
		System.out.println("\nAlbumA's balance is: " + albumA.getBalance());
		System.out.println("AlbumB's balance is: " + albumA.getBalance());
		
		PhotoBlock block2 = new PhotoBlock(block1.hash);
		System.out.println("\nAlbumA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(albumA.sendPhotos(albumB.publicKey, 1000f));
		addBlock(block2);
		System.out.println("\nAlbumA's balance is: " + albumA.getBalance());
		System.out.println("AlbumB's balance is: " + albumB.getBalance());
		
		Block block3 = new Block(block2.hash);
		System.out.println("\nAlbumB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(albumB.sendPhoto(albumA.publicKey, 20));
		System.out.println("\nAlbumA's balance is: " + albumA.getBalance());
		System.out.println("AlbumB balance is: " + albumB.getBalance());
	
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
		
		PhotoBlock cBlock;
		PhotoBlock pBlock;
		
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String, PhotoOut> tempUTXOs = new HashMap<String, PhotoOut>();
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
			
			PhotoOut tempOutput;
			for (int j = 0; j < cBlock.transactions.size(); j++) {
				
				PhotoTransaction cTransaction = cBlock.transactions.get(j);
				if(!cTransaction.verifySignature()){
				
					System.out.println("Signature on Transaction (" + j + ") is *invalid");
					return false;
				}
				
				if(cTransaction.getInputsValue() != cTransaction.getOutputsValue()) {
				
					System.out.println("Inputs are not equal to outputs on Transaction (" + j + ")");
					return false;
				}
				
				for (PhotoIn input : cTransaction.inputs){
					
					tempOutput = tempUTXOs.get(input.photoOutId);
					
					if(tempOutput == null) {
						
						System.out.println("Referenced input on Transaction (" + j + ") is *missing");
						return false;
						
					}
					
					if(input.UTXO.value != tempOutput.value) {
						
						System.out.println("Referenced input on Transaction (" + j + ") is *invalid");
						return false;
					
					}
					
					tempUTXOs.remove(input.photoOutId);
					
				}
				
				for (PhotoOut output: cTransaction.outputs) {
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
	
	public static void addBlock(PhotoBlock newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
	
}
