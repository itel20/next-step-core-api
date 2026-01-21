package com.nextstepsenegal.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.nextstepsenegal.app.domain.PublicationLike;
import com.nextstepsenegal.app.repository.PublicationLikeRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link PublicationLike} entity.
 */
public interface PublicationLikeSearchRepository
    extends ElasticsearchRepository<PublicationLike, Long>, PublicationLikeSearchRepositoryInternal {}

interface PublicationLikeSearchRepositoryInternal {
    Page<PublicationLike> search(String query, Pageable pageable);

    Page<PublicationLike> search(Query query);

    @Async
    void index(PublicationLike entity);

    @Async
    void deleteFromIndexById(Long id);
}

class PublicationLikeSearchRepositoryInternalImpl implements PublicationLikeSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final PublicationLikeRepository repository;

    PublicationLikeSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, PublicationLikeRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<PublicationLike> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<PublicationLike> search(Query query) {
        SearchHits<PublicationLike> searchHits = elasticsearchTemplate.search(query, PublicationLike.class);
        List<PublicationLike> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(PublicationLike entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), PublicationLike.class);
    }
}
