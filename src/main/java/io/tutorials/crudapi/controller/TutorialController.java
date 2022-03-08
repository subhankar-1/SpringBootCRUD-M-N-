package io.tutorials.crudapi.controller;

import io.tutorials.crudapi.exception.ResourceNotFoundException;
import io.tutorials.crudapi.model.Tutorial;
import io.tutorials.crudapi.repository.TutorialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class TutorialController {
    @Autowired
    TutorialRepository tutorialRepository;
    @GetMapping("/tutorials")
    public ResponseEntity<Map<String, Object>> getAllTutorials(@RequestParam(required = false) String title,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "3") int size) {
        List<Tutorial> tutorials = new ArrayList<Tutorial>();
        Pageable paging = PageRequest.of(page, size);

        Page<Tutorial> pageTuts;
        if (title == null)
            pageTuts = tutorialRepository.findAll(paging);
        else
            pageTuts = tutorialRepository.findByTitleContaining(title, paging);
        tutorials = pageTuts.getContent();
        Map<String, Object> response = new HashMap<>();
        response.put("tutorials", tutorials);
        response.put("currentPage", pageTuts.getNumber());
        response.put("totalItems", pageTuts.getTotalElements());
        response.put("totalPages", pageTuts.getTotalPages());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/tutorials/{id}")
    public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
        Tutorial tutorial = tutorialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Tutorial with id = " + id));

        return new ResponseEntity<>(tutorial, HttpStatus.OK);
    }
    @PostMapping("/tutorials")
    public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
        Tutorial _tutorial = tutorialRepository
                .save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(), false));
        return new ResponseEntity<>(_tutorial, HttpStatus.CREATED);

    }
    @PutMapping("/tutorials/{id}")
    public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") long id, @RequestBody Tutorial tutorial) {
        Tutorial _tutorial = tutorialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Not found Tutorial with id = " + id));

        _tutorial.setTitle(tutorial.getTitle());
        _tutorial.setDescription(tutorial.getDescription());
        _tutorial.setPublished(tutorial.isPublished());

        return new ResponseEntity<>(tutorialRepository.save(_tutorial), HttpStatus.OK);
    }
    @DeleteMapping("/tutorials/{id}")
    public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable("id") long id) {
        tutorialRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping("/tutorials")
    public ResponseEntity<HttpStatus> deleteAllTutorials() {
        try {
            tutorialRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/tutorials/published")
    public ResponseEntity<Map<String, Object>> findByPublished(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "3") int size) {
        List<Tutorial> tutorials = new ArrayList<Tutorial>();
        Pageable paging = PageRequest.of(page, size);

        Page<Tutorial> pageTuts = tutorialRepository.findByPublished(true, paging);
        tutorials = pageTuts.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("tutorials", tutorials);
        response.put("currentPage", pageTuts.getNumber());
        response.put("totalItems", pageTuts.getTotalElements());
        response.put("totalPages", pageTuts.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
