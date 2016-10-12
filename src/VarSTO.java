

//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

public class VarSTO extends STO
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	VarSTO (String strName)
	{
		super (strName);
		super.setIsModLValue(true);
		// You may want to change the isModifiable and isAddressable 
		// fields as necessary
	}

	public 
	VarSTO (String strName, Type typ)
	{
		super (strName, typ);
		super.setIsModLValue(true);
		// You may want to change the isModifiable and isAddressable 
		// fields as necessary
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean   
	isVar () 
	{
		return true;
	}
	

	
	public void
	setIsConstant(boolean status) {
		this.isConstant = status;
	}
	

	
	public boolean
	getIsConstant() {
		return this.isConstant;
		
	}
	
	public void
	setOffset(int offset){
		super.offset = offset;
	}
	
	public int
	getOffset(){
		return super.offset;
	}
	
	
	

	private boolean isConstant = false;
}
