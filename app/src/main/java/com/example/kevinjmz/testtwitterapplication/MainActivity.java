package com.example.kevinjmz.testtwitterapplication;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;
import io.fabric.sdk.android.services.concurrency.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import static io.fabric.sdk.android.services.network.UrlUtils.urlEncode;

public class MainActivity extends AppCompatActivity {

    String key="vO5oCxvbuBwT2LXXW3CBE6jIX";
    String secret_key="qvQouM1YKt0ceMRW3fcgyMpuBbeaq3G8XMQbF1my2tKJvhuJ01";
    String callback_url="http//google.com";

    TwitterLoginButton login;
    Button logout;
    TextView name;
    TwitterAuthToken twitterAuthToken;
    ImageButton record;
    TextView message;

    String finalTweet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig(key,secret_key);
        Fabric.with(this,new Twitter(twitterAuthConfig));

        setContentView(R.layout.activity_main);

        logout = (Button) findViewById(R.id.logout_button);
        login = (TwitterLoginButton) findViewById(R.id.login_button);
        name = (TextView) findViewById(R.id.welcome_name);
        record = (ImageButton) findViewById(R.id.imageButton);
        message = (TextView) findViewById(R.id.tv_result);


        login.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                twitterAuthToken = result.data.getAuthToken();
                name.setText("Hello "+result.data.getUserName());
                login.setVisibility(View.INVISIBLE);//hide login button
                logout.setVisibility(View.VISIBLE);
                record.setVisibility(View.VISIBLE);
                message.setVisibility(View.VISIBLE);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(MainActivity.this, "Sorry couldnt identify you", Toast.LENGTH_SHORT).show();
            }
        });

        //get session
        final TwitterSession twitterSession = Twitter.getSessionManager().getActiveSession();

        logout.setOnClickListener(new View.OnClickListener() { //enable click listener to logout button
            @Override
            public void onClick(View v) {
                if(twitterSession!=null){
                    //gather cookies
                    CookieManager cookieManager = CookieManager.getInstance();
                    if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
                        cookieManager.removeSessionCookies(null);
                    }
                    else{
                        //remove cookies
                        cookieManager.removeSessionCookie();
                    }
                    Twitter.getSessionManager().clearActiveSession(); //clear session
                    Twitter.logOut(); //logout from session


        //enable and disable buttons
                    name.setText("End of session");
                    logout.setVisibility(View.INVISIBLE);
                    record.setVisibility(View.INVISIBLE);
                    message.setVisibility(View.INVISIBLE);
                    login.setVisibility(View.VISIBLE);
                }
            }
        });


        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }


    public void promptSpeechInput(){
        Intent i = new Intent (RecognizerIntent.ACTION_RECOGNIZE_SPEECH); //initialize intent as a speech recognition intent
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);//add language configuration
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something!");//Add custom message to the intent

        try {
            startActivityForResult(i, 100);//notice that the result of the intent will be sent to the key 100 !
        }
        //Toast a message in case device does not support speech recognition
        catch (ActivityNotFoundException e){
            Toast.makeText(MainActivity.this,"Sorry your device doesnt support speech language", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        login.onActivityResult(requestCode,resultCode,data);

        switch (requestCode){
            case 100:
                if (resultCode == RESULT_OK && data != null){
                ArrayList<String> r = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                message.setText(r.get(0));
                Intent i = new Intent(MainActivity.this, EditTweet.class);
                Bundle e = new Bundle();
                e.putSerializable("FROMSPEECH", r.get(0));
                i.putExtras(e);
                startActivityForResult(i, 200);//intent, request code
                break;
            }
            case 200: if (resultCode == RESULT_OK && data != null){
                //send tweet with data retreived from EditTweet class
                finalTweet = data.getData().toString();

                try{
                    String tweet_data = new sendTweet(twitterAuthToken,finalTweet).execute().get();
                    Log.d("Developer",tweet_data);
                    Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                catch (ExecutionException e){
                    e.printStackTrace();
                }
                break;
            }
            default: break;
        }
    }

    protected String writeJSON(String tweetInfo){
        StringBuilder tweetUrl = new StringBuilder("https://api.twitter.com/1.1/statuses/update.json?status=");
        tweetUrl.append(urlEncode(tweetInfo));
        return tweetUrl.toString();
    }

    private class sendTweet extends AsyncTask<String, Void, String > {
        TwitterAuthToken token; //create local variable token
        String message; //creaate local variable message
        public sendTweet(TwitterAuthToken token,String message) { //inner constructor
            this.token = token; //link locals with externals
            this.message = message;
        }
        @Override
        protected String doInBackground(String... strings) {
            final OAuthService s = new ServiceBuilder() //create a service with our information to help us connect to twitter
                    .provider(TwitterApi.SSL.class)
                    .apiKey(key)
                    .apiSecret(secret_key)
                    .callback(callback_url).build();

            Token newAccessToken = new Token(token.token,token.secret); //create a generic Token that uses both tokens to enter Twitter

            //generate a request to the API,
            // in our case it is a POST because we want to post a tweet
            final OAuthRequest request = new OAuthRequest(Verb.POST, writeJSON(message));
            s.signRequest (newAccessToken,request); //let the service do the request by using both, the generic token and the request
            Response response = request.send(); //Twitters response to our request is saved in a Response variable
            String body = response.getBody(); //Response is transformed to String

            return body; //return the body which contains the feedback from Twitter REST API
        }
    }
}
