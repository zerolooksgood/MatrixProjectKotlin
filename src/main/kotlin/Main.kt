package org.example

import java.security.InvalidParameterException
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

fun main() {
    val originalMatrix: Array<IntArray> = matrixGenerator()
    displayMatrix(originalMatrix)

    val shortestPathMatrix: Array<IntArray> = calculateMatrix(originalMatrix, true)
    val shortestPath: MutableList<IntArray> = findPath(shortestPathMatrix, true)

    val longestPathMatrix: Array<IntArray> = calculateMatrix(originalMatrix, false)
    val longestPath: MutableList<IntArray> = findPath(longestPathMatrix, false)

    displayMatrix(shortestPathMatrix, shortestPath)
    displayMatrix(longestPathMatrix, longestPath)
}

private fun matrixGenerator(height: Int = Random.nextInt(2, 11), width: Int = Random.nextInt(2, 11), minValue: Int = 1, maxValue: Int = 100): Array<IntArray> {
    if (height < 2 || width < 2) throw InvalidParameterException("The height or width of the matrix has to be at least 2")

    return Array(height) { IntArray(width) { Random.nextInt(minValue, maxValue + 1) } }
}

private fun displayMatrix(matrix: Array<IntArray>, path: MutableList<IntArray>? = null) {
    validateMatrix(matrix)

    var maxNumLength = 0
    matrix.forEach { row ->
        row.forEach { col ->
            maxNumLength = max(maxNumLength, col.toString().length)
        }
    }

    matrix.forEachIndexed { rowIndex, row ->
        row.forEachIndexed { colIndex, col ->
            var formatedText: String = col.toString()
            while (formatedText.length < maxNumLength) {
                formatedText = " $formatedText"
            }

            if (pathContainsPos(path, intArrayOf(rowIndex, colIndex))) {
                print("\u001B[32m[$formatedText]\u001B[0m")
            } else {
                print("[$formatedText]")
            }
        }
        println()
    }
    println()
}

private fun pathContainsPos(path: MutableList<IntArray>?, pos: IntArray): Boolean {
    path?.let {::validatePath}

    if (pos.size != 2) {  throw InvalidParameterException("Provided position is not two dimensional") }

    path?.let { return path.any { pathPos -> pathPos[0] == pos[0] && pathPos[1] == pos[1] } }
    return false
}

private fun calculateMatrix(matrix: Array<IntArray>, shortestPath: Boolean): Array<IntArray> { //shortestPath is true if the shortest path is to be calculated and false if the highest path
    val copiedMatrix = Array(matrix.size) { row ->
        IntArray(matrix[0].size) { col ->
            matrix[row][col]
        }
    }

    (1..<copiedMatrix.size).forEach { row ->
        copiedMatrix[row][0] += copiedMatrix[row - 1][0]
    }

    (1..<copiedMatrix[0].size).forEach { col ->
        copiedMatrix[0][col] += copiedMatrix[0][col - 1]
    }

    (1..<copiedMatrix.size).forEach { row ->
        (1..<copiedMatrix[row].size).forEach { col ->
            val num1: Int = copiedMatrix[row - 1][col]
            val num2: Int = copiedMatrix[row][col - 1]

            copiedMatrix[row][col] += if (shortestPath) min(num1, num2) else max(num1, num2)
        }
    }

    return copiedMatrix
}

private fun findPath(matrix: Array<IntArray>, shortestPath: Boolean): MutableList<IntArray> { //Same here as calculatePath when it comes to shortestPath
    val path: MutableList<IntArray> = mutableListOf<IntArray>()

    var row = matrix.size - 1
    var col = matrix[0].size - 1

    while (row != 0 || col != 0) {
        path.add(intArrayOf(row, col))

        when {
            row == 0 -> col--
            col == 0 -> row--
            (matrix[row][col - 1] <= matrix[row -1][col]) == shortestPath -> col--
            else -> row--
        }
    }

    return path
}

private fun validateMatrix(matrix: Array<IntArray>, checkUniformSize: Boolean = true, checkMinSize: Boolean = true) {
    if (checkUniformSize) {
        val referenceSize: Int = matrix[0].size
        if (matrix.any { row -> row.size != referenceSize }) {
            throw InvalidParameterException("Not all the rows in the matrix are the same size")
        }
    }

    if (checkMinSize) {
        if (matrix.size < 2 || matrix.any { row -> row.size < 2 }) {
            throw InvalidParameterException("Not all dimensions in the matrix are larger than 2")
        }
    }
}

private fun validatePath(path: MutableList<IntArray>, checkCoordinateSize: Boolean = true, checkPositiveCoordinates: Boolean = true) {
    if (checkCoordinateSize) {
        if (path.any { position -> position.size != 2 }) {
            throw InvalidParameterException("Not all the positions in the path are two dimensional")
        }
    }

    if (checkPositiveCoordinates) {
        if (path.any { position -> position[0] < 0 || position[1] < 0 }) {
            throw InvalidParameterException("Not all the positions in the path are positive positions")
        }
    }
}