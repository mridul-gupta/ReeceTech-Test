package ui

import data.Contact
import data.source.DefaultRepository
import data.source.Repository
import data.source.local.LocalDataSource


fun printPrompt() {
    println("\n=================================================================")
    println("Selected Addressbook: ${presenter.selectedAB}")
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
            presenter.selectedAB = newAB
        }
    } catch (e: Exception) {
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


fun addContact() {
    val contact = getContactFromUser() ?: return
    presenter.addContact(contact)
    printContacts()
}

fun removeContact() {
    val contact = getContactFromUser() ?: return
    presenter.removeContact(contact)
    printContacts()
}

fun printContacts() {
    println(presenter.getContacts())
}

fun printUniqueContacts() {
    println(presenter.getUniqueContactsAcross())
}

private fun obtainRepository(): Repository {
    return DefaultRepository(
        LocalDataSource()
    )
}

val presenter: Presenter = Presenter(obtainRepository())

fun main() {

    while (true) {
        printPrompt()

        val input = readLine()
        if (input?.length!! != 1) {
            println("Invalid input\n")
            continue
        }

        when (input[0].toUpperCase()) {
            'S' -> switchAB()
            'A' -> addContact()
            'R' -> removeContact()
            'P' -> printContacts()
            'U' -> printUniqueContacts()
            'Q' -> {
                println("Bye!!")
                return
            }
            else -> println("Invalid input")
        }
    }
}

