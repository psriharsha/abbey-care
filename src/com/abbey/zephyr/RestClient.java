package com.abbey.zephyr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;

public class RestClient {

	private List<NameValuePair> params;
	private List<NameValuePair> headers;

	private String url;
	private int responseCode;
	private String message;

	private String response;

	public enum RequestMethod {
		GET, POST
	}

	public String getResponse() {
		return response;
	}

	public String getErrorMessage() {
		return message;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public RestClient(String url) {
		this.url = url;
		params = new ArrayList<NameValuePair>();
		headers = new ArrayList<NameValuePair>();
	}

	public void AddParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	public void AddHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}

	public void Execute(RequestMethod method) throws Exception {
		HttpAsyncTask httpAsyncTask = new HttpAsyncTask();
		switch (method) {
		case GET: {
			// add parameters
			String combinedParams = "";
			if (!params.isEmpty()) {
				combinedParams += "?";
				for (NameValuePair p : params) {
					String paramString = p.getName() + "="
							+ URLEncoder.encode(p.getValue(), "UTF-8");
					if (combinedParams.length() > 1) {
						combinedParams += "&" + paramString;
					} else {
						combinedParams += paramString;
					}
				}
			}

			HttpGet request = new HttpGet(url + combinedParams);

			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			httpAsyncTask.execute(request);
			response = "success";
			break;
		}
		case POST: {
			HttpPost request = new HttpPost(url);
			// add headers
			for (NameValuePair h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}

			if (!params.isEmpty()) {
				request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			}
			httpAsyncTask.execute(request);
			break;
		}
		}
	}

	private class HttpAsyncTask extends AsyncTask<HttpUriRequest,Integer,String>{
		
		public void execute(HttpUriRequest request){
			response = doInBackground(request);
		}
		
		@SuppressLint("NewApi")
		@Override
		protected String doInBackground(HttpUriRequest... request) {
			// TODO Auto-generated method stub
			HttpClient client = new DefaultHttpClient();
			HttpResponse httpResponse;
			HttpUriRequest req = request[0];

			try {
				httpResponse = client.execute(req);
				responseCode = httpResponse.getStatusLine().getStatusCode();
				message = httpResponse.getStatusLine().getReasonPhrase();

				HttpEntity entity = httpResponse.getEntity();

				if (entity != null) {

					InputStream instream = entity.getContent();
					response = convertStreamToString(instream);
					// Closing the input stream will trigger connection release
					instream.close();
				}

			} catch (ClientProtocolException e) {
				client.getConnectionManager().shutdown();
				e.printStackTrace();
			} catch (IOException e) {
				client.getConnectionManager().shutdown();
				e.printStackTrace();
			} catch(NetworkOnMainThreadException e){
				client.getConnectionManager().shutdown();
				e.printStackTrace();
			}
			return response;
		}
		
		private String convertStreamToString(InputStream is) {

			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuilder sb = new StringBuilder();

			String line = null;
			boolean first = true;
			try {
				while ((line = reader.readLine()) != null) {
					if(first){
						sb.append(line);
						first = false;
					}
					else
						sb.append("\n" + line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return sb.toString();
		}
	}
	
}
