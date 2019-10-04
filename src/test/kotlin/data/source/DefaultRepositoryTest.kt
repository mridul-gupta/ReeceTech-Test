package data.source

import data.Contact
import data.Result
import data.source.local.LocalDataSource
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultRepositoryTest {

    private val contact1 = Contact("Donald Trump", "435625701")
    private val contact2 = Contact("Scott Morrison", "435625702")
    private val contact3 = Contact("Narendra Modi", "435625703")
    private val contact4 = Contact("Borris Jhonson", "435625704")
    private val contact5 = Contact("Xi Jinping", "435625705")
    private lateinit var localDataSource: LocalDataSource


    /* class under test */
    private lateinit var repository: DefaultRepository

    @Before
    fun setUp() = runBlocking {
        localDataSource = LocalDataSource()
        /* add to AB1 */
        localDataSource.addContact(1, contact1)
        localDataSource.addContact(1, contact2)
        localDataSource.addContact(1, contact3)

        /* add to AB2 */
        localDataSource.addContact(2, contact4)
        localDataSource.addContact(2, contact5)

        repository = DefaultRepository(localDataSource = localDataSource)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun addContact_CheckPresenceInSameAddressBook() = runBlocking {
        val addressBokId = 1
        val newContact = Contact("Vladimir Putin", "435625706")

        assertFalse((localDataSource.getContacts(addressBokId) as Result.Success).data.contains(newContact))
        assertFalse((repository.getContacts(addressBokId) as Result.Success).data.contains(newContact))

        repository.addContact(addressBokId, newContact)

        assertTrue((localDataSource.getContacts(addressBokId) as Result.Success).data.contains(newContact))
        assertTrue((repository.getContacts(addressBokId) as Result.Success).data.contains(newContact))
    }

    @Test
    fun addContact_NotPresentInAnotherAddressBook() = runBlocking {
        val addressBokIdAdd = 1
        val addressBokIdCheck = 2
        val newContact = Contact("Putin Vladimir", "435625706")
        assertFalse((localDataSource.getContacts(addressBokIdAdd) as Result.Success).data.contains(newContact))
        assertFalse((repository.getContacts(addressBokIdAdd) as Result.Success).data.contains(newContact))
        assertFalse((localDataSource.getContacts(addressBokIdCheck) as Result.Success).data.contains(newContact))
        assertFalse((repository.getContacts(addressBokIdCheck) as Result.Success).data.contains(newContact))

        repository.addContact(addressBokIdAdd, newContact)

        assertTrue((localDataSource.getContacts(addressBokIdAdd) as Result.Success).data.contains(newContact))
        assertTrue((repository.getContacts(addressBokIdAdd) as Result.Success).data.contains(newContact))
        assertFalse((localDataSource.getContacts(addressBokIdCheck) as Result.Success).data.contains(newContact))
        assertFalse((repository.getContacts(addressBokIdCheck) as Result.Success).data.contains(newContact))
    }

    @Test
    fun addContact_ExistingContactAddedAgain() = runBlocking {
        val addressBokIdAdd = 1
        assertTrue((localDataSource.getContacts(addressBokIdAdd) as Result.Success).data.contains(contact1))
        assertTrue((repository.getContacts(addressBokIdAdd) as Result.Success).data.contains(contact1))

        repository.addContact(addressBokIdAdd, contact1)

        assertTrue(
            ((localDataSource.getContacts(addressBokIdAdd) as Result.Success).data)
                .filter { it == contact1 }.size == 2
        )
        assertTrue((repository.getContacts(addressBokIdAdd) as Result.Success).data.contains(contact1))
    }

    @Test
    fun removeContact_CheckPresenceInSameAddressBook() = runBlocking {
        val addressBokId = 1
        val testContact = contact3
        assertTrue((localDataSource.getContacts(addressBokId) as Result.Success).data.contains(testContact))
        assertTrue((repository.getContacts(addressBokId) as Result.Success).data.contains(testContact))

        repository.removeContact(addressBokId, testContact)

        assertFalse((localDataSource.getContacts(addressBokId) as Result.Success).data.contains(testContact))
        assertFalse((repository.getContacts(addressBokId) as Result.Success).data.contains(testContact))
    }

    @Test
    fun removeContact_NotRemovedFromAnotherAddressBook() = runBlocking {
        val addressBook1 = 1
        val addressBook2 = 2
        val newContact = Contact("Justin Trudeau", "435625707")

        /* not present in both AB */
        assertFalse((localDataSource.getContacts(addressBook1) as Result.Success).data.contains(newContact))
        assertFalse((repository.getContacts(addressBook1) as Result.Success).data.contains(newContact))
        assertFalse((localDataSource.getContacts(addressBook2) as Result.Success).data.contains(newContact))
        assertFalse((repository.getContacts(addressBook2) as Result.Success).data.contains(newContact))

        /* add to both AB */
        repository.addContact(addressBook1, newContact)
        repository.addContact(addressBook2, newContact)

        /* present in both AB */
        assertTrue((localDataSource.getContacts(addressBook1) as Result.Success).data.contains(newContact))
        assertTrue((repository.getContacts(addressBook1) as Result.Success).data.contains(newContact))
        assertTrue((localDataSource.getContacts(addressBook2) as Result.Success).data.contains(newContact))
        assertTrue((repository.getContacts(addressBook2) as Result.Success).data.contains(newContact))

        /* remove from AB 2 */
        repository.removeContact(addressBook2, newContact)

        /* present in AB 1 only */
        assertTrue((localDataSource.getContacts(addressBook1) as Result.Success).data.contains(newContact))
        assertTrue((repository.getContacts(addressBook1) as Result.Success).data.contains(newContact))
        assertFalse((localDataSource.getContacts(addressBook2) as Result.Success).data.contains(newContact))
        assertFalse((repository.getContacts(addressBook2) as Result.Success).data.contains(newContact))
    }

    @Test
    fun removeContact_NotExistingContact() = runBlocking {
        val addressBook1 = 1
        val addressBookBeforeSize = (repository.getContacts(addressBook1) as Result.Success).data.size
        val newContact = Contact("Justin Trudeau", "435625707")

        /* not present in both AB */
        assertFalse((localDataSource.getContacts(addressBook1) as Result.Success).data.contains(newContact))
        assertFalse((repository.getContacts(addressBook1) as Result.Success).data.contains(newContact))

        /* remove from AB */
        repository.removeContact(addressBook1, newContact)

        val addressBookAfterSize = (repository.getContacts(addressBook1) as Result.Success).data.size

        /* present in AB 1 only */
        assertTrue(addressBookBeforeSize == addressBookAfterSize)
    }

    @Test
    fun getContacts_StandardCase() = runBlocking {
        val addressBook1 = 1
        val addressBook2 = 2

        assertTrue((repository.getContacts(addressBook1) as Result.Success).data.size == 3)
        assertTrue((repository.getContacts(addressBook2) as Result.Success).data.size == 2)
    }

    @Test
    fun getContacts_NoAddressBooks_Error() = runBlocking {
        /* init new repo */
        val localDataSource = LocalDataSource()
        val repository = DefaultRepository(localDataSource)
        val addressBook1 = 1

        assertTrue(repository.getContacts(addressBook1) is Result.Error)
    }

    @Test
    fun getContacts_AddressBookNotExists_Error() = runBlocking {
        val addressBook1 = 500
        val result = repository.getContacts(addressBook1)
        assertTrue(result is Result.Error)
    }

    @Test
    fun getContacts_AddressBookEmpty() = runBlocking {
        val addressBook1 = 1
        repository.removeContact(addressBook1, contact1)
        repository.removeContact(addressBook1, contact2)
        repository.removeContact(addressBook1, contact3)

        assertTrue((repository.getContacts(addressBook1) as Result.Success).data.isEmpty())
    }


    @Test
    fun getUniqueContactsAcross_StandardCase() = runBlocking {
        assertTrue((repository.getUniqueContactsAcross() as Result.Success).data.size == 5)
    }

    @Test
    fun getUniqueContactsAcross_AddRepeated() = runBlocking {
        val addressBook1 = 1
        val addressBook2 = 2
        repository.addContact(addressBook1, contact1)
        repository.addContact(addressBook1, contact1)
        repository.addContact(addressBook1, contact1)
        repository.addContact(addressBook2, contact3)
        repository.addContact(addressBook2, contact2)
        assertTrue((repository.getUniqueContactsAcross() as Result.Success).data.size == 5)
    }

    @Test
    fun getUniqueContactsAcross_AddExistingToOtherAddressBook() = runBlocking {
        val addressBook3 = 3
        val addressBook4 = 4
        val addressBook10 = 10
        val addressBook11 = 11
        val addressBook12 = 12
        repository.addContact(addressBook3, contact1)
        repository.addContact(addressBook4, contact1)
        repository.addContact(addressBook10, contact1)
        repository.addContact(addressBook11, contact3)
        repository.addContact(addressBook12, contact2)
        assertTrue((repository.getUniqueContactsAcross() as Result.Success).data.size == 5)
    }
}