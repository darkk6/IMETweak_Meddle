package tw.darkk6.meddle.imetweak;

import java.util.ArrayList;

import net.fybertech.meddle.MeddleMod;
import net.fybertech.meddleapi.MeddleAPI;
import tw.darkk6.meddle.api.ClientEventAPI;
import tw.darkk6.meddle.imetweak.proxy.ClientProxy;
import tw.darkk6.meddle.imetweak.proxy.CommonProxy;
import tw.darkk6.meddle.imetweak.util.NameMap;
import tw.darkk6.meddle.imetweak.util.Reference;

@MeddleMod(depends={"dynamicmappings", "meddleapi","clienteventapi"},id=Reference.MODID, name=Reference.MOD_NAME, version=Reference.MOD_VER, author="darkk6")
public class IMETweakMod {
	//預設有哪些列表是要自動切換回輸入法的
	public static ArrayList<String> DEFAULT_ENABLE_LIST;
	static{
		DEFAULT_ENABLE_LIST=new ArrayList<String>();
		DEFAULT_ENABLE_LIST.add(NameMap.clzGuiChat);
		DEFAULT_ENABLE_LIST.add(NameMap.clzGuiEditSign);
		DEFAULT_ENABLE_LIST.add(NameMap.clzGuiScreenBook);
	}
	
	
	public static CommonProxy proxy = (CommonProxy) MeddleAPI.createProxyInstance(CommonProxy.class.getName(), ClientProxy.class.getName());
	
	public void init(){
		ClientEventAPI.checkApiVersionWithException("1.3");
		
		proxy.init();
	}
}
