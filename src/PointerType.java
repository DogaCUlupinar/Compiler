

public class PointerType extends PointerGroupType {

	public PointerType(String strName, int size) {
		super(strName, 4);
		// TODO Auto-generated constructor stub
	}
	
	public PointerType(String strName) {
		super(strName,4);
		// TODO Auto-generated constructor stub
	}
	

	public Type getPointerTo () {
		return this.m_pointerTo;
	}
	
	public void setPointerTo (Type typ) {
		this.m_pointerTo = typ;
		
	}
	public void
	setnumOfPointers(int num)
	{
		this.numOfPointers = num;
	}
	
	public int
	getnumOfPointers()
	{
		return this.numOfPointers;
	}
	
	public void
	setBaseType(Type type)
	{
		this.baseType = type;
	}
	
	public Type
	getBaseType()
	{
		return this.baseType;
	}
	
	private Type m_pointerTo;
	private int numOfPointers = 1;
	private Type baseType;
	

}
