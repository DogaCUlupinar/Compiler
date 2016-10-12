


public abstract class Type implements Cloneable
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	Type (String strName, int size)
	{
		setName(strName);
		setSize(size);
	}

	public
	Type(){}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	@Override
	public Type clone(){
		try{
		return (Type)super.clone();
		}catch (Exception e){
			return null;
		}
	}
	public String
	getName ()
	{
		return m_typeName;
	}

	public void
	setName (String str)
	{
		m_typeName = str;
	}
	public void
	setTypedefName (String str)
	{
		m_typedefName = str;
	}
	public String
	getTypedefName ()
	{
		return m_typedefName;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public int
	getSize ()
	{
		return m_size;
	}

	public void
	setSize (int size)
	{
		m_size = size;
	}

	
	public boolean
	getIsTypedef ()
	{
		return	this.m_isTypedef;
	}

	public void
	setIsTypedef (boolean s)
	{
		this.m_isTypedef = s;
	}
	
	
	

	//----------------------------------------------------------------
	//	It will be helpful to ask a Type what specific Type it is.
	//	The Java operator instanceof will do this, but you may
	//	also want to implement methods like isNumeric(), isInt(),
	//	etc. Below is an example of isInt(). Feel free to
	//	change this around.
	//----------------------------------------------------------------
	public boolean	isInt ()	{ return false; }


	//----------------------------------------------------------------
	//	Name of the Type (e.g., int, bool, or some typedef
	//----------------------------------------------------------------
	protected  String  	m_typeName;
	protected  int		m_size;
	protected  Type		m_underType;
	protected  String   m_typedefName = null;
	protected  boolean	m_isTypedef = false;
	private    String   m_labelName = null;
	public void setUnderType(Type typ) {
		// TODO Auto-generated method stub
		
	}
	
	
	public Type getUnderType() {
		return this.m_underType;
	}

	public void setLabelName(String strName) {
		
		this.m_labelName = strName;
	}
	public String getLabelName() {
		
		return this.m_labelName;
	}
}
