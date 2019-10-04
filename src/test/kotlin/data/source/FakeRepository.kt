package data.source

import data.Contact
import data.Result

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 */
class FakeRepository : Repository {
    private var addressBooks: MutableMap<Int, MutableList<Contact>> = mutableMapOf()
    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun addContact(addressBookId: Int, contact: Contact) {
        var localList = addressBooks[addressBookId]

        if (localList == null)
            localList = mutableListOf()
        localList.add(contact)
        addressBooks[addressBookId] = localList
    }

    override suspend fun removeContact(addressBookId: Int, contact: Contact) {
        addressBooks[addressBookId]?.remove(contact)
    }

    override suspend fun getContacts(addressBookId: Int): Result<List<Contact>> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test exception"))
        }

        val list = addressBooks[addressBookId] as List<Contact>?
        return when {
            list == null -> Result.Error(Exception("Address book not found"))
            list.isEmpty() -> Result.Success(list)
            else -> Result.Success(list)
        }
    }

    override suspend fun getUniqueContactsAcross(): Result<List<Contact>> {
        if (shouldReturnError) {
            return Result.Error(Exception("Test exception"))
        }
        return Result.Success(addressBooks.flatMap { it.value }.distinct())
    }
}
