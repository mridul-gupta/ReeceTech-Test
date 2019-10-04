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

    fun getContacts(): List<Contact> =
        runBlocking {
            val result = repository.getContacts(selectedAB)
            if (result is Result.Success) {
                return@runBlocking result.data
            } else {
                logger(result as Result.Error)
                return@runBlocking emptyList<Contact>()
            }
        }

    fun getUniqueContactsAcross(): List<Contact> =
        runBlocking {
            val result = repository.getUniqueContactsAcross()
            if (result is Result.Success) {
                return@runBlocking result.data
            } else {
                logger(result as Result.Error)
                return@runBlocking emptyList<Contact>()
            }
        }
}