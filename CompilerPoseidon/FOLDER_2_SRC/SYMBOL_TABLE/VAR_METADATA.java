package SYMBOL_TABLE;


//metadata - who am I? (global/local/data_member/argument) + what's my number
public class VAR_METADATA {

	public enum VAR_TYPE {
		GLOBAL, LOCAL, DATA_MEMBER, ARGUMENT;
	}
	private VAR_TYPE var_type;
	private int index;

	public VAR_METADATA(VAR_TYPE var_type, int index) {
		this.var_type = var_type;
		this.index = index;
	}

	public VAR_TYPE getVarStorageType() {return this.var_type;}
	public int getVarIndexInStorage() { return this.index; }
}
