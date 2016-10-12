


public class BooleanOpp extends BinaryOpp {

	BooleanOpp(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	BooleanOpp(){};
	
	
	public STO checkOperands( STO a, STO b,  Operator o){
		Type aType = a.getType();
		Type bType = b.getType();
		STO result = null;
		
		if ((aType instanceof BoolType) && (bType instanceof BoolType)) {
		
			if ((result = doCalculate(a,b,o)) instanceof ConstSTO) {
				result.setType(new BoolType());
				return result;
			}			
				return new ExprSTO(a.getName() + b.getName(),new BoolType());
				
			} 
			else {
				
				String name;
				
				if ( !(bType instanceof BoolType)) {
					name = bType.getName();
				}
				else {
					
					name = aType.getName();
				}			
				
				m_errors.print (Formatter.toString(ErrorMsg.error1n_Expr, 
						name,o.getName()));
				
				return new ErrorSTO("Error");
			}

	}
	
	public STO doCalculate(STO a, STO b , Operator o) {
		if ((a instanceof ConstSTO )&&  (b instanceof ConstSTO)){
			Double ans = null;
			ConstSTO valueA = (ConstSTO)a;
			ConstSTO valueB = (ConstSTO)b;
			
			if (o.getName().equals("&&")) {
				if (valueA.getBoolValue() && valueB.getBoolValue()) {
					ans = new Double("1");
				}
				else
					ans = new Double("-1");
				
			}else if (o.getName().equals("||")) {
				if (valueA.getBoolValue() || valueB.getBoolValue()) {
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
