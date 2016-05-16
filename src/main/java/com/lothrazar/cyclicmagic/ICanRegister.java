package com.lothrazar.cyclicmagic;

public interface ICanRegister {

	public void setEnabled(boolean e);
	
	//does nothing if setEnabled was given false
	public void tryRegister(String name);
	
}
