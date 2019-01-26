package main;

public class Main {

	public static void main(String[] args) {
		
		Block genesisBlock = new Block("I am the alpha block", "0");
		System.out.println("Hash for alpha block: " + genesisBlock.hash);
		
		Block secondBlock = new Block("I am the second block", genesisBlock.hash);
		System.out.println("Hash for 2nd block: " + secondBlock.hash);
		
		Block thirdBlock = new Block("I am the third block", secondBlock.hash);
		System.out.println("Hash for 3rd block: " + thirdBlock.hash);
	}
	
}
