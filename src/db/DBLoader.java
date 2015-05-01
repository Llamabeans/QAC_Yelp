package db;

import java.sql.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import json.JSONArray;
import json.JSONException;
import json.JSONObject;
import structures.Post;

public class DBLoader {

	public JSONObject loadJson(String filename) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "UTF-8"));
			StringBuffer buffer = new StringBuffer(1024);
			String line;

			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			reader.close();

			return new JSONObject(buffer.toString());
		} catch (IOException e) {
			System.err.format("[Error]Failed to open file %s!", filename);
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			System.err.format("[Error]Failed to parse json file %s!", filename);
			e.printStackTrace();
			return null;
		}
	}

	// Load files from directory
	public void loadDirectory(String folder, String suffix) {
		int count = 0;
		File dir = new File(folder);
		for (File f : dir.listFiles()) {
			if (f.isFile() && f.getName().endsWith(suffix)) {
				processReview(loadJson(f.getAbsolutePath()));
				count++;
				if (count % 10 == 0) {
					System.out.println(count);
				}
			} else if (f.isDirectory())
				loadDirectory(f.getAbsolutePath(), suffix);
		}
	}

	// Pull a single review from file, place into a Post
	public void processReview(JSONObject json) {
		try {
			JSONArray jarray = json.getJSONArray("Reviews");
			for (int i = 0; i < jarray.length(); i++) {
				Post review = new Post(jarray.getJSONObject(i));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "demo";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root";
		String password = "mypassword";
		try {
			Class.forName(driver).newInstance();
			Connection conn = DriverManager.getConnection(url + dbName,
					userName, password);
			Statement st = conn.createStatement();
			ResultSet res = st.executeQuery("SELECT * FROM event");
			while (res.next()) {
				int id = res.getInt("id");
				String msg = res.getString("msg");
				System.out.println(id + "\t" + msg);
			}
			int val = st.executeUpdate("INSERT into event VALUES(" + 1 + ","
					+ "'Easy'" + ")");
			if (val == 1)
				System.out.print("Successfully inserted value");
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
