package com.example.skillswap.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeolocationService {

    private static final Logger logger = LoggerFactory.getLogger(GeolocationService.class);
    private static final String NOMINATIM_BASE_URL = "https://nominatim.openstreetmap.org/search";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeolocationService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Geocode a city name or address to latitude and longitude coordinates
     * Handles "City, State" format for better accuracy
     * @param cityName The city name or address to geocode (e.g., "Atlanta, GA" or "New York, NY")
     * @return Map containing "latitude" and "longitude" keys, or null if geocoding fails
     */
    public Map<String, Double> geocodeCity(String cityName) {
        if (cityName == null || cityName.trim().isEmpty()) {
            return null;
        }

        String trimmedCity = cityName.trim();
        
        try {
            // Try multiple query formats for better accuracy
            String[] queryFormats = {
                // Format 1: Add "USA" for US locations (most accurate for City, State)
                trimmedCity + ", USA",
                // Format 2: Original query (for international cities)
                trimmedCity,
                // Format 3: If it contains comma, try with country code
                trimmedCity.contains(",") ? trimmedCity + ", United States" : trimmedCity
            };

            for (String query : queryFormats) {
                Map<String, Double> result = tryGeocode(query, trimmedCity);
                if (result != null) {
                    return result;
                }
            }

            logger.warn("All geocoding attempts failed for: {}", trimmedCity);
            return null;

        } catch (Exception e) {
            logger.error("Unexpected error geocoding city: {}", trimmedCity, e);
            return null;
        }
    }

    /**
     * Attempt to geocode a location with a specific query string
     */
    private Map<String, Double> tryGeocode(String query, String originalCity) {
        try {
            // Build the request URL with proper encoding and parameters for better accuracy
            String encodedQuery = java.net.URLEncoder.encode(query, java.nio.charset.StandardCharsets.UTF_8);
            // Add addressdetails=1 for better matching, countrycodes=us to prioritize US locations
            String url = String.format("%s?q=%s&format=json&limit=5&addressdetails=1&countrycodes=us", 
                NOMINATIM_BASE_URL, encodedQuery);

            // Add User-Agent header (required by Nominatim)
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "SkillSwap/1.0");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            // Make the request
            String response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class).getBody();

            if (response == null || response.trim().isEmpty() || response.equals("[]")) {
                return null;
            }

            // Parse JSON response
            JsonNode results = objectMapper.readTree(response);
            if (results.isArray() && results.size() > 0) {
                // Try to find the best match
                JsonNode bestMatch = findBestMatch(results, originalCity);
                
                if (bestMatch != null) {
                    double lat = bestMatch.get("lat").asDouble();
                    double lon = bestMatch.get("lon").asDouble();

                    Map<String, Double> coordinates = new HashMap<>();
                    coordinates.put("latitude", lat);
                    coordinates.put("longitude", lon);

                    logger.info("Successfully geocoded '{}' to ({}, {})", originalCity, lat, lon);
                    return coordinates;
                }
            }

            return null;

        } catch (RestClientException e) {
            logger.debug("Geocoding attempt failed for query '{}': {}", query, e.getMessage());
            return null;
        } catch (Exception e) {
            logger.debug("Error in geocoding attempt for query '{}': {}", query, e.getMessage());
            return null;
        }
    }

    /**
     * Find the best matching result from geocoding results
     * Prioritizes city matches and US locations
     */
    private JsonNode findBestMatch(JsonNode results, String originalCity) {
        if (!results.isArray() || results.size() == 0) {
            return null;
        }

        String lowerOriginal = originalCity.toLowerCase();
        
        // Extract city and state from original if present (e.g., "Atlanta, GA" -> city="Atlanta", state="GA")
        String[] parts = lowerOriginal.split(",");
        String cityPart = parts[0].trim();
        String statePart = parts.length > 1 ? parts[1].trim() : "";

        // Score each result and find the best match
        JsonNode bestMatch = null;
        int bestScore = -1;

        for (JsonNode result : results) {
            int score = 0;
            JsonNode address = result.get("address");
            
            if (address != null) {
                // Check if it's in the US
                String country = address.has("country_code") ? address.get("country_code").asText().toLowerCase() : "";
                if ("us".equals(country)) {
                    score += 10;
                }

                // Check city match
                String city = "";
                if (address.has("city")) {
                    city = address.get("city").asText().toLowerCase();
                } else if (address.has("town")) {
                    city = address.get("town").asText().toLowerCase();
                } else if (address.has("village")) {
                    city = address.get("village").asText().toLowerCase();
                }

                if (city.contains(cityPart) || cityPart.contains(city)) {
                    score += 20;
                }

                // Check state match if state was provided
                if (!statePart.isEmpty()) {
                    String state = "";
                    if (address.has("state")) {
                        state = address.get("state").asText().toLowerCase();
                    }
                    
                    // Check full state name or abbreviation
                    if (state.contains(statePart) || statePart.length() == 2) {
                        // Try to match state abbreviation
                        String stateAbbr = "";
                        if (address.has("state_code")) {
                            stateAbbr = address.get("state_code").asText().toLowerCase();
                        }
                        if (stateAbbr.equals(statePart) || state.contains(statePart)) {
                            score += 15;
                        }
                    }
                }

                // Prefer city/town type over other types
                String type = result.has("type") ? result.get("type").asText().toLowerCase() : "";
                if (type.contains("city") || type.contains("town")) {
                    score += 5;
                }
            }

            if (score > bestScore) {
                bestScore = score;
                bestMatch = result;
            }
        }

        // If we found a good match (score > 0), return it; otherwise return first result
        return bestScore > 0 ? bestMatch : results.get(0);
    }

    /**
     * Calculate the distance between two coordinates using the Haversine formula
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                   Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Get city suggestions based on partial input
     * Returns a list of city suggestions in "City, State" format
     * @param query Partial city name or "City, State" input
     * @return List of city suggestion strings in "City, State" format
     */
    public List<String> getCitySuggestions(String query) {
        if (query == null || query.trim().isEmpty() || query.trim().length() < 2) {
            return new ArrayList<>();
        }

        String trimmedQuery = query.trim();
        List<String> suggestions = new ArrayList<>();

        try {
            // Build query - prioritize US cities
            String searchQuery = trimmedQuery;
            if (!trimmedQuery.contains(",")) {
                // If no comma, add ", USA" to prioritize US cities
                searchQuery = trimmedQuery + ", USA";
            } else {
                // If comma exists, add "USA" at the end if not present
                if (!trimmedQuery.toLowerCase().contains("usa") && 
                    !trimmedQuery.toLowerCase().contains("united states")) {
                    searchQuery = trimmedQuery + ", USA";
                }
            }

            String encodedQuery = java.net.URLEncoder.encode(searchQuery, java.nio.charset.StandardCharsets.UTF_8);
            // Limit to 8 results, prioritize cities and towns in US
            String url = String.format("%s?q=%s&format=json&limit=8&addressdetails=1&countrycodes=us&featuretype=city,town", 
                NOMINATIM_BASE_URL, encodedQuery);

            // Add User-Agent header (required by Nominatim)
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "SkillSwap/1.0");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            // Make the request
            String response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class).getBody();

            if (response == null || response.trim().isEmpty() || response.equals("[]")) {
                return suggestions;
            }

            // Parse JSON response
            JsonNode results = objectMapper.readTree(response);
            if (results.isArray()) {
                for (JsonNode result : results) {
                    String suggestion = formatCitySuggestion(result);
                    if (suggestion != null && !suggestions.contains(suggestion)) {
                        suggestions.add(suggestion);
                    }
                }
            }

        } catch (RestClientException e) {
            logger.debug("Error getting city suggestions for '{}': {}", query, e.getMessage());
        } catch (Exception e) {
            logger.debug("Unexpected error getting city suggestions for '{}': {}", query, e.getMessage());
        }

        return suggestions;
    }

    /**
     * Format a geocoding result into a "City, State" suggestion string
     */
    private String formatCitySuggestion(JsonNode result) {
        try {
            JsonNode address = result.get("address");
            if (address == null) {
                return null;
            }

            String city = "";
            if (address.has("city")) {
                city = address.get("city").asText();
            } else if (address.has("town")) {
                city = address.get("town").asText();
            } else if (address.has("village")) {
                city = address.get("village").asText();
            } else if (address.has("municipality")) {
                city = address.get("municipality").asText();
            }

            if (city.isEmpty()) {
                return null;
            }

            String state = "";
            if (address.has("state")) {
                state = address.get("state").asText();
            }

            // Prefer state abbreviation if available
            if (address.has("state_code")) {
                String stateCode = address.get("state_code").asText();
                if (stateCode.length() == 2) {
                    state = stateCode;
                }
            }

            if (state.isEmpty()) {
                return city;
            } else {
                return city + ", " + state;
            }

        } catch (Exception e) {
            logger.debug("Error formatting city suggestion: {}", e.getMessage());
            return null;
        }
    }
}

