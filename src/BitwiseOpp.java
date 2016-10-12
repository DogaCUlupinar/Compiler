


public class BitwiseOpp extends BinaryOpp {

	public BitwiseOpp(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public BitwiseOpp(){};
	
	public STO checkOperands( STO a, STO b,  Operator o){
		Type aType = a.getType();
		Type bType = b.getType();
		STO result = null;

		if ((aType instanceof IntType) && (bType instanceof IntType)) {
			
			if ((result = doCalculate(a,b,o)) instanceof ConstSTO) {
				result.setType(new IntType());
				return result;
			}			
			
				return new ExprSTO(a.getName() + b.getName(),new IntType());
			} 
			else {
				
				String name;
				IntType bb = new IntType();
				
				if ( !(bType instanceof IntType))
					name = bType.getName();
				else
					name = aType.getName();
				
				m_errors.print (Formatter.toString(ErrorMsg.error1w_Expr, 
						name,o.getName(),bb.getName()));
				
				return new ErrorSTO("Error");
			}

	}
	

	
	public STO doCalculate(STO a, STO b , Operator o) {
		if ((a instanceof ConstSTO )&&  (b instanceof ConstSTO)){
			Double ans = null;
			ConstSTO valueA = (ConstSTO)a;
			ConstSTO valueB = (ConstSTO)b;
			
			if (o.getName().equals("&")) {
				ans = new Double(valueA.getIntValue() & valueB.getIntValue());
					
			}else if (o.getName().equals("|")) {
				ans = new Double(valueA.getIntValue() | valueB.getIntValue());
				
			}else if (o.getName().equals("^")) {
				ans = new Double(valueA.getIntValue() ^ valueB.getIntValue());
				
			}
				
			return new ConstSTO("const", ans);
		
		}
		else {
			return new VarSTO("xx");
		}
			
	}
	
	
}
