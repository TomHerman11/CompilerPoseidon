package IR;

import MIPS.MIPSER;

public class IRcommand_Jump_Label extends IRcommand {
    String name;
    public IRcommand_Jump_Label(String label_start) {
        super();
        name = label_start;
    }

    @Override
    public void MIPSme() {
        MIPSER.getInstance().jump(name);
    }

    public String labelName() { return name; }
	
	public String ref_labelName() {
		return labelName();
	}

}
