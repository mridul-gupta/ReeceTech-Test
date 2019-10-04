package ui

import data.Contact
import data.Result
import data.source.Repository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class Presenter(
    private val repository: Repository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    var selectedAB: Int = 1

    /* change to observable type to update UI */
    var selectedABContacts: MutableList<Contact> = mutableListOf()
    var allUniqueContacts: MutableList<Contact> = mutableListOf()

    var dataLoading: Boolean = false

    fun addContact(contact: Contact) {
        runBlocking {
            repository.addContact(selectedAB, contact)
        }
    }

    fun removeContact(contact: Contact) {
        runBlocking {
            repository.removeContact(selectedAB, contact)
        }
    }

    fun getContacts() {
        dataLoading = true

        runBlocking {
            dataLoading = true
            val result = repository.getContacts(selectedAB)

            selectedABContacts.clear()
            if (result is Result.Success) {
                selectedABContacts.addAll(result.data)
            } else {
                logger(result as Result.Error)
            }
        }
        dataLoading = false
    }

    fun getUniqueContactsAcross() {
        dataLoading = true

        runBlocking {
            val result = repository.getUniqueContactsAcross()

            allUniqueContacts.clear()
            if (result is Result.Success) {
                allUniqueContacts.addAll(result.data)
            } else {
                logger(result as Result.Error)
            }
        }
        dataLoading = false
    }
}