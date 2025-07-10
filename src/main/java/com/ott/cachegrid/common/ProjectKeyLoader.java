package com.ott.cachegrid.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class ProjectKeyLoader implements ApplicationRunner {

    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    public ProjectKeyLoader(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Value("${KEYS_FILE_PATH:/opt/keys.json}")
    private String keysFilePath;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println(" ProjectKeyLoader started with file path: " + this.keysFilePath);

        File file = new File(this.keysFilePath);

        if (!file.exists()) {
            System.out.println(" Keys.json not found at " + this.keysFilePath);
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(file);

        for (JsonNode entry: root) {
            String projID = entry.get("projID").asText();
            String value = mapper.writeValueAsString(entry);
            redisTemplate.opsForValue().set(projID, value);
        }

        System.out.println("Project Keys Loaded successfully from " + this.keysFilePath);
    }

}
