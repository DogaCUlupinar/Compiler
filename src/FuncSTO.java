
import java.util.ArrayList;
import java.util.Vector;


//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

public class FuncSTO extends STO
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	FuncSTO (String strName)
	{
		super (strName);
		setReturnType (null);
		this.m_isReference = false;
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
		this.m_hasReturn = false;
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean
	isFunc () 
	{ 
		return true;
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
	}


	//----------------------------------------------------------------
	// This is the return type of the function. This is different from 
	// the function's type (for function pointers).
	//----------------------------------------------------------------
	public void
	setReturnType (Type typ)
	{
		m_returnType = typ;
	}

	public Type
	getReturnType ()
	{
		return m_returnType;
	}
	
	public void
	setFuncType (Type typ) {
		m_funcType = typ;
	}

	public Type
	getFuncType ()
	{
		return m_funcType;
	}
	
	public void
	setArgCount (int num) {
		m_argCount = num;
	}

	public int
	getArgCount ()
	{
		return m_argCount;
	}
	
	
	public void
	setParameters (Vector<Parameter> paraVector) {
		this.m_paraVector = paraVector;
	}

	public Vector<Parameter>
	getParameters ()
	{
		return m_paraVector;
	}
	
	public void
	setIsReference (boolean ref) {
		this.m_isReference = ref;
	}
	
	public boolean
	getIsReference () {
		return this.m_isReference;
	}
	
	public boolean
	getHasReturn(){
		return this.m_hasReturn;
	}
	
	public void
	setHasReturn(boolean hasReturn){
		this.m_hasReturn = hasReturn;
	}	
	
	public boolean
	getIsOverLoading(){
		return this.m_isOverLoading;
	}
	
	public void
	setIsOverLoading(boolean flag){
		this.m_isOverLoading = flag;
	}


//----------------------------------------------------------------
//	Instance variables.
//----------------------------------------------------------------
	private Type 		m_returnType;
	private Type 		m_funcType;
	private int 		m_argCount;
	private boolean 	m_isReference;
	private Vector<Parameter>	m_paraVector;
	private boolean		m_hasReturn;
	private boolean 	m_isOverLoading;
}


















