package mercandalli.com.filespace.extras.ia.crypt;


public class Noise extends CryptMethod {

    static final char BORNE_INF = CryptConst.BORNE_INF;
    static final char BORNE_SUP = CryptConst.BORNE_SUP;
    static final char DOMAINE = CryptConst.DOMAINE;
    protected NoisePredicat predicat;

    public Noise(CryptMethod pNext, NoisePredicat pPredicat) {
        super(pNext);
        predicat = pPredicat;
    }

    public Noise(NoisePredicat pPredicat) {
        super();
        predicat = pPredicat;
    }

    @Override
    public void crypter(Message message) {
        message.setValue(bruiter(message.toString()));
        if (next != null)
            next.crypter(message);
    }

    @Override
    public void decrypter(Message message) {
        if (next != null)
            next.decrypter(message);

        message.setValue(deBruiter(message.toString()));
    }

    public String bruiter(String input) {
        String retour = "";
        int i = 0, j = 0;
        while (i < input.length()) {
            if (predicat.isValid(j)) {
                retour += input.charAt(i);
                i++;
            } else //BRUIT
                retour += Character.toChars((CryptConst.random(BORNE_INF, BORNE_SUP) - BORNE_INF) % (DOMAINE) + BORNE_INF)[0];

            j++;
        }
        /*for(int i=0;i<2*input.length(); i++) {
			if(predicat.isValid(i))
				retour += input.charAt(i/2);
			else //BRUIT
				retour += Character.toChars((random(BORNE_INF, BORNE_SUP)-BORNE_INF)%(DOMAINE)+BORNE_INF)[0];
		}*/
        return retour;
    }

    public String deBruiter(String input) {
        String retour = "";
        for (int i = 0; i < input.length(); i++) {
            if (predicat.isValid(i))
                retour += input.charAt(i);
        }
        return retour;
    }


}
