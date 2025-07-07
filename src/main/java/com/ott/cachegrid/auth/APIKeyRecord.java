package com.ott.cachegrid.auth;

import java.time.Instant;

public record APIKeyRecord(String projID, String key, Instant exp) {
}
