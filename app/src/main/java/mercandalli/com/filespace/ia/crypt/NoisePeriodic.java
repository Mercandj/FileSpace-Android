package mercandalli.com.filespace.ia.crypt;

public class NoisePeriodic implements NoisePredicat {

	private int frequence;
	
	public NoisePeriodic(int pFrequence) {
		frequence = pFrequence;
	}
	
	@Override
	public boolean isValid(int position) {
		if(position%frequence==0)
		return true;
		return false;
	}
}
