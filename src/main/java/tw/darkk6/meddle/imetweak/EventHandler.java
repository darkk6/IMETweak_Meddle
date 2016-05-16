package tw.darkk6.meddle.imetweak;

import java.lang.reflect.Field;

import net.fybertech.meddleapi.MeddleClient.IKeyBindingState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import tw.darkk6.meddle.api.listener.IGuiOpenListener;
import tw.darkk6.meddle.api.util.APILog;
import tw.darkk6.meddle.imetweak.util.Config;
import tw.darkk6.meddle.imetweak.util.IME;
import tw.darkk6.meddle.imetweak.util.NameMap;
import tw.darkk6.meddle.imetweak.util.Reference;

public class EventHandler implements IGuiOpenListener,IKeyBindingState {

	private boolean lastStatus=false;
	private Class lastGUIClass = null;
	
	@Override
	public void onsetKeyBindState(int code, boolean state, KeyBinding keybinding) {
		//按下 Esc 的時候重新載入設定檔
		if(state && code==Keyboard.KEY_ESCAPE){
			if(Config.instance.update())
				APILog.infoChat("重新載入設定檔完成", Reference.LOG_TAG);
		}
	}
	
	@Override
	public void onGuiOpen(GuiScreen gui){
		if(!Config.isEnabled) return;
		IME ime=IME.getInstance();
		if(ime==null) return;
		if( gui==null || gui.getClass()!=lastGUIClass){
			//切換 GUI 了 , 上一個 GUI 是否是要記錄狀態的 GUI
			if(lastGUIClass!=null && Config.guiAutoSwitch.contains(lastGUIClass.getName()))
				lastStatus = ime.isIMEOpen();//是，就記錄剛剛的狀態
			lastGUIClass = (gui==null) ? null : gui.getClass();
		}
		
		if(hasTextField(gui)){
			//如果有 文字輸入區，就啟用輸入法
			ime.enableIME();
			//若是要自動切回的，就切到上次的狀態
			if(Config.guiAutoSwitch.contains(gui.getClass().getName()))
				ime.setIsIMEOpen(lastStatus);
			else ime.setIsIMEOpen(false);
		}else{
			//沒有文字輸入區或者遊戲中(gui=null)
			if(Config.noDisable){
				ime.enableIME();
				ime.setIsIMEOpen(false);
			}else
				ime.disableIME();
		}
	}
	
	/*
	 * 	判斷順序：
	 * 		GUI => null false
	 * 		是否在 autoSwitch 名單 , yes = true
	 * 		是否在 hasText , yes = true
	 * 		是否在 noText , yes = false
	 * 		透過 reflection 檢查是否有 GuiTextField , yes/no = true/false
	 * 		程式出錯 true
	 */
	private boolean hasTextField(GuiScreen gui){
		if(gui==null) return false;
		if(Config.guiAutoSwitch.contains(gui.getClass().getName())) return true;
		else if(Config.cacheHasText.contains(gui.getClass().getName())) return true;
		else if(Config.cacheNoText.contains(gui.getClass().getName())) return false;
		//如果沒有儲存就判斷
		try{
			Class<? extends GuiScreen> cls=gui.getClass();
			Field[] fields=cls.getDeclaredFields();
			for(Field f:fields){
				try{
					if(f.getType() == Class.forName(NameMap.clzGuiTextField)){
						Config.instance.addGUICache(cls, true);
						return true;
					}
				}catch(Exception e){}
			}
			Config.instance.addGUICache(cls, false);
			return false;
		}catch(Exception e){
			e.printStackTrace();
			return true;//怕會誤判，所以傳回 true
		}
	}
}
