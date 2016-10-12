

//---------------------------------------------------------------------
// For typedefs like: typedef int myInteger1, myInteger2;
// Also can hold the structdefs
//---------------------------------------------------------------------

public class TypedefSTO extends STO
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	TypedefSTO (String strName)
	{
		super (strName);
		this.setIsModLValue(false);
	}

	public 
	TypedefSTO (String strName, Type typ)
	{
		super (strName, typ);
		this.setIsModLValue(false);
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean
	isTypedef ()
	{
		return true;
	}
}
