package com.autobuild.pipeline.dto.updator;

public interface EntityUpdator<T, R> {
    public void update(T dto, R entity);
}
