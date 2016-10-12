


public class ComparisonOpp extends BinaryOpp {

	ComparisonOpp(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	ComparisonOpp() {
		
		// TODO Auto-generated constructor stub
	}
	
	public STO checkOperands( STO a, STO b,  Operator o){
		Type aType = a.getType();
		Type bType = b.getType();
		STO result = null;
		
		if (!(aType instanceof NumericType) || !(bType instanceof NumericType)) {
			// error
			String name;
			
			if ( !(bType instanceof NumericType))
				name = bType.getName();
			else
				name = aType.getName();
			
			m_errors.print (Formatter.toString(ErrorMsg.error1n_Expr, 
					name,o.getName()));
			
				return new ErrorSTO("Error");
			
		} 
		else {
			if ((result = doCompare(a,b,o)) instanceof ConstSTO) {
				result.setType(new BoolType());
				return result;
			}
				return new ExprSTO(a.getName() + b.getName(),new BoolType());
			// return ExprSTO of float type
		}

	}
	
	
	public STO doCompare(STO a, STO b , Operator o) {
		if ((a instanceof ConstSTO )&&  (b instanceof ConstSTO)){
			Double ans = null;
			ConstSTO valueA = (ConstSTO)a;
			ConstSTO valueB = (ConstSTO)b;
			
			if (o.getName().equals("<=")) {
				
				if (valueA.getFloatValue() <= valueB.getFloatValue()) {
					ans = new Double("1");
				}
				else 
					ans = new Double("-1");
			}else if (o.getName().equals(">=")) {
				
				if (valueA.getFloatValue() >= valueB.getFloatValue()) {
					ans = new Double("1");
				}
				else 
					ans = new Double("-1");
			}else if (o.getName().equals(">")) {
				
				if (valueA.getFloatValue() > valueB.getFloatValue()) {
					ans = new Double("1");
				}
				else 
					ans = new Double("-1");
			}else if (o.getName().equals("<")) {
					
				if (valueA.getFloatValue() < valueB.getFloatValue()) {
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
