package com.aecs.eyenotes.core.services;

import com.aecs.eyenotes.core.dto.GenericBaseDto;
import com.aecs.eyenotes.core.entities.GenericBaseEntity;
import com.aecs.eyenotes.core.repositories.IRepository;
import com.aecs.eyenotes.core.response.PagedResponse;
import com.aecs.eyenotes.core.IUnitOfWork;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Service<T, TEntity extends GenericBaseEntity<T>, TDto extends GenericBaseDto<T>> 
      extends BaseService<T, TEntity, TDto> 
      implements IService<T, TEntity, TDto> {
    
    private final IRepository<T, TEntity> repository;

    protected Service(IRepository<T, TEntity> repository, IUnitOfWork unitOfWork) {
        super(repository, unitOfWork);
        this.repository = repository;
    }

    @Override
    public CompletableFuture<List<TDto>> getAll() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<TEntity> entities = repository.getAll();
                return mapEntitiesToDtos(entities);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<PagedResponse<TDto>> getPaged(int pageIndex, int pageSize, 
                                                         String filter, String sort) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                PagedResponse<TEntity> pagedResponse = repository.getPaged(pageIndex, pageSize, filter, sort);
                List<TDto> dtos = mapEntitiesToDtos(pagedResponse.getItems());
                return new PagedResponse<>(pagedResponse.getCount(), dtos);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> exists(T id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return repository.any("id = " + id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Long> count() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return repository.count(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<TDto> create(TDto dto) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TEntity entity = mapDtoToEntity(dto);
                repository.add(entity);
                unitOfWork.saveChanges();
                return mapEntityToDto(entity);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    // Utility method to map a list of entities to DTOs
    private List<TDto> mapEntitiesToDtos(List<TEntity> entities) {
        // This will be implemented with proper mapping logic
        // For now, returning an empty list
        return List.of();
    }
}