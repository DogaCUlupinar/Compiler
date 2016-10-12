
public class ModOpp extends ArithmeticOpp {

	ModOpp(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public ModOpp(){
		this.myName = "%";
	}

	@Override
	public STO checkOperands( STO a, STO b,  Operator o){
		Type aType = a.getType();
		Type bType = b.getType();
		STO result = null;

		if (!(aType instanceof IntType) || !(bType instanceof IntType)) {
			// error
			String name;
			IntType temp = new IntType();
			
			if ( !(aType instanceof IntType))
				name = aType.getName();
			else
				name = bType.getName();
			
			m_errors.print (Formatter.toString(ErrorMsg.error1w_Expr, 
					name,o.getName(),temp.getName()));	
				
				return new ErrorSTO("Error");
				
			} else {
				
				result = doCompare(a,b,o);
				
				if (result  instanceof ConstSTO) {
					result.setType(new IntType());
					return result;
					
				}else if (result instanceof ErrorSTO ) {
					return new ErrorSTO("Error");
				}
				
				return new ExprSTO(a.getName() + b.getName(),new IntType());
			// return ExprSTO of float type
			}

		}
	
	public STO doCompare(STO a, STO b , Operator o) {
		if ((a instanceof ConstSTO )&&  (b instanceof ConstSTO)){
			Double ans = null;
			ConstSTO valueA = (ConstSTO)a;
			ConstSTO valueB = (ConstSTO)b;	
			
			
			if (valueB.getFloatValue() == 0.0)  {	
				m_errors.print(ErrorMsg.error8_Arithmetic);
				return new ErrorSTO("Error");
			}
			
			ans = new Double(valueA.getIntValue() % valueB.getIntValue()); 
	
			return new ConstSTO("const", ans);
		
		}
		else {
			return new VarSTO("xx");
		}
			
	}

}
