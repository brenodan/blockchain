package wallet;

import java.security.PublicKey;

import util.StringUtil;

public class TransactionOutput {

	public String id;
	public PublicKey recipient;
	public float value;
	public String parentTransactionId;
	
	public TransactionOutput(PublicKey recipient, float value, String parentTransationId){
		
		this.recipient = recipient;
		this.value = value;
		this.parentTransactionId = parentTransationId;
		this.id = StringUtil.applySha256(
				StringUtil.getStringFromKey(recipient) 	+ 
				Float.toString(value) 					+ 
				parentTransactionId);
		
	}
	
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == recipient);
	}
	
	
	
}
