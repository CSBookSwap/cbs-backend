/*
 * MIT License
 *
 * Copyright (c) 2023 Artyom Nefedov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package tech.cbs.api.repository;

import tech.cbs.api.repository.model.Model;
import tech.cbs.api.service.dto.Page;

import java.util.List;
import java.util.Optional;

/**
 * Interface for all repositories
 *
 * @param <T> model type
 */
public interface AbstractModelRepository <T extends Model> {

    /**
     * Find all models
     *
     * @param page page
     * @return list of models
     */
    List<T> findAll(Page page);

    /**
     * Find model by id
     *
     * @param id model id
     * @return model
     */
    Optional<T> findById(int id);

    /**
     * Save model
     *
     * @param model model
     * @return saved model id
     */
    int save(T model);


    /**
     * Update model
     *
     * @param model model
     * @return updated model
     */
    boolean update(T model);

    /**
     * Delete model by id
     *
     * @param id model id
     * @return true if deleted
     */
    boolean deleteById(int id);
}
