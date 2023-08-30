# Daitch-Mokotoff Soundex (D-M Soundex) implementation in Scala for Spark

Open source project The **Daitch-Mokotoff Soundex in Scala**  designed to improve the accuracy of phonetic matching for names that come from a variety of languages, especially those with Slavic origins. It produces codes that are more distinct for different names that might sound similar but have different linguistic origins. The algorithm takes into account both the beginning and ending of a name to generate its code.
Project allows run implementation using **Spark**. So it's most efficient for process huge amount of data.

## Algorithm

1. Assign Numeric Values: Assign numeric values to the letters of the alphabet based on their phonetic properties. Similar sounding letters receive similar values. For example:

 - b, f, p, v → 1
 - c, g, j, k, q, s, x, z → 2
 - d, t → 3
 - l → 4
 - m, n → 5
 - r → 6
 - a, e, i, o, u, y → 7

2. Prepare Name: Convert the name to uppercase and remove any non-alphabet characters.

3. Generate Codes:
 - Initial Character Code: Assign a code to the first letter of the name based on its numeric value.
 - Ending Pattern: Determine the last few characters of the name that form a specific pattern. This pattern is assigned a special code.

4. Output: Combine the initial character code and the ending pattern code to create the final D-M Soundex code.

## Implementation 
Developed using functional language **Scala** and adapted for distributed running in **Spark**
## Input 
CSV formatted file with column delimiter `|`
Example of input file 
```
FIO|BirthDate
Yana A. Petrova|1990.01.01
Jana Alex. Petrovva|1990.1.1
Ksenija, Ivanoff|1992.02.02
Ivanova, Xeniya Pavlovna|1992.02.2
Maxim Andreevich Sidorov|1974.12.23
Anastasia Aleksandrova|1992.02.2
```

## Output
```
ID|FIO|BirthDate
TJABB739700760000|Yana A. Petrova|1990.01.01
TJABB739700760000|Jana Alex. Petrovva|1990.1.1
TJCCC076700560000|Ksenija, Ivanoff|1992.02.02
TJCCC076700560000|Ivanova, Xeniya Pavlovna|1992.02.2
THEMX439700654600|Maxim Andreevich Sidorov|1974.12.23
TJCCC06434008546397|Anastasia Aleksandrova|1992.02.2
```