




public abstract class Operator {
	protected String myName;
	
	Operator(String name) {
		myName = name;
	}
	
	Operator() {
		
	}
	public String getName() {
		// TODO Auto-generated method stub
		return myName;
	}
	
	
	public STO checkOperands( STO a, STO b, Operator o){
		return b;

	}
	
	public STO checkOperand( STO a, Operator o){
		return a;
	}

	public void
	setPreOp(boolean s) {
		m_isPreOp = s;
	}
	
	public boolean
	getPreOp() {
		return m_isPreOp;
	}
	
	private 	boolean			m_isPreOp = false;
	protected ErrorPrinter		m_errors = MyParser.m_errors;
}
