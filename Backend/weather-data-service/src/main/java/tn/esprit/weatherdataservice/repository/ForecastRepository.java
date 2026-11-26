package tn.esprit.weatherdataservice.repository;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import tn.esprit.weatherdataservice.model.Forecast;
import tn.esprit.weatherdataservice.model.Granularity;
public interface ForecastRepository extends MongoRepository<Forecast, String> {
    @Query("{ 'cityId': ?0, 'period.granularity': ?1, 'period.startTime': { $gte: ?2, $lt: ?3 } }")
    List<Forecast> search(Long cityId, Granularity granularity, Instant from, Instant to, Sort sort);
}