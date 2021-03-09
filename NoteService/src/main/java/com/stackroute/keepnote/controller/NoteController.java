package com.stackroute.keepnote.controller;

import java.net.http.HttpHeaders;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.stackroute.keepnote.exception.NoteNotFoundExeption;
import com.stackroute.keepnote.model.Note;
import com.stackroute.keepnote.service.NoteService;

/*
 * As in this assignment, we are working with creating RESTful web service, hence annotate
 * the class with @RestController annotation.A class annotated with @Controller annotation
 * has handler methods which returns a view. However, if we use @ResponseBody annotation along
 * with @Controller annotation, it will return the data directly in a serialized 
 * format. Starting from Spring 4 and above, we can use @RestController annotation which 
 * is equivalent to using @Controller and @ResposeBody annotation
 */
@RestController
public class NoteController {

	/*
	 * Autowiring should be implemented for the NoteService. (Use Constructor-based
	 * autowiring) Please note that we should not create any object using the new
	 * keyword
	 */
	@Autowired
	NoteService noteService;

	public NoteController(NoteService noteService) {
		this.noteService = noteService;
	}

	/*
	 * Define a handler method which will create a specific note by reading the
	 * Serialized object from request body and save the note details in the
	 * database.This handler method should return any one of the status messages
	 * basis on different situations: 1. 201(CREATED) - If the note created
	 * successfully. 2. 409(CONFLICT) - If the noteId conflicts with any existing
	 * user.
	 * 
	 * This handler method should map to the URL "/api/v1/note" using HTTP POST
	 * method
	 */
	@PostMapping("/api/v1/note")
	public ResponseEntity<?> addNote(@RequestBody Note note) {
		if (noteService.createNote(note)) {
			return new ResponseEntity<Note>(note, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<String>("Note not created.", HttpStatus.CONFLICT);
		}
	}

	/*
	 * Define a handler method which will delete a note from a database. This
	 * handler method should return any one of the status messages basis on
	 * different situations: 1. 200(OK) - If the note deleted successfully from
	 * database. 2. 404(NOT FOUND) - If the note with specified noteId is not found.
	 *
	 * This handler method should map to the URL "/api/v1/note/{id}" using HTTP
	 * Delete method" where "id" should be replaced by a valid noteId without {}
	 */
	@DeleteMapping("/api/v1/note/{userId}/{id}")
	public ResponseEntity<String> deleteNote(@PathVariable String userId, @PathVariable() int id) {
		if (noteService.deleteNote(userId, id)) {
			return new ResponseEntity<String>("Note Deleted.", HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("Note not Deleted.", HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/api/v1/note/{userId}")
	public ResponseEntity<String> deleteAllNotes(@PathVariable() String userId) {
		try {
			noteService.deleteAllNotes(userId);
			return new ResponseEntity<String>("Deleted all notes.", HttpStatus.OK);
		} catch (NoteNotFoundExeption exception) {
			return new ResponseEntity<String>("\"Notes not Deleted.", HttpStatus.NOT_FOUND);
		}
	}

	/*
	 * Define a handler method which will update a specific note by reading the
	 * Serialized object from request body and save the updated note details in a
	 * database. This handler method should return any one of the status messages
	 * basis on different situations: 1. 200(OK) - If the note updated successfully.
	 * 2. 404(NOT FOUND) - If the note with specified noteId is not found.
	 * 
	 * This handler method should map to the URL "/api/v1/note/{id}" using HTTP PUT
	 * method.
	 */
	@PutMapping("/api/v1/note/{userId}/{id}")
	public ResponseEntity<?> updateNote(@PathVariable("userId") String userId, @PathVariable("id") int id,
			@RequestBody Note note) {
		try {
			Note noteUpd = noteService.updateNote(note, id, userId);
			return new ResponseEntity<>("Note Updated", HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity<>("Note Not Found", HttpStatus.NOT_FOUND);
		}

	}

	/*
	 * Define a handler method which will get us the all notes by a userId. This
	 * handler method should return any one of the status messages basis on
	 * different situations: 1. 200(OK) - If the note found successfully.
	 * 
	 * This handler method should map to the URL "/api/v1/note" using HTTP GET
	 * method
	 */
	@GetMapping("/api/v1/note/{userId}")
	public ResponseEntity<?> getAllNoteByUserId(@PathVariable("userId") String userId) {
		List<Note> noteList = noteService.getAllNoteByUserId(userId);
		return new ResponseEntity<>(noteList, HttpStatus.OK);
	}

	/*
	 * Define a handler method which will show details of a specific note created by
	 * specific user. This handler method should return any one of the status
	 * messages basis on different situations: 1. 200(OK) - If the note found
	 * successfully. 2. 404(NOT FOUND) - If the note with specified noteId is not
	 * found. This handler method should map to the URL
	 * "/api/v1/note/{userId}/{noteId}" using HTTP GET method where "id" should be
	 * replaced by a valid reminderId without {}
	 * 
	 */
	@GetMapping("/api/v1/note/{userId}/{noteId}")
	public ResponseEntity<?> getNoteByUserId(@PathVariable String userId, @PathVariable("noteId") int noteId) {
		try {
			Note note = noteService.getNoteByNoteId(userId, noteId);
			return new ResponseEntity<>(note, HttpStatus.OK);
		} catch (NoteNotFoundExeption e) {
			return new ResponseEntity<>("Note not Found.", HttpStatus.NOT_FOUND);
		}
	}

}
