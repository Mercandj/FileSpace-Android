package mercandalli.com.filespace.extras.ia.crypt;

import java.util.Random;

public class CryptConst {
	static final char BORNE_INF = ' ';
	static final char BORNE_SUP = 0xfffd;
	static final char DOMAINE = BORNE_SUP+1-BORNE_INF;
	
	public static int random(int min, int max) {
		Random r = new Random();
		return r.nextInt(max - min + 1) + min;
	}
}
