package org.example.taskmanger.controller;

import lombok.AllArgsConstructor;
import org.example.taskmanger.database.dto.TaskCreateEditDto;
import org.example.taskmanger.database.dto.TaskReadDto;
import org.example.taskmanger.database.entity.TaskStatus;
import org.example.taskmanger.service.TasksService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {

    private final TasksService tasksService;

    @PostMapping
    public ResponseEntity<TaskReadDto> create(@RequestBody TaskCreateEditDto dto){
        return mappingResponseTask(tasksService.create(dto));
    }


    @GetMapping
    public ResponseEntity<List<TaskReadDto>> findAll(){
        return mappingResponseListTask(tasksService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskReadDto> findById(@PathVariable Long id) {
        return mappingResponseTask(tasksService.findById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskReadDto>> findByStatus(@PathVariable TaskStatus status){
        return mappingResponseListTask(tasksService.findByStatus(status));

    }
    @GetMapping("/category/{id}")
    public ResponseEntity<List<TaskReadDto>> findByCategoryId(@PathVariable Long id){
        return mappingResponseListTask(tasksService.findByCategoryId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskReadDto> update(
            @PathVariable Long id,
            @RequestBody TaskCreateEditDto dto){
        return mappingResponseTask(tasksService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public HttpStatus delete(@PathVariable Long id){
        tasksService.delete(id);
        return HttpStatus.OK;
    }

    private ResponseEntity<TaskReadDto> mappingResponseTask(TaskReadDto dto){
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private ResponseEntity<List<TaskReadDto>> mappingResponseListTask(List<TaskReadDto> dto){
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
