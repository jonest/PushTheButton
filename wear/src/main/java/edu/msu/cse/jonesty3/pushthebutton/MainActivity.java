package edu.msu.cse.jonesty3.pushthebutton;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

   GoogleApiClient googleApiClient;

   private TextView mTextView;

   @Override
   protected void onCreate( Bundle savedInstanceState ) {
      super.onCreate( savedInstanceState );
      setContentView( R.layout.activity_main );
      final WatchViewStub stub = (WatchViewStub) findViewById( R.id.watch_view_stub );
      stub.setOnLayoutInflatedListener( new WatchViewStub.OnLayoutInflatedListener() {
         @Override
         public void onLayoutInflated( WatchViewStub stub ) {
            mTextView = (TextView) stub.findViewById( R.id.text );
         }
      } );

      googleApiClient = new GoogleApiClient.Builder( this )
              .addApi( Wearable.API )
              .addConnectionCallbacks( this )
              .addOnConnectionFailedListener( this )
              .build();

      IntentFilter messageFilter = new IntentFilter( Intent.ACTION_SEND );
      MessageReceiver messageReceiver = new MessageReceiver();
      LocalBroadcastManager.getInstance( this ).registerReceiver( messageReceiver, messageFilter );
   }

   @Override
   protected void onStart() {
      super.onStart();
      googleApiClient.connect();
   }

   @Override
   protected void onStop() {
      if ( googleApiClient != null && googleApiClient.isConnected() ) {
         googleApiClient.disconnect();
      }
      super.onStop();
   }

   @Override
   public void onConnected( Bundle bundle ) {

   }

   @Override
   public void onConnectionSuspended( int i ) {

   }

   @Override
   public void onConnectionFailed( ConnectionResult connectionResult ) {

   }

   public void buttonPush( View view ) {
      mTextView.setText( getString( R.string.button_pressed ) );
      String message = getString( R.string.wear_button_pressed );
      new SendToDataLayerThread( getString( R.string.message_path ), message ).start();
   }

   public class MessageReceiver extends BroadcastReceiver {
      @Override
      public void onReceive( Context context, Intent intent ) {
         String message = intent.getStringExtra( getString( R.string.message_key ) );
         mTextView.setText( message );
         mTextView.performHapticFeedback( HapticFeedbackConstants.VIRTUAL_KEY );
      }
   }

   class SendToDataLayerThread extends Thread {
      String path;
      String message;

      SendToDataLayerThread( String path, String message ) {
         this.path = path;
         this.message = message;
      }

      public void run() {
         NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( googleApiClient ).await();
         for ( Node node : nodes.getNodes() ) {
            Wearable.MessageApi.sendMessage( googleApiClient, node.getId(), path, message.getBytes() ).await();
         }
      }
   }
}
