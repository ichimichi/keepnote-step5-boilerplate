package com.stackroute.keepnote.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.stackroute.keepnote.exception.NoteNotFoundExeption;
import com.stackroute.keepnote.model.Note;
import com.stackroute.keepnote.model.NoteUser;
import com.stackroute.keepnote.repository.NoteRepository;

/*
* Service classes are used here to implement additional business logic/validation 
* This class has to be annotated with @Service annotation.
* @Service - It is a specialization of the component annotation. It doesn't currently 
* provide any additional behavior over the @Component annotation, but it's a good idea 
* to use @Service over @Component in service-layer classes because it specifies intent 
* better. Additionally, tool support and additional behavior might rely on it in the 
* future.
* */
@Service
public class NoteServiceImpl implements NoteService {

	/*
	 * Autowiring should be implemented for the NoteRepository and MongoOperation.
	 * (Use Constructor-based autowiring) Please note that we should not create any
	 * object using the new keyword.
	 */
	@Autowired
	NoteRepository noteRepository;

	/*
	 * This method should be used to save a new note.
	 */
	public boolean createNote(Note note) {
		NoteUser noteuser = new NoteUser();
		noteuser.setUserId(note.getNoteCreatedBy());

		List<Note> noteList = new ArrayList<>();

		noteList.add(note);
		noteuser.setNotes(noteList);

		NoteUser noteUser = noteRepository.insert(noteuser);
		if (noteUser == null) {
			return false;
		}
		return true;
	}
	/* This method should be used to delete an existing note. */

	public boolean deleteNote(String userId, int noteId) {
		NoteUser noteUser = noteRepository.findById(userId).get();
		if (noteUser == null) {
			return false;
		}
		noteRepository.delete(noteUser);
		return true;
	}
	/* This method should be used to delete all notes with specific userId. */

	public boolean deleteAllNotes(String userId) {
		NoteUser noteUser = noteRepository.findById(userId).get();
		List<Note> noteList = noteUser.getNotes();
		if (noteList != null) {
			List<Note> filteredNoteList = new ArrayList<>();
			noteList.forEach(note -> {
				if (!note.getNoteCreatedBy().equals(userId)) {
					filteredNoteList.add(note);
				}
			});
			noteUser.setNotes(filteredNoteList);
			noteRepository.save(noteUser);
			return true;
		}
		return false;
	}

	/*
	 * This method should be used to update a existing note.
	 */
	public Note updateNote(Note note, int id, String userId) throws NoteNotFoundExeption {
		try {
			NoteUser noteUser = noteRepository.findById(userId).get();
			List<Note> noteList = noteUser.getNotes();
			if (noteList != null) {
				List<Note> updateNotesList = new ArrayList<>();
				for (Note noteElement : noteList) {
					if (noteElement.getNoteId() == id) {
						updateNotesList.add(note);
					} else {
						updateNotesList.add(noteElement);
					}
				}
				noteUser.setNotes(updateNotesList);
				noteRepository.save(noteUser);
			}
		} catch (NoSuchElementException e) {
			throw new NoteNotFoundExeption("NoteNotFoundExeption");
		}
		return note;
	}

	/*
	 * This method should be used to get a note by noteId created by specific user
	 */
	public Note getNoteByNoteId(String userId, int noteId) throws NoteNotFoundExeption {
		try {
			NoteUser noteUser = noteRepository.findById(userId).get();
			List<Note> noteList = noteUser.getNotes();
			if (noteList != null) {
				for (Note note : noteList) {
					if (note.getNoteId() == noteId) {
						return note;
					}
				}
			}
		} catch (NoSuchElementException exception) {
			throw new NoteNotFoundExeption("NoteNotFoundExeption");
		}
		return null;
	}

	/*
	 * This method should be used to get all notes with specific userId.
	 */
	public List<Note> getAllNoteByUserId(String userId) {
		NoteUser noteUser = noteRepository.findById(userId).get();
		return noteUser.getNotes();
	}

}
