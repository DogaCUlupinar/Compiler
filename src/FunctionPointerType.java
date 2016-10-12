

import java.util.Vector;

public class FunctionPointerType extends PointerType {

	public FunctionPointerType(String strName, int size) {
		super(strName, 4);
		// TODO Auto-generated constructor stub
	}
	
	public FunctionPointerType(String strName, int size, Type returnType, String optRef, Vector<Parameter> paramsList)  {
		super(strName, 4);
		
		if (optRef == null) {
			this.setOptRef(false);
		}else {
			this.setOptRef(true);
		}
		
		this.setReturnType(returnType);
		this.setParamsList(paramsList);;
		// TODO Auto-generated constructor stub
	}
	

	public void setReturnType(Type typ) {
		this.m_returnType = typ;
	}
	
	public Type getReturnType() {
		return this.m_returnType; 
	}
	
	public boolean getOptRef() {
		return this.m_optRef;
	}
	
	public void setOptRef(boolean Ref) {
		this.m_optRef = Ref;
	}
	
	public void setParamsList(Vector<Parameter> params) {
		this.m_optParamLists = params;
	}
	
	public Vector<Parameter> getParamsList() {
		return this.m_optParamLists; 
	}
	
	
	public FuncSTO getBaseFunc() {
		return this.m_baseFunc;
	}
	
	public void setBaseFunc(FuncSTO func) {
		this.m_baseFunc = func;
	}
	
	
	
	
	private Type m_returnType;
	private boolean m_optRef;
	private Vector<Parameter> m_optParamLists;
	private FuncSTO m_baseFunc = null;
}
