package powercraft.mobile;

import java.util.List;

import powercraft.management.PC_INBT;
import powercraft.management.PC_GresTextEditMultiline.Keyword;
import powercraft.mobile.PCmo_Command.ParseException;

public interface PCmo_IMinerBrain extends PC_INBT<PCmo_IMinerBrain> {

	public String getName();
	public void setProgram(String prog);
	public String getProgram();
	public void restart();
	public void launch() throws ParseException;
	public boolean hasError();
	public String getError();
	public void run();
	public List<Keyword> getKeywords();
	public void compileProgram(String text) throws Exception;
	public void msg(Object[] obj);
	
}
