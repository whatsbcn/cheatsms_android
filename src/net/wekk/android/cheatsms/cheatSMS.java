package net.wekk.android.cheatsms;

import net.wekk.android.cheatsms.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class cheatSMS extends Activity implements OnClickListener {
	private String TAG;

	TextView textNewMessage;
	SharedPreferences prefs;
	DBHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	SimpleCursorAdapter adapter;
	ListView messagesList;
	Intent i;

	
	 public boolean getFirstRun() {
	    return prefs.getBoolean("firstRun", true);
	 }
	 
	 public void setRunned() {
	    SharedPreferences.Editor edit = prefs.edit();
	    edit.putBoolean("firstRun", false);
	    edit.commit();
	 }
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
        // Initialize variables
        TAG=getResources().getString(R.string.app_name);

        // Inflate UI from XML
        setContentView(R.layout.main);
        
        // Get a hang of UI components
        textNewMessage = (TextView)findViewById(R.id.textNewMessage);
        messagesList  = (ListView)findViewById(R.id.listMessages);
        
        // Add onClick listeners
        textNewMessage.setOnClickListener(this);
        
        // Get preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Initialize the database
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        // Load the data (essentially executes a SELECT statement)
        cursor = db.query(DBHelper.TABLE_NAME, new String[] {"_id", DBHelper.COL_FROM, DBHelper.COL_TO, DBHelper.COL_CONTENT, DBHelper.COL_TIMESTAMP2}, null, null, null, null, DBHelper.COL_TIMESTAMP + " DESC");
        startManagingCursor(cursor);
        
        // Set the list adapter
        String[] from = {DBHelper.COL_FROM, DBHelper.COL_TO, DBHelper.COL_CONTENT, DBHelper.COL_TIMESTAMP2};
        int[] to = {R.id.rowFrom, R.id.rowTo, R.id.rowContent, R.id.rowDate};
        adapter = new SimpleCursorAdapter(this, R.layout.message, cursor, from, to);
        messagesList.setAdapter(adapter);

        if(getFirstRun()) {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
            alertbox.setMessage("Cada SMS enviado tiene un coste de 1,2€+IVA.\nRecuerda que tu eres el único responsable de los mensajes que envies.");
            alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface arg0, int arg1) {}
            });
            alertbox.show();
            
        	setRunned();
        }
        
        Log.d(TAG, "onCreated()");
    }
    
    @Override
    public void onRestoreInstanceState(Bundle inState) {
        /*score = (inState!=null) ? inState.getInt("score",0) : 0;
        Log.d(TAG, "onRestoreInstanceState score=" +score);
        editScore.setText( score.toString() );*/
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	/*outState.putInt("score", score);
    	Log.d(TAG, "onSaveInstanceState score=" +score);
    	super.onSaveInstanceState(outState);*/
    }
    

    public void onClick(View src) {
    	switch(src.getId()) {
    	
    	case R.id.textNewMessage:
    		i = new Intent(this, NewMessage.class);
    		startActivity(i);
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
    }
    

}
