package yasheri.ru.infoseclab1.data;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
class DataService {

    private final DataRepository repository;

    DataService(DataRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<DataResponse> findAll() {
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(DataResponse::from)
                .toList();
    }

    @Transactional
    public DataResponse create(CreateDataRequest request) {
        return DataResponse.from(repository.save(new Data(request.text())));
    }
}
