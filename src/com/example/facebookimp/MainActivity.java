package com.example.facebookimp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

public class MainActivity extends Activity {

	// Creating Facebook CallbackManager Value
	public static CallbackManager callbackmanager;
	private String TAG_CANCEL = "Cancel";
	private String TAG_ERROR = "Error";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Initialize SDK before setContentView(Layout ID)
		setContentView(R.layout.activity_main);
		FacebookSdk.sdkInitialize(getApplicationContext());

	}

	public void onLoginClick(View v) {
		callbackmanager = CallbackManager.Factory.create();

		// Set permissions 
		LoginManager.getInstance().logInWithReadPermissions(this,
				Arrays.asList("public_profile", "email", "user_birthday"));

		LoginManager.getInstance().registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {

				GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
						new GraphRequest.GraphJSONObjectCallback() {
							@Override
							public void onCompleted(JSONObject json, GraphResponse response) {
								if (response.getError() != null) {
									// handle error
									System.out.println("ERROR");
								} else {
									System.out.println("Success");
									try {

										String jsonresult = String.valueOf(json);
										System.out.println("JSON Result" + jsonresult);

										//String str_email = json.getString("email");
										String str_id = json.getString("id");
										//String str_firstname = json.getString("first_name");
										String str_name = json.getString("name");

										Log.i("MainActivity", " ID: " + str_id + "\nName: " + str_name + "\nKey HAsh: "
												+ printKeyHash(MainActivity.this));
										Profile profile = Profile.getCurrentProfile();
										String firstName = profile.getFirstName();
										System.out.println(profile.getProfilePictureUri(20, 20));
										System.out.println(profile.getLinkUri());
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}
							}

						});
				Bundle parameters = new Bundle();
				parameters.putString("fields", "id, name,email, gender,birthday");
				request.setParameters(parameters);
				request.executeAsync();

			}

			@Override
			public void onCancel() {
				Log.d(TAG_CANCEL, "On cancel");
			}

			@Override
			public void onError(FacebookException error) {
				Log.d(TAG_ERROR, error.toString());
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		callbackmanager.onActivityResult(requestCode, resultCode, data);
	}

	public static String printKeyHash(Activity context) {
		PackageInfo packageInfo;
		String key = null;
		try {
			//getting application package name, as defined in manifest
			String packageName = context.getApplicationContext().getPackageName();

			//Retriving package info
			packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

			Log.e("Package Name=", context.getApplicationContext().getPackageName());

			for (Signature signature : packageInfo.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				key = new String(Base64.encode(md.digest(), 0));

				// String key = new String(Base64.encodeBytes(md.digest()));
				Log.e("Key Hash=", key);
			}
		} catch (NameNotFoundException e1) {
			Log.e("Name not found", e1.toString());
		} catch (NoSuchAlgorithmException e) {
			Log.e("No such an algorithm", e.toString());
		} catch (Exception e) {
			Log.e("Exception", e.toString());
		}

		return key;
	}
}
