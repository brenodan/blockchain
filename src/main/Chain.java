package main;

import java.util.ArrayList;

import com.google.gson.GsonBuilder;

public class Chain {

	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	
	public static void main(String[] args) {
		
		blockchain.add(new Block("I am the alpha block", "0"));
		blockchain.add(new Block("I am the second block", blockchain.get(blockchain.size()-1).hash));
		blockchain.add(new Block("I am the third block", blockchain.get(blockchain.size()-1).hash));
		
		
		/*
		Block genesisBlock = new Block("I am the alpha block", "0");
		System.out.println("Hash for alpha block: " + genesisBlock.hash);
		
		Block secondBlock = new Block("I am the second block", genesisBlock.hash);
		System.out.println("Hash for 2nd block: " + secondBlock.hash);
		
		Block thirdBlock = new Block("I am the third block", secondBlock.hash);
		System.out.println("Hash for 3rd block: " + thirdBlock.hash);
		*/
		
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println(blockchainJson);
		
	}
	
	public static Boolean isChainValid() {
		
		Block cBlock;
		Block pBlock;
		
		for (int i = 0; i < blockchain.size(); i++) {
			
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
			return true;
			
		}
		
		return true;
		
	}
	
}
