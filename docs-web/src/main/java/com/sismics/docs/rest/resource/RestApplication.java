package com.sismics.docs.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class RestApplication extends Application {
    // 可以留空，JAX-RS会自动扫描同包及子包的资源和Provider
}