package indigo

val SUIT = arrayOf('♠', '♥', '♦', '♣')
val RANK = arrayOf("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A")

var deck = mutableListOf<String>()
var player = mutableListOf<String>()
var computer = mutableListOf<String>()
var playerTurn = true
var lastCard = ""
var table = mutableListOf<String>()
var playerCards = 0
var computerCards = 0
var playerScore = 0
var computerScore = 0
var lastWins = ""

fun main() {
    startGame()
    play()
    println("Game Over")
}

fun startGame() {
    println("Indigo Card Game")
    while (true) {
        println("Play first?")
        when (readln().lowercase()) {
            "yes" -> break
            "no" -> {
                playerTurn = false
                break
            }
        }
    }
    newDeck()
    deck.shuffle()
    print("Initial cards on the table:")
    repeat(4) {
        val card = deck.removeLast()
        print(" $card")
        lastCard = card
        table += card
    }
    println("\n")
}

fun newDeck() {
    for (r in RANK) {
        for (s in SUIT) {
            deck += "$r$s"
        }
    }
}

fun play() {
    while (deck.isNotEmpty()) {
        dealCards()
        repeat(12) {
            println(
                if (table.isEmpty())
                    "No cards on the table"
                else
                    "${table.size} cards on the table, and the top card is $lastCard"
            )
            val card = if (playerTurn) {
                playerTurn()
            } else {
                computerTurn()
            }
            if (card == "exit") return
            table += card
            lastCard = if (wins(card)) {
                printResults()
            } else {
                card
            }
            println()
            playerTurn = !playerTurn
        }
    }
    end()
}

fun playerTurn(): String {
    print("Cards in hand:")
    for (i in 1..player.size) {
        print(" $i)${player[i - 1]}")
    }
    while (true) {
        println("\nChoose a card to play (1-${player.size}):")
        val input = readln()
        if (input == "exit") return input
        try {
            val n = input.toInt()
            if (n in 1..player.size) {
                return player.removeAt(n - 1)
            }
        } catch (_: NumberFormatException) {}
    }
}

fun computerTurn(): String {
    computer.forEach { print("$it ") }
    val card = getCandidate()
    println("\nComputer plays $card")
    return card
}

fun getCandidate(): String {
    val candidates = if (table.isEmpty() || computer.count { wins(it) } == 0) {
        computer.toList()
    } else {
        computer.filter { wins(it) }
    }
    for (s in SUIT) {
        if (candidates.count { it.endsWith(s) } > 1) {
            candidates.forEach {
                if (it.endsWith(s)) {
                    computer.remove(it)
                    return it
                }
            }
        }
    }
    for (r in RANK) {
        if (candidates.count { it.startsWith(r) } > 1) {
            candidates.forEach {
                if (it.startsWith(r)) {
                    computer.remove(it)
                    return it
                }
            }
        }
    }
    val card = candidates.first()
    computer.remove(card)
    return card
}

fun printResults(): String {
    countScore()
    lastWins = if (playerTurn) "Player" else "Computer"
    table.clear()
    println("$lastWins wins cards")
    printScores()
    return "?"
}

fun end() {
    if (table.isEmpty()) {
        println("No cards on the table")
    } else {
        println("${table.size} cards on the table, and the top card is $lastCard")
        playerTurn = lastWins == "Player" || lastWins.isEmpty() && playerTurn
        countScore()
    }
    addingScore()
    printScores()
}

fun countScore() {
    val score = table.count { Regex("[1AJQK]").matches(it.substring(0, 1)) }
    if (playerTurn) {
        playerScore += score
        playerCards += table.size
    } else {
        computerScore += score
        computerCards += table.size
    }
}

fun printScores() {
    println("Score: Player $playerScore - Computer $computerScore")
    println("Cards: Player $playerCards - Computer $computerCards")
}

fun wins(card: String) = card[0] == lastCard[0] || card.last() == lastCard.last()

fun dealCards() {
    repeat(6) {
        player.add(deck.removeLast())
        computer.add(deck.removeLast())
    }
}

fun addingScore() {
    if (playerCards > computerCards || playerCards == computerCards && playerTurn) {
        playerScore += 3
    } else {
        computerScore += 3
    }
}