package tw.darkk6.meddle.imetweak;

import java.util.ArrayList;

import net.fybertech.meddle.MeddleMod;
import net.fybertech.meddleapi.MeddleAPI;
import tw.darkk6.meddle.api.ClientEventAPI;
import tw.darkk6.meddle.imetweak.proxy.ClientProxy;
import tw.darkk6.meddle.imetweak.proxy.CommonProxy;
import tw.darkk6.meddle.imetweak.util.Reference;

@MeddleMod(depends={"dynamicmappings", "meddleapi","clienteventapi"},id=Reference.MODID, name=Reference.MOD_NAME, version=Reference.MOD_VER, author="darkk6")
public class IMETweakMod {
	//預設有哪些列表是要自動切換回輸入法的
	public static ArrayList<String> DEFAULT_ENABLE_LIST;
	static{
		/*//原始的的 ClassName
		String[] tmpList=new String[]{
				"net.minecraft.client.gui.inventory.GuiEditSign",
				"net.minecraft.client.gui.GuiScreenBook",
				"net.minecraft.client.gui.GuiChat"
			};
		DEFAULT_ENABLE_LIST=new ArrayList<String>(Arrays.asList(tmpList));
		*/
		//根據這些 Class Name 透過 DynamicMapping 取得 srg
		//DEFAULT_ENABLE_LIST.addAll(Util.getSrgClassNames(DEFAULT_ENABLE_LIST));
		//底下是 for 1.9.4 Only 的 Mapping hardcode
		DEFAULT_ENABLE_LIST=new ArrayList<String>();
		DEFAULT_ENABLE_LIST.add("bec");
		DEFAULT_ENABLE_LIST.add("bgl");
		DEFAULT_ENABLE_LIST.add("bfu");
	}
	
	
	public static CommonProxy proxy = (CommonProxy) MeddleAPI.createProxyInstance(CommonProxy.class.getName(), ClientProxy.class.getName());
	
	public void init(){
		ClientEventAPI.checkApiVersionWithException("1.3");
		
		proxy.init();
	}
}
