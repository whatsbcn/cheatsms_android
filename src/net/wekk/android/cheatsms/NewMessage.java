package net.wekk.android.cheatsms;


import java.sql.Timestamp;
import java.util.Calendar;

import net.wekk.android.cheatsms.R;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewMessage extends Activity implements OnClickListener {
	private String TAG;
	Button buttonEnviar;
	EditText textSmsFrom, textSmsTo, textSmsContent;
	SQLiteDatabase db;
	DBHelper dbHelper;
	String premiumNumber = "25532";
	Cursor cursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG=getResources().getString(R.string.app_name);
		
        setContentView(R.layout.newmessage);

    	
        // Get a hang of UI components
        buttonEnviar = (Button)findViewById(R.id.buttonEnviar);
        
        // Add onClick listeners
        buttonEnviar.setOnClickListener(this);
        
        // Initialize the database
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
	}
	
   public void onClick(View src) {
    	switch(src.getId()) {
    	
    	case R.id.buttonEnviar:
    		textSmsFrom = (EditText)findViewById(R.id.textSmsFrom);
    		String from =  textSmsFrom.getText().toString();
    		textSmsTo = (EditText)findViewById(R.id.textSmsTo);
    		String to =  textSmsTo.getText().toString();
    		textSmsContent = (EditText)findViewById(R.id.textSmsContent);
    		String content =  textSmsContent.getText().toString();
    		
    		if (checkSmsParams(from, to, content)) {
	    		
	    		ContentValues values = new ContentValues();
	    		values.put(DBHelper.COL_FROM, textSmsFrom.getText().toString());
	    		values.put(DBHelper.COL_TO, textSmsTo.getText().toString());
	    		values.put(DBHelper.COL_CONTENT, textSmsContent.getText().toString());
	    		values.put(DBHelper.COL_TIMESTAMP, new Timestamp(Calendar.getInstance().getTimeInMillis() ).toString());
	    		db.insert(DBHelper.TABLE_NAME, null, values);
	    		Log.d(TAG, "onClick() buttonOk inserted values="+values);

    			cursor = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
    			startManagingCursor(cursor);

    			//adapter.changeCursor(cursor);
    		
    			sendSMS(premiumNumber, "JOKE " + from + " " + to + " " + content);
    		}

    		break;
    	/*case R.id.buttonPrefs:
    		i = new Intent(this, Preferences.class);
    		startActivity(i);
    		break;
    	case R.id.buttonAbout:
    		i = new Intent(this, About.class);
    		i.putExtra("aboutText", "This is the latest version of GolfCaddy");
    		startActivity(i);
    		break;*/
    	}
    	
    	try  {
    		Thread.sleep(2*1000);
    	} catch(Exception e) {}
    }
    
    private boolean checkSmsParams(String from, String to, String content) {
    	if (from.length() <= 0 || to.length() <= 0 || content.length() <= 0) {
    		Toast.makeText(getBaseContext(), "Tienes que rellenar todos los campos.",
    				Toast.LENGTH_SHORT).show();
    		return false;
    	} else if (to.length() != 9 || (to.charAt(0) != '6' && to.charAt(0) != '7')) {
    		Toast.makeText(getBaseContext(), "El destinatario debe ser un número español sin el +34.",
    				Toast.LENGTH_SHORT).show();
    		return false;
    	} else if (content.length() > 100) {
    		Toast.makeText(getBaseContext(), "El contenido puede ser de 100 carácteres como máximo.",
    				Toast.LENGTH_SHORT).show();    		
    		return false;
    	}
    	return true;
    }
    
    private void sendSMS(String phoneNumber, String message)
    {      
    	/*
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, test.class), 0);                
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, pi, null);        
        */
    	
    	String SENT = "SMS_SENT";
    	String DELIVERED = "SMS_DELIVERED";
    	
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
        
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);
    	
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {

				switch (getResultCode())
				{
				    case Activity.RESULT_OK:
					    Toast.makeText(getBaseContext(), "SMS sent", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					    Toast.makeText(getBaseContext(), "Generic failure", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_NO_SERVICE:
					    Toast.makeText(getBaseContext(), "No service", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_NULL_PDU:
					    Toast.makeText(getBaseContext(), "Null PDU", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_RADIO_OFF:
					    Toast.makeText(getBaseContext(), "Radio off", 
					    		Toast.LENGTH_SHORT).show();
					    break;
					default:
					    Toast.makeText(getBaseContext(), "Error desconocido: " + getResultCode(), 
					    		Toast.LENGTH_SHORT).show();						
				}
			}
        }, new IntentFilter(SENT));
        
        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
				    case Activity.RESULT_OK:
					    Toast.makeText(getBaseContext(), "SMS delivered", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case Activity.RESULT_CANCELED:
					    Toast.makeText(getBaseContext(), "SMS not delivered", 
					    		Toast.LENGTH_SHORT).show();
					    break;					    
				}
			}
        }, new IntentFilter(DELIVERED));        
    	
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);          
        finish();
    }  
	
}