public class DecOpp extends UnaryOpp {
	public DecOpp(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public DecOpp() {
		this.myName = "--";
		// TODO Auto-generated constructor stub
	}
	
	public STO checkOperand( STO a, Operator o){
		Type aType = a.getType();
		
		if (!(aType instanceof NumericType)
				&& !(aType instanceof PointerType)) {
			// error
			
			m_errors.print (Formatter.toString(ErrorMsg.error2_Type, 
					aType.getName(),o.getName()));
			
				return new ErrorSTO("Error");	
		} 
		
		if (!a.isModLValue() || aType instanceof ArrayType) {
			m_errors.print (Formatter.toString(ErrorMsg.error2_Lval, 
					o.getName()));
				
				return new ErrorSTO("Error");
		}
		
		
		if (aType instanceof NullPointerType) {
			
			m_errors.print (Formatter.toString(ErrorMsg.error2_Type, 
					aType.getName(),o.getName()));
			
			return new ErrorSTO("Error");	
		}
		
/*		if (aType instanceof PointerType) {
			return new ExprSTO(a.getName(),aType);*/
			
		if (aType instanceof IntType || 
					aType instanceof FloatType || aType instanceof PointerType) {
			
			if (o.getPreOp() == true) { //a--
				//CHANGE 4
				int size = 0;
				if (aType instanceof PointerType){
					size = ((ConstSTO)MyParser.DoSizeOfType(((PointerType)aType).getPointerTo())).getIntValue(); //CHANGE 4 FROM HERE
				}
				VarSTO tempx = new VarSTO("tempa" , aType);
				MyParser.getCodeGen().writePreOp(a, tempx , Template.DEC_OP, size);
				//to here
				return tempx;
			}else {//--a
				int size = 0;
				if (aType instanceof PointerType){
					size = ((ConstSTO)MyParser.DoSizeOfType(((PointerType)aType).getPointerTo())).getIntValue(); //CHANGE 4 FROM HERE
				}
				MyParser.getCodeGen().writePostOp(a,Template.DEC_OP, size); //CHANGE 4 TO HERE
				ExprSTO xx = new ExprSTO(a.getName(),aType);	//DANGEROUS : maybe need copy more
				xx.setOffset(a.getOffset());
				return xx;
			}
			
				
			
		}
		else if (aType instanceof FloatType){
				return new ExprSTO(a.getName(),aType);
			// return ExprSTO of float type
		}
		else {
			
			m_errors.print (Formatter.toString(ErrorMsg.error2_Type, 
					aType.getName(),o.getName()));
			
			return new ErrorSTO("Error");	
		}
	}
}