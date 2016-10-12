

public class HelperSTO extends STO {

	public HelperSTO(String strName) {
		super(strName);
		// TODO Auto-generated constructor stub
	}
	public void setOptInit(STO sto){
		this.m_optInit = sto;
	}
	
	public STO getOptInit(){
		return this.m_optInit;
	}
	
	public void setModifierSTO(STO sto){
		this.m_modifierSTO = sto;
	}
	
	public STO getModifierSTO(){
		return this.m_modifierSTO;
	}
	
	private STO m_modifierSTO;
	private STO m_optInit;
}
