package com.udacity.vehicles.api;


import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.service.CarService;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/cars")
class CarController {

    private final CarService carService;
    private final CarResourceAssembler assembler;

    CarController(CarService carService, CarResourceAssembler assembler) {
        this.carService = carService;
        this.assembler = assembler;
    }

    @GetMapping
    Resources<Resource<Car>> list() {
        List<Resource<Car>> resources = carService.list().stream().map(assembler::toResource)
                .collect(Collectors.toList());
        return new Resources<>(resources,
                linkTo(methodOn(CarController.class).list()).withSelfRel());
    }

    @GetMapping("/{id}")
    Resource<Car> get(@PathVariable Long id) {
        Car car = carService.findById(id);
        return assembler.toResource(car);
    }

    @PostMapping
    ResponseEntity<?> post(@Valid @RequestBody Car car) throws URISyntaxException {
        Car newCar = carService.save(car);
        Resource<Car> resource = assembler.toResource(newCar);
        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    @PutMapping("/{id}")
    ResponseEntity<?> put(@PathVariable Long id, @Valid @RequestBody Car car) {
        car.setId(id);
        Car updatedCar = carService.save(car);
        Resource<Car> resource = assembler.toResource(updatedCar);
        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
