package hu.selester.seltransport.objects

class SessionClass {
    companion object {
        private val dataMap = mutableMapOf<String, String>()

        fun getValue(key: String): String {
            return if (dataMap.containsKey(key)) dataMap[key]!! else ""
        }

        fun setValue(key: String, value: String) {
            dataMap[key] = value
        }
    }
}