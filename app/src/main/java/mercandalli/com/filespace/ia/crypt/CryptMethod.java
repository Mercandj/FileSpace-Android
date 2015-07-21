package mercandalli.com.filespace.ia.crypt;

/**
 * 
 * Permet de d�finir une cha�ne de cryptage. L'op�ration de cryptage doit �tre inversible, afin de pouvoir d�crypter le message obtenu.
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
