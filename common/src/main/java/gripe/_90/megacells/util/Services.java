package gripe._90.megacells.util;

import java.util.ServiceLoader;

import gripe._90.megacells.util.service.IPlatformHelper;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    private static <T> T load(Class<T> service) {
        return ServiceLoader.load(service).findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + service.getName()));
    }
}
