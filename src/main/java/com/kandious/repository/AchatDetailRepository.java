package com.kandious.repository;

import com.kandious.entity.AchatDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AchatDetailRepository
        extends JpaRepository<AchatDetail, Long> {

    List<AchatDetail> findByAchatId(Long achatId);

    List<AchatDetail> findByProduitId(Long produitId);
}