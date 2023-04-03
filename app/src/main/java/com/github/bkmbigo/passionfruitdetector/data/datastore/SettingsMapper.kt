package com.github.bkmbigo.passionfruitdetector.data.datastore

inline fun <reified  T: Enum<T>> Int.settingGetValue(): T = enumValues<T>()[this]
inline fun <reified  T: Enum<T>> T.settingSetValue(): Int = enumValues<T>().indexOf(this)