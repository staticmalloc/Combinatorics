import kotlin.math.min
import kotlin.system.measureTimeMillis

// Author: Afanasyev Semyon Timofeevich
// Date: 09.12.2021

// Функция для перебора размещений границ
internal fun increment(c: IntArray, bordersSize: Int, combinationSize: Int): Boolean {
    val positions = bordersSize + combinationSize
    var ind = c.size - 1
    while (ind >= 0) {
        c[ind]++
        val base = positions - (bordersSize - ind)
        if (c[ind] > base) {
            c[ind] = if (ind > 0) min((c[ind - 1] + 2), base) else 0
            for (i in ind until bordersSize - 1)
                c[i + 1] = min((c[i] + 1), base + (1 + i - ind))
            ind--
        } else {
            return true
        }
    }
    return false
}

// Итеративная реализация метода, возвращающего сочетания с повторениями из заданного множества
// Реализация через границы количеств взятий каждого элемента
// Возращает сет ассоциативных массивов, где каждый из массивов - это одно сочетание.
// Ассоциативный массив содержит в себе пары "элемент - количество"
fun <T> Set<T>.combinationsWithRepetitionsIter(combinationSize: Int): Set<Map<T, Int>> = when {
    combinationSize < 0 -> throw Error("Размер комбинации не может быть отрицательным. Получен размер: {$combinationSize}")
    combinationSize == 0 -> setOf(mapOf())
    else -> {
        val result = mutableSetOf<Map<T, Int>>()
        val bordersSize = this.size - 1
        val bordersPositions = IntArray(bordersSize) { i -> i }
        do {
            var i = -1
            val curMap = mutableMapOf<T, Int>()
            for (el in this) {
                val count = (
                        when {
                            i == -1 -> bordersPositions[0]
                            i < bordersSize - 1 ->
                                bordersPositions[i + 1] - bordersPositions[i] - 1
                            else -> bordersSize + combinationSize - 1 - bordersPositions[i]
                        })
                if (count > 0)
                    curMap[el] = count
                i++
            }
            result.add(curMap)
        } while (increment(bordersPositions, bordersSize, combinationSize))
        result
    }
}

// Функциональная реализация метода, возвращающего сочетания с повторениями из заданного множества
// Возращает сет ассоциативных массивов, где каждый из массивов - это одно сочетание.
// Ассоциативный массив содержит в себе пары "элемент - количество"
fun <T> Set<T>.combinationsWithRepetitionsFunc(combinationSize: Int): Set<Map<T, Int>> = when {
    combinationSize < 0 -> throw Error("Размер комбинации не может быть отрицательным. Получен размер: {$combinationSize}")
    combinationSize == 0 -> setOf(mapOf())
    else -> combinationsWithRepetitionsFunc(combinationSize - 1)
        .flatMap { subset -> this.map { subset + (it to (subset.getOrElse(it) { 0 } + 1)) } }
        .toSet()
}

// Функция для отображения сочетаний
fun <T> displayCombinations(set: Set<Map<T, Int>>) = set.forEach {
    it.forEach { (k, v) ->
        for (i in 0 until v) print(k)
    }
    println()
}

fun main() {
    println("Результат работы функциональной реализации:")
    displayCombinations(setOf('a', 'b', 'c').combinationsWithRepetitionsFunc(3))
    println("Результат работы итеративной реализации:")
    displayCombinations(setOf('a', 'b', 'c').combinationsWithRepetitionsFunc(3))
    println()
    val set = setOf('a', 'b', 'c', 'd', 'e', 'f')
    val combinationSize = 20
    println("Время исполнения функциональной реализации: " + (measureTimeMillis {
        set.combinationsWithRepetitionsFunc(
            combinationSize
        )
    }) + " ms")
    println("Время исполнения итеративной реализации: " + (measureTimeMillis {
        set.combinationsWithRepetitionsIter(
            combinationSize
        )
    }) + " ms")
}

// Заключение
// Безусловно, функциональный метод через рекурсию горазда проще в реазиации и занимает всего 5 строк,
// выглядит очень понятно и лаконично
// Итеративный метод совсем не тривиален, но несмотря на 3 вложенных цикла, он дает лучшую производительность
// Можно сделать скидку на то, что итеративный метод решает задачу не в лоб, а через теоремы комбинаторики,
// решая тем самым смежную задачу.

// Итого по резульататам тестов:
// Функциональная реализация имеет факториальную сложность от количества элементов O(n!)
// Итеративная реализация имеет экспоненциальную сложность с линейным показателем от количества элементов O(2^n)
// Функциональная реализация имеет квадратичную сложность от размера сочетания O(k^2)
// Итеративная реализация имеет линейную сложность от размера сочетания O(k*a)
// Получаем сложность функциональной реализации O(n! * k^2)
// Получаем сложность итеративной реализации O(2^n * k * a)

/* Вывод программы при n=6 и k=20:
Результат работы функциональной реализации:
aaa
aab
aac
abb
abc
acc
bbb
bbc
bcc
ccc
Результат работы итеративной реализации:
aaa
aab
aac
abb
abc
acc
bbb
bbc
bcc
ccc

Время исполнения функциональной реализации: 14179 ms
Время исполнения итеративной реализации: 1642 ms
*/