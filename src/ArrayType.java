


public class ArrayType extends CompositeType {

	public ArrayType(String strName, int size) {
		super(strName, size);
		
		// TODO Auto-generated constructor stub
	}
	
	public int
	getArraySize(){
		return this.m_size;
	}

	public void setUnderType(Type typ) {
		// TODO Auto-generated method stub
		this.m_underType = typ;
		
	}
	
	public Type getUnderType() {
		return this.m_underType;
	}
}
