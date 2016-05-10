package tw.darkk6.meddle.imetweak.proxy;

import java.io.File;

import net.fybertech.meddle.Meddle;
import net.fybertech.meddleapi.MeddleClient;
import tw.darkk6.meddle.api.EventRegister;
import tw.darkk6.meddle.api.util.APILog;
import tw.darkk6.meddle.imetweak.EventHandler;
import tw.darkk6.meddle.imetweak.util.Config;
import tw.darkk6.meddle.imetweak.util.Reference;
import tw.darkk6.meddle.imetweak.util.Util;

public class ClientProxy extends CommonProxy {
	
	
	private EventHandler eventhandler;
	@Override
	public void init(){
		if (!Util.isWindows()) {
			APILog.info("OS is not Windows, IME Tweak mod is disabled",Reference.LOG_TAG);
			return;
		}
		Config.instance=new Config(new File(Meddle.getConfigDir(),Reference.MODID+".cfg"));
		Util.doSearchMCWindow();
		eventhandler=new EventHandler();
		EventRegister.addGuiOpenListener(eventhandler);
		MeddleClient.registerKeyBindStateHandler(eventhandler);
	}
}
