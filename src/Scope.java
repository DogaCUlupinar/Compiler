

import java.util.Vector;


class Scope
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	Scope ()
	{
		m_lstLocals = new Vector<STO> ();
		isWhile = false;
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public STO
	access (String strName)
	{
		return	accessLocal (strName);
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public STO
	accessLocal (String strName)
	{
		STO		sto = null;

		for (int i = 0; i < m_lstLocals.size (); i++)
		{
			sto = m_lstLocals.elementAt (i);

			if (sto.getName ().equals (strName))
				return (sto);
		}

		return (null);
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void
	InsertLocal (STO sto)
	{
		m_lstLocals.addElement (sto);
	}
	
	public void setisWhile() {
		this.isWhile = true;
	}

	public boolean getWhile() {
		return isWhile;
	}
	
	public void setisIf() {
		this.isIf = true;
	}

	public boolean getisIf() {
		return isIf;
	}
	
	
	
	public void 
	RemoveLocal (STO sto) {
		this.m_lstLocals.removeElement(sto);
	}
	
	public int
	getLocalOffSet(){
		return Offset;
	}
	
	public void
	incOffset(){
		Offset -= 4 ;
	}
//----------------------------------------------------------------
//	Instance variables.
//----------------------------------------------------------------
	private Vector<STO>		m_lstLocals;
	private int Offset = -4;
	private boolean isWhile;
	private boolean isIf;
}	
