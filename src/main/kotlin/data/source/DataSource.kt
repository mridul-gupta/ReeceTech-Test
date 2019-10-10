package data.source

import data.Contact
import data.Result

interface DataSource {

    suspend fun addContact(addressBookId: Int, contact: Contact)

    suspend fun removeContact(addressBookId: Int, contact: Contact)

    suspend fun getContacts(addressBookId: Int): Result<List<Contact>>

    suspend fun getUniqueContactsAcross(): Result<List<Contact>>

    suspend fun getDuplicates(): Result<List<Contact>>

}