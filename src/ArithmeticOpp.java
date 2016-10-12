

public class ArithmeticOpp extends BinaryOpp {

	public ArithmeticOpp(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public ArithmeticOpp() {
		// TODO Auto-generated constructor stub
	}
	
	public STO checkOperands( STO a, STO b, Operator o){
		Type aType = a.getType();
		Type bType = b.getType();
		STO result;

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
			
			} else if (aType instanceof IntType && bType instanceof IntType) {
				
				result = doCalculate(a,b,o);
				if (result instanceof ConstSTO) {
					
					result.setType(new IntType());
					return result;
					
				}else if (result instanceof ErrorSTO) {
					return new ErrorSTO("Error");
				}
					
				return new ExprSTO(a.getName() + b.getName(),new IntType());
			// return ExprSTO of int type
			} else {
				if ((result = doCalculate(a,b,o)) instanceof ConstSTO) {
					result.setType(new FloatType());
					return result;
				}
				
				return new ExprSTO(a.getName() + b.getName(),new FloatType());
			// return ExprSTO of float type
			}
		
	}
	
	public STO doCalculate(STO a, STO b , Operator o) {
		if ((a instanceof ConstSTO )&&  (b instanceof ConstSTO)){
			Double ans = null;
			ConstSTO valueA = (ConstSTO)a;
			ConstSTO valueB = (ConstSTO)b;
			
			if (o.getName().equals("+")) {
				ans = new Double(valueA.getFloatValue() + valueB.getFloatValue());
			}else if (o.getName().equals("-")) {
				ans = new Double(valueA.getFloatValue() - valueB.getFloatValue());
			}else if (o.getName().equals("*")) {
				ans = new Double(valueA.getFloatValue() * valueB.getFloatValue());
			}else if (o.getName().equals("/")) {
				
				if (valueB.getFloatValue() == 0.0)  {	
					m_errors.print(ErrorMsg.error8_Arithmetic);
					return new ErrorSTO("DONTPRINT");
				}
				ans = new Double(valueA.getFloatValue() / valueB.getFloatValue());
			}			
				
			return new ConstSTO("const", ans);
		
		}
		else {
			return new VarSTO("xx");
		}
			
	}

}
