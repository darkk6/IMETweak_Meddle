package tw.darkk6.meddle.imetweak.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.fybertech.meddle.MeddleUtil;
import net.fybertech.meddleapi.ConfigFile;
import net.minecraft.client.gui.GuiScreen;
import tw.darkk6.meddle.api.util.APILog;
import tw.darkk6.meddle.imetweak.IMETweakMod;

//此 Mod 不支援中途修改
public class Config {
	public static boolean isEnabled=true,noDisable=false;
	public static List<String> guiAutoSwitch;
	public static List<String> cacheHasText,cacheNoText;
	private static String mcGameVer=MeddleUtil.findMinecraftVersion();
	private static String cfgMCVer;
	
	public static Config instance;
	
	private ConfigFile.ConfigKey<String> keyAuto,keyHas,keyNo;
	
	private File file;
	private ConfigFile cfg;
	private long lastModify=0L;
	
	public Config(File file){
		this.file=file;
		load();
		lastModify = file.lastModified();
	}
	
	public boolean update(){
		if(lastModify != file.lastModified()){
			load();
			lastModify = file.lastModified();
			return true;
		}
		return false;
	}
	
	//提供給  EventHandler.hasTextField() 儲存用的 method
	public void addGUICache(Class<? extends GuiScreen> clz , boolean withTextField){
		if(withTextField){
			cacheHasText.add(clz.getName());
			keyHas.setValue(parseToString(cacheHasText));
		}else{
			cacheNoText.add(clz.getName());
			keyNo.setValue(parseToString(cacheNoText));
		}
		//資料改變了，儲存
		cfg.save();
		lastModify = file.lastModified();
	}
	
	private void load(){
		cfg=new ConfigFile(file);
		cfg.load();
		
		isEnabled=((Boolean)cfg.get(ConfigFile.key(
				"general", "enableIMETweak", Boolean.valueOf(isEnabled),
				"啟用 IMETweak mod"))).booleanValue();
		
		noDisable=((Boolean)cfg.get(ConfigFile.key(
				"general", "dontDisableIME", Boolean.valueOf(noDisable),
				"沒有輸入區的 GUI 不停用輸入法(僅切為英數模式)"))).booleanValue();
		
		ConfigFile.ConfigKey<String> verKey=ConfigFile.key(
				"internal", "minecraftVersion", "-----" ,
				"紀錄版本用，請勿修改");
		
		cfgMCVer=cfg.get(verKey);
		
		keyAuto=ConfigFile.key(
				"internal", "autoSwitchGuiList", parseToString(IMETweakMod.DEFAULT_ENABLE_LIST),
				"自動恢復 IME 狀態的 GUI 列表");
		
		keyHas=ConfigFile.key(
				"internal", "guiWithTextbox","[]","可輸入文字的 GUI 列表");
		
		keyNo=ConfigFile.key(
				"internal", "guiWithoutTextbox", "[]","沒有文字輸入的 GUI 列表");
		
		if( !mcGameVer.equals(cfgMCVer) ){
			//如果版本不符，就不載入 keyAuto,keyHas,keyNo 清單，並且要清空
			verKey.setValue(mcGameVer);
			cfg.get(keyAuto); cfg.get(keyHas); cfg.get(keyNo);
			keyAuto.setValue(parseToString(IMETweakMod.DEFAULT_ENABLE_LIST));
			keyHas.setValue("[]");keyNo.setValue("[]");
			guiAutoSwitch=new ArrayList<String>();
			cacheHasText=new ArrayList<String>();
			cacheNoText=new ArrayList<String>();
			APILog.info("版本變更，清除快取資料",Reference.LOG_TAG);
		}else{
			String tmp;
			tmp=cfg.get(keyAuto);
			guiAutoSwitch=parseToList(tmp);

			tmp=cfg.get(keyHas);
			cacheHasText=parseToList(tmp);
			
			tmp=cfg.get(keyNo);
			cacheNoText=parseToList(tmp);
		}
		
		APILog.info("設定檔載入完成",Reference.LOG_TAG);
		//if(!file.exists() || needSave) cfg.save();
		if(cfg.hasChanged()) cfg.save();
	}
	
	private String parseToString(List<String> list){
		return Arrays.toString(list.toArray());
	}
	
	private List<String> parseToList(String str){
		try{
			String[] arr=str.replaceAll("[\\[ \\]]","").split(",");
			return new ArrayList<String>(Arrays.asList(arr));
		}catch(Exception e){
			APILog.error("Error when list parseing : "+str,Reference.LOG_TAG);
			return new ArrayList<String>(0);
		}
	}
}
