package com.kotlin.kotlinspring.web

import com.kotlin.kotlinspring.model.Note
import com.kotlin.kotlinspring.repository.NoteRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping
@RestController("/notes")
class NoteController(val repository: NoteRepository) {

    @GetMapping
    fun findAll() = repository.findAll()

    @GetMapping("/{id}")
    fun find(@PathVariable("id") id: Long) = repository.findById(id).map { note ->
        ResponseEntity.ok(note)
    }.orElse(ResponseEntity.notFound().build())

    @PostMapping
    fun create(@RequestBody note: Note) = repository.save(note)

    @PutMapping("/{id}")
    fun update(@PathVariable("id") id: Long, @RequestBody note: Note) =
        repository.findById(id).map { n ->
            val updated: Note = n.copy(title = note.title, body = note.body)
            repository.save(updated)
            ResponseEntity.ok(updated)
        }.orElse(ResponseEntity.notFound().build())

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Long) = repository.deleteById(id)

}