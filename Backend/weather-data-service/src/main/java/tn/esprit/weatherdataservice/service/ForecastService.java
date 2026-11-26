package tn.esprit.weatherdataservice.service;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tn.esprit.weatherdataservice.dto.ForecastRequest;
import tn.esprit.weatherdataservice.exception.BadRequestException;
import tn.esprit.weatherdataservice.exception.ResourceNotFoundException;
import tn.esprit.weatherdataservice.model.Forecast;
import tn.esprit.weatherdataservice.model.ForecastPeriod;
import tn.esprit.weatherdataservice.model.Granularity;
import tn.esprit.weatherdataservice.repository.ForecastRepository;
@Service
public class ForecastService {
    private final ForecastRepository repo;

    public ForecastService(ForecastRepository repo) { this.repo = repo; }

    public Forecast create(ForecastRequest r) { return repo.save(toDoc(null, r)); }

    public Forecast get(String id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Prévision " + id + " introuvable"));
    }

    public List<Forecast> search(Long cityId, Granularity g, Instant from, Instant to) {
        if (!to.isAfter(from)) throw new BadRequestException("'to' doit être postérieur à 'from'");
        return repo.search(cityId, g, from, to, Sort.by("period.startTime").ascending());
    }

    public Forecast update(String id, ForecastRequest r) { get(id); return repo.save(toDoc(id, r)); }

    public void delete(String id) { get(id); repo.deleteById(id); }

    private Forecast toDoc(String id, ForecastRequest r) {
        if (r.temperatureMin() > r.temperatureMax())
            throw new BadRequestException("temperatureMin doit être inférieure ou égale à temperatureMax");
        var p = r.period();
        if (!p.endTime().isAfter(p.startTime()))
            throw new BadRequestException("endTime doit être postérieur à startTime");
        return new Forecast(id, r.cityId(), new ForecastPeriod(p.startTime(), p.endTime(), p.granularity()),
                r.temperatureMin(), r.temperatureMax(), r.precipitationProbability(),
                r.precipitationAmount(), r.windSpeed(), r.condition());
    }
}