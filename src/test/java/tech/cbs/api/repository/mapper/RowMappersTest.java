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

package tech.cbs.api.repository.mapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tech.cbs.api.repository.model.Author;
import tech.cbs.api.repository.model.Tag;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link AuthorRowMapper}, {@link TagRowMapper}
 */
class RowMappersTest {

    @Test
    void AuthorRowMapper_MapRow_ReturnsAuthor() throws SQLException {

        Author author = new Author(1, "John Doe", "Biography");

        ResultSet resultSet = Mockito.mock(ResultSet.class);

        Mockito.when(resultSet.getInt("id")).thenReturn(author.id());
        Mockito.when(resultSet.getString("name")).thenReturn(author.name());
        Mockito.when(resultSet.getString("biography")).thenReturn(author.biography());

        AuthorRowMapper authorRowMapper = new AuthorRowMapper();

        assertThat(author).isEqualTo(authorRowMapper.mapRow(resultSet, 1));
    }

    @Test
    void TagRowMapper_MapRow_ReturnTag() throws SQLException {
        Tag tag = new Tag(1, "Tag name");

        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.getInt("id")).thenReturn(tag.id());
        Mockito.when(resultSet.getString("name")).thenReturn(tag.name());

        TagRowMapper tagRowMapper = new TagRowMapper();

        assertThat(tag).isEqualTo(tagRowMapper.mapRow(resultSet, 1));

    }
}