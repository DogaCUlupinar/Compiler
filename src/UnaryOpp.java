


public class UnaryOpp extends Operator {

	UnaryOpp(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	UnaryOpp() {
		// TODO Auto-generated constructor stub
	}
	
	public STO checkOperand( STO a, Operator o){
		Type aType = a.getType();
		STO result = null;
		
		if (!(aType instanceof BoolType)) {
			// error
			BoolType name = new BoolType();
			m_errors.print (Formatter.toString(ErrorMsg.error1u_Expr, 
					aType.getName(),o.getName(),name.getName()));
				
				return new ErrorSTO("Error");
			
		} 
		else {
			
			if ((result = doCalculate(a,o)) instanceof ConstSTO) {
				result.setType(new BoolType());
				return result;
			}	
			
				return new ExprSTO(a.getName(),new BoolType());
			// return ExprSTO of float type
			}
	}
	
	public STO doCalculate(STO a,Operator o) {
		if (a instanceof ConstSTO ){
			Double ans = null;
			ConstSTO valueA = (ConstSTO)a;
			
			if (o.getName().equals("!")) {
				if (!valueA.getBoolValue()) {
					ans = new Double("1");
				}
				else
					ans = new Double("-1");
				
			}
				
			return new ConstSTO("const", ans);
		
		}
		else {
			return new VarSTO("xx");
		}
			
	}
}
