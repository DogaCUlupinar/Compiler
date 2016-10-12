

public class StructType extends CompositeType {

	public StructType(String strName, int size) {
		super(strName, size);
		// TODO Auto-generated constructor stub
	}
	
	public StructType(String strName) {
		super(strName, 0);
		// TODO Auto-generated constructor stub
	}
	
	
	public void 
	setBaseName (String name) {
		this.baseName = name;
	}
	
	public String
	getBaseName () {
		return this.baseName;
		
	}
	
	
	private String baseName = null;
	

}
