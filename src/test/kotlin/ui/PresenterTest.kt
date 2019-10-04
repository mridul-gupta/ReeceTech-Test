package ui

import data.Contact
import data.source.FakeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PresenterTest {
    /* Subject under test */
    private lateinit var presenter: Presenter

    /* use a fake repository to inject into presenter */
    private lateinit var repository: FakeRepository

    private val contact1 = Contact("Donald Trump", "435625701")
    private val contact2 = Contact("Scott Morrison", "435625702")
    private val contact3 = Contact("Narendra Modi", "435625703")
    private val contact4 = Contact("Borris Jhonson", "435625704")
    private val contact5 = Contact("Xi Jinping", "435625705")


    @Before
    fun setUp() {
        repository = FakeRepository()
        presenter = Presenter(repository)

        presenter.addContact(contact1)
        presenter.addContact(contact2)
        presenter.addContact(contact3)
        presenter.addContact(contact4)
        presenter.addContact(contact5)
    }

    @Test
    fun addContact_Success() {
        val newContact = Contact("Roger Federer", "435433223")

        /* check if new contact is not present */
        presenter.getContacts()
        assertFalse(presenter.selectedABContacts.contains(newContact))

        /* add a new contact */
        presenter.addContact(newContact)

        /* get contacts after adding */
        presenter.getContacts()

        /* check if new contact is present */
        assertTrue(presenter.selectedABContacts.contains(newContact))
    }

    @Test
    fun removeContact_Success() {
        val remContact = Contact("Scott Morrison", "435625702")

        /* check if new contact is present */
        presenter.getContacts()
        assertTrue(presenter.selectedABContacts.contains(remContact))

        /* remove a contact */
        presenter.removeContact(remContact)

        /* get contacts after adding */
        presenter.getContacts()

        /* check if new contact is present */
        assertFalse(presenter.selectedABContacts.contains(remContact))
    }

    @Test
    fun getContacts_Error() {
        /* Make the repository return errors */
        repository.setReturnError(true)

        /* get contacts */
        presenter.getContacts()

        /* Then data loading is not set */
        assertFalse(presenter.dataLoading)

        /* And the list is empty */
        assertTrue(presenter.selectedABContacts.isEmpty())
    }

    @Test
    fun getContacts_Success() {
        /* dont make the repository return errors */
        repository.setReturnError(false)

        /* get contacts */
        presenter.getContacts()

        /* Then data loading is not set */
        assertFalse(presenter.dataLoading)

        /* And the list is empty */
        assertTrue(presenter.selectedABContacts.size == 5)
    }

    @Test
    fun getUniqueContactsAcross_Success() {
        /* dont make the repository return errors */
        repository.setReturnError(false)

        /* add more */
        presenter.addContact(contact1)
        presenter.addContact(contact2)
        presenter.addContact(contact3)
        presenter.addContact(contact4)
        presenter.addContact(contact5)

        /* get contacts */
        presenter.getUniqueContactsAcross()

        /* Then data loading is not set */
        assertFalse(presenter.dataLoading)

        /* And the list is empty */
        assertTrue(presenter.allUniqueContacts.size == 5)
    }

    @Test
    fun getUniqueContactsAcross_Error() {
        /* dont make the repository return errors */
        repository.setReturnError(true)

        /* add more */
        presenter.addContact(contact1)
        presenter.addContact(contact2)
        presenter.addContact(contact3)
        presenter.addContact(contact4)
        presenter.addContact(contact5)

        /* get contacts */
        presenter.getUniqueContactsAcross()

        /* Then data loading is not set */
        assertFalse(presenter.dataLoading)

        /* And the list is empty */
        assertTrue(presenter.allUniqueContacts.isEmpty())
    }
}