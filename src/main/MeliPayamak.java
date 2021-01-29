package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

public class MeliPayamak {

	private String userName;
	private String password;
	private long bodyId;

	public MeliPayamak(String userName, String password, long bodyId) {
		super();
		this.userName = userName;
		this.password = password;
		this.bodyId = bodyId;
	}

	public void send(String text, String receiverNumber, OnMessageSent onMessageSent) throws IOException {

		Hashtable<String, String> values = new Hashtable<>();
		values.put("username", userName);
		values.put("password", password);
		values.put("to", receiverNumber);
		values.put("text", text);
		values.put("bodyId", String.valueOf(bodyId));

		// sms route
		URL url = new URL("https://rest.payamak-panel.com/api/SendSMS/" + "BaseServiceNumber");
		makeRequest(url, values, onMessageSent);
	}

	private void makeRequest(URL url, Hashtable<String, String> values, OnMessageSent onMessageSent)
			throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");

		StringBuilder result = new StringBuilder();
		String line;

		try {
			conn.setDoOutput(true);
			conn.setChunkedStreamingMode(0);

			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(getPostParamString(values));
			writer.flush();
			writer.close();

			BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = r.readLine()) != null) {
				result.append(line).append('\n');
			}

		} finally {
			conn.disconnect();
			try {
				JSONObject object = new JSONObject(String.valueOf(result));

				// based on meli payamak's docs
				// 1 means sms has been sent
				// 35 means something went wrong
				if (object.getLong("RetStatus") == 1 && object.getLong("Value") > 15) {
					// sms has been sent successfully
					onMessageSent.onSuccess(object);
				} else {
					// failed to send sms
					onMessageSent.onFailed(object);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private String getPostParamString(Hashtable<String, String> params) {
		if (params.size() == 0)
			return "";

		StringBuffer buf = new StringBuffer();
		Enumeration<String> keys = params.keys();
		while (keys.hasMoreElements()) {
			buf.append(buf.length() == 0 ? "" : "&");
			String key = keys.nextElement();
			buf.append(key).append("=").append(params.get(key));
		}
		return buf.toString();
	}
}
