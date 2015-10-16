package mercandalli.com.filespace.extras.ia.crypt;

/**
 * 
 * Permet de définir une chaine de cryptage. L'opération de cryptage doit être inversible, afin de pouvoir décrypter le message obtenu.
 * 
 */
public abstract class CryptMethod {
	protected CryptMethod next;
	
	public CryptMethod(CryptMethod pNext) {
		next = pNext;
	}
	
	public CryptMethod() {
		
	}
	
	public abstract void crypter(Message message);
	
	public abstract void decrypter(Message message);
}
