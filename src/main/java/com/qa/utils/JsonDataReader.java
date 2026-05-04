package com.qa.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class to read JSON data files and provide access to data
 * Supports both simple and nested JSON structures
 * 
 * Usage:
 * 1. Initialize with file path: JsonDataReader reader = new JsonDataReader(filePath);
 * 2. Get all records as List: List<Map<String, Object>> records = reader.getDataAsListOfMaps();
 * 3. Get specific value: Object value = reader.getValueFromMap(dataMap, "fieldName");
 * 4. Get nested value: Object value = reader.getNestedValue(dataMap, "address.city");
 */
public class JsonDataReader {
	
	private ObjectMapper objectMapper;
	private JsonNode rootNode;
	private String filePath;
	
	/**
	 * Constructor to initialize JsonDataReader with file path
	 * @param filePath - Path to the JSON file
	 */
	public JsonDataReader(String filePath) {
		this.filePath = filePath;
		this.objectMapper = new ObjectMapper();
		this.rootNode = readJsonFile();
	}
	
	/**
	 * Read JSON file and return as JsonNode
	 * @return JsonNode containing the parsed JSON
	 */
	private JsonNode readJsonFile() {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				throw new RuntimeException("JSON file not found at: " + filePath);
			}
			return objectMapper.readTree(file);
		} catch (IOException e) {
			throw new RuntimeException("Error reading JSON file: " + filePath, e);
		}
	}
	
	/**
	 * Get all JSON array data as List of Maps
	 * Converts each JSON object in the array to a HashMap
	 * @return List of Maps containing the data
	 */
	public List<Map<String, Object>> getDataAsListOfMaps() {
		List<Map<String, Object>> dataList = new ArrayList<>();
		
		if (rootNode.isArray()) {
			for (JsonNode node : rootNode) {
				dataList.add(jsonNodeToMap(node));
			}
		} else {
			// If it's a single object, add it as a single-item list
			dataList.add(jsonNodeToMap(rootNode));
		}
		
		return dataList;
	}
	
	/**
	 * Convert a JsonNode to HashMap for easier access
	 * Handles nested objects recursively
	 * @param node - JsonNode to convert
	 * @return Map containing the data
	 */
	private Map<String, Object> jsonNodeToMap(JsonNode node) {
		Map<String, Object> map = new HashMap<>();
		
		node.fields().forEachRemaining(entry -> {
			String key = entry.getKey();
			JsonNode value = entry.getValue();
			
			if (value.isObject()) {
				// Recursively convert nested objects
				map.put(key, jsonNodeToMap(value));
			} else if (value.isArray()) {
				// Handle arrays
				List<Object> list = new ArrayList<>();
				for (JsonNode item : value) {
					if (item.isObject()) {
						list.add(jsonNodeToMap(item));
					} else {
						list.add(item.asText());
					}
				}
				map.put(key, list);
			} else if (value.isNumber()) {
				map.put(key, value.numberValue());
			} else if (value.isBoolean()) {
				map.put(key, value.asBoolean());
			} else {
				map.put(key, value.asText());
			}
		});
		
		return map;
	}
	
	/**
	 * Get a value from a Map (supports dot notation for nested values)
	 * Example: "address.city" will traverse nested objects
	 * @param dataMap - Map to get value from
	 * @param fieldName - Field name (supports dot notation for nested fields)
	 * @return The value, or null if not found
	 */
	public Object getValue(Map<String, Object> dataMap, String fieldName) {
		if (fieldName.contains(".")) {
			// Handle nested field access like "address.city"
			return getNestedValue(dataMap, fieldName);
		}
		
		return dataMap.getOrDefault(fieldName, null);
	}
	
	/**
	 * Get nested value from map using dot notation
	 * Example: "address.city" will get city from address object
	 * @param dataMap - Map to traverse
	 * @param fieldPath - Dot-separated path (e.g., "address.city")
	 * @return The nested value, or null if path doesn't exist
	 */
	@SuppressWarnings("unchecked")
	public Object getNestedValue(Map<String, Object> dataMap, String fieldPath) {
		String[] keys = fieldPath.split("\\.");
		Object current = dataMap;
		
		for (String key : keys) {
			if (current instanceof Map) {
				current = ((Map<String, Object>) current).get(key);
			} else {
				return null; // Path doesn't exist
			}
		}
		
		return current;
	}
	
	/**
	 * Get data at specific index from the array
	 * @param index - Index of the data object
	 * @return Map containing the data at that index
	 */
	public Map<String, Object> getDataAtIndex(int index) {
		List<Map<String, Object>> dataList = getDataAsListOfMaps();
		if (index >= 0 && index < dataList.size()) {
			return dataList.get(index);
		}
		return null;
	}
	
	/**
	 * Get total number of data records in the JSON array
	 * @return Count of records
	 */
	public int getTotalRecords() {
		return getDataAsListOfMaps().size();
	}
	
	/**
	 * Print all data (useful for debugging)
	 */
	public void printAllData() {
		List<Map<String, Object>> dataList = getDataAsListOfMaps();
		for (int i = 0; i < dataList.size(); i++) {
			System.out.println("\n=== Record " + (i + 1) + " ===");
			dataList.get(i).forEach((key, value) -> System.out.println(key + " : " + value));
		}
	}
}
