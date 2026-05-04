package com.qa.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ScenarioContext - A context class to store and share data between Hooks and
 * Step Definitions
 * 
 * This class acts as a data holder that can be passed to different components
 * of the test
 * Data stored here is accessible from hooks, step definitions, page objects,
 * etc.
 * 
 * Usage:
 * 1. Create instance: ScenarioContext context = new ScenarioContext();
 * 2. Store data: context.setData("username", "standard_user");
 * 3. Retrieve data: String username = (String) context.getData("username");
 * 4. Store objects: context.setTestData(dataMap);
 * 5. Get objects: Map<String, Object> data = context.getTestData();
 */
public class ScenarioContext {

    private Map<String, Object> testData;
    private List<Map<String, Object>> testDataList; // ALL records from JSON
    private int currentDataIndex;
    private int totalDataRecords;

    /**
     * Constructor - Initializes the context with empty data structures
     */
    public ScenarioContext() {
        this.testData = new HashMap<>();
        this.testDataList = null; // Initially null, set when JSON is read
        this.currentDataIndex = 0;
        this.totalDataRecords = 0;
    }

    /**
     * Set the current test data object (usually a Map from JSON)
     * Contains all fields for the current scenario iteration
     * 
     * @param data - Test data map
     */
    public void setTestData(Map<String, Object> data) {
        this.testData = data;
    }

    /**
     * Get the current test data object
     * 
     * @return Test data map
     */
    public Map<String, Object> getTestData() {
        return testData;
    }

    /**
     * Set the current data index (which record is being processed)
     * 
     * @param index - Index of current record
     */
    public void setCurrentDataIndex(int index) {
        this.currentDataIndex = index;
    }

    /**
     * Get the current data index
     * 
     * @return Current index
     */
    public int getCurrentDataIndex() {
        return currentDataIndex;
    }

    /**
     * Set the total number of data records
     * 
     * @param total - Total record count
     */
    public void setTotalDataRecords(int total) {
        this.totalDataRecords = total;
    }

    /**
     * Get the total number of data records
     * 
     * @return Total record count
     */
    public int getTotalDataRecords() {
        return totalDataRecords;
    }

    /**
     * Check if this is the last data record
     * 
     * @return true if current index is the last record
     */
    public boolean isLastRecord() {
        return currentDataIndex == totalDataRecords - 1;
    }

    /**
     * Set the complete list of all test data records from JSON file
     * This persists across iterations since it's stored in context
     * 
     * @param dataList - List of all data maps from JSON
     */
    public void setTestDataList(List<Map<String, Object>> dataList) {
        this.testDataList = dataList;
    }

    /**
     * Get the complete list of all test data records
     * 
     * @return List of all data maps
     */
    public List<Map<String, Object>> getTestDataList() {
        return testDataList;
    }

    /**
     * Check if test data list has been loaded
     * 
     * @return true if testDataList is not null
     */
    public boolean hasTestDataList() {
        return testDataList != null;
    }

    /**
     * Check if there are more records to process
     * 
     * @return true if current index + 1 < total records
     */
    public boolean hasMoreRecords() {
        return currentDataIndex + 1 < totalDataRecords;
    }

    /**
     * Advance to the next data record
     * Returns true if successful, false if already at last record
     */
    public boolean moveToNextRecord() {
        if (hasMoreRecords()) {
            currentDataIndex++;
            if (testDataList != null && currentDataIndex < testDataList.size()) {
                this.testData = testDataList.get(currentDataIndex);
                return true;
            }
        }
        return false;
    }
}
