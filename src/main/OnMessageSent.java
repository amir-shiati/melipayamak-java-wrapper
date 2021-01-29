package main;

import org.json.JSONObject;

public interface OnMessageSent {

	void onSuccess(JSONObject object);

	void onFailed(JSONObject object);

}
