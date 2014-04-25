package HIP;

public class tuple {
	int m = -1;
	int n = -1;
	int u = -1;
	int itr = -1;
	double p_c = -1;
	double p_m = -1;
	String FileName = null;
	
	public tuple(int m, int n, int u, int itr, double p_c, double p_m, String FileName){
		this.m=m;
		this.n=n;
		this.u=u;
		this.itr = itr;
		this.p_c = p_c;
		this.p_m = p_m;
		this.FileName = FileName;
	}
	public int getm(){
		return m;
	}
	public int getn(){
		return n;
	}
	public int getu(){
		return u;
	}
	public int getItr(){
		return itr;
	}
	public double getp_c(){
		return p_c;
	}
	public double getp_m(){
		return p_m;
	}
	public String getString(){
		return FileName;
	}
}
