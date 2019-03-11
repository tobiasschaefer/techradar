package test;

public class Work {
	
	final FPoint np = new FPoint();
	int idx;
	
	public Work () {
	}
	
	public void copyValues (FPoint np, int idx) {
		this.np.x = np.x;
		this.np.y = np.y;
		this.np.cCount = np.cCount;
		this.np.rSum = np.rSum;
		this.np.gSum = np.gSum;
		this.np.bSum = np.bSum;
		this.idx = idx;
	}

}
