package com.tmser.core;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Iterables;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceFinder<T> {
    private static final Map<Class, String> errorMessages = new HashMap<>();
    private static final ConcurrentHashMap<Class<?>, ServiceFinder<?>> instanceMap;
    private final Supplier<T> supplier;

    private ServiceFinder(final Class<T> clz) {
        this.supplier = Suppliers.memoize(() ->
        {
            ServiceLoader<T> loader = ServiceLoader.load(clz);
            return (T) Iterables.getFirst(loader, null);
        });
    }

    public static <T> T getService(Class<T> clz) {
        T instance = getServiceWithoutCheck(clz);
        Preconditions.checkNotNull(instance, errorMessages.get(clz));
        return instance;
    }

    public static <T> T getServiceWithoutCheck(Class<T> clz) {
        Preconditions.checkNotNull(clz);
        Preconditions.checkArgument(clz.isInterface(), "clz is not a interface");
        ServiceFinder serviceFinder = instanceMap.get(clz);
        if (serviceFinder == null) {
            instanceMap.put(clz, new ServiceFinder(clz));
            serviceFinder = instanceMap.get(clz);
        }

        return (T) serviceFinder.supplier.get();
    }

    static {
        errorMessages.put(ServerManagement.class, "请检查是否引用了common，并且common各个包版本必须一致");
        instanceMap = new ConcurrentHashMap();
    }
}