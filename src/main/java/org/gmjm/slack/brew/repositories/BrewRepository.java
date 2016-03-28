package org.gmjm.slack.brew.repositories;

import java.util.Date;
import java.util.List;

import org.gmjm.slack.brew.domain.Brew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrewRepository extends JpaRepository<Brew, Long> {

    List<Brew> findFirstByOrderByBrewDateDesc();

    List<Brew> findTop20ByOrderByBrewDateDesc();

    List<Brew> findByBrewDateGreaterThan(Date date);

    List<Brew> findByGone(boolean gone);

}
