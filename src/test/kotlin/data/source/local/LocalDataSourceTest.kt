package data.source.local

import data.Contact
import data.Result
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LocalDataSourceTest {
    /* class under test */
    private lateinit var localDataSource: LocalDataSource

    private val contact1 = Contact("Donald Trump", "435625701")
    private val contact2 = Contact("Scott Morrison", "435625702")
    private val contact3 = Contact("Narendra Modi", "435625703")
    private val contact4 = Contact("Borris Jhonson", "435625704")
    private val contact5 = Contact("Xi Jinping", "435625705")
    private val contact6 = Contact("Xi Jinping", "435625705")

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
        localDataSource.addContact(2, contact6)
    }


    @Test
    fun addContact_CheckPresenceInSameAddressBook() = runBlocking {
        val addressBokId = 1
        val newContact = Contact("Vladimir Putin", "435625706")

        assertFalse((localDataSource.getContacts(addressBokId) as Result.Success).data.contains(newContact))

        localDataSource.addContact(addressBokId, newContact)

        assertTrue((localDataSource.getContacts(addressBokId) as Result.Success).data.contains(newContact))
    }

    @Test
    fun addContact_NotPresentInAnotherAddressBook() = runBlocking {
        val addressBokIdAdd = 1
        val addressBokIdCheck = 2
        val newContact = Contact("Putin Vladimir", "435625706")
        assertFalse((localDataSource.getContacts(addressBokIdAdd) as Result.Success).data.contains(newContact))
        assertFalse((localDataSource.getContacts(addressBokIdCheck) as Result.Success).data.contains(newContact))

        localDataSource.addContact(addressBokIdAdd, newContact)

        assertTrue((localDataSource.getContacts(addressBokIdAdd) as Result.Success).data.contains(newContact))
        assertFalse((localDataSource.getContacts(addressBokIdCheck) as Result.Success).data.contains(newContact))
    }

    @Test
    fun addContact_ExistingContactAddedAgain() = runBlocking {
        val addressBokIdAdd = 1
        assertTrue((localDataSource.getContacts(addressBokIdAdd) as Result.Success).data.contains(contact1))

        localDataSource.addContact(addressBokIdAdd, contact1)

        assertTrue(
            ((localDataSource.getContacts(addressBokIdAdd) as Result.Success).data)
                .filter { it == contact1 }.size == 2
        )
    }

    @Test
    fun removeContact_CheckPresenceInSameAddressBook() = runBlocking {
        val addressBokId = 1
        val testContact = contact3
        assertTrue((localDataSource.getContacts(addressBokId) as Result.Success).data.contains(testContact))

        localDataSource.removeContact(addressBokId, testContact)

        assertFalse((localDataSource.getContacts(addressBokId) as Result.Success).data.contains(testContact))
    }

    @Test
    fun removeContact_NotRemovedFromAnotherAddressBook() = runBlocking {
        val addressBook1 = 1
        val addressBook2 = 2
        val newContact = Contact("Justin Trudeau", "435625707")

        /* not present in both AB */
        assertFalse((localDataSource.getContacts(addressBook1) as Result.Success).data.contains(newContact))
        assertFalse((localDataSource.getContacts(addressBook2) as Result.Success).data.contains(newContact))

        /* add to both AB */
        localDataSource.addContact(addressBook1, newContact)
        localDataSource.addContact(addressBook2, newContact)

        /* present in both AB */
        assertTrue((localDataSource.getContacts(addressBook1) as Result.Success).data.contains(newContact))
        assertTrue((localDataSource.getContacts(addressBook2) as Result.Success).data.contains(newContact))

        /* remove from AB 2 */
        localDataSource.removeContact(addressBook2, newContact)

        /* present in AB 1 only */
        assertTrue((localDataSource.getContacts(addressBook1) as Result.Success).data.contains(newContact))
        assertFalse((localDataSource.getContacts(addressBook2) as Result.Success).data.contains(newContact))
    }

    @Test
    fun removeContact_NotExistingContact() = runBlocking {
        val addressBook1 = 1
        val addressBookBeforeSize = (localDataSource.getContacts(addressBook1) as Result.Success).data.size
        val newContact = Contact("Justin Trudeau", "435625707")

        /* not present in both AB */
        assertFalse((localDataSource.getContacts(addressBook1) as Result.Success).data.contains(newContact))

        /* remove from AB */
        localDataSource.removeContact(addressBook1, newContact)

        val addressBookAfterSize = (localDataSource.getContacts(addressBook1) as Result.Success).data.size

        /* present in AB 1 only */
        assertTrue(addressBookBeforeSize == addressBookAfterSize)
    }

    @Test
    fun getContacts_StandardCase() = runBlocking {
        val addressBook1 = 1
        val addressBook2 = 2

        assertTrue((localDataSource.getContacts(addressBook1) as Result.Success).data.size == 3)
        assertTrue((localDataSource.getContacts(addressBook2) as Result.Success).data.size == 2)
    }

    @Test
    fun getContacts_NoAddressBooks_Error() = runBlocking {
        /* init new repo */
        val localDataSource = LocalDataSource()
        val addressBook1 = 1

        assertTrue(localDataSource.getContacts(addressBook1) is Result.Error)
    }

    @Test
    fun getContacts_AddressBookNotExists_Error() = runBlocking {
        val addressBook1 = 500
        val result = localDataSource.getContacts(addressBook1)
        assertTrue(result is Result.Error)
    }

    @Test
    fun getContacts_AddressBookEmpty() = runBlocking {
        val addressBook1 = 1
        localDataSource.removeContact(addressBook1, contact1)
        localDataSource.removeContact(addressBook1, contact2)
        localDataSource.removeContact(addressBook1, contact3)

        assertTrue((localDataSource.getContacts(addressBook1) as Result.Success).data.isEmpty())
    }


    @Test
    fun getUniqueContactsAcross_StandardCase() = runBlocking {
        assertTrue((localDataSource.getUniqueContactsAcross() as Result.Success).data.size == 5)
    }

    @Test
    fun getUniqueContactsAcross_AddRepeated() = runBlocking {
        val addressBook1 = 1
        val addressBook2 = 2
        localDataSource.addContact(addressBook1, contact1)
        localDataSource.addContact(addressBook1, contact1)
        localDataSource.addContact(addressBook1, contact1)
        localDataSource.addContact(addressBook2, contact3)
        localDataSource.addContact(addressBook2, contact2)
        assertTrue((localDataSource.getUniqueContactsAcross() as Result.Success).data.size == 5)
    }

    @Test
    fun getUniqueContactsAcross_AddExistingToOtherAddressBook() = runBlocking {
        val addressBook3 = 3
        val addressBook4 = 4
        val addressBook10 = 10
        val addressBook11 = 11
        val addressBook12 = 12
        localDataSource.addContact(addressBook3, contact1)
        localDataSource.addContact(addressBook4, contact1)
        localDataSource.addContact(addressBook10, contact1)
        localDataSource.addContact(addressBook11, contact3)
        localDataSource.addContact(addressBook12, contact2)
        assertTrue((localDataSource.getUniqueContactsAcross() as Result.Success).data.size == 5)
    }

    @Test
    fun getDuplicates_Success() = runBlocking {
        val result = localDataSource.getDuplicates()

        if (result is Result.Success) {
            assertTrue(result.data.size == 1);
            assertTrue(result.data[0].name == "Xi Jinping");
            assertTrue(result.data[0].phone == "435625705");
        } else
            assertTrue(false)
    }
}