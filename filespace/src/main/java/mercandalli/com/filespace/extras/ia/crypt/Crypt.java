package mercandalli.com.filespace.extras.ia.crypt;


public class Crypt {

    static CryptMethod METHODE_1 = new CryptCesar(new Noise(new NoisePeriodic(2)), 69);

    public static String crypte(String pmessage, int offset) {
        Message message = new Message(pmessage);
        (new CryptCesar(new Noise(new NoisePeriodic(2)), offset)).crypter(message);

        return message.toString();
    }

    public static String decrypte(String pmessage, int offset) {
        Message message = new Message(pmessage);
        (new CryptCesar(new Noise(new NoisePeriodic(2)), offset)).decrypter(message);

        return message.toString();
    }
}