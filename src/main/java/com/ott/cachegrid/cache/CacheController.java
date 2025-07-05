package com.ott.cachegrid.cache;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/cache")
public class CacheController {

    private final CacheService service;

    public static class CacheRequest {
        public String key;
        public String value;
    }

    public CacheController(CacheService service) {
        this.service = service;
    }

    @PostMapping("")
    public void saveData(@RequestBody CacheRequest request) {
        this.service.set(request.key, request.value);
    }

    @GetMapping("/{key}")
    public String getData(@PathVariable String key) {
        return this.service.get(key);
    }

    @DeleteMapping("/{key}")
    public void deleteData(@PathVariable String key) {
        this.service.delete(key);
    }

    @GetMapping("/is-exists/{key}")
    public Boolean isDataExists(@PathVariable String key) {
        return this.service.exists(key);
    }

    @PutMapping("")
    public void updateData(@RequestBody CacheRequest request) {
        this.service.update(request.key, request.value);
    }
}
