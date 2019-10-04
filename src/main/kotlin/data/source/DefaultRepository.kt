package data.source

import data.Contact
import data.Result
import data.source.local.LocalDataSource
import kotlinx.coroutines.*

class DefaultRepository(
    private val localDataSource: LocalDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : Repository {

    /**
     * Data Cache can be implemented here.
     */

    override suspend fun addContact(addressBookId: Int, contact: Contact) {
        coroutineScope {
            launch { localDataSource.addContact(addressBookId, contact) }
        }
    }

    override suspend fun removeContact(addressBookId: Int, contact: Contact) {
        coroutineScope {
            launch { localDataSource.removeContact(addressBookId, contact) }
        }
    }

    override suspend fun getContacts(addressBookId: Int): Result<List<Contact>> {
        return withContext(ioDispatcher) {
            /**
             * get from remote if applicable
             * cache data if needed
             */

            val localContacts = localDataSource.getContacts(addressBookId)
            if (localContacts is Result.Success) {
                return@withContext localContacts
            }
            return@withContext Result.Error(Exception("getContacts: Error fetching from remote and local"))
        }
    }

    override suspend fun getUniqueContactsAcross(): Result<List<Contact>> {
        return withContext(ioDispatcher) {
            /**
             * get from remote if applicable
             * cache data if needed
             */

            val localContacts = localDataSource.getUniqueContactsAcross()
            if (localContacts is Result.Success) {
                return@withContext localContacts
            }
            return@withContext Result.Error(Exception("getUniqueContactsAcross: Error fetching from remote and local"))
        }
    }
}

