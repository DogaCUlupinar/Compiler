

public class Parameter {
	public Parameter(Type typ, String name) {
		m_type = typ;
		m_name = name;
		m_reference = false;
	}
	
	public Type getType() {
		return m_type;
	}
	
	public String getName() {
		return m_name;
	}
	
	public void setReference(boolean ref){
		m_reference = ref;
	}
	public boolean getReference(){
		return m_reference;
	}
	private Type m_type;
	private String m_name;
	private boolean m_reference;
	
}
