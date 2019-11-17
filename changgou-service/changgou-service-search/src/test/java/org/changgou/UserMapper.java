package org.changgou;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserMapper extends ElasticsearchRepository<User,Integer> {
}
