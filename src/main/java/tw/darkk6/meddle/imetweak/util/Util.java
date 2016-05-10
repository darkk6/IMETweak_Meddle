package tw.darkk6.meddle.imetweak.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

import net.fybertech.dynamicmappings.DynamicMappings;
import tw.darkk6.meddle.api.util.APILog;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.IntByReference;

public class Util {
	
	public static List<String> getSrgClassNames(List<String> list){
		List<String> res=new ArrayList<String>();
		for(String str:list){
			String cls=DynamicMappings.getClassMapping(str.replaceAll("\\.", "/"));
			System.out.println("*** Mapped as :"+cls+" ("+str+")");
			if(cls==null) continue;
			res.add(cls);
		}
		return res;
	}
	
	
	private static int javaPID=0;
	public static int getProcessID() {
		if (javaPID == 0) {
			RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
			String vmName = bean.getName();
			int pid = Integer.valueOf(vmName.split("@")[0]).intValue();
			javaPID = pid;
		}
		return javaPID;
	}
	
	private static User32 user32 = User32.INSTANCE;

	public static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().startsWith("windows");
	}

	public static void doSearchMCWindow() {
		if (IME.MC_hWnd != null) return;
		user32.EnumWindows(new WinUser.WNDENUMPROC() {
			public boolean callback(WinDef.HWND hWnd, Pointer userData) {
				char[] windowText = new char[''];
				user32.GetWindowText(hWnd, windowText, 128);
				String wText = Native.toString(windowText);
				wText = wText.isEmpty() ? "" : wText;
				//檢查視窗標題是否包含 Minecraft
				if (wText.contains("Minecraft")) {
					char[] classText = new char[''];
					user32.GetClassName(hWnd, classText, 128);
					String cText = Native.toString(classText);
					cText = cText.isEmpty() ? "" : cText;
					//檢查視窗 ClassName 是否是 LWJGL
					if (cText.equalsIgnoreCase("LWJGL")) {
						IntByReference retPID = new IntByReference();
						user32.GetWindowThreadProcessId(hWnd, retPID);
						//最後確認該視窗的 ProcessID 是否和此 Mod 相同
						if (retPID.getValue() == Util.getProcessID()){
							APILog.info("Minecraft Window found:"+hWnd,Reference.MODID);
							IME.MC_hWnd=hWnd;
							return false;//傳回 false 不再繼續
						}
					}
				}
				return true;
			}
		}, null);
	}
}
