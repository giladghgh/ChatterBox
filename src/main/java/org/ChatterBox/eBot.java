package org.ChatterBox;

import java.io.IOException;





public abstract class eBot {
	public String prompt;
	public String answer;
	
	public String name;
	
	public abstract void boot() throws IOException;
	public abstract void init();
	public abstract void chat();
}
