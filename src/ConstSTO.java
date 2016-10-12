

//---------------------------------------------------------------------
/*import Type.*;
import Operator.*;
import STO.*;
import java.util.HashMap;*/
//---------------------------------------------------------------------

public class ConstSTO extends STO
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	ConstSTO (String strName, Type typ, String Value)
	{
		super (strName,typ);
        this.setIsAddressable(false);      		// You may want to change the isModifiable and isAddressable                      
		this.setIsModifiable(false);				// fields as necessary
		this.setIsModLValue(false);
		this.isForArray = false;
		if (typ instanceof StrType){
			this.m_strValue = Value;
			this.isStr = true;
		}else{
			m_value =  new Double(Value);
		}
	}	
	
	public 
	ConstSTO (String strName, Type typ)
	{
		super (strName,typ);
        this.setIsAddressable(false);      		// You may want to change the isModifiable and isAddressable                      
		this.setIsModifiable(false);				// fields as necessary
		this.setIsModLValue(false);
		this.isForArray = false;
		m_value = null;
	}	

	public 
	ConstSTO (String strName, Type typ, double val)
	{
		super (strName, typ);
        this.setIsAddressable(false);      		// You may want to change the isModifiable and isAddressable                      
		this.setIsModifiable(false);				// fields as necessary
		this.setIsModLValue(false);
		this.isForArray = false;
		this.setValue(val);
		
	}
	
	public 
	ConstSTO (String strName, Double val)
	{
		super (strName);
        this.setIsAddressable(false);      		// You may want to change the isModifiable and isAddressable                      
		this.setIsModifiable(false);				// fields as necessary
		this.setIsModLValue(false);
		this.isForArray = false;
		this.m_value  = val;
	}
	
	public 
	ConstSTO (String strName)
	{
		super (strName);
        this.setIsAddressable(false);      		// You may want to change the isModifiable and isAddressable                      
		this.setIsModifiable(false);				// fields as necessary
		this.setIsModLValue(false);
		this.isForArray = false;
		m_value = null;
	}	

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean
	isConst () 
	{
		return true;
	}


	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void
	setValue (double val) 
	{
		m_value = new Double(val);
	}

	public Double
	getValue () 
	{
		return m_value;
	}
	
	
	public String
	getStrValue () 
	{
		return this.m_strValue;
	}
	
	public void
	setStrValue (String a ) 
	{
		m_strValue = a;
	}

	public int
	getIntValue () 
	{
		return m_value.intValue();
	}

	public float
	getFloatValue () 
	{
		return m_value.floatValue();
	}

	public boolean
	getBoolValue () 
	{
		return (m_value.intValue() == 1);
	}
	
	public boolean
	getIsForArray()
	{
		return this.isForArray;
	}
	
	public void
	setIsForArray()
	{
		this.isForArray = true;
	}
	
	
	
	
	
	public boolean
	getIsStr()
	{
		return this.isStr;
	}
	
	public void
	setIsStr()
	{
		this.isStr = true;
	}
	
	
	
	
	
	public boolean
	getIsForPointer()
	{
		return this.isForPointer;
	}
	
	public void
	setIsForPointer()
	{
		this.isForPointer = true;
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
	setIsStatic(boolean status) {
		this.isStatic = status;
	}
	
	
	public boolean
	getIsStatic() {
		return this.isStatic;
		
	}
	
	public void
	setOffset(int offset){
		this.offset = offset;
	}
	
	public int
	getOffset(){
		return this.offset;
	}
	
	

//----------------------------------------------------------------
//	Constants have a value, so you should store them here.
//	Note: We suggest using Java's Double class, which can hold
//	floats and ints. You can then do .floatValue() or 
//	.intValue() to get the corresponding value based on the
//	type. Booleans/Ptrs can easily be handled by ints.
//	Feel free to change this if you don't like it!
//----------------------------------------------------------------
        private Double		m_value;
        private String		m_strValue;
    	private int offset;
        private boolean		isForArray;
        private boolean		isForPointer;
        private boolean		isStr = false;
        private int			numOfPointers;
        private boolean		isStatic = false;
        
}
