package com.elisoft.radiomovilclasico;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Inicio extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog pDialog;
    Suceso suceso;
    AccessTokenTracker accessTokenTracker;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

getSupportActionBar().setDisplayHomeAsUpEnabled(true);



            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            }

            try {
                PackageInfo info = getPackageManager().getPackageInfo(
                        "com.elisoft.radiomovilclasico",
                        PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    // Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
            } catch (PackageManager.NameNotFoundException e) {

            } catch (NoSuchAlgorithmException e) {

            }


            callbackManager = CallbackManager.Factory.create();
            loginButton = (LoginButton) findViewById(R.id.loginButtonFacebook);

            loginButton.setReadPermissions(Arrays.asList("public_profile, email"));

            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
        //obtenemos todos los datos del usuario
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {

                                    String name = "";
                                    String email = "";
                                    String id_facebook="";
                                    try {

                                        name = object.getString("name");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {

                                        id_facebook = object.getString("id");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        email = object.getString("email");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    menu_usuario(name, email,id_facebook);

                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email");
                    request.setParameters(parameters);
                    request.executeAsync();


                }

                @Override
                public void onCancel() {
                    Toast.makeText(getApplicationContext(), "Se ha cancelado el login", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Toast.makeText(getApplicationContext(), "Se produjo un Error al autentificar." + error, Toast.LENGTH_LONG).show();
                }
            });


    }

    private void menu_usuario(String name, String email, String s_id_facebook) {
        Intent intent=new Intent(getApplicationContext(),Registrar_nombre.class);
        intent.putExtra("name",name);
        intent.putExtra("email",email);
        intent.putExtra("id_facebook",s_id_facebook);
        startActivity(intent);
        finish();


    }

    @Override
    public void onClick(View v) {

    }

    public void mensaje(String mensaje)
    {
        Toast toast = Toast.makeText(this,mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }


    @Override
    protected  void onActivityResult(int requesCode,int resultCode,Intent data)
    {super.onActivityResult(requesCode,resultCode,data);
        callbackManager.onActivityResult(requesCode,resultCode,data);
    }



}
