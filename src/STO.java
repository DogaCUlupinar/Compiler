

//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

public abstract class STO
{

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public 
	STO (String strName)
	{
		this(strName, null);
	}

	public 
	STO (String strName, Type typ)
	{
		setName(strName);
		setType(typ);
		setIsAddressable(false);
		setIsModifiable(false);
	}


	
	public void setExtraOffset(int aoff){
		//offset within the array
		m_arrayOffset = aoff;
	}
	public int getExtraOffset(){
		return m_arrayOffset;
	}
	
	public void setNeedsExtraOffSet(boolean status){
		m_isArrayDeRef = status;
	}
	
	public boolean getNeedsExtraOffSet(){
		return m_isArrayDeRef;
	}
	public void
	setIsWrappedArray(boolean status){
		m_isWrappedArray = status;
	}
	public boolean
	getIsWrappedArray(){
		return m_isWrappedArray;
	}
	
	
	
	public void
	setIsGlobal( boolean status){
		m_isGlobal = status;
	}
	
	
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public String
	getName ()
	{
		return m_strName;
	}

	public void
	setName (String str)
	{
		m_strName = str;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public Type
	getType ()
	{
		return	m_type;
	}

	public void
	setType (Type type)
	{
		m_type = type;
	}


	//----------------------------------------------------------------
	// Addressable refers to if the object has an address. Variables
	// and declared constants have an address, whereas results from 
	// expression like (x + y) and literal constants like 77 do not 
	// have an address.
	//----------------------------------------------------------------
	public boolean
	getIsAddressable ()
	{
		return	m_isAddressable;
	}

	public void
	setIsAddressable (boolean addressable)
	{
		m_isAddressable = addressable;
	}

	//----------------------------------------------------------------
	// You shouldn't need to use these two routines directly
	//----------------------------------------------------------------
	private boolean
	getIsModifiable ()
	{
		return	m_isModifiable;
	}

	public void
	setIsModifiable (boolean modifiable)
	{
		m_isModifiable = modifiable;
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
	
	
	public void
	setLabelName(String name){
		this.m_labelName = name;
	}
	
	public String
	getLabelName(){
		return m_labelName;
	}

	//----------------------------------------------------------------
	// A modifiable L-value is an object that is both addressable and
	// modifiable. Objects like constants are not modifiable, so they 
	// are not modifiable L-values.
	//----------------------------------------------------------------
	public boolean
	isModLValue ()
	{
		return	getIsModifiable() && getIsAddressable();
	}

	public void
	setIsModLValue (boolean m)
	{
		setIsModifiable(m);
		setIsAddressable(m);
	}
	
	public void
	setOffset(int offset){
		this.offset = offset;
	}
	
	public int
	getOffset(){
		return this.offset;
	}
	
	public boolean
	getIsGlobal(){
		return m_isGlobal;
	}
	
	public void
	setIsGlobal(){
		m_isGlobal = true;
	}
	
	public boolean getIsInitialized() {
		
		return m_isInitialized;
	}

	public void setIsInitialized(boolean b) {
		// TODO Auto-generated method stub
		m_isInitialized = b;
		
	}
	
	public boolean getIsFuncReturn() {
		
		return m_isFuncReturn;
	}

	public void setIsFuncReturn(boolean b) {
		// TODO Auto-generated method stub
		m_isFuncReturn = b;
		
	}
	
	
	public boolean getIsReturnByRef() {
		
		return m_isReturnByRef;
	}

	public void setIsReturnByRef(boolean b) {
		// TODO Auto-generated method stub
		m_isReturnByRef = b;
		
	}
	
	
	public boolean getIsPassedByRef() {
		
		return m_isPassedByRef;
	}

	public void setIsPassedByRef(boolean b) {
		// TODO Auto-generated method stub
		m_isPassedByRef = b;
		
	}
	
	public boolean getIsPassedByValue() {
		
		return m_isPassedByValue;
	}

	public void setIsPassedByValue(boolean b) {
		// TODO Auto-generated method stub
		m_isPassedByValue = b;
		
	}
	
	
	
	public boolean getIsArrayPassedByRef() {
		
		return m_isArrayPassedByRef;
	}

	public void setIsArrayPassedByRef(boolean b) {
		// TODO Auto-generated method stub
		m_isArrayPassedByRef = b;
		
	}
	
	public boolean getIsStructPassedByRef() {
		
		return m_isStructPassedByRef;
	}

	public void setIsStructPassedByRef(boolean b) {
		// TODO Auto-generated method stub
		m_isStructPassedByRef = b;
		
	}
	
	
	public boolean getIsStructPassedByValue() {
		
		return m_isStructPassedByValue;
	}

	public void setIsStructPassedByValue(boolean b) {
		// TODO Auto-generated method stub
		m_isStructPassedByValue = b;
		
	}
	
	
	public void copyProperties( STO stoFrom){
		this.setExtraOffset(stoFrom.getExtraOffset());
		this.setIsFuncReturn(stoFrom.getIsFuncReturn());
		this.setIsGlobal(stoFrom.getIsGlobal());
		this.setIsPassedByRef(stoFrom.getIsPassedByRef());
		this.setIsReturnByRef(stoFrom.getIsReturnByRef());
		this.setIsWrappedArray(stoFrom.getIsWrappedArray());
		this.setIsPointerDeRef(stoFrom.getIsPointerDeRef());
		this.setLabelName(stoFrom.getLabelName());
		//this.setName(stoFrom.getName());
		this.setNeedsExtraOffSet(stoFrom.getNeedsExtraOffSet());
		this.setOffset(stoFrom.getOffset());
		this.setExtraOffset(stoFrom.getExtraOffset());
	}
	
	
	
	public boolean getIsAddressOf() {

		return m_isAddressOf;
	}
	
	
	public void setIsAddressOf(boolean b) {

		m_isAddressOf = b;

	}

	
	public boolean getIsPointerDeRef() {
		return m_isPointerDeRef;
	}
	
	

	public void setIsPointerDeRef(boolean b) {
		m_isPointerDeRef = b;
	}
	
	
	
	public boolean getIsReturnStructByRef() {
		return this.m_isReturnStructByref;
	}
	
	

	public void setIsReturnStructByRef(boolean b) {
		m_isReturnStructByref = b;
	}
	
	
	public boolean getIsReturnStructByValue() {
		return this.m_isReturnStructByValue;
	}
	
	

	public void setIsReturnStructByValue(boolean b) {
		m_isReturnStructByValue = b;
	}
	
	
	
	
	
	public boolean getIsReturnArrayByRef() {
		return this.m_isReturnArrayByref;
	}
	
	

	public void setIsReturnArrayByRef(boolean b) {
		m_isReturnArrayByref = b;
	}
	
	
	
	
	public void setIsArrow(boolean b) {
		
		 m_isArrow = b;
	}
	
	public boolean getIsArrow() {
		
		return m_isArrow;
	}
	
	
	public void setIsExtern(boolean b){
		m_isExtern = b;
	}
	
	public boolean getIsExtern(){
		return m_isExtern;
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
	setIsPtrArray(boolean b){
		this.m_isPtrArray = b;
	}
	
	public boolean
	getIsPtrArray(){
		return m_isPtrArray;
	}
	//----------------------------------------------------------------
	//	It will be helpful to ask a STO what specific STO it is.
	//	The Java operator instanceof will do this, but these methods 
	//	will allow more flexibility (ErrorSTO is an example of the
	//	flexibility needed).
	//----------------------------------------------------------------
	public boolean	isVar () 	{ return false; }
	public boolean	isConst ()	{ return false; }
	public boolean	isExpr ()	{ return false; }
	public boolean	isFunc () 	{ return false; }
	public boolean	isTypedef () 	{ return false; }
	public boolean	isError () 	{ return false; }


	//----------------------------------------------------------------
	// 
	//----------------------------------------------------------------
	private boolean		m_isPtrArray = false;
	private String      m_labelName;
	private String  	m_strName;
	private boolean 	m_isGlobal = false;
	protected int			offset;
	private Type		m_type;
	private boolean		m_isAddressable;
	private boolean		m_isModifiable;
	private boolean		m_isTypedef = false;
	private boolean 	m_isInitialized = false;
	private boolean		m_isFuncReturn	= false;
	private boolean		m_isReturnByRef	= false;
	private boolean		m_isPassedByRef	= false;
	
	private boolean		m_isPassedByValue	= false;
	private boolean		m_isStructPassedByValue	= false;
	
	private boolean		m_isArrayPassedByRef	= false;
	private boolean		m_isStructPassedByRef	= false;
	
	private int 		m_arrayOffset = 0; //CHANGED LINE
	private boolean		m_isArrayDeRef = false; //CHANGED LINE
	private boolean		m_isWrappedArray = false; //CHANGED
	
	private boolean 	m_isAddressOf = false; //CHANGE2	LINE
	private boolean 	m_isPointerDeRef = false; //CHANGE2	LINE
	
	private boolean 	m_isArrow = false; //CHANGE3
	
	private boolean     m_isExtern = false; //CHANGE4
	private boolean isStatic = false;
	
	private boolean m_isReturnStructByref = false;
	private boolean m_isReturnStructByValue = false;
	private boolean m_isReturnArrayByref = false;
	
	
	

}
