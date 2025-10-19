package com.autobuild.pipeline.dto.mapper;

/**
 * Generic Mapping interface for dto to entity mapping and vice versa.
 * 
 * @author Suvabrata Chowdhury
 */
public interface Mapper<T, R> {
    public R dtoToEntity(T dto);
    public T entityToDto(R entity);
}
