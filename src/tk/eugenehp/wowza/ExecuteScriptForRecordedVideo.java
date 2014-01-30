package tk.eugenehp.wowza;

import java.io.*; 
import java.util.*;

import com.wowza.wms.application.*;
import com.wowza.wms.module.*;
import com.wowza.wms.stream.*;

public class ExecuteScriptForRecordedVideo extends ModuleBase {

	class WriteListener implements IMediaWriterActionNotify
	{
		public String script = "";
		public void onFLVAddMetadata(IMediaStream stream, Map<String, Object> extraMetadata)
		{
			getLogger().info("ModuleWriteListener.onFLVAddMetadata["+stream.getContextStr()+"]");
		}

		public void onWriteComplete(IMediaStream stream, File file)
		{
			getLogger().info("ModuleWriteListener.onWriteComplete["+stream.getContextStr()+"]: "+file);
			getLogger().info("ExecuteScriptForRecordedVideo script name is "+script);
			String result = this.executeCommand(script+" "+file);
			getLogger().info("ExecuteScriptForRecordedVideo result is\n\n"+result+"\n\n====================\n\n");
		}
		
		private String executeCommand(String command) {
			StringBuffer output = new StringBuffer();
			Process p;
			try {
				p = Runtime.getRuntime().exec(command);
				p.waitFor();
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = "";			
				while ((line = reader.readLine())!= null) {
					output.append(line + "\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return output.toString();
		}
	}
	
	public void onAppStart(IApplicationInstance appInstance)
	{
		WriteListener listener = new WriteListener();
		
		WMSProperties appProp = appInstance.getProperties();
		listener.script = appProp.getPropertyStr("script");
		appInstance.addMediaWriterListener(listener);
	}

}