package com.tealeaf.plugin.plugins;
import java.util.Map;
import java.io.File;
import java.io.FileOutputStream;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.tealeaf.EventQueue;
import com.tealeaf.GLSurfaceView;
import com.tealeaf.TeaLeaf;
import com.tealeaf.logger;
import com.tealeaf.event.PluginEvent;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import java.util.HashMap;

import com.tealeaf.plugin.IPlugin;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.tealeaf.EventQueue;
import com.tealeaf.event.*;

public class StoreMePlugin implements IPlugin {
	public class StoreMeEvent extends com.tealeaf.event.Event {
		boolean failed;
        int max_outputs = 5;
        int max_inputs = 5;
        Map<String, FileWriter> files_out = new HashMap<String, FileWriter>();
        Map<String, FileReader> files_in = new HashMap<String, FileReader>();
        boolean bOutput = false;
        String result;
        String Filename;
		public StoreMeEvent() {
			super("storeme");
            this.Filename = null;
            this.result = null;
			this.failed = true;
		}
        
        public StoreMeEvent(String Filename, boolean bOutput, String content) {
			super("storeme");
			this.Filename = Filename;
            this.failed = false;
		    
            
            if (bOutput){
                try { 
                    this.bOutput = true;
                    this.filePutContents(this.Filename,content);
                } catch(Exception e){
                    logger.log("{storeme} failed to write contents to ", Filename);
                  this.failed = true;
               }
            }
             else {
                this.result = this.fileGetContents(this.Filename);
                this.failed =false;
             }
		}

        
       
         private boolean openOutFile(String Filename, int Mode, String key){
         _res = false;
         try {    
             _out = openFileOutput(Filename, Mode);
             this.files_out.put(key, new FileWriter(_out.getFD()));
             _res = true;
             
            } catch(Exception e){
               Log.e(LOG_TAG, "File Not Opened for Output");
                _res = false;
            }
            return _res;
    	}
         private boolean openInputFile(String Filename, int Mode, String key){
                 _res = false;
         try {    
             _in = openFileInput(Filename, Mode);
             this.files_in.put(key,  new FileReader(_in.getFD()));
             _res = true;
            } catch(Exception e){
               Log.e(LOG_TAG, "File Not Opened for Input");
                _res = false;
            }
            return _res;
    	}
        public void write(String key, String content){
    	   if (this.files_out.containsKey(key)){
                this.files_out.get(key).write(content.getBytes());
            }
            else {
                logger.log("{storeme} output file not in map ", key);
            }
		}
        public String read(String key){
           String decoded = '';
           if (this.files_in.containsKey(key)){
               byteCount = 8192;
               _read = null;
               
               while(_read !== -1 && (_read === null || _read === byteCount) ){
               char[] rbuff = new char[byteCount];
                
                _read = this.files_in.get(key).read(rbuff,0, byteCount);
                 if (_read > 0){
                   decoded += new String(rbuff, "UTF-8");
                 }
                }           
             }
            else {
                logger.log("{storeme} output file not in map ", key);
                decoded = null;
            }
            return decoded;
		}
	    public void closeInput(String key){
           if(this.files_in.containsKey(key)){
               try {
                   this.files_in.get(key).close();
                   
               }catch(Exception e){
                   logger.log("{storeme} failed to close input file ", key );
                   
               }
               finally {
                   this.files_in.remove(key);
                   
               }
               
           }
         
         
	    }
        
        public void closeOutput(String key){
           if(this.files_out.containsKey(key)){
               try {
                   this.files_out.get(key).close();
                   
               }catch(Exception e){
                   logger.log("{storeme} failed to close output file ", key );
                   
               }
               finally {
                   this.files_out.remove(key);
                   
               }
               
           }
         
         
        }
        
        public String fileGetContents(String Filename){
            String key = Filename;
            boolean bBare = true;
            String result = null;
             while (this.files_in.containsKey(key)){
                if (bBare) key.concat("_");
                key.concat("1");
            }
            try {
            this.openInputFile(Filename, Context.MODE_PRIVATE, key );
             result = this.read(key);
            this.closeInput(key);
            } catch(Exception e){
                logger.log("{storeme} failed to read contents of ", Filename);
                result = null;
            }
            return result;
        }
        public void filePutContents(String Filename, String content){
            String key = Filename;
            boolean bBare = true;
          
             while (this.files_out.containsKey(key)){
                if (bBare) key.concat("_");
                key.concat("1");
            }
            try {
            this.openOutFile(Filename, Context.MODE_PRIVATE, key );
             this.write(key, content);
            this.closeOutput(key);
            } catch(Exception e){
                logger.log("{storeme} failed to write contents to ", Filename);
            }
        }
    }


	boolean _file_ask;
	Context _ctx;
	
	public StoreMePlugin() {
	    
	}

	public void onCreateApplication(Context applicationContext) {
		_ctx = applicationContext;
	}

	public void onCreate(Activity activity, Bundle savedInstanceState) {
	
		_file_ask = false;
	}

	public void onResume() {
	}

	public void onStart() {
	}

	public void onPause() {
	}

	public void onStop() {
	}

	public void onDestroy() {
	}

	public void onNewIntent(Intent intent) {
	}

	public void setInstallReferrer(String referrer) {
	}

	public void onActivityResult(Integer request, Integer result, Intent data) {
	}

	
	public void onRequest(String jsonData) {
		try {
            JSONObject data = new JSONObject(jsonData);
            String method = data.optString("method","read");
            String filename = data.optString("filename", "");
            String content = data.optString("content", "");
            String bOutput = false;
            if (method.equals("write")){
                bOutput = true;
            }
             EventQueue.pushEvent(new StoreMeEvent(filename, bOutput,content));            
			
			}	
		 catch (Exception e) {
			logger.log(e);
			e.printStackTrace();
			EventQueue.pushEvent(new StoreMeEvent());
		}
	}

	public void logError(String error) {
	}

	public boolean consumeOnBackPressed() {
		return false;
	}

	public void onBackPressed() {
	}
}

