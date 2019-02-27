package wallet;

import java.security.PublicKey;
import java.util.ArrayList;

import util.LogFile;
import util.StringUtil;

public class LogTransactionOutput {
	
	public String id;
	public PublicKey recipient;
	public float value;
	public String parentTransactionId;
	public ArrayList<LogFile> files;
	
	public LogTransactionOutput(PublicKey recipient, float value, ArrayList<LogFile> files, String parentTransationId){
		
		this.recipient = recipient;
		this.value = value;
		this.parentTransactionId = parentTransationId;
		this.files = files;
		this.id = StringUtil.applySha256(
				StringUtil.getStringFromKey(recipient) 	+ 
				Float.toString(value) 					+ 
				parentTransactionId);
		
	}
	
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == recipient);
	}
}
