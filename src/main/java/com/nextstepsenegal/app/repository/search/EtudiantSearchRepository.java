package com.nextstepsenegal.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.nextstepsenegal.app.domain.Etudiant;
import com.nextstepsenegal.app.repository.EtudiantRepository;
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
 * Spring Data Elasticsearch repository for the {@link Etudiant} entity.
 */
public interface EtudiantSearchRepository extends ElasticsearchRepository<Etudiant, Long>, EtudiantSearchRepositoryInternal {}

interface EtudiantSearchRepositoryInternal {
    Page<Etudiant> search(String query, Pageable pageable);

    Page<Etudiant> search(Query query);

    @Async
    void index(Etudiant entity);

    @Async
    void deleteFromIndexById(Long id);
}

class EtudiantSearchRepositoryInternalImpl implements EtudiantSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final EtudiantRepository repository;

    EtudiantSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, EtudiantRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Etudiant> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Etudiant> search(Query query) {
        SearchHits<Etudiant> searchHits = elasticsearchTemplate.search(query, Etudiant.class);
        List<Etudiant> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Etudiant entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Etudiant.class);
    }
}
