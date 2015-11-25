package com.mercandalli.android.filespace.extras.ia.crypt;

public class CryptOffset extends CryptMethod {
    static final char BORNE_INF = CryptConst.BORNE_INF;
    static final char BORNE_SUP = CryptConst.BORNE_SUP;
    static final char DOMAINE = CryptConst.DOMAINE;
    protected int offset;

    public CryptOffset(CryptMethod pNext, int pOffset) {
        super(pNext);
        offset = pOffset;
    }

    public CryptOffset(int pOffset) {
        super();
        offset = pOffset;
    }

    @Override
    public void crypter(Message message) {
        message.setValue(crypteCesar(message.toString(), offset));
        if (next != null)
            next.crypter(message);
    }

    @Override
    public void decrypter(Message message) {
        if (next != null)
            next.decrypter(message);
        message.setValue(decrypteCesar(message.toString(), offset));
    }

    protected String crypteCesar(String input, int offset) {
        String retour = "";
        for (int i = 0; i < input.length(); i++)
            retour += Character.toChars((input.charAt(i) + offset - BORNE_INF) % (DOMAINE) + BORNE_INF)[0];

        return retour;
    }

    protected String decrypteCesar(String input, int offset) {
        return crypteCesar(input, DOMAINE - offset);
    }
}
