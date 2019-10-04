package ui

import kotlinx.coroutines.runBlocking
import data.Contact
import data.Result
import data.source.DefaultRepository
import data.source.Repository
import data.source.local.LocalDataSource


fun printPrompt() {
    println("\np=================================================================")
    println("Selected Addressbook: $selectedAB")
    println("Commands:")
    println("    press 'S' to select address book (default 1)")
    println("    press 'A' to add contact")
    println("    press 'R' to remove contact")
    println("    press 'P' print all contacts")
    println("    press 'U' to print unique contacts in all address books")
    println("    press 'Q' to exit")
    print("Enter command: ")
}

fun switchAB() {
    try {
        print("Please enter Address book number: ")
        val newAB: Int? = readLine()?.toInt()

        if (newAB != null && newAB > 0 && newAB <= 5) {
            selectedAB = newAB
        }
    } catch (e : Exception) {
        println("Invalid Address book selected. Max 5 address books.")
    }
}

fun getContactFromUser(): Contact? {
    print("Please enter name: ")
    val name = readLine()

    print("Please enter phone: ")
    val phone = readLine()

    if (name.isNullOrBlank() || phone.isNullOrBlank()) {
        println("Error")
        return null
    }
    return Contact(name, phone)
}


fun addContact(selectedAB: Int) {
    val contact = getContactFromUser() ?: return
    runBlocking { repository.addContact(selectedAB, contact) }
    printContacts(selectedAB)
}

fun removeContact(selectedAB: Int) {
    val contact = getContactFromUser() ?: return

    runBlocking { repository.removeContact(selectedAB, contact) }
    printContacts(selectedAB)
}

fun printContacts(selectedAB: Int) {
    runBlocking {
        val result = repository.getContacts(selectedAB)
        if (result is Result.Success) {
            if (result.data.isNotEmpty())
                println(result.data)
            else
                println("Address book empty")
        } else {
            println(result)
        }
    }
}

fun printUniqueContacts() {
    runBlocking { println(repository.getUniqueContactsAcross()) }
}

fun obtainRepository(): Repository {
    return DefaultRepository(
        LocalDataSource()
    )
}

var selectedAB: Int = 1
lateinit var repository: Repository

fun main() {
    repository = obtainRepository()

    while (true) {
        printPrompt()

        val input = readLine()
        if (input?.length!! != 1) {
            println("Invalid input\n")
            continue
        }

        when (input[0].toUpperCase()) {
            'S' -> switchAB()
            'A' -> addContact(selectedAB)
            'R' -> removeContact(selectedAB)
            'P' -> printContacts(selectedAB)
            'U' -> printUniqueContacts()
            'Q' -> {
                println("Bye!!")
                return
            }
            else -> println("Invalid input")
        }
    }
}

