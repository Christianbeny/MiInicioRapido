package com.example.miiniciorapido;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoginStatusCallback;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Button compartir ;
    CallbackManager callbackManager;
    LoginButton loginButton;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            PackageInfo info=getPackageManager().getPackageInfo(
                    "com.example.miiniciorapido.MainActivity",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures){
                MessageDigest md=MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));
                System.out.println(Base64.encodeToString(md.digest(),Base64.DEFAULT));
            }
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        callbackManager = CallbackManager.Factory.create();
        loginButton =(LoginButton) findViewById(R.id.login_button);

        // Checking the Access Token.
        if (AccessToken.getCurrentAccessToken() != null) {

            GraphLoginRequest(AccessToken.getCurrentAccessToken());

            // If already login in then show the Toast.
            Toast.makeText(MainActivity.this, "Inicio de Sesión Listo", Toast.LENGTH_SHORT).show();

        } else {

            // If not login in then show the Toast.
            Toast.makeText(MainActivity.this, "Usuario no logeado", Toast.LENGTH_SHORT).show();
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // Calling method to access User Data After successfully login.
                GraphLoginRequest(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login cancelado", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(MainActivity.this, "Login fallado", Toast.LENGTH_SHORT).show();
            }
        });

        compartir = (Button) findViewById(R.id.btnCompartir);
        compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareLinkContent cont = new ShareLinkContent.Builder().setContentUrl(Uri.parse("https://developers.facebook.com/")).build();
                ShareDialog.show(MainActivity.this,cont);
            }
        });
    }
    protected void GraphLoginRequest(AccessToken accessToken) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        compartir.setVisibility(View.VISIBLE);
                    }
                });
        Bundle bundle = new Bundle();
        bundle.putString(
                "fields",
                "id,name,feed"
        );
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
    }
}