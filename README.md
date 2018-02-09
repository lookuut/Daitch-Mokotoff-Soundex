# Daitch-Mokotoff-Soundex
<p>Daitch–Mokotoff Soundex realization in scala</p>
<p>Spark app for csv parse and calculate algorithm codes for Russian names</p>
<p>Sample of work</p> 
In csv file
<br/>
FIO|BirthDate<br/>
Yana A. Petrova|1990.01.01<br/>
Jana Alex. Petrovva|1990.1.1<br/>
Ksenija, Ivanoff|1992.02.02<br/>
Ivanova, Xeniya Pavlovna|1992.02.2<br/>
Maxim Andreevich Sidorov|1974.12.23<br/>
Anastasia Aleksandrova|1992.02.2<br/>


Result<br/>
<br/>
ID|FIO|BirthDate<br/>
TJABB739700760000|Yana A. Petrova|1990.01.01<br/>
TJABB739700760000|Jana Alex. Petrovva|1990.1.1<br/>
TJCCC076700560000|Ksenija, Ivanoff|1992.02.02<br/>
TJCCC076700560000|Ivanova, Xeniya Pavlovna|1992.02.2<br/>
THEMX439700654600|Maxim Andreevich Sidorov|1974.12.23<br/>
TJCCC06434008546397|Anastasia Aleksandrova|1992.02.2<br/>
