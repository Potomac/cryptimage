package com.ib.cryptimage.core.systems.discret12;

public class Discret12Conf {
	public static boolean isEncode = true;
	public static boolean isDecode = false;
	
	public static boolean isDiscret11 = false;
	public static boolean isDiscret12 = true;
		
	public static String seedCode = "12345678";
	
	private static Discret12Gui gui;
	
	public static Discret12Gui getGui() {
		return gui;
	}
	
	public static void setGui(Discret12Gui discret12Gui) {
		gui = discret12Gui;
	}
	public static int width = 768;
	public static int height = 576;
	
	public static int frameStart = 1;
	public static int frameEnd = 200000;
	
	public static int selectedFrameStart = 1;
	public static int selectedFrameEnd = 200000;
}
