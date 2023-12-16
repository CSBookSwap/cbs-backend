package tech.cbs.api.repository;

import tech.cbs.api.repository.model.Model;
import tech.cbs.api.service.dto.Page;

import java.util.List;
import java.util.Optional;

public interface AbstractModelRepository <T extends Model> {

    List<T> findAll(Page page);
    Optional<T> findById(int id);
    T save(T model);
    boolean saveAll(List<T> models);
    boolean update(T model);
    boolean deleteById(int id);

    boolean deleteByIds(List<Integer> ids);
}
