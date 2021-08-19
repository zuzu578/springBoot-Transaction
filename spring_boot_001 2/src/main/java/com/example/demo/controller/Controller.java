package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Dto.BoardDto;
import com.example.demo.dao.Dao;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@org.springframework.stereotype.Controller
//@RestController

public class Controller {
	// API keys
	// api 키는 유효기간이 존재하므로 재발급을 받아야합니다.
	final static String API_KEY = "RGAPI-cc46787e-a801-4fd9-9c25-3644166d635d";
	@Autowired
	private Dao dao;
	// jsp MVC pattern 
	@RequestMapping("/test")
	public String testJsp(HttpServletRequest req , Model model) {
		String msg = "hello";
		model.addAttribute("hello",msg);
		return "test";
	}

	@GetMapping(path = "/helloWorld")
	public String helloWorld() {
		return dao.selectName();
	}

	@GetMapping(path = "/getBoardData")
	public JSONArray getBoardData(HttpServletRequest req, Model model) {
		List<Map<String, Object>> boardList = dao.getBoardList();
		JSONArray jsonArray = new JSONArray();

		for (Map<String, Object> map : boardList) {
			jsonArray.add(convertMapToJson(map));
		}
		return jsonArray;

	}

	/*
	 * desc : map list data를 jsonObject 로 변환합니다. 그런다음 jsonArray 로 변환합니다.
	 * 
	 * @return jsonObject
	 * 
	 */
	@SuppressWarnings({ "unchecked" })

	public static JSONObject convertMapToJson(Map<String, Object> map) {

		JSONObject json = new JSONObject();

		for (Map.Entry<String, Object> entry : map.entrySet()) {

			String key = entry.getKey();

			Object value = entry.getValue();

			// json.addProperty(key, value);

			json.put(key, value);

		}

		return json;

	}

	// test : random 게임관전 정보를 가져옵니다.
	@RequestMapping("/getRandomSpector")
	@ResponseBody
	public Map<String, Object> getRandomSpector(HttpServletRequest req, Model model)
			throws IOException, ParseException {
		System.out.println("getMatchInfo!");
		BufferedReader br = null;
		// https://kr.api.riotgames.com/lol/spectator/v4/featured-games?api_key=RGAPI-7aa13dd6-fab2-4ce0-aac3-27add86f7677
		String urlstr = "https://kr.api.riotgames.com/lol/spectator/v4/featured-games?api_key=" + API_KEY;

		URL url = new URL(urlstr);
		HttpURLConnection urlconnection = (HttpURLConnection) url.openConnection();
		urlconnection.setRequestMethod("GET");
		br = new BufferedReader(new InputStreamReader(urlconnection.getInputStream(), "UTF-8"));
		String result = "";
		String line;
		while ((line = br.readLine()) != null) {
			result = result + line;
		}

		JSONParser parser = new JSONParser();
		Object object = parser.parse(result.toString());
		JSONObject json = (JSONObject) object;
		System.out.println(json);
		return getMapFromJsonObject(json);

	}

	public Map<String, Object> getMapFromJsonObject(JSONObject jsonObj) {
		Map<String, Object> map = null;
		try {
			map = new ObjectMapper().readValue(jsonObj.toJSONString(), Map.class);
			System.out.println("map data ===> " + map);

		} catch (JsonParseException e) {

		} catch (JsonMappingException e) {

		} catch (IOException e) {

		}
		return map;

	}

}
