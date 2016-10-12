


public class EqualOpp extends ComparisonOpp {

	public EqualOpp(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public EqualOpp() {
		this.myName = "==";
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public STO checkOperands( STO a, STO b , Operator o){
		Type aType = a.getType();
		Type bType = b.getType();
		STO result = null;

		if ((aType instanceof NumericType) && (bType instanceof NumericType)) {
			
			if ((result = doCompare(a,b,o)) instanceof ConstSTO) {
				result.setType(new BoolType());
				return result;
			}
			
			return new ExprSTO(a.getName() + b.getName(),new BoolType());	
			
			
			} else if (aType instanceof BoolType && bType instanceof BoolType) {
				
				if ((result = doCompare2(a,b,o)) instanceof ConstSTO) {
					result.setType(new BoolType());
					return result;
				}
				
				return new ExprSTO(a.getName() + b.getName(),new BoolType());
			// return ExprSTO of int type
			} else if (aType instanceof NullPointerType &&
						bType instanceof NullPointerType)  {
				
				return new ConstSTO("nullPtr",new BoolType(),1);
				
			} else if (aType instanceof PointerType &&
					bType instanceof PointerType) {
				
				if (aType instanceof NullPointerType || bType instanceof NullPointerType) {
					return new ExprSTO(a.getName() + b.getName(),new BoolType());
				}
				
				PointerType pa = (PointerType)aType;
				PointerType pb = (PointerType)bType;
				
				if (pa.getPointerTo().getName().equals(pb.getPointerTo().getName())) {
					return new ExprSTO(a.getName() + b.getName(),new BoolType());
				}else {
					
					m_errors.print (Formatter.toString(ErrorMsg.error17_Expr, 
							o.getName(),aType.getName(),bType.getName()));
					
					return new ErrorSTO("Error");
					
				}
			
			}else {
					
				m_errors.print (Formatter.toString(ErrorMsg.error1b_Expr, 
						aType.getName(),o.getName(),bType.getName()));
				
				return new ErrorSTO("Error");
			// return ExprSTO of float type
			}

	}
	
	public STO doCompare(STO a, STO b , Operator o) {
		if ((a instanceof ConstSTO )&&  (b instanceof ConstSTO)){
			Double ans = null;
			ConstSTO valueA = (ConstSTO)a;
			ConstSTO valueB = (ConstSTO)b;	
			
			if (valueA.getIntValue() == valueB.getIntValue()) {
				ans = new Double ("1");
			}else {
				ans = new Double ("-1");
			}
	
			return new ConstSTO("const", ans);
		
		}
		else {
			return new VarSTO("xx");
		}
			
	}
	
	
	public STO doCompare2(STO a, STO b , Operator o) {
		if ((a instanceof ConstSTO )&&  (b instanceof ConstSTO)){
			Double ans = null;
			ConstSTO valueA = (ConstSTO)a;
			ConstSTO valueB = (ConstSTO)b;	
			
			if (valueA.getBoolValue() == valueB.getBoolValue()) {
				ans = new Double ("1");
			}else {
				ans = new Double ("-1");
			}
	
			return new ConstSTO("const", ans);
		
		}
		else {
			return new VarSTO("xx");
		}
			
	}

}
