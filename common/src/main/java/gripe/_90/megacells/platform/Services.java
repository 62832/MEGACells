package gripe._90.megacells.platform;

import java.util.ServiceLoader;

import gripe._90.megacells.platform.service.IPlatformHelper;

public class Services {
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> service) {
        return ServiceLoader.load(service).findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + service.getName()));
    }
}
