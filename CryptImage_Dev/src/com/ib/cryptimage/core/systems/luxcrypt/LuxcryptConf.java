package com.ib.cryptimage.core.systems.luxcrypt;



public final class LuxcryptConf {

	private static LuxcryptGui gui;

	public static LuxcryptGui getGui() {
		return gui;
	}

	public static void setGui(LuxcryptGui gui) {
		LuxcryptConf.gui = gui;
	}
	
	public static boolean isDisableHorSync = true;
	public static boolean isDisableVerSync = true;
	public static boolean isInverse = false;
	
	public static int width = 768;
	public static int height = 576;
	
	public static int frameStart = 1;
	public static int frameEnd = 200000;
	
	public static int selectedFrameStart = 1;
	public static int selectedFrameEnd = 200000;
}
