

//---------------------------------------------------------------------
//
//---------------------------------------------------------------------
import java.util.*;


class SymbolTable
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	SymbolTable ()
	{
		m_nLevel = 0;
		m_stkScopes = new Stack<Scope> ();
		m_scopeGlobal = null;
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void
	insert (STO sto)
	{
		Scope		scope = m_stkScopes.peek ();

		scope.InsertLocal (sto);
	}
	
	public void
	remove (STO sto)
	{
		Scope		scope = m_stkScopes.peek ();

		scope.RemoveLocal(sto); 
	}
	
	
	public int
	getLocalOffset()
	{
		//for local variables
		//Scope scope = m_stkScopes.peek();
		
		Scope scope = null;
		
		Iterator it = m_stkScopes.iterator();
		while (it.hasNext()) {
			
			scope = (Scope) it.next();
			if (scope.getisIf() == false
					&& scope.getWhile() == false) {//DANGEROUS : maybe need change
				break;
			}
			
		}
		
		int offset = scope.getLocalOffSet();
		scope.incOffset();
		return offset;
				
	}
	
	public int
	getTempOffset()
	{
		//for temp variables
		Scope scope = m_stkScopes.peek();
		int offset = scope.getLocalOffSet();
		return offset;
				
	}

	
	
	public int
	getLocalOffset(int numOffset)
	{
		//num offset is the number of elements we need to offset, not the number of bytes
		//for local variables
		//Scope scope = m_stkScopes.peek();
		
		Scope scope = null;
		
		Iterator<Scope> it = m_stkScopes.iterator();
		while (it.hasNext()) {
			
			scope = (Scope) it.next();
			if (scope.getisIf() == false) {//DANGEROUS : maybe need change
				break;
			}
			
		}
		
		
		for (int i = 0; i < numOffset; i++){
			
			scope.incOffset();
			
		}
		int offset = scope.getLocalOffSet();
		return offset + 4;
				
	}
	
	
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public STO
	accessGlobal (String strName)
	{
		return (m_scopeGlobal.access (strName));
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public STO
	accessLocal (String strName)
	{
		Scope		scope = m_stkScopes.peek ();

		return (scope.accessLocal (strName));
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public STO
	access (String strName)
	{
		Stack		stk = new Stack ();
		Scope		scope;
		STO		stoReturn = null;	
		for (Enumeration<Scope> e = m_stkScopes.elements(); e.hasMoreElements(); )
		{
			stk.push(e.nextElement());
		}

		while (!stk.isEmpty())
		{
			scope = (Scope)stk.pop();
			if ((stoReturn = scope.access (strName)) != null)
				return	stoReturn;
		}

		return (null);
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void
	openScope ()
	{
		Scope		scope = new Scope();

		//	The first scope created will be the global scope.
		if (m_scopeGlobal == null)
			m_scopeGlobal = scope;

		m_stkScopes.push (scope);
		m_nLevel++;
	}
	
	public void
	openScope (String str)
	{
		Scope		scope = new Scope();

		//	The first scope created will be the global scope.
		if (m_scopeGlobal == null)
			m_scopeGlobal = scope;
		
		if (str.equals("while")) {
			scope.setisWhile();
		}
		
		if (str.equals("if")) {
			scope.setisIf();
		}
		
		m_stkScopes.push (scope);
		m_nLevel++;
	}
	
	


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void
	closeScope ()
	{
		m_stkScopes.pop ();
		m_nLevel--;
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public int
	getLevel ()
	{
		return m_nLevel;
	}
	
	public boolean getIsWhile() {
		
		for (Scope s : m_stkScopes) {
			if (s.getWhile() == true)
				return true;
		}
		
		return false;
	}


	//----------------------------------------------------------------
	//	This is the function currently being parsed.
	//----------------------------------------------------------------
	public FuncSTO		getFunc () { return m_func; }
	public void		
	setFunc (FuncSTO sto) 
	{ 
		m_func = sto; 
		m_funcLevel = m_nLevel;
	}
	public boolean
	isInFunc()
	{
		boolean inFunc = true;
		if (m_funcLevel != m_nLevel){
			inFunc = false;
		}
		return inFunc;
	}


//----------------------------------------------------------------
//	Instance variables.
//----------------------------------------------------------------
	private Stack<Scope>  		m_stkScopes;
	private int		m_nLevel;
	private int		m_funcLevel;
	private Scope		m_scopeGlobal;
	private FuncSTO	        m_func = null;

}
