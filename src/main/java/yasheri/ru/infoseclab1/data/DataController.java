package yasheri.ru.infoseclab1.data;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/data")
class DataController {

    private final DataService service;

    DataController(DataService service) {
        this.service = service;
    }

    @GetMapping
    public List<DataResponse> findAll() {
        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<DataResponse> create(
            @Valid @RequestBody CreateDataRequest request
    ) {
        DataResponse created = service.create(request);
        return ResponseEntity
                .created(URI.create("/api/data/" + created.id()))
                .body(created);
    }
}
