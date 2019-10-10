package data.source.local

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import data.Contact
import data.Result
import data.source.DataSource

class LocalDataSource(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DataSource {

    /* local data */
    private var addressBooks: MutableMap<Int, MutableList<Contact>> = mutableMapOf()
    private var contactCount: MutableMap<Contact, Int> = mutableMapOf()

    override suspend fun addContact(addressBookId: Int, contact: Contact) {
        var localList = addressBooks[addressBookId]

        if (localList == null)
            localList = mutableListOf()
        localList.add(contact)
        addressBooks[addressBookId] = localList

        if (contactCount[contact] == null) {
            contactCount[contact] = 1
        } else {
            contactCount[contact] = contactCount[contact]!!.plus(1)
        }
    }

    override suspend fun removeContact(addressBookId: Int, contact: Contact) {
        addressBooks[addressBookId]?.remove(contact)
    }

    override suspend fun getContacts(addressBookId: Int): Result<List<Contact>> =
        withContext(ioDispatcher) {
            val list = addressBooks[addressBookId] as List<Contact>?

            when {
                list == null -> Result.Error(Exception("Address book not found"))
                list.isEmpty() -> Result.Success(list)
                else -> Result.Success(list)
            }
        }

    override suspend fun getUniqueContactsAcross(): Result<List<Contact>> =
        withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(addressBooks.flatMap { it.value }.distinct())
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getDuplicates(): Result<List<Contact>> =
        withContext(ioDispatcher) {
            val duplicates = contactCount.filter { it.value > 1 }.map { it.key }

            return@withContext try {
                Result.Success(duplicates)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
}