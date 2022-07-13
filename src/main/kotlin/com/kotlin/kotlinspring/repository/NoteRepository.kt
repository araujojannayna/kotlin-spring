package com.kotlin.kotlinspring.repository

import com.kotlin.kotlinspring.model.Note
import org.springframework.data.repository.CrudRepository

interface NoteRepository: CrudRepository<Note, Long> {
}