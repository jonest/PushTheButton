package edu.msu.cse.jonesty3.pushthebutton;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Ty on 6/3/2015.
 */
public class MobileListenerService extends WearableListenerService {
   @Override
   public void onMessageReceived( MessageEvent messageEvent ) {
      if ( messageEvent.getPath().equals( getString( R.string.message_path ) ) ) {
         final String message = new String( messageEvent.getData() );

         Intent messageIntent = new Intent();
         messageIntent.setAction( Intent.ACTION_SEND );
         messageIntent.putExtra( getString( R.string.message_key ), message );
         LocalBroadcastManager.getInstance( this ).sendBroadcast( messageIntent );

      } else {
         super.onMessageReceived( messageEvent );
      }
   }
}
