/**
 * An Image Picker Plugin for Cordova/PhoneGap.
 */
package com.synconset;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.content.pm.PackageManager;
import android.Manifest;

public class ImagePicker extends CordovaPlugin {
	public static String TAG = "ImagePicker";
	public static final int PERMISSION_DENIED_ERROR = 20;

	private CallbackContext callbackContext;
	private JSONObject params;
	protected static final int PERMISSION_REQUEST_CODE = 100;
	protected static final String PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;

	public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;
		this.params = args.getJSONObject(0);
		if (action.equals("getPictures")) {
			if(PermissionHelper.hasPermission(this, PERMISSION)) {
				openMultiImageChooser();
			} else {
				PermissionHelper.requestPermission(this, PERMISSION_REQUEST_CODE, PERMISSION);
			}
		}
		return true;
	}

	public void openMultiImageChooser() throws JSONException {
		Intent intent = new Intent(cordova.getActivity(), MultiImageChooserActivity.class);
		int max = 20;
		int desiredWidth = 0;
		int desiredHeight = 0;
		int quality = 100;
		if (this.params.has("maximumImagesCount")) {
			max = this.params.getInt("maximumImagesCount");
		}
		if (this.params.has("width")) {
			desiredWidth = this.params.getInt("width");
		}
		if (this.params.has("height")) {
			desiredHeight = this.params.getInt("height");
		}
		if (this.params.has("quality")) {
			quality = this.params.getInt("quality");
		}
		intent.putExtra("MAX_IMAGES", max);
		intent.putExtra("WIDTH", desiredWidth);
		intent.putExtra("HEIGHT", desiredHeight);
		intent.putExtra("QUALITY", quality);
		if (this.cordova != null) {
			this.cordova.startActivityForResult((CordovaPlugin) this, intent, 0);
		}
	}

	public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
		for(int r:grantResults) {
			if(r == PackageManager.PERMISSION_DENIED) {
				this.callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, PERMISSION_DENIED_ERROR));
				return;
			}
		}

		if (requestCode == PERMISSION_REQUEST_CODE) {
			openMultiImageChooser();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && data != null) {
			ArrayList<Picture> pictureList = data.getParcelableArrayListExtra("MULTIPLEFILENAMES");
			JSONArray res = new JSONArray();
			for (int i = 0; i < pictureList.size(); i++) {
				try {
					res.put(pictureList.get(i).getJSONObject());
				} catch (JSONException e) {
					this.callbackContext.error(e.getMessage());
					return;
				}
			}
			this.callbackContext.success(res);
		} else if (resultCode == Activity.RESULT_CANCELED && data != null) {
			String error = data.getStringExtra("ERRORMESSAGE");
			this.callbackContext.error(error);
		} else if (resultCode == Activity.RESULT_CANCELED) {
			JSONArray res = new JSONArray();
			this.callbackContext.success(res);
		} else {
			this.callbackContext.error("No images selected");
		}
	}
}
